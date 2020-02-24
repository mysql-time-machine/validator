package com.booking.validator.data.source.hbase;

import com.booking.validator.data.source.DataSourceQueryOptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 20/02/20.
 */
public class HbaseQueryOptions extends DataSourceQueryOptions {
    private final String tableName;
    private final String row;
    private final String columnFamily;
    private final Map<String, Object> transformations;

    @JsonCreator
    public HbaseQueryOptions(@JsonProperty("type") String type,
                             @JsonProperty("table_name") final String tableName,
                             @JsonProperty("row") final String row,
                             @JsonProperty("column_family") final String columnFamily,
                             @JsonProperty("transformations") final Map<String, Object> transformations) {
        super(type);
        this.tableName = requireNonNull(tableName);
        this.row = requireNonNull(row);
        this.columnFamily = requireNonNull(columnFamily);
        this.transformations = transformations;
    }

    public String getTableName() {
        return tableName;
    }

    public String getRow() {
        return row;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public Map<String, Object> getTransformations() {
        return transformations;
    }
}
