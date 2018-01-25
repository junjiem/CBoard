package org.cboard.grmp.model;

import org.cboard.dataprovider.config.AggConfig;

/**
 * Created by PC on 2017/12/13.
 */
public class AggDataRequest {
    private String bizTableName;
    private AggConfig aggConfig;

    public AggDataRequest() {
    }

    public AggDataRequest(String bizTableName, AggConfig aggConfig) {
        this.bizTableName = bizTableName;
        this.aggConfig = aggConfig;
    }

    public String getBizTableName() {
        return bizTableName;
    }

    public void setBizTableName(String bizTableName) {
        this.bizTableName = bizTableName;
    }

    public AggConfig getAggConfig() {
        return aggConfig;
    }

    public void setAggConfig(AggConfig aggConfig) {
        this.aggConfig = aggConfig;
    }
}
