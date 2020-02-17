package com.booking.validator.task;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.ActiveDataSourceConnections;
import com.booking.validator.data.source.DataSource;
import org.apache.htrace.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class TaskV1 implements Task {

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


    public TaskV1(String tag, DataSource source, DataSource target, Map<String, Object> extra) {
        this.source = requireNonNull(source);
        this.target = requireNonNull(target);
        this.tag = tag;
        this.extra = extra != null ? extra : new HashMap<String, Object>();
        this.createTime = System.currentTimeMillis();
        this.triesCount = 0;
    }

    private Supplier<Data> getSupplier(DataSource dataSource) {
        return new Supplier<Data>() {
            @Override
            public Data get() {
                return ActiveDataSourceConnections.getInstance().query(dataSource);
            }
        };
    }

    public CompletableFuture<TaskComparisonResult> get(){
        return CompletableFuture.supplyAsync(getSupplier(source))
                .thenCombineAsync(
                        CompletableFuture.supplyAsync(getSupplier(target)), this::validate)
                .exceptionally( t -> new TaskComparisonResultV1(this, null, t));
    }

    private TaskComparisonResult validate(Data sourceData, Data targetData){
        triesCount ++;
        return (TaskComparisonResult) new TaskComparisonResultV1(this, Data.discrepancy(sourceData, targetData), null);
    }
}
