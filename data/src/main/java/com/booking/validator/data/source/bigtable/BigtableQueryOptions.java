package com.booking.validator.data.source.bigtable;

import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.hbase.HbaseQueryOptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by dbatheja on 20/02/20.
 */
public class BigtableQueryOptions extends HbaseQueryOptions {

    @JsonCreator
    public BigtableQueryOptions(@JsonProperty("table_name") final String tableName,
                                @JsonProperty("row") final String row,
                                @JsonProperty("column_family") final String columnFamily,
                                @JsonProperty("transformations") final Map<String, Object> transformations) {
        super(Types.BIGTABLE.getValue(), tableName, row, columnFamily, transformations);
    }
}
