package com.booking.validator.task;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSource;
import org.apache.htrace.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class TaskV1 implements Task {

    @JsonIgnore
    @JsonProperty("v")
    private final String v="v1";

    @JsonProperty("extra")
    private Map<String, Object> extra;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("source")
    private DataSource source;

    @JsonProperty("target")
    private DataSource target;

    @JsonIgnore
    @JsonProperty("create_time")
    private long createTime;

    @JsonIgnore
    @JsonProperty("tries_count")
    private int triesCount;

    public Map<String, Object> getExtra() {
        return extra;
    }

    public String getTag() {
        return tag;
    }

    public DataSource getSource() {
        return source;
    }

    public DataSource getTarget() {
        return target;
    }

    @JsonIgnore
    public long getCreateTime() {
        return createTime;
    }

    @JsonIgnore
    public int getTriesCount() {
        return triesCount;
    }

    @JsonIgnore
    public int getRetriesCount() {
        return triesCount - 1;
    }

    final private ObjectMapper mapper = new ObjectMapper();

    public TaskV1(String tag, DataSource source, DataSource target, Map<String, Object> extra) {
        this.source = requireNonNull(source);
        this.target = requireNonNull(target);
        this.tag = tag;
        this.extra = extra;
        this.createTime = System.currentTimeMillis();
        this.triesCount = 0;

    }

    // FOR INTERNAL JSON DESERIALIZATION ONLY
    // DON'T USE IT FOR TASK CREATION
    public TaskV1() {}

    @Override
    public TaskComparisonResult validate(Data sourceData, Data targetData) {
        triesCount ++;
        return (TaskComparisonResult) new TaskComparisonResultV1(this, Data.discrepancy(sourceData, targetData), null);
    }

    @Override
    public String toJson() throws RuntimeException {
        try {
            String task = mapper.writeValueAsString(this);
            return task;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize task object to JSON string", e);
        }
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public void setTarget(DataSource target) {
        this.target = target;
    }
}
