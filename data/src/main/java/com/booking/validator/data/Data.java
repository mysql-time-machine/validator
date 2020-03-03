package com.booking.validator.data;

import com.booking.validator.data.transformation.AliasColumnsTransformation;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 9/5/16.
 */
public class Data {

    public static class Discrepancy {
        static final Logger LOGGER = LoggerFactory.getLogger(Discrepancy.class);
        private final static String ACCEPTABLE_DIFFERENCE_RANGES = "ranges"; // absolute difference between source and destination column to be in a certain range
        private Map<String, Object> ranges;
        private static String columnDiff(String column, Object[] discrepancy){

            return String.format("%s=[%s,%s]", column,
                    discrepancy[0] == null ? "MISSING" : discrepancy[0].toString(),
                    discrepancy[1] == null ? "MISSING" : discrepancy[1].toString()
            );

        }

        private final Map<String, Object[]> discrepancies = new HashMap<>();
        private final boolean isActualExists;
        private final boolean isExpectedExists;

        private void initRanges(Map<String, Object> extra) {
            ranges = extra != null ? (Map<String, Object>) extra.getOrDefault(ACCEPTABLE_DIFFERENCE_RANGES, null) : null;
            ranges.entrySet().forEach(e->{
                if (!(e.getValue() instanceof List || e.getValue() instanceof Number)) {
                    LOGGER.warn("AcceptableDifference: each range should be a number or a list of 2 numbers");
                    ranges.remove(e.getKey());
                } else if (e.getValue() instanceof Number) {
                    ArrayList<Double> l = new ArrayList<>();
                    l.add(-Math.abs(((Number) e.getValue()).doubleValue()));
                    l.add(Math.abs(((Number) e.getValue()).doubleValue()));
                    ranges.put(e.getKey(), l);
                } else if (e.getValue() instanceof List) {
                    if (((List) e.getValue()).size() != 2) {
                        LOGGER.warn("AcceptableDifference: each range should be a number or a list of 2 numbers");
                        ranges.remove(e.getKey());
                    }
                }
            });
        }

        public boolean isInRange(Object sourceValue, Object targetValue, List<Double> range) {
            if (targetValue instanceof String || sourceValue instanceof String) {
                LOGGER.warn("Ranged comparison can't be applied to string columns");
                return false;
            } else if (targetValue instanceof Number && sourceValue instanceof Number) {
                Double doubleSourceValue = ((Number) sourceValue).doubleValue();
                Double doubleTargetValue = ((Number) targetValue).doubleValue();
                if (doubleTargetValue >= range.get(0)+doubleSourceValue && doubleTargetValue <= range.get(1)+doubleSourceValue) {
                    return true;
                }
            } else {
                LOGGER.warn("Ranged comparison can't compare values of non Number type.", targetValue);
            }
            return false;
        }

        private Discrepancy(Data expected, Data actual, Map<String, Object> extra) {
            initRanges(extra);
            isExpectedExists = expected != null;
            isActualExists = actual != null;
            if (expected == null || actual == null) return;

            EqualityTester equalityTester = new EqualityTester();
            Set<String> columnNames = new HashSet<>();
            columnNames.addAll(expected.row.keySet());
            columnNames.addAll(actual.row.keySet());

            columnNames.stream()
                    .filter( k -> ! equalityTester.testEquality(expected.row.get(k), actual.row.get(k)) )
                    .filter( k-> {
                        if (ranges == null) {
                            return true;
                        } else if (ranges.containsKey(k)) {
                            return !isInRange(expected.row.get(k), actual.row.get(k), (List<Double>) ranges.get(k));
                        }
                        return true;
                    })
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

    public static Discrepancy discrepancy(Data expected, Data actual, Map<String, Object> extra) {
        return new Discrepancy(expected, actual, extra);
    }

    public static Discrepancy discrepancy(Data expected, Data actual) {
        return new Discrepancy(expected, actual, null);
    }

    private final Map<String, Object> row;

    public Map<String, Object> getRow() {
        return row;
    }

    public Data(Map < String, Object > row) {

        if (row == null) throw new IllegalArgumentException();

        this.row = new HashMap<>(row);
    }

}
