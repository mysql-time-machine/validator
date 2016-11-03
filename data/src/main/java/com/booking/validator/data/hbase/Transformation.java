package com.booking.validator.data.hbase;

import java.util.Map;

/**
 * Created by psalimov on 11/3/16.
 */
public class Transformation {

    private static final String ROW_STATUS_COLUMN_PROPERTY_NAME = "row_status_column";

    private final String rowStatusColumn;

    public Transformation(Map<String,Object> description){

        Object rowStatusColumnObject = description.get(ROW_STATUS_COLUMN_PROPERTY_NAME);

        rowStatusColumn = rowStatusColumnObject == null ? null : (String)rowStatusColumnObject;

    }


}
