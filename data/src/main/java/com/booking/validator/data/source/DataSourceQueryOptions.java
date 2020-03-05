package com.booking.validator.data.source;

import com.booking.validator.data.source.bigtable.BigtableQueryOptions;
import com.booking.validator.data.source.constant.ConstantQueryOptions;
import com.booking.validator.data.source.hbase.HbaseQueryOptions;
import com.booking.validator.data.source.mysql.MysqlQueryOptions;
import com.fasterxml.jackson.annotation.*;

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
        @JsonSubTypes.Type(value = MysqlQueryOptions.class, name = Types.Constants.MYSQL_VALUE),
        @JsonSubTypes.Type(value = HbaseQueryOptions.class, name = Types.Constants.HBASE_VALUE),
        @JsonSubTypes.Type(value = BigtableQueryOptions.class, name = Types.Constants.BIGTABLE_VALUE)
})
public abstract class DataSourceQueryOptions {

    private String type;

    @JsonCreator
    public DataSourceQueryOptions(@JsonProperty("type") String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract Map<String, Object> getTransformations();
}
