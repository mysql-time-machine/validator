package com.booking.validator.data.source.bigtable;

import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.hbase.HbaseQueryOptions;

import java.util.Map;

/**
 * Created by dbatheja on 20/02/20.
 */
public class BigtableQueryOptions extends HbaseQueryOptions {
    public BigtableQueryOptions(String tableName, String row, String columnFamily, Map<String, Object> transformations) {
        super(Types.BIGTABLE.getValue(), tableName, row, columnFamily, transformations);
    }
}
