package com.booking.validator.service.task;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by psalimov on 9/5/16.
 */
public class ValidationTask implements Supplier<CompletableFuture<ValidationTaskResult>> {

    @JsonProperty("source")
    private final String sourceUrl;

    private final DataPointer source;

    @JsonProperty("target")
    private final String targetUrl;

    private final DataPointer target;

    @JsonProperty("tag")
    private final String tag;

    @JsonProperty("id")
    private final String id;

    @JsonProperty("create_time")
    private final long createTime;

    @JsonProperty("tries_count")
    private int triesCount;

    public ValidationTask(String id, String tag, DataPointer source, DataPointer target) {
        this(id, tag, source, target, System.currentTimeMillis());
    }

    public ValidationTask(String id, String tag, DataPointer source, DataPointer target, long createTime) {
        this.tag = tag;
        this.source = source;
        this.target = target;
        this.id = id;
        this.createTime = createTime;
        this.sourceUrl = source.toString();
        this.targetUrl = target.toString();
    }

    public CompletableFuture<ValidationTaskResult> get(){

        return CompletableFuture.supplyAsync( source::resolve )
                .thenCombineAsync(
                        CompletableFuture.supplyAsync( target::resolve ), this::validate)
                .exceptionally( t -> new ValidationTaskResult(this, null, t) );

    }

    public String getTag() { return tag; }

    public String getId() { return  id; }

    protected ValidationTaskResult validate(Data sourceData, Data targetData ){

        triesCount++;

        return new ValidationTaskResult(this, Data.discrepancy(sourceData, targetData), null);

    }

    public String toString(){

        return String.format("[Task id=%s tag=%s source=%s target=%s]", id, tag, source, target);

    }

    public int getTriesCount() {

        return triesCount;

    }

    public int getRetriesCount() {

        return triesCount - 1;

    }

    public long getCreateTime() {

        return createTime;

    }

}
