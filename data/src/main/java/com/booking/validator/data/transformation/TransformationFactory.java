package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by dbatheja on 10/02/20.
 */
public class TransformationFactory {
    static final Logger LOGGER = LoggerFactory.getLogger(TransformationFactory.class);

    private static LinkedHashMap<TransformationTypes, Transformation> prioritizedTransformationMap =
            new LinkedHashMap<TransformationTypes, Transformation>(){{
        put(TransformationTypes.IGNORE_COLUMNS, IgnoreColumnsTransformation.getInstance());
        put(TransformationTypes.KEEP_COLUMNS, KeepColumnsTransformation.getInstance());
        put(TransformationTypes.TIMESTAMPS_TO_EPOCHS, TimestampsToEpochsTransformation.getInstance());
        put(TransformationTypes.MAP_NULL_COLUMNS, MapNullColumnsTransformation.getInstance());
        put(TransformationTypes.ALIAS_COLUMNS, AliasColumnsTransformation.getInstance());
    }};

    public static Data applyTransformations(Data data, Map<String, Object> transformations) {
        Data ret = data;
        if(transformations == null) return ret;
        for(TransformationTypes key : prioritizedTransformationMap.keySet()) {
            for(Map.Entry<String,Object> entry : transformations.entrySet()) {
                if (entry.getKey().equals(key.getValue())) {
                    Transformation transformation = prioritizedTransformationMap.getOrDefault(key, null);
                    if (transformation != null) {
                        ret = transformation.apply(ret, entry.getValue());
                    } else {
                        LOGGER.warn("Transformation not found of type: " + entry.getKey());
                    }
                }
            }
        }
        return ret;
    }
}
