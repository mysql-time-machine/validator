package com.booking.validator.data.mysql;

import com.booking.validator.data.Data;
import com.booking.validator.utils.HexEncoder;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by psalimov on 11/3/16.
 */
public class Transformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(Transformation.class);

    private static final String NULL_MAPPING_PROPERTY_NAME = "map_null";
    private static final String IGNORE_COLUMNS_PROPERTY_NAME = "ignore_columns";
    private static final String MAP_TIMESTAMP_TO_EPOCH_PROPERTY_NAME = "convert_timestamps_to_epoch";

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

    public Data transform(MysqlCell[] cells) throws SQLException{


        Map<String,String> result = new HashMap<>();

        for (MysqlCell cell : cells){

            String column = cell.getColumn();

            if (ignoredColumns.contains(column)) continue;

            Object rawValue = cell.getValue();

            String value = null;

            String type = cell.getType();

            switch (type){

                case "DATETIME":

                case "TIMESTAMP":

                    if (rawValue != null){

                        Timestamp t = (Timestamp) rawValue;

                        if (convertTimestampsToEpoch) {

                            value = String.valueOf(t.getTime());

                        } else {

                            value = t.toString();

                        }

                    }

                    break;

                default:

                    if (rawValue instanceof byte[]){

                        value = HexEncoder.encode((byte[]) rawValue);

                    } else {

                        value = (rawValue == null ? null : rawValue.toString());

                    }

                    break;
            }

            if (value == null) value = nullValue;

            result.put(column,value);

        }

        return new Data(result);

    }

}
