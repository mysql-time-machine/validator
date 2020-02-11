package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;

public class IgnoreColumnsTransformation implements Transformation {

    public IgnoreColumnsTransformation(Object options) {

    }

    @Override
    public Data apply(Data data) {
        return null;
    }
}
