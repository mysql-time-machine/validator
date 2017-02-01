package com.booking.validator.utils;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by psalimov on 11/18/16.
 */
public class NonblockingDelayingSupplier<T> implements Supplier<T>, Retrier<T> {

    private static class TimestampedHolder<T> {

        private static AtomicInteger idGenerator = new AtomicInteger(0);

        private final long timestamp;

        private final T value;

        private final int id;

        private TimestampedHolder(long timestamp, T value) {
            this.timestamp = timestamp;
            this.value = value;
            this.id = idGenerator.getAndIncrement();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public T getValue() { return value; }

        public int getId() { return id; }

    }

    private AtomicInteger size = new AtomicInteger();
    private final int highWaterMark;
    private final int lowWaterMark;
    private final CurrentTimestampProvider currentTimestampProvider;

    private final ConcurrentSkipListSet<TimestampedHolder<T>> items = new ConcurrentSkipListSet<>((o1, o2) -> {
        if (o1.equals(o2)) return 0;
        int compareTimestamp = Long.compare(o1.getTimestamp(), o2.getTimestamp());
        if (compareTimestamp != 0) return compareTimestamp;
        return Integer.compare(o1.getId(), o2.getId());
    });


    public NonblockingDelayingSupplier(int highWaterMark, int lowWaterMark) {
        this(highWaterMark, lowWaterMark, new CurrentTimestampProviderImpl());
    }

    public NonblockingDelayingSupplier(int highWaterMark, int lowWaterMark, CurrentTimestampProvider currentTimestampProvider) {
        this.highWaterMark = highWaterMark;
        this.lowWaterMark = lowWaterMark;
        this.currentTimestampProvider = currentTimestampProvider;
    }

    @Override
    public T get() {

        long time = currentTimestampProvider.getCurrentTimeMillis();

        TimestampedHolder<T> item = items.pollFirst();

        if (item != null) {

            if (item.getTimestamp() < time) {

                if (size.decrementAndGet() == lowWaterMark) onLowWaterMarkReached();

                return item.getValue();

            } else {

                items.add(item);

            }
        }

        return null;

    }

    @Override
    public void accept(T value, long delay) {

        long time = currentTimestampProvider.getCurrentTimeMillis();

        items.add(new TimestampedHolder(time + delay, value));

        if (size.incrementAndGet() == highWaterMark) onHighWaterMarkReached();

    }

    protected void onHighWaterMarkReached() {}

    protected void onLowWaterMarkReached() {}

}
