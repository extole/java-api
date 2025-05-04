package com.extole.api.impl.service;

import com.extole.api.service.DoubleService;
import com.extole.api.service.InvalidNumberException;

public class DoubleServiceImpl implements DoubleService {
    @Override
    public Double valueOfOrDefault(String value, double defaultValue) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return Double.valueOf(defaultValue);
        }
    }

    @Override
    public Double valueOf(String value) throws InvalidNumberException {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException("Invalid number " + value, e);
        }
    }

    @Override
    public Double valueOf(double value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(byte value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(short value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(int value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(long value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(float value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(char value) {
        return Double.valueOf(value);
    }

    @Override
    public Double valueOf(boolean value) {
        return Double.valueOf(value ? 1 : 0);
    }

}
