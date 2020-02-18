package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by dbatheja on 10/02/20.
 */
public interface Transformation {
    public Data apply(Data data, Object options);
    public Types getType();
}

