package com.booking.validator.data.source.mysql;

import com.booking.validator.data.source.DataSourceQueryOptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 07/02/20.
 */
public class MysqlQueryOptions extends DataSourceQueryOptions {
    private final String tableName;
    private final Map<String, Object> primaryKeys;
    private final Map<String, Object> transformations;

    @JsonCreator
    public MysqlQueryOptions(@JsonProperty("type") String type,
                             @JsonProperty("table_name") final String tableName,
                             @JsonProperty("primary_keys") final Map<String, Object> primaryKeys,
                             @JsonProperty("transformations") final Map<String, Object> transformations) {
        super(type);
        this.tableName = requireNonNull(tableName);
        this.primaryKeys = requireNonNull(primaryKeys);
        this.transformations = transformations;
    }

    @JsonGetter("table_name")
    public String getTableName() { return tableName; }

    @JsonGetter("primary_keys")
    public Map<String, Object> getPrimaryKeys() { return primaryKeys; }

    @JsonGetter("transformations")
    public Map<String, Object> getTransformations() {
        return transformations;
    }
}
