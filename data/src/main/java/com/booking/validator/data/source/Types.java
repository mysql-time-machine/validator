package com.booking.validator.data.source;

/**
 * Created by dbatheja on 07/02/20.
 */
public enum Types {
    CONSTANT("constant"),
    MYSQL("mysql"),
    HBASE("hbase"),
    BIGTABLE("bigtable");

    private String value;

    Types(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Types fromString(String text) {
        for (Types b : Types.values()) {
            if (b.getValue().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
