package com.booking.validator.service.utils;

import java.util.Map;

/**
 * Created by psalimov on 10/13/16.
 */
public interface Property {

    String getName();

    default String value(Map<String, String> values){

        String value = values.get(getName());

        if (value == null) throw new IllegalArgumentException("Property " + getName() + " is not defined");

        return value;
    }

    default int asInt(Map<String, String> values){ return Integer.valueOf( value(values) ); }

    default long asLong(Map<String, String> values){ return Long.valueOf( value(values) ); }

}
