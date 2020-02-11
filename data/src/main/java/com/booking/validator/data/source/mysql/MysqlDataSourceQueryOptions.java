package com.booking.validator.data.source.mysql;

import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.transformation.Transformation;
import com.booking.validator.data.transformation.TransformationFactory;
import org.apache.htrace.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 07/02/20.
 */
public class MysqlDataSourceQueryOptions implements DataSourceQueryOptions {
    private final String tableName;
    private final Map<String, Object> primaryKeys;
    private final Map<String, Object> transformations;

    @JsonCreator
    public MysqlDataSourceQueryOptions(@JsonProperty("table_name") final String tableName,
                                       @JsonProperty("primary_keys") final Map<String, Object> primaryKeys,
                                       @JsonProperty("transformations") final Map<String, Object> transformations) {
        this.tableName = requireNonNull(tableName);
        this.primaryKeys = requireNonNull(primaryKeys);
        this.transformations = transformations;
    }

    public List<Transformation> getTransformations() {
        return TransformationFactory.getTransformations(transformations);
    }
}
