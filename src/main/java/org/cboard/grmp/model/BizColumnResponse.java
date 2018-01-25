package org.cboard.grmp.model;

import java.util.List;

/**
 * Created by JunjieM on 2017-12-15.
 */
public class BizColumnResponse {
    List<BizColumn> columns;

    public BizColumnResponse() {
    }

    public BizColumnResponse(List<BizColumn> columns) {
        this.columns = columns;
    }

    public List<BizColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<BizColumn> columns) {
        this.columns = columns;
    }
}
