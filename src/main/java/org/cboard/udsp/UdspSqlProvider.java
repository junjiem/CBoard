package org.cboard.udsp;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.hex.bigdata.udsp.client.factory.ConsumerClientFactory;
import com.hex.bigdata.udsp.client.impl.SqlClient;
import com.hex.bigdata.udsp.constant.SdkConstant;
import com.hex.bigdata.udsp.model.request.SqlRequest;
import com.hex.bigdata.udsp.model.response.pack.SyncPackResponse;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.cboard.cache.CacheManager;
import org.cboard.cache.HeapCacheManager;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.Initializing;
import org.cboard.dataprovider.aggregator.Aggregatable;
import org.cboard.dataprovider.annotation.DatasourceParameter;
import org.cboard.dataprovider.annotation.ProviderName;
import org.cboard.dataprovider.annotation.QueryParameter;
import org.cboard.dataprovider.config.*;
import org.cboard.dataprovider.result.AggregateResult;
import org.cboard.dataprovider.result.ColumnIndex;
import org.cboard.dataprovider.util.DPCommonUtils;
import org.cboard.exception.CBoardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Types;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by JunjieM on 2017-7-11.
 */
@ProviderName(name = "UdspSql")
public class UdspSqlProvider extends DataProvider implements Aggregatable, Initializing {
    private static final Logger LOG = LoggerFactory.getLogger(UdspSqlProvider.class);

    @Value("${dataprovider.resultLimit:300000}")
    private int resultLimit;

    @DatasourceParameter(label = "{{'DATAPROVIDER.UDSP.UDSP_SERVERS'|translate}}",
            required = true,
            placeholder = "<ip>:<port>",
            type = DatasourceParameter.Type.Input,
            order = 1)
    private String UDSP_SERVERS = "udspServers";

    @DatasourceParameter(label = "{{'DATAPROVIDER.UDSP.USERNAME'|translate}}",
            type = DatasourceParameter.Type.Input,
            order = 2)
    private String USERNAME = "username";

    @DatasourceParameter(label = "{{'DATAPROVIDER.UDSP.PASSWORD'|translate}}",
            type = DatasourceParameter.Type.Password,
            order = 3)
    private String PASSWORD = "password";

    @DatasourceParameter(label = "{{'DATAPROVIDER.AGGREGATABLE_PROVIDER'|translate}}",
            type = DatasourceParameter.Type.Checkbox,
            order = 4)
    private String AGGREGATE_PROVIDER = "aggregateProvider";

    @QueryParameter(label = "{{'DATAPROVIDER.UDSP.SERVICE_NAME'|translate}}",
            required = true,
            type = QueryParameter.Type.Input,
            order = 1)
    private String SERVICE_NAME = "serviceName";

    @QueryParameter(label = "{{'DATAPROVIDER.UDSP.SQL_TEXT'|translate}}",
            required = true,
            type = QueryParameter.Type.TextArea,
            order = 2)
    private String SQL = "sql";

    private static final CacheManager<Map<String, String>> typeCahce = new HeapCacheManager<>();

    private DimensionConfigHelper dimensionConfigHelper;

    /**
     * Convert the sql text to subquery string:
     * remove blank line
     * remove end semicolon ;
     *
     * @param rawQueryText
     * @return
     */
    private String getAsSubQuery(String rawQueryText) {
        String deletedBlankLine = rawQueryText.replaceAll("(?m)^[\\s\t]*\r?\n", "").trim();
        return deletedBlankLine.endsWith(";") ? deletedBlankLine.substring(0, deletedBlankLine.length() - 1) : deletedBlankLine;
    }

    private String getUrl() {
        String udspServers = dataSource.get(UDSP_SERVERS);
        if (StringUtils.isBlank(udspServers))
            throw new CBoardException("Datasource config UDSP Servers can not be empty.");
        String url = "http://" + udspServers + "/udsp/http/consume";
        LOG.info("UDSP url: " + url);
        return url;
    }

