package com.booking.validator.data;

import java.util.HashMap;
import java.util.Map;

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

            return String.format("Expected %s, actual %s", expected.row.toString(), actual.row.toString());
        }

    }

    public static Discrepancy discrepancy(Data expected, Data actual){

        if ( expected == null && actual == null ) return null;
        if ( expected == null || actual == null ) return new Discrepancy(expected, actual);

        return expected.row.equals(actual.row) ? null : new Discrepancy(expected, actual);

    }

    private final Map<String,String> row;

    public Data(Map<String,String> row){
        this.row = new HashMap<>(row);
    }

}
