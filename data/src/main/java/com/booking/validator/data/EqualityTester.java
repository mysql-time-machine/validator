package com.booking.validator.data;

import org.apache.directory.api.util.Strings;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Created by edmitriev on 1/12/17.
 */
public class EqualityTester {

    // there is a bug in jdbc so it loses some precision when reading floating point types through
    // ResultSet methods. Shortly, under the hood, it retrieves a decimal string representation of a
    // value that is not long enough. Then it converts this string back to IEEE float/double losing
    // some bits. As a workaround, we have to compare this values separately

    private Map<Class, Map<Class, BiFunction<Object, Object, Boolean>>> variants = new HashMap<>();

    public EqualityTester() {
        Map<Class, BiFunction<Object, Object, Boolean>> lowerVariantsDouble = new HashMap<>();
        lowerVariantsDouble.put(Double.class, this::isDoubleEqualToDouble);
        lowerVariantsDouble.put(String.class, this::isDoubleEqualToDouble);
        variants.put(Double.class, lowerVariantsDouble);

        Map<Class, BiFunction<Object, Object, Boolean>> lowerVariantsFloat = new HashMap<>();
        lowerVariantsFloat.put(Float.class, this::isFloatEqualToFloat);
        lowerVariantsFloat.put(String.class, this::isFloatEqualToFloat);
        variants.put(Float.class, lowerVariantsFloat);

        Map<Class, BiFunction<Object, Object, Boolean>> lowerVariantsString = new HashMap<>();
        lowerVariantsString.put(String.class, this::isStringEqualToString);
        variants.put(String.class, lowerVariantsString);
    }

    private boolean isDoubleEqualToDouble(Object value1, Object value2) {
        return isDoubleEqualToDouble(
                (value1 instanceof String) ? Double.parseDouble(value1.toString()) : (double) value1,
                (value2 instanceof String) ? Double.parseDouble(value2.toString()) : (double) value2
        );

    }

    private boolean isDoubleEqualToDouble(Double value1, Double value2) {
        return value1.equals(value2) ||  value2.equals(Math.nextAfter(value1,value2)) || value1.equals(Math.nextAfter(value2,value1));
    }

    private boolean isFloatEqualToFloat(Object value1, Object value2) {
        return isFloatEqualToFloat(
                (value1 instanceof String) ? Float.parseFloat(value1.toString()) : (float) value1,
                (value2 instanceof String) ? Float.parseFloat(value2.toString()) : (float) value2
        );
    }

    private boolean isFloatEqualToFloat(Float value1, Float value2) {
        return value1.equals(value2) ||  value2.equals(Math.nextAfter(value1,value2)) || value1.equals(Math.nextAfter(value2,value1));
    }

    private boolean isStringEqualToString(Object value1, Object value2) {
        return Strings.equals((String) value1, (String) value2);
    }

    public boolean testEquality(Object value1, Object value2) {
        if (Objects.equals(value1, value2)) return true;
        if (value1 == null || value2 == null) return false;

        BiFunction<Object, Object, Boolean> biFunction = variants.getOrDefault(value1.getClass(), Collections.emptyMap()).get(value2.getClass());
        if (biFunction == null)
            biFunction = variants.getOrDefault(value2.getClass(), Collections.emptyMap()).get(value1.getClass());

        if (biFunction == null) return false;
        return biFunction.apply(value1, value2);
    }
}
