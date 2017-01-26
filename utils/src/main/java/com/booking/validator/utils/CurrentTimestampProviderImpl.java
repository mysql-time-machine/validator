package com.booking.validator.utils;

/**
 * Created by edmitriev on 1/27/17.
 */
public class CurrentTimestampProviderImpl implements CurrentTimestampProvider {
    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
