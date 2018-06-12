package org.cboard.grmp.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by JunjieM on 2017-12-11.
 */
public class GrmpHttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(GrmpHttpUtil.class);

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private static final String CONTEXT_TYPE_TEXT_JSON = "text/json";

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final int REQUEST_TIME_OUT = 5000;


    /**
     * 请求GRMP
     *
     * @param reqJsonStr
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T requestGrmp(String reqJsonStr, String url, Class<T> clazz) {
        return requestGrmp(reqJsonStr, url, clazz, Charset.forName("UTF-8"));
    }

    /**
     * 请求GRMP
     *
     * @param reqJsonStr
     * @param url
     * @param clazz
     * @param charset
     * @param <T>
     * @return
     */
    public static <T> T requestGrmp(String reqJsonStr, String url, Class<T> clazz, Charset charset) {
        LOG.debug("Request JSON:\n" + reqJsonStr);
        T responseObject = null;
        CloseableHttpClient httpClient = HttpUtils.getConnection();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
        try {
            StringEntity stringEntity = new StringEntity(reqJsonStr, charset);
            stringEntity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            String returnString = analysisResponse(response);
            responseObject = JSON.parseObject(returnString, clazz);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseObject;
    }

    /**
     * 分解Response
     *
     * @param httpResponse
     * @return
     */
    public static String analysisResponse(HttpResponse httpResponse) {
        if (httpResponse == null) {
            return null;
        }
        String returnString = "";
        int statuscode = httpResponse.getStatusLine().getStatusCode();
        if (statuscode == HttpStatus.SC_NOT_FOUND) {
            throw new RuntimeException("[404]请检查URL!");
        } else if (statuscode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            throw new RuntimeException("[500]GRMP内部错误!");
        } else if (statuscode == HttpStatus.SC_BAD_REQUEST) {
            throw new RuntimeException("[400]错误的请求!");
        } else {
            InputStream resultStream = null;
            BufferedReader br = null;
            StringBuffer buffer = new StringBuffer();
            HttpEntity responseEntity = httpResponse.getEntity();
            try {
                resultStream = responseEntity.getContent();
                br = new BufferedReader(new InputStreamReader(resultStream, Charset.forName("UTF-8")));
                String tempStr;
                while ((tempStr = br.readLine()) != null) {
                    buffer.append(tempStr);
                }
                returnString = buffer.toString();
                returnString = StringEscapeUtils.unescapeJava(returnString);
            } catch (Exception e) {
                throw new RuntimeException("解析返回结果失败!");
            }
            return returnString;
        }
    }

}
