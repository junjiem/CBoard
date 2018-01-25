package org.cboard.grmp.model;

/**
 * 业务表字段
 */
public class BizColumn {
    private String name; // 字段名称
    private String comment; // 字段注释
    private String type; // 字段类型
    private String length; // 字段长度

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
