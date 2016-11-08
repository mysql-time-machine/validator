package com.booking.validator.data.mysql;

import com.booking.validator.data.Data;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by psalimov on 11/3/16.
 */
public class Transformation {

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

    public Data transform(ResultSet row) throws SQLException{

        ResultSetMetaData meta = row.getMetaData();

        int columnCount = meta.getColumnCount();

        Map<String,String> result = new HashMap<>();

        for (int columnIndex=1; columnIndex <= columnCount; columnIndex++){

            String column = meta.getColumnName(columnIndex);

            if (ignoredColumns.contains(column)) continue;

            String value;

            String type = meta.getColumnTypeName(columnIndex);

            switch (type){


                case "TIME":

                case "DATE":

                    // we rely on jdbc zeroDateTimeBehavior=convertToNull to filter out timestamps that are composed entirely of zeros

                    value = (row.getObject(columnIndex) == null ? null : row.getString(columnIndex));

                    break;

                case "DATETIME":

                case "TIMESTAMP":

                    // we rely on jdbc zeroDateTimeBehavior=convertToNull to filter out timestamps that are composed entirely of zeros

                    Timestamp t = row.getTimestamp(columnIndex);

                    if (t == null){

                        value = null;

                    } else if (convertTimestampsToEpoch) {

                        value = String.valueOf(t.getTime());

                    } else {

                        value = row.getString(columnIndex);

                    }

                    break;

                case "YEAR":

                    // TODO: in view of jdbc default yearIsDateType=true consider handling year as a date

                    value = row.getString(columnIndex);

                    if ("0000".equals(value)) value = null;

                    break;

                default:

                    value = row.getString(columnIndex);

                    break;
            }

            if (value == null) value = nullValue;

            result.put(column,value);

        }

        return new Data(result);

    }

}
