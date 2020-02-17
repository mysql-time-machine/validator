package com.booking.validator.data.source.constant;

import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.mysql.MysqlDataSourceQueryOptions;
import org.apache.htrace.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 07/02/20.
 */
public class ConstantDataSource implements DataSource {

    private final String name;
    private final Types type;
    private final DataSourceQueryOptions options;

    @JsonCreator
    public ConstantDataSource(@JsonProperty("name") final String name,
                           @JsonProperty("type") final String type,
                           @JsonProperty("options") final ConstantDataSourceQueryOptions options) {
        this.name = requireNonNull(name);
        this.type = requireNonNull(Types.fromString(type));
        this.options = (DataSourceQueryOptions) options;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Types getType() {
        return type;
    }

    @Override
    public DataSourceQueryOptions getOptions() {
        return options;
    }

    public String toString() {
        return String.format("[name=%s type=%s]", getName(), getType());
    }
}
