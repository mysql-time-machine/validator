package com.booking.validator.task;

import com.booking.validator.data.source.DataSource;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

public class TaskV1 implements Task {

    @JsonProperty("v")
    private final String v="V1";

    @JsonProperty("tag")
    private final String tag;

    @JsonProperty("source")
    private final DataSource source;

    @JsonProperty("target")
    private final DataSource target;

    public TaskV1(String tag, DataSource source, DataSource target) {
        this.tag = tag;
        this.source = source;
        this.target = target;
    }
}
