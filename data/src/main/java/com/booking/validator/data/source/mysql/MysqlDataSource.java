package com.booking.validator.data.source.mysql;

import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.Types;
import org.apache.htrace.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 07/02/20.
 */
public class MysqlDataSource implements DataSource {

    private final String name;
    private final Types type;
    private final DataSourceQueryOptions options;

    @JsonCreator
    public MysqlDataSource(@JsonProperty("name") final String name,
                           @JsonProperty("type") final String type,
                           @JsonProperty("options") final MysqlDataSourceQueryOptions options) {
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
}
