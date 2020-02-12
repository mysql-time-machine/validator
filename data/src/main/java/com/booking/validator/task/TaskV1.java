package com.booking.validator.task;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.ActiveDataSourceConnections;
import com.booking.validator.data.source.DataSource;
import org.apache.htrace.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class TaskV1 implements Task {

    @JsonProperty("v")
    private final String v="V1";

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
    @JsonProperty("id")
    private String id;

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

    public long getCreateTime() {
        return createTime;
    }

    public int getTriesCount() {
        return triesCount;
    }

    public int getRetriesCount() {
        return triesCount - 1;
    }

    public void setTriesCount(int triesCount) {
        this.triesCount = triesCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskV1(){}

    public TaskV1(String tag, DataSource source, DataSource target, Map<String, Object> extra) {
        this.tag = tag;
        this.source = requireNonNull(source);
        this.target = requireNonNull(target);
        this.extra = extra;
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
                .exceptionally( t -> (TaskComparisonResult) new TaskComparisonResultV1(this, null, t));
    }

    private TaskComparisonResult validate(Data sourceData, Data targetData){
        triesCount ++;
        return (TaskComparisonResult) new TaskComparisonResultV1(this, Data.discrepancy(sourceData, targetData), null);
    }
}
