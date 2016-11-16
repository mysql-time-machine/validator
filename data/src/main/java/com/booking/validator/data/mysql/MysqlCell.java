package com.booking.validator.data.mysql;

/**
 * Created by psalimov on 11/14/16.
 */
public class MysqlCell {

    private final Object value;
    private final String column;
    private final String type;


    public MysqlCell(String column, String type, Object value) {
        this.value = value;
        this.column = column;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public String getColumn() {
        return column;
    }

    public String getType() {
        return type;
    }
}
