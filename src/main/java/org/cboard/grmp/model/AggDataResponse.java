package org.cboard.grmp.model;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2017/12/13.
 */
public class AggDataResponse {
    Map<String, String> columns;
    private List<List<String>> datas;

    public AggDataResponse() {
    }

    public AggDataResponse(Map<String, String> columns, List<List<String>> datas) {
        this.columns = columns;
        this.datas = datas;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getDatas() {
        return datas;
    }

    public void setDatas(List<List<String>> datas) {
        this.datas = datas;
    }
}
