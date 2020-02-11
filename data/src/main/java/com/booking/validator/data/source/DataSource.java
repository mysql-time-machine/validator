package com.booking.validator.data.source;

/**
 * Created by dbatheja on 07/02/20.
 *
 * This is defined in the Task
 */
public interface DataSource {
    String getName();
    Types getType();
    DataSourceQueryOptions getOptions();
}
