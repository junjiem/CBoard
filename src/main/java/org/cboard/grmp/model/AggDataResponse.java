package org.cboard.grmp.model;

import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2017-12-19.
 */
public class AggDataResponse {
    private List<List<String>> datas;
    private boolean status = true;
    private String message = "SUCCEED";

    public AggDataResponse() {
    }

    public AggDataResponse(List<List<String>> datas) {
        this.datas = datas;
    }

    public AggDataResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public AggDataResponse(List<List<String>> datas, boolean status, String message) {
        this.datas = datas;
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<List<String>> getDatas() {
        return datas;
    }

    public void setDatas(List<List<String>> datas) {
        this.datas = datas;
    }
}
