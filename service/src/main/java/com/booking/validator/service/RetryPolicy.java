package com.booking.validator.service;

/**
 * Created by edmitriev on 2/1/17.
 */

public class RetryPolicy {

    private long[] delay;
    private int queueSize;
    private int retriesLimit;

    public RetryPolicy(long[] delay, int queueSize, int retriesLimit) {
        this.delay = delay;
        this.queueSize = queueSize;
        this.retriesLimit = retriesLimit;
    }

    public int getRetriesLimit() {
        return retriesLimit;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public long getDelayForRetry(int pos) { return delay[pos]; }

}
