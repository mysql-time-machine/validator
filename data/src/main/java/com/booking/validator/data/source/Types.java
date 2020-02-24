package com.booking.validator.data.source;

/**
 * Created by dbatheja on 07/02/20.
 */
public enum Types {
    CONSTANT(Constants.CONSTANT_VALUE),
    MYSQL(Constants.MYSQL_VALUE),
    HBASE(Constants.HBASE_VALUE),
    BIGTABLE(Constants.BIGTABLE_VALUE);

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

    // While adding a new DataSource, make sure to add it to the DataSource interface as well for Jackson Deserialization
    public static class Constants {
        public static final String CONSTANT_VALUE = "constant";
        public static final String MYSQL_VALUE = "mysql";
        public static final String HBASE_VALUE = "hbase";
        public static final String BIGTABLE_VALUE = "bigtable";
    }
}
