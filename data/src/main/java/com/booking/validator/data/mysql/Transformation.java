package com.booking.validator.data.mysql;

import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by psalimov on 11/3/16.
 */
public class Transformation {

    private static String NULL_MAPPING_PROPERTY_NAME = "map_null";
    private static String IGNORE_COLUMNS_PROPERTY_NAME = "ignore_columns";
    private static String MAP_TIMESTAMP_TO_EPOCH_PROPERTY_NAME = "convert_timestamps_to_epoch";

    private final String nullValue;
    private final boolean convertTimestampsToEpoch;
    private final Set<String> ignoredColumns;

    public Transformation (Map<String, Object> definition){

        if (definition == null) definition = Collections.emptyMap();

        Object nullValueObject = definition.get(NULL_MAPPING_PROPERTY_NAME);

        nullValue = nullValueObject == null ? null : (String)nullValueObject;

        Object convertTimestampsToEpochObject = definition.get(MAP_TIMESTAMP_TO_EPOCH_PROPERTY_NAME);

        convertTimestampsToEpoch = convertTimestampsToEpochObject == null ? false : ((Boolean)convertTimestampsToEpochObject);

        Object ignoredColumnsObject = definition.get(IGNORE_COLUMNS_PROPERTY_NAME);

        ignoredColumns = ignoredColumnsObject == null ? Collections.EMPTY_SET : new HashSet<>((List<String>)ignoredColumnsObject);

    }

    public Map<String,String> transform(Map<String, MysqlDataPointer.Cell> cells){

        Map<String,String> result = new HashMap<>();

        for (Map.Entry<String,MysqlDataPointer.Cell> entry : cells.entrySet()){

            String column = entry.getKey();

            if (ignoredColumns.contains(column)) continue;

            Object valueRaw = entry.getValue().getValue();

            String value = nullValue;

            if (valueRaw != null){

                String type = entry.getValue().getType();

                switch (type){

                    case "DATETIME":

                    case "TIMESTAMP":

                        if (convertTimestampsToEpoch) {

                            Timestamp t = (Timestamp) valueRaw;

                            value = String.valueOf(t.getTime());

                            break;

                        }

                    default:

                        value = valueRaw.toString();

                        break;
                }

            }

            result.put(column,value);

        }

        return result;

    }

}
