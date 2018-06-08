package org.cboard.grmp.model;

import java.util.List;

/**
 * Created by JunjieM on 2017-12-19.
 */
public class BizTableResponse {
    private List<BizTable> tables;
    private boolean status = true;
    private String message = "SUCCEED";

    public BizTableResponse() {
    }

    public BizTableResponse(List<BizTable> tables) {
        this.tables = tables;
    }

    public BizTableResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public BizTableResponse(List<BizTable> tables, boolean status, String message) {
        this.tables = tables;
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

    public List<BizTable> getTables() {
        return tables;
    }

    public void setTables(List<BizTable> tables) {
        this.tables = tables;
    }
}
