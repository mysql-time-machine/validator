package com.booking.validator.data.source.hbase;

import com.booking.validator.data.source.DataSourceQueryOptions;
import org.apache.htrace.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 20/02/20.
 */
public class HbaseDataSourceQueryOptions implements DataSourceQueryOptions {
    private final String tableName;
    private final String row;
    private final String columnFamily;
    private final Map<String, Object> transformations;

    @JsonCreator
    public HbaseDataSourceQueryOptions(@JsonProperty("table_name") final String tableName,
                                       @JsonProperty("row") final String row,
                                       @JsonProperty("column_family") final String columnFamily,
                                       @JsonProperty("transformations") final Map<String, Object> transformations) {
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