    private SqlRequest getRequest() {
        String username = dataSource.get(USERNAME);
        if (StringUtils.isBlank(username))
            throw new CBoardException("Datasource config Username can not be empty.");
        String password = dataSource.get(PASSWORD);
        if (StringUtils.isBlank(password))
            throw new CBoardException("Datasource config Password can not be empty.");
        String serviceName = query.get(SERVICE_NAME);
        if (StringUtils.isBlank(serviceName))
            throw new CBoardException("Dataset config ServiceName can not be empty.");
        String sql = query.get(SQL);
        if (StringUtils.isBlank(sql))
            throw new CBoardException("Dataset config SQL can not be empty.");
        sql = getAsSubQuery(sql);
        LOG.info("SQL String: \n" + sql);

        SqlRequest request = new SqlRequest();
        request.setServiceName(serviceName);
        request.setEntity(SdkConstant.CONSUMER_ENTITY_START);
        request.setType(SdkConstant.CONSUMER_TYPE_SYNC);
        request.setUdspUser(username);
        request.setToken(password);
        request.setAppUser("admin");
        request.setSql(sql);

        return request;
    }

    @Override
    public String[][] getData() throws Exception {
        LOG.debug("Execute UdspSqlProvider.getData() Start!");
        List<Map<String, String>> results = getResults(getRequest());
        List<String[]> list = getHeaderAndDatas(results);
        return list.toArray(new String[][]{});
    }

    @Override
    public void test() throws Exception {
        getColumnInfos();
    }

    private List<Map<String, String>> getResults(SqlRequest request) {
        SqlClient client = ConsumerClientFactory.createCustomClient(SqlClient.class, getUrl());
        SyncPackResponse response = null;
        try {
            response = client.syncStart(request);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new CBoardException(t.getMessage());
        }
        if ("DEFEAT".equals(response.getStatus())) {
            throw new CBoardException(response.getMessage());
        }
        List<Map<String, String>> results = response.getRecords();
        if (results == null || results.size() == 0) {
            throw new CBoardException("dataset is null");
        }
        if (results.size() > resultLimit) {
            throw new CBoardException("Cube result count is greater than limit " + resultLimit);
        }
        return results;
    }

    /**
     * 获取列名称以及列类型
     *
     * @param request
     * @return
     */
    private LinkedHashMap<String, String> getColumnInfos(SqlRequest request) {
        SqlClient client = ConsumerClientFactory.createCustomClient(SqlClient.class, getUrl());
        SyncPackResponse response = null;
        try {
            response = client.syncStart(request);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new CBoardException(t.getMessage());
        }
        if ("DEFEAT".equals(response.getStatus())) {
            throw new CBoardException(response.getMessage());
        }
        return response.getReturnColumns();
    }

    /**
     * 获取列名称以及列类型
     *
     * @return
     */
    public Map<String, String> getColumnInfos() {
        String fsql = "\nSELECT * FROM (\n%s\n) hb_view WHERE 1=0";
        SqlRequest request = getRequest();
        String sql = String.format(fsql, request.getSql());
        LOG.info(sql);
        request.setSql(sql);
        return this.getColumnInfos(request);
    }


    private List<String[]> getHeaderAndDatas(List<Map<String, String>> results) {
        List<String[]> list = new LinkedList<>();
        String[] columns = getColumns(results);
        list.add(columns);
        list.addAll(getDatas(results, columns));
        return list;
    }

    private List<String[]> getDatas(List<Map<String, String>> results, String[] columns) {
        List<String[]> list = new LinkedList<>();
        for (int i = 0; i < results.size(); i++) {
            list.add(getValues(columns, results.get(i)));
        }
        return list;
    }

    private String[] getValues(String[] columns, Map<String, String> map) {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        String[] row = new String[columns.length];
        int i = 0;
        for (String col : columns) {
            row[i++] = map.get(col);
        }
        return row;
    }

    private String[] getColumns(List<Map<String, String>> results) {
        Set<Map.Entry<String, String>> entrySet = results.get(0).entrySet();
        int i = 0;
        String[] row = new String[entrySet.size()];
        for (Map.Entry<String, String> entry : entrySet) {
            row[i++] = entry.getKey();
        }
        return row;
    }

    private String[] getArrayColumns(Map<String, String> columns) {
        String[] row = new String[columns.size()];
        int i = 0;
        for (String column : columns.keySet()) {
            row[i++] = column;
        }
        return row;
    }

