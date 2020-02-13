package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;

/**
 * Created by dbatheja on 10/02/20.
 */
public class IgnoreColumnsTransformation implements Transformation {

    public IgnoreColumnsTransformation(Object options) {

    }

    @Override
    public Data apply(Data data) {
        return null;
    }

    @Override
    public Types getType() {
        return Types.IGNORE_COLUMNS;
    }
}
