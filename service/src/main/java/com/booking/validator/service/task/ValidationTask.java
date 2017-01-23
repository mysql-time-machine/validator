package com.booking.validator.service.task;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by psalimov on 9/5/16.
 */
public class ValidationTask implements Supplier<CompletableFuture<ValidationTaskResult>> {

    private final DataPointer source;
    private final DataPointer target;
    private final String tag;
    private final String id;

    private int triesCount;

    public ValidationTask(String id, String tag, DataPointer source, DataPointer target) {
        this.tag = tag;
        this.source = source;
        this.target = target;
        this.id = id;
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

}
