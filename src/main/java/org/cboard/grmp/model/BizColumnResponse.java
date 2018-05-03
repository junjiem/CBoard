package org.cboard.grmp.model;

import java.util.List;

/**
 * Created by JunjieM on 2017-12-19.
 */
public class BizColumnResponse {
    private List<BizColumn> columns;
    private boolean status = true;
    private String message = "SUCCEED";

    public BizColumnResponse() {
    }

    public BizColumnResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public BizColumnResponse(List<BizColumn> columns) {
        this.columns = columns;
    }

    public BizColumnResponse(List<BizColumn> columns, boolean status, String message) {
        this.columns = columns;
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

    public List<BizColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<BizColumn> columns) {
        this.columns = columns;
    }
}
