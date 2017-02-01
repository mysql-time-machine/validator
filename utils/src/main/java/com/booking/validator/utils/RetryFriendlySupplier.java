package com.booking.validator.utils;

import java.util.function.Supplier;

/**
 * Created by psalimov on 11/18/16.
 */
public class RetryFriendlySupplier<T> extends NonblockingDelayingSupplier<T> implements Service {

    private final Supplier<T> supplier;

    public RetryFriendlySupplier(Supplier<T> supplier, int retryQueueSoftSizeLimit) {
        super(retryQueueSoftSizeLimit, retryQueueSoftSizeLimit/2);
        this.supplier = supplier;
    }

    public RetryFriendlySupplier(Supplier<T> supplier, int retryQueueSoftSizeLimit, CurrentTimestampProvider currentTimestampProvider) {
        super(retryQueueSoftSizeLimit, retryQueueSoftSizeLimit/2, currentTimestampProvider);
        this.supplier = supplier;
    }

    @Override
    public T get(){

        T result = super.get();
        if (result == null) result = supplier.get();

        return result;

    }

    @Override
    public void start() {

        if (supplier instanceof Service) ((Service) supplier).start();

    }

    @Override
    public void stop() {

        if (supplier instanceof Service) ((Service) supplier).stop();

    }


    @Override
    protected void onHighWaterMarkReached(){

        if (supplier instanceof Service) ((Service) supplier).pause();

    }

    @Override
    protected void onLowWaterMarkReached(){

        if (supplier instanceof Service) ((Service) supplier).resume();

    }

}
