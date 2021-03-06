package com.booking.validator.data.source.constant;

import com.booking.validator.data.source.DataSourceQueryOptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 07/02/20.
 */
public class ConstantQueryOptions extends DataSourceQueryOptions {
    public static interface Constants {
        public String DATA = "data";
        public String TRANSFORMATIONS = "transformations";
    }

    private Map<String, Object> data;
    private Map<String, Object> transformations;

    @JsonCreator
    public ConstantQueryOptions(@JsonProperty("type") String type, @JsonProperty("data") Map<String, Object> data, @JsonProperty("transformations") Map<String, Object> transformations) {
        super(type);
        this.data = requireNonNull(data);
        this.transformations = transformations;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, Object> getTransformations() {
        return transformations;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setTransformations(Map<String, Object> transformations) {
        this.transformations = transformations;
    }
}
