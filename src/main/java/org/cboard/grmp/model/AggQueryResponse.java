package org.cboard.grmp.model;

/**
 * Created by JunjieM on 2017-12-19.
 */
public class AggQueryResponse {
    private String query;
    private boolean status = true;
    private String message = "SUCCEED";

    public AggQueryResponse() {
    }

    public AggQueryResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public AggQueryResponse(String query) {
        this.query = query;
    }

    public AggQueryResponse(String query, boolean status, String message) {
        this.query = query;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
