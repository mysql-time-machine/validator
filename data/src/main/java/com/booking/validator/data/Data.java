package com.booking.validator.data;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 9/5/16.
 */
public class Data {

    public static class Discrepancy {

        private final Data expected;
        private final Data actual;

        private Discrepancy(Data source, Data target) {
            this.expected = source;
            this.actual = target;
        }

        @Override
        public String toString(){

            assert expected != null || actual != null;

            if ( expected == null ) return "Expected no data";
            if ( actual == null ) return "Expected data";

            Set<String> keys = new HashSet<>(expected.row.keySet());
            keys.addAll(actual.row.keySet());

            String difference = keys.stream().map( k -> columnDiff(k, expected.row, actual.row) ).filter(Objects::nonNull).collect(Collectors.joining(", "));

            return String.format("Expected values differs from actual: %s",difference);
        }

    }

    private static String columnDiff(String column, Map<String,String> expectedRow, Map<String,String> actualRow){

        String expected = null;
        String actual = null;

        boolean columnExpected = expectedRow.containsKey(column);
        boolean columnPresents = actualRow.containsKey(column);

        if (columnExpected && columnPresents){

            expected = "\"" + expectedRow.get(column) + "\"";
            actual = "\"" + actualRow.get(column) + "\"";

            if ( Objects.equals(expected, actual) ) return null;

        } else {

            if (!columnExpected) expected = "MISSING";

            if (!columnPresents) actual = "MISSING";

        }


        return String.format("%s=[%s,%s]", column, expected, actual);

    }

    public static Discrepancy discrepancy(Data expected, Data actual){

        if ( expected == null && actual == null ) return null;
        if ( expected == null || actual == null ) return new Discrepancy(expected, actual);

        return expected.row.equals(actual.row) ? null : new Discrepancy(expected, actual);

    }

    private final Map<String,String> row;

    public Data(Map<String,String> row){

        if (row == null) throw new IllegalArgumentException();

        this.row = new HashMap<>(row);
    }

}
