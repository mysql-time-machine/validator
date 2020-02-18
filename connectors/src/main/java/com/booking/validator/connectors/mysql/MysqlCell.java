package com.booking.validator.connectors.mysql;

/**
 * Created by dbatheja on 18/02/20.
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
