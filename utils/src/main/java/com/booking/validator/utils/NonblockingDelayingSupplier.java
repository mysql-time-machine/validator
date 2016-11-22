package com.booking.validator.utils;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by psalimov on 11/18/16.
 */
public abstract class NonblockingDelayingSupplier<T> implements Supplier<T>, Consumer<T> {

    private static class TimestampedHolder<T> {

        private final long timestamp;

        private final T value;

        private TimestampedHolder(long timestamp, T value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public T getValue() {
            return value;
        }
    }

    private AtomicInteger size = new AtomicInteger();
    private final int highWaterMark;
    private final int lowWaterMark;

    private final Deque<TimestampedHolder<T>> items = new ConcurrentLinkedDeque<>();

    private final long delay;

    public NonblockingDelayingSupplier(long delay, int highWaterMark, int lowWaterMark) {
        this.highWaterMark = highWaterMark;
        this.lowWaterMark = lowWaterMark;
        this.delay = delay;
    }

    @Override
    public T get() {

        long time = System.currentTimeMillis();

        TimestampedHolder<T> item = items.pollFirst();

        if (item == null){

            return null;

        } else {

            if (item.getTimestamp()<time){

                if (size.decrementAndGet() == lowWaterMark ) onLowWaterMarkReached();

                return item.getValue();

            } else {

                // this messes up order of the front of the queue, so that in theory all the tasks submitted between submission
                // of the given one and the time it becomes ready could went in front of it delaying its consumption up to
                // about 2x delay instead of 1x delay. Still as not precision is needed here, we use this naive implementation
                items.addFirst(item);

                return null;
            }

        }

    }

    @Override
    public void accept(T value){

        items.addLast(new TimestampedHolder(System.currentTimeMillis() + delay, value));

        if (size.incrementAndGet() == highWaterMark) onHighWaterMarkReached();

    }

    protected void onHighWaterMarkReached() {}

    protected void onLowWaterMarkReached() {}

}
