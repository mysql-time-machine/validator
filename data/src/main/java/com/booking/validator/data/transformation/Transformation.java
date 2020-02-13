package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;

/**
 * Created by dbatheja on 10/02/20.
 */
public interface Transformation {
    public Data apply(Data data);
    public Types getType();
}
