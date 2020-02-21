package com.booking.validator.data.source;

import com.booking.validator.data.source.constant.ConstantQueryOptions;

import java.util.Map;

public class QueryOptionsFactory {
    public static DataSourceQueryOptions get(Types type, Map<String, Object> options) {
        switch (type) {
            case CONSTANT:
                return ConstantQueryOptions.build(options);
            default:
                return null;
        }
    }
}
