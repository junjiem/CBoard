package org.cboard.grmp.model;

import org.cboard.dataprovider.config.AggConfig;

/**
 * Created by JunjieM on 2017-12-11.
 */
public class DimValRequest {
    private String bizTableName;
    private String bizColumnName;
    private AggConfig aggConfig;

    public DimValRequest() {
    }

    public DimValRequest(String bizTableName, String bizColumnName, AggConfig aggConfig) {
        this.bizTableName = bizTableName;
        this.bizColumnName = bizColumnName;
        this.aggConfig = aggConfig;
    }

    public String getBizTableName() {
        return bizTableName;
    }

    public void setBizTableName(String bizTableName) {
        this.bizTableName = bizTableName;
    }

    public String getBizColumnName() {
        return bizColumnName;
    }

    public void setBizColumnName(String bizColumnName) {
        this.bizColumnName = bizColumnName;
    }

    public AggConfig getAggConfig() {
        return aggConfig;
    }

    public void setAggConfig(AggConfig aggConfig) {
        this.aggConfig = aggConfig;
    }
}