    @Override
    public boolean doAggregationInDataSource() {
        String v = dataSource.get(AGGREGATE_PROVIDER);
        return v != null && "true".equals(v);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            dimensionConfigHelper = new DimensionConfigHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] queryDimVals(String columnName, AggConfig config) throws Exception {
        SqlRequest request = getRequest();
        List<String> filtered = new ArrayList<>();
        String whereStr = "";
        if (config != null) {
            Stream<DimensionConfig> c = config.getColumns().stream();
            Stream<DimensionConfig> r = config.getRows().stream();
            Stream<ConfigComponent> f = config.getFilters().stream();
            Stream<ConfigComponent> filters = Stream.concat(Stream.concat(c, r), f);
            whereStr = assembleSqlFilter(filters, "WHERE");
        }
        String fsql = "SELECT hb_view.%s FROM (\n%s\n) hb_view %s GROUP BY hb_view.%s";
        String exec = String.format(fsql, columnName, request.getSql(), whereStr, columnName);
        LOG.info(exec);
        request.setSql(exec);
        List<Map<String, String>> results = getResults(request);
        for (Map<String, String> map : results) {
            if (map.entrySet().size() == 1) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    filtered.add(entry.getValue());
                }
            }
        }
        return filtered.toArray(new String[]{});
    }

    @Override
    public String[] getColumn() throws Exception {
        String fsql = "\nSELECT * FROM (\n%s\n) hb_view WHERE 1=0";
        SqlRequest request = getRequest();
        String sql = String.format(fsql, request.getSql());
        LOG.info(sql);
        request.setSql(sql);
        LinkedHashMap<String, String> results = getColumnInfos(request);
        String[] columns = getArrayColumns(results);
        return columns;
    }


    @Override
    public AggregateResult queryAggData(AggConfig config) throws Exception {
        SqlRequest request = getRequest();
        String exec = getQueryAggDataSql(request, config);
        LOG.info(exec);
        request.setSql(exec);
        List<Map<String, String>> results = getResults(request);
        List<String[]> list = getDatas(results, getColumns(results));
        return DPCommonUtils.transform2AggResult(config, list);
    }

    @Override
    public String viewAggDataQuery(AggConfig config) throws Exception {
        return getQueryAggDataSql(getRequest(), config);
    }

    private String getQueryAggDataSql(SqlRequest request, AggConfig config) throws Exception {
        Stream<DimensionConfig> c = config.getColumns().stream();
        Stream<DimensionConfig> r = config.getRows().stream();
        Stream<ConfigComponent> f = config.getFilters().stream();
        Stream<ConfigComponent> filters = Stream.concat(Stream.concat(c, r), f);
        Map<String, String> types = getColumnType();
        Stream<DimensionConfig> dimStream = Stream.concat(config.getColumns().stream(), config.getRows().stream());

        String dimColsStr = assembleDimColumns(dimStream);
        String aggColsStr = assembleAggValColumns(config.getValues().stream(), types);
        String whereStr = assembleSqlFilter(filters, "WHERE");
        String groupByStr = org.apache.commons.lang.StringUtils.isBlank(dimColsStr) ? "" : "GROUP BY " + dimColsStr;

        StringJoiner selectColsStr = new StringJoiner(",");
        if (!org.apache.commons.lang.StringUtils.isBlank(dimColsStr)) {
            selectColsStr.add(dimColsStr);
        }
        if (!org.apache.commons.lang.StringUtils.isBlank(aggColsStr)) {
            selectColsStr.add(aggColsStr);
        }

        String fsql = "\nSELECT %s \n FROM (\n%s\n) hb_view \n %s \n %s";
        String exec = String.format(fsql, selectColsStr, request.getSql(), whereStr, groupByStr);
        return exec;
    }

    private String assembleDimColumns(Stream<DimensionConfig> columnsStream) {
        StringJoiner columns = new StringJoiner(", ", "", " ");
        columns.setEmptyValue("");
        columnsStream.map(g -> g.getColumnName()).distinct().filter(e -> e != null).forEach(columns::add);
        return columns.toString();
    }

    private String assembleAggValColumns(Stream<ValueConfig> selectStream, Map<String, String> types) {
        StringJoiner columns = new StringJoiner(", ", "", " ");
        columns.setEmptyValue("");
        selectStream.map(m -> toSelect.apply(m, types)).filter(e -> e != null).forEach(columns::add);
        return columns.toString();
    }

    /**
     * Assemble all the filter to a legal sal where script
     *
     * @param filterStream
     * @param prefix       HAVING or WHERE
     * @return
     */
    private String assembleSqlFilter(Stream<ConfigComponent> filterStream, String prefix) {
        StringJoiner where = new StringJoiner("\nAND ", prefix + " ", "");
        where.setEmptyValue("");
        filterStream.map(e -> separateNull(e)).map(e -> configComponentToSql(e)).filter(e -> e != null).forEach(where::add);
        return where.toString();
    }

    private String configComponentToSql(ConfigComponent cc) {
        if (cc instanceof DimensionConfig) {
            return filter2SqlCondtion.apply((DimensionConfig) cc);
        } else if (cc instanceof CompositeConfig) {
            CompositeConfig compositeConfig = (CompositeConfig) cc;
            String sql = compositeConfig.getConfigComponents().stream().map(e -> separateNull(e)).map(e -> configComponentToSql(e)).collect(Collectors.joining(" " + compositeConfig.getType() + " "));
            return "(" + sql + ")";
        }
        return null;
    }

    private BiFunction<ValueConfig, Map<String, String>, String> toSelect = (config, types) -> {
        String aggExp;
        if (config.getColumn().contains(" ")) {
            aggExp = config.getColumn();
            for (String column : types.keySet()) {
                aggExp = aggExp.replaceAll(" " + column + " ", " hb_view." + column + " ");
            }
        } else {
            aggExp = "hb_view." + config.getColumn();
        }
        switch (config.getAggType()) {
            case "sum":
                return "SUM(" + aggExp + ")";
            case "avg":
                return "AVG(" + aggExp + ")";
            case "max":
                return "MAX(" + aggExp + ")";
            case "min":
                return "MIN(" + aggExp + ")";
            case "distinct":
                return "COUNT(DISTINCT " + aggExp + ")";
            default:
                return "COUNT(" + aggExp + ")";
        }
    };

    /**
     * Parser a single filter configuration to sql syntax
     */
    private Function<DimensionConfig, String> filter2SqlCondtion = (config) -> {
        if (config.getValues().size() == 0) {
            return null;
        }
        if (NULL_STRING.equals(config.getValues().get(0))) {
            switch (config.getFilterType()) {
                case "=":
                case "≠":
                    return config.getColumnName() + ("=".equals(config.getFilterType()) ? " IS NULL" : " IS NOT NULL");
            }
        }

        switch (config.getFilterType()) {
            case "=":
            case "eq":
                return config.getColumnName() + " IN (" + IntStream.range(0, config.getValues().size()).boxed().map(i -> dimensionConfigHelper.getValueStr(config, i)).collect(Collectors.joining(",")) + ")";
            case "≠":
            case "ne":
                return config.getColumnName() + " NOT IN (" + IntStream.range(0, config.getValues().size()).boxed().map(i -> dimensionConfigHelper.getValueStr(config, i)).collect(Collectors.joining(",")) + ")";
            case ">":
                return config.getColumnName() + " > " + dimensionConfigHelper.getValueStr(config, 0);
            case "<":
                return config.getColumnName() + " < " + dimensionConfigHelper.getValueStr(config, 0);
            case "≥":
                return config.getColumnName() + " >= " + dimensionConfigHelper.getValueStr(config, 0);
            case "≤":
                return config.getColumnName() + " <= " + dimensionConfigHelper.getValueStr(config, 0);
            case "(a,b]":
                if (config.getValues().size() >= 2) {
                    return "(" + config.getColumnName() + " > '" + dimensionConfigHelper.getValueStr(config, 0) + "' AND " + config.getColumnName() + " <= " + dimensionConfigHelper.getValueStr(config, 1) + ")";
                } else {
                    return null;
                }
            case "[a,b)":
                if (config.getValues().size() >= 2) {
                    return "(" + config.getColumnName() + " >= " + dimensionConfigHelper.getValueStr(config, 0) + " AND " + config.getColumnName() + " < " + dimensionConfigHelper.getValueStr(config, 1) + ")";
                } else {
                    return null;
                }
            case "(a,b)":
                if (config.getValues().size() >= 2) {
                    return "(" + config.getColumnName() + " > " + dimensionConfigHelper.getValueStr(config, 0) + " AND " + config.getColumnName() + " < " + dimensionConfigHelper.getValueStr(config, 1) + ")";
                } else {
                    return null;
                }
            case "[a,b]":
                if (config.getValues().size() >= 2) {
                    return "(" + config.getColumnName() + " >= " + dimensionConfigHelper.getValueStr(config, 0) + " AND " + config.getColumnName() + " <= " + dimensionConfigHelper.getValueStr(config, 1) + ")";
                } else {
                    return null;
                }
        }
        return null;
    };

    private class DimensionConfigHelper {
        private Map<String, String> types = getColumnType();

        private DimensionConfigHelper() throws Exception {
        }

        public String getValueStr(DimensionConfig dc, int index) {
            switch (types.get(dc.getColumnName())) {
                case DbTypes.CHAR:
                case DbTypes.CLOB:
                case DbTypes.DATE:
                case DbTypes.NCLOB:
                case DbTypes.NVARCHAR2:
                case DbTypes.VARCHAR2:
                case DbTypes.TIMESTAMP:
                case DbTypes.TIMESTAMP_WITH_TIMEZONE:
                case DbTypes.NCHAR:
                case DbTypes.TEXT:
                case DbTypes.NTEXT:
                case DbTypes.XML:
                case DbTypes.DATETIME:
                case DbTypes.SMALLDATETIME:
                case DbTypes.NVARCHAR:
                case DbTypes.CHARACTER:
                case DbTypes.GRAPHIC:
                case DbTypes.LONGVARGRAPHIC:
                case DbTypes.LONGVARCHAR:
                case DbTypes.TIME:
                case DbTypes.VARGRAPHIC:
                case DbTypes.ENUM:
                case DbTypes.SET:
                case DbTypes.YEAR:
                case DbTypes.TINYTEXT:
                case DbTypes.VARCHAR:
                case DbTypes.STRING:
                    return "'" + dc.getValues().get(index) + "'";
                default:
                    return dc.getValues().get(index);
            }
        }
    }

    /**
     * 获取列名及列类型
     *
     * @return
     * @throws Exception
     */
    private Map<String, String> getColumnType() throws Exception {
        Map<String, String> result = null;
        String key = getKey();
        result = typeCahce.get(key);
        if (result != null) {
            return result;
        } else {
            Map<String, String> columnInfosMap = getColumnInfos();
            typeCahce.put(key, columnInfosMap, 12 * 60 * 60 * 1000);
            return result;
        }
    }

    private String getKey() {
        return Hashing.md5().newHasher().putString(JSONObject.toJSON(dataSource).toString() + JSONObject.toJSON(query).toString(), Charsets.UTF_8).hash().toString();
    }

    class DbTypes {

        //ORACLE
        private static final String CHAR = "CHAR";
        private static final String CLOB = "CLOB";
        private static final String DATE = "DATE";
        private static final String NCLOB = "NCLOB";
        private static final String NVARCHAR2 = "NVARCHAR2";
        private static final String VARCHAR2 = "VARCHAR2";
        private static final String TIMESTAMP = "TIMESTAMP";
        private static final String TIMESTAMP_WITH_TIMEZONE = "TIMESTAMP_WITH_TIMEZONE";

        //SQL SERVER
        private static final String NCHAR = "NCHAR";
        private static final String TEXT = "TEXT";
        private static final String NTEXT = "NTEXT";
        private static final String XML = "XML";
        private static final String DATETIME = "DATETIME";
        private static final String SMALLDATETIME = "SMALLDATETIME";
        private static final String NVARCHAR = "NVARCHAR";

        //DB2
        private static final String CHARACTER = "CHARACTER";
        private static final String GRAPHIC = "GRAPHIC";
        private static final String LONGVARGRAPHIC = "LONGVARGRAPHIC";
        private static final String LONGVARCHAR = "LONGVARCHAR";
        private static final String TIME = "TIME";
        private static final String VARGRAPHIC = "VARGRAPHIC";

        //MYSQL
        private static final String ENUM = "ENUM";
        private static final String SET = "SET";
        private static final String YEAR = "YEAR";
        private static final String TINYTEXT = "TINYTEXT";

        //IMPALA
        private static final String VARCHAR = "VARCHAR";
        private static final String STRING = "STRING";
        //HIVE
        //KYLIN
        //PGSQL
    }

}
