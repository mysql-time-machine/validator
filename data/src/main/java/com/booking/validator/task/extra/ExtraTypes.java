package com.booking.validator.task.extra;

/**
 * Created by dbatheja on 07/02/20.
 */
public enum ExtraTypes {

    ACCEPTABLE_RANGE_DIFFERENCES(Constants.ACCEPTABLE_RANGE_DIFFERENCES),
    CHECK_NESTED_FIELDS(Constants.CHECK_NESTED_FIELDS);

    public interface Constants {
        String ACCEPTABLE_RANGE_DIFFERENCES = "ranges";
        String CHECK_NESTED_FIELDS = "nested_checks";
    }

    private String value;

    ExtraTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExtraTypes fromString(String text) {
        for (ExtraTypes b : ExtraTypes.values()) {
            if (b.getValue().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}

