package com.booking.validator.data.source;

import com.booking.validator.data.transformation.Transformation;

import java.util.List;
import java.util.Map;

/**
 * Created by dbatheja on 11/02/20.
 *
 * This is defined within the DataSource
 */
public interface DataSourceQueryOptions {
    public Map<String, Object> getTransformations();
}
