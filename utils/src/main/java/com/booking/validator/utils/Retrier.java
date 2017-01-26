package com.booking.validator.utils;

/**
 * Created by edmitriev on 1/24/17.
 */
public interface Retrier<T> {
    void accept(T value, long delay);
}
