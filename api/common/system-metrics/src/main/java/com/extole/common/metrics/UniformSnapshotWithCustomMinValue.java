package com.extole.common.metrics;

import java.util.function.Predicate;

import com.codahale.metrics.UniformSnapshot;

public class UniformSnapshotWithCustomMinValue extends UniformSnapshot {

    public UniformSnapshotWithCustomMinValue(long[] histogramValues, long customMinValue,
        Predicate<Long> customMinApplicablePredicate) {
        super(transform(histogramValues, customMinValue, customMinApplicablePredicate));
    }

    private static long[] transform(long[] histogramValues, long customMinValue,
        Predicate<Long> customMinApplicablePredicate) {
        long[] values = histogramValues;
        if (values.length == 0) {
            values = new long[] {customMinValue};
        }
        if (customMinApplicablePredicate.test(Long.valueOf(values[0]))) {
            values[0] = customMinValue;
        }
        return values;
    }
}
