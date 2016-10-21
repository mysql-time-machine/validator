package com.booking.validator.data;

import java.net.URL;

/**
 * Created by psalimov on 9/15/16.
 */
public interface DataPointerFactory  {

    class InvalidDataPointerDescription extends RuntimeException {

        public InvalidDataPointerDescription(String message) { super(message);}

    }

    DataPointer produce(String uri) throws InvalidDataPointerDescription;

}
