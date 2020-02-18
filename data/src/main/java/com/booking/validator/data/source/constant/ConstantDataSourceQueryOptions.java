package com.booking.validator.data.source.constant;

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
public class ConstantDataSourceQueryOptions implements DataSourceQueryOptions {

    private final Map<String, Object> data;
    private final Map<String, Object> transformations;

    @JsonCreator
    public ConstantDataSourceQueryOptions(@JsonProperty("data") final Map<String, Object> data,
                                          @JsonProperty("transformations") final Map<String, Object> transformations) {
        this.data = requireNonNull(data);
        this.transformations = transformations;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, Object> getTransformations() {
        return transformations;
    }
}
