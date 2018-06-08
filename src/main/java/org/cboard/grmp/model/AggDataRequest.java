package org.cboard.grmp.model;

import org.cboard.dataprovider.config.AggConfig;

/**
 * Created by JunjieM on 2017-12-11.
 */
public class AggDataRequest {
    private String bizTableName;
    private AggConfig aggConfig;
    private boolean showDictValue = false;

    public AggDataRequest() {
    }

    public AggDataRequest(String bizTableName, AggConfig aggConfig) {
        this.bizTableName = bizTableName;
        this.aggConfig = aggConfig;
    }

    public AggDataRequest(String bizTableName, AggConfig aggConfig, boolean showDictValue) {
        this.bizTableName = bizTableName;
        this.aggConfig = aggConfig;
        this.showDictValue = showDictValue;
    }

    public boolean isShowDictValue() {
        return showDictValue;
    }

    public void setShowDictValue(boolean showDictValue) {
        this.showDictValue = showDictValue;
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
