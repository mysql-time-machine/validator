package com.booking.validator.data.source;

import com.booking.validator.data.source.constant.ConstantQueryOptions;
import com.booking.validator.data.source.mysql.MysqQuerylOptions;
import com.booking.validator.data.transformation.Transformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


import java.util.List;
import java.util.Map;

/**
 * Created by dbatheja on 11/02/20.
 *
 * This is defined within the DataSource
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConstantQueryOptions.class, name = Types.Constants.CONSTANT_VALUE),
        @JsonSubTypes.Type(value = MysqQuerylOptions.class, name = Types.Constants.MYSQL_VALUE)
})
public abstract class DataSourceQueryOptions {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    @JsonCreator
    public DataSourceQueryOptions(@JsonProperty("type") String type) {
        this.type = type;
    }
    public abstract Map<String, Object> getTransformations();
}
