package com.booking.validator.data.transformation;

/**
 * Created by dbatheja on 07/02/20.
 */
public enum TransformationTypes {
    IGNORE_COLUMNS("ignore_columns"),
    KEEP_COLUMNS("keep_columns"),
    ALIAS_COLUMNS("alias_columns"),
    MAP_NULL_COLUMNS("map_null_columns"),
    TIMESTAMPS_TO_EPOCHS("timestamps_to_epochs");


    private String value;

    TransformationTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransformationTypes fromString(String text) {
        for (TransformationTypes b : TransformationTypes.values()) {
            if (b.getValue().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}