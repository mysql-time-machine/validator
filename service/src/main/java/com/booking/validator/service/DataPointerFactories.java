package com.booking.validator.service;


import com.booking.validator.data.DataPointer;
import com.booking.validator.data.DataPointerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;


/**
 * Created by psalimov on 9/5/16.
 */
public class DataPointerFactories {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPointerFactories.class);

    private final Map<String, DataPointerFactory> factories;

    public DataPointerFactories(Map<String, DataPointerFactory> factories) {
        this.factories = factories;
    }

    public DataPointer produce(String pointer){

        URI uri = URI.create(pointer);

        String type = uri.getScheme();

        DataPointerFactory factory = factories.get(type);

        if (factory == null) throw new RuntimeException("No factory given for a data pointer of the type " + type);

        return factory.produce(pointer);

    }


}
