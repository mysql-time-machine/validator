package com.booking.validator.data.hbase;

import com.booking.validator.data.Data;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 11/3/16.
 */
public class Transformation {

    private static final String ROW_STATUS_COLUMN_PROPERTY_NAME = "row_status_column";
    private static final String IGNORE_COLUMNS_PROPERTY_NAME = "ignore_columns";

    private final String rowStatusColumn;
    private final Set<String> ignoredColumns;

    public Transformation(Map<String,Object> definition){

        if (definition == null) definition = Collections.emptyMap();

        Object rowStatusColumnObject = definition.get(ROW_STATUS_COLUMN_PROPERTY_NAME);

        rowStatusColumn = rowStatusColumnObject == null ? null : (String)rowStatusColumnObject;

        Object ignoredColumnsObject = definition.get(IGNORE_COLUMNS_PROPERTY_NAME);

        ignoredColumns = ignoredColumnsObject == null ? Collections.EMPTY_SET : new HashSet<>((List<String>)ignoredColumnsObject);

    }

    public Data transform(NavigableMap<byte[],byte[]> familyMap){

        Map<String,Object> row = new HashMap<>();

        // the row status "D" means the row should be treated as deleted
        // Should be handled in client/TaskProvider
        if ( Arrays.equals(Bytes.toBytes("D"),familyMap.remove(Bytes.toBytes(rowStatusColumn)))) return null;

        for (Map.Entry<byte[],byte[]> entry : familyMap.entrySet()){

            String column = Bytes.toString( entry.getKey() );

            if (!ignoredColumns.contains(column)){

                row.put(column, Bytes.toString( entry.getValue() ));

            }

        }

        return new Data( row );

    }


}
