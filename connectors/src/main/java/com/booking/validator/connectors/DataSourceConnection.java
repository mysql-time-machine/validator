package com.booking.validator.connectors;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSourceQueryOptions;

/**
 * Created by dbatheja on 10/02/20.
 *
 * This is defined in Validator configuration
 */
public interface DataSourceConnection {
    public Data query(DataSourceQueryOptions options);
    public void close();
}
