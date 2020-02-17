package com.booking.validator.data.source.mysql;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSourceConnection;
import com.booking.validator.data.source.DataSourceQueryOptions;

import java.util.Map;

/**
 * Created by dbatheja on 10/02/20.
 */
public class MysqlDataSourceConnection implements DataSourceConnection {

    public MysqlDataSourceConnection(Map<String, String> configuration) {
    }

    @Override
    public Data query(DataSourceQueryOptions options) {
        return null;
    }

    @Override
    public void close() {

    }
}
