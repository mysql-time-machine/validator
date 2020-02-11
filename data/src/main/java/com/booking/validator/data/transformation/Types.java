package com.booking.validator.data.transformation;

/**
 * Created by dbatheja on 07/02/20.
 */
public enum Types {
    IGNORE_COLUMNS("ignore_columns"),
    ALIAS_COLUMNS("alias_columns");


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