package org.cboard.grmp.model;

import java.util.List;

/**
 * Created by JunjieM on 2017-12-19.
 */
public class DimValResponse {
    private List<String> values;
    private boolean status = true;
    private String message = "SUCCEED";

    public DimValResponse() {
    }

    public DimValResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public DimValResponse(List<String> values) {
        this.values = values;
    }

    public DimValResponse(List<String> values, boolean status, String message) {
        this.values = values;
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

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
