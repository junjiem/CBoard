package org.cboard.grmp.model;

import java.util.List;

/**
 * Created by JunjieM on 2017-12-15.
 */
public class DimValResponse {
    private List<String> values;

    public DimValResponse() {
    }

    public DimValResponse(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
