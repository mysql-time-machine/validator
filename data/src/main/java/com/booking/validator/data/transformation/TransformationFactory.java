package com.booking.validator.data.transformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformationFactory {
    static final Logger LOGGER = LoggerFactory.getLogger(TransformationFactory.class);

    public static Transformation getTransformation(String key, Object value) {
        switch(Types.fromString(key)){
            case IGNORE_COLUMNS:
                return new IgnoreColumnsTransformation(value);
            default:
                LOGGER.warn("Transformation not found for:", key);
                return null;
        }
    }

    public static List<Transformation> getTransformations(Map<String, Object> transformations) {
        ArrayList<Transformation> ret= new ArrayList<Transformation>();
        for(Map.Entry<String,Object> entry : transformations.entrySet()) {
            Transformation transformation = getTransformation(entry.getKey(), entry.getValue());
            if (transformation != null) {
                ret.add(transformation);
            }
        }
        return ret;
    }

}
