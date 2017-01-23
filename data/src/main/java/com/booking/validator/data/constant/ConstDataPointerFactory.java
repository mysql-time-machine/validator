package com.booking.validator.data.constant;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import com.booking.validator.data.DataPointerFactory;

import java.net.URI;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 9/21/16.
 */
public class ConstDataPointerFactory implements DataPointerFactory {

    public static final String VALUE_PROPERTY_NAME = "value";

    private static class ConstDataPointer implements DataPointer {

        private final Data data;

        private ConstDataPointer(Data data) {
            this.data = data;
        }

        @Override
        public Data resolve() {
            return data;
        }
    }

    @Override
    public DataPointer produce(String uriString, Map<String, Object> transformations) throws InvalidDataPointerDescription {

        URI uri = URI.create(uriString);

        Map<String,Object> rows = Arrays.stream(uri.getQuery().split("&")).map(s->s.split("=")).collect(Collectors.toMap(s->s[0], s->s[1]));

        return new ConstDataPointer(new Data(rows));

    }

}
