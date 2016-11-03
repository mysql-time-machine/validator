package com.booking.validator.data;

import java.util.Map;

/**
 * Created by psalimov on 9/15/16.
 */
public interface DataPointerFactory  {

    class InvalidDataPointerDescription extends RuntimeException {

        public InvalidDataPointerDescription(String message) { super(message);}

    }

    DataPointer produce(String uri, Map<String, Object> transformations) throws InvalidDataPointerDescription;

}
