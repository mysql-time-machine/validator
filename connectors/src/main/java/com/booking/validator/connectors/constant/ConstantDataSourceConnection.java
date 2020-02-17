package com.booking.validator.connectors.constant;

import com.booking.validator.connectors.DataSourceConnection;
import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.constant.ConstantDataSourceQueryOptions;

import java.util.Map;

/**
 * Created by dbatheja on 07/02/20.
 */
public class ConstantDataSourceConnection implements DataSourceConnection {
    public ConstantDataSourceConnection(Map<String, String> configuration) {
        // Doesn't need any connection
        // Data resides in Query Options
    }

    @Override
    public Data query(DataSourceQueryOptions options) {
        return new Data(((ConstantDataSourceQueryOptions) options).getData());
    }

    @Override
    public void close() {

    }
}
