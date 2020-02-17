package com.booking.validator.data.source;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.mysql.MysqlDataSourceConnection;

import java.util.Map;

/**
 * Created by dbatheja on 10/02/20.
 *
 * This is defined in Validator configuration
 */
public interface DataSourceConnection {
    public Data query(DataSourceQueryOptions options);
    public void close();
}
