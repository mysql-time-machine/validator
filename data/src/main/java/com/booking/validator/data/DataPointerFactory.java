package com.booking.validator.data;

import java.util.Map;

/**
 * Created by psalimov on 9/15/16.
 */
public interface DataPointerFactory  {

    class MissingDataSourceException extends RuntimeException {

        // Hide stacktrace for this exception
        public MissingDataSourceException(String message) { super(message, null, true, false); }

    }

    DataPointer produce(String uri, Map<String, Object> transformations) throws MissingDataSourceException;

}
