package com.booking.validator.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by psalimov on 9/7/16.
 *
 * This class implements concurrent execution of the sequence:
 *
 * 1) fetch a task (that is a supplier of a completable future) from the supplier
 * 2) execute the task
 * 3) feed the consumer with the task result or with the error
 *
 * such a way that no more then the specified number of tasks are being processed simultaneously.
 *
 * The supplier and consumer must be thread safe.
 */
public class ConcurrentPipeline<T> implements Service {

    private final int concurrencyLimit;
    private final Supplier<? extends Supplier<CompletableFuture<T>>> supplier;
    private final BiConsumer<T, Throwable> consumer;

    private volatile boolean run = false;

    public ConcurrentPipeline(Supplier<? extends Supplier<CompletableFuture<T>>> supplier, BiConsumer<T, Throwable> consumer, int concurrencyLimit) {
        this.supplier = supplier;
        this.consumer = consumer;
        this.concurrencyLimit = concurrencyLimit;
    }

    @Override
    public void start(){

        run = true;

        for (int i = 0; i < concurrencyLimit; i++) startTaskAsync();

    }

    @Override
    public void stop(){

        run = false;

    }

    private void startTaskAsync(){
        if (run) CompletableFuture.supplyAsync(supplier)
                .thenCompose( Supplier::get )
                .whenComplete( consumer )
                .whenComplete( (x,t)-> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startTaskAsync();
                }); // a consumer exception could be handled here
    }

}
