package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbatheja on 17/02/20.
 */
public class MapNullColumnsTransformation implements Transformation {
    static final Logger LOGGER = LoggerFactory.getLogger(IgnoreColumnsTransformation.class);
    private MapNullColumnsTransformation() {}
    private static MapNullColumnsTransformation instance = new MapNullColumnsTransformation();
    public static MapNullColumnsTransformation getInstance() {
        return instance;
    }


    @Override
    public Data apply(Data data, Object options) {
        Map<String, Object> mapNullColumns = new HashMap<String, Object>();
        boolean applyToAll;
        Object applyToAllValue;
        if(options instanceof HashMap) {
            mapNullColumns = (HashMap<String, Object>) options;
            applyToAll = false;
            applyToAllValue = null;
        } else if (options != null) {
            applyToAll = true;
            applyToAllValue = options;
        } else {
            applyToAll = true;
            applyToAllValue = null;
            LOGGER.warn("MapNullColumns Transformation expects a HashMap of null columns with new values or a single value for all null columns");
        }

        Map<String, Object> row = data.getRow();
        if (applyToAll == true) {
            row.replaceAll((k, v) -> v == null ? applyToAllValue : v);
        } else {
            Map<String, Object> finalMapNullColumns = mapNullColumns;
            row.replaceAll((k, v) -> v == null ? finalMapNullColumns.getOrDefault(k, null) : v);
        }
        return data;
    }

    @Override
    public TransformationTypes getType() {
        return TransformationTypes.MAP_NULL_COLUMNS;
    }

}
