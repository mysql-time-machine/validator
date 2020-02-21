package com.booking.validator.connectors.bigtable;

import com.booking.validator.connectors.hbase.HbaseDataSourceConnection;

import java.util.Map;

public class BigtableDataSourceConnection extends HbaseDataSourceConnection {
    protected String name = "bigtable";
    public BigtableDataSourceConnection(Map<String, String> config) {
        super(config);
    }
}
