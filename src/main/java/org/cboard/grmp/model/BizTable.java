package org.cboard.grmp.model;

/**
 * 业务表
 */
public class BizTable {
    private String id; // 表ID
    private String name; // 表名称
    private String comment; // 表注释

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
