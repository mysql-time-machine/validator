package com.booking.validator.data.source;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

/**
 * Created by dbatheja on 07/02/20.
 *
 * This is defined in the Task
 */
public class DataSource {

    private String name;
    private Types type;
    private DataSourceQueryOptions options;

    @JsonCreator
    public DataSource(@JsonProperty("name") final String name,
                      @JsonProperty("type") final String type,
                      @JsonProperty("options") final DataSourceQueryOptions options) {
        this.name = requireNonNull(name);
        this.type = requireNonNull(Types.fromString(type));
        this.options = requireNonNull(options);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.getValue();
    }

    public DataSourceQueryOptions getOptions() {
        return options;
    }

    public String toString() {
        return String.format("[name=%s type=%s]", getName(), getType());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = Types.fromString(type);
    }

    public void setOptions(DataSourceQueryOptions options) {
        this.options = options;
    }
}
