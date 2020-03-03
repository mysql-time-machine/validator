package com.booking.validator.data;

import com.booking.validator.task.extra.AcceptableRanges;
import com.booking.validator.task.extra.Extra;
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
        private AcceptableRanges ranges;
        private static String columnDiff(String column, Object[] discrepancy){

            return String.format("%s=[%s,%s]", column,
                    discrepancy[0] == null ? "MISSING" : discrepancy[0].toString(),
                    discrepancy[1] == null ? "MISSING" : discrepancy[1].toString()
            );

        }

        private final Map<String, Object[]> discrepancies = new HashMap<>();
        private final boolean isActualExists;
        private final boolean isExpectedExists;

        private void initRanges(List<Extra> extra) {
            ranges = null;
            if (extra == null) return;
            extra.stream().filter(a-> a instanceof AcceptableRanges).findFirst().ifPresent(x-> ranges = (AcceptableRanges) x);
        }

        public boolean isInRange(Object sourceValue, Object targetValue, List<Double> range) {
            if (targetValue instanceof String || sourceValue instanceof String) {
                LOGGER.warn("Ranged comparison can't be applied to string columns");
                return false;
            } else if (targetValue instanceof Number && sourceValue instanceof Number) {
                double doubleSourceValue = ((Number) sourceValue).doubleValue();
                double doubleTargetValue = ((Number) targetValue).doubleValue();
                if (doubleTargetValue >= range.get(0)+doubleSourceValue && doubleTargetValue <= range.get(1)+doubleSourceValue) {
                    return true;
                }
            } else {
                LOGGER.warn("Ranged comparison can't compare values of non Number type." + targetValue.toString());
            }
            return false;
        }

        private Discrepancy(Data expected, Data actual, List<Extra> extra) {
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
                        } else if (ranges.getRanges().containsKey(k)) {
                            return !isInRange(expected.row.get(k), actual.row.get(k), ranges.getRanges().get(k));
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

    public static Discrepancy discrepancy(Data expected, Data actual, List<Extra> extra) {
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
