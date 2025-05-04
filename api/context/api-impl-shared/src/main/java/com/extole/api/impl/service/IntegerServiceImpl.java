package com.extole.api.impl.service;

import com.extole.api.service.IntegerService;
import com.extole.api.service.InvalidNumberException;
import com.extole.api.service.NumberOverflowException;

public class IntegerServiceImpl implements IntegerService {
    @Override
    public Integer valueOfOrDefault(String value, int defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return Integer.valueOf(defaultValue);
        }
    }

    @Override
    public Integer valueOf(String value) throws InvalidNumberException {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException("Invalid number " + value, e);
        }
    }

    @Override
    public Integer valueOf(String value, int radix) throws InvalidNumberException {
        try {
            return Integer.valueOf(value, radix);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException("Invalid number " + value, e);
        }
    }

    @Override
    public Integer valueOf(byte value) {
        return Integer.valueOf(value);
    }

    @Override
    public Integer valueOf(short value) {
        return Integer.valueOf(value);
    }

    @Override
    public Integer valueOf(int value) {
        return Integer.valueOf(value);
    }

    @Override
    public Integer valueOf(long value) throws NumberOverflowException {
        validateOverflow(value);
        return Integer.valueOf((int) value);
    }

    @Override
    public Integer valueOf(float value) throws NumberOverflowException {
        validateOverflow(Float.valueOf(value).longValue());
        return Integer.valueOf((int) value);
    }

    @Override
    public Integer valueOf(double value) throws NumberOverflowException {
        validateOverflow(Double.valueOf(value).longValue());
        return Integer.valueOf((int) value);
    }

    @Override
    public Integer valueOf(char value) {
        return Integer.valueOf(value);
    }

    @Override
    public Integer valueOf(boolean value) {
        return Integer.valueOf(value ? 1 : 0);
    }

    private void validateOverflow(long value) throws NumberOverflowException {
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            throw new NumberOverflowException("Number overflow " + value
                + " allowed range " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE);
        }
    }
}
