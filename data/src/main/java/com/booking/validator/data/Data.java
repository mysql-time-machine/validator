package com.booking.validator.data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 9/5/16.
 */
public class Data {

    public static class Discrepancy {

        private static String columnDiff(String column, Object[] discrepancy){

            return String.format("%s=[%s,%s]", column,
                    discrepancy[0] == null ? "MISSING" : discrepancy[0].toString(),
                    discrepancy[1] == null ? "MISSING" : discrepancy[1].toString()
            );

        }

        private final Map<String, Object[]> discrepancies = new HashMap<>();
        private final boolean isActualExists;
        private final boolean isExpectedExists;

        private Discrepancy(Data expected, Data actual) {
            isExpectedExists = expected != null;
            isActualExists = actual != null;
            if (expected == null || actual == null) return;

            EqualityTester equalityTester = new EqualityTester();
            Set<String> columnNames = new HashSet<>();
            columnNames.addAll(expected.row.keySet());
            columnNames.addAll(actual.row.keySet());

            columnNames.stream()
                    .filter( k -> ! equalityTester.testEquality(expected.row.get(k), actual.row.get(k)) )
                    .forEach( k -> discrepancies.put(k, new Object[] {expected.row.get(k), actual.row.get(k)})
                    );
        }

        public boolean hasDiscrepancy() {
            if (!isExpectedExists && !isActualExists) return false;
            if (isExpectedExists != isActualExists) return true;

            return discrepancies.size() > 0;
        }

        @Override
        public String toString(){
            if (!hasDiscrepancy()) return "Data are equal";
            if ( !isExpectedExists ) return "Expected no data, but have got some";
            if ( !isActualExists ) return "Expected data, but have got nothing";

            String difference = discrepancies.keySet().stream()
                    .map( k -> columnDiff(k, discrepancies.get(k)) )
                    .collect(Collectors.joining(", "));

            return String.format("Expected values differs from actual: %s", difference);
        }

    }

    public static Discrepancy discrepancy(Data expected, Data actual) {
        return new Discrepancy(expected, actual);
    }

    private final Map<String, Object> row;

    public Data(Map < String, Object > row) {

        if (row == null) throw new IllegalArgumentException();

        this.row = new HashMap<>(row);
    }
}
