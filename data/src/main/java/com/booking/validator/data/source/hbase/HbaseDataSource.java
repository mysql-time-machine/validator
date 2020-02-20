package com.booking.validator.data.source.hbase;

import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.mysql.MysqlDataSourceQueryOptions;
import org.apache.htrace.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 20/02/20.
 */
public class HbaseDataSource implements DataSource {
    private final String name;
    private final Types type;
    private final DataSourceQueryOptions options;

    @JsonCreator
    public HbaseDataSource(@JsonProperty("name") final String name,
                           @JsonProperty("type") final String type,
                           @JsonProperty("options") final HbaseDataSourceQueryOptions options) {
        this.name = requireNonNull(name);
        this.type = requireNonNull(Types.fromString(type));
        this.options = requireNonNull((DataSourceQueryOptions) options);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Types getType() {
        return null;
    }

    @Override
    public DataSourceQueryOptions getOptions() {
        return null;
    }

    public String toString() {
        return String.format("[name=%s type=%s]", getName(), getType());
    }
}
