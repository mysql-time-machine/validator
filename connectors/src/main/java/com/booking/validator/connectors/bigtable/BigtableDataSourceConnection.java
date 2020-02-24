package com.booking.validator.connectors.bigtable;

import com.booking.validator.connectors.hbase.HbaseDataSourceConnection;
import com.booking.validator.data.source.Types;

import java.util.Map;

public class BigtableDataSourceConnection extends HbaseDataSourceConnection {
    public BigtableDataSourceConnection(Map<String, String> config) {
        super(config, Types.Constants.BIGTABLE_VALUE);
    }
}
