package com.extole.api.impl.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.extole.api.service.BigDecimalService;
import com.extole.api.service.InvalidNumberException;

public class BigDecimalServiceImpl implements BigDecimalService {

    @Override
    public BigDecimal valueOfOrDefault(String val, long defaultValue) {
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return BigDecimal.valueOf(defaultValue);
        }
    }

    @Override
    public BigDecimal valueOf(String val) throws InvalidNumberException {
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            throw new InvalidNumberException("Invalid number " + val, e);
        }
    }

    @Override
    public BigDecimal valueOf(double val) {
        return BigDecimal.valueOf(val);
    }

    @Override
    public BigDecimal valueOf(BigInteger val) {
        return new BigDecimal(val);
    }

    @Override
    public BigDecimal valueOf(int val) {
        return new BigDecimal(val);
    }

    @Override
    public BigDecimal valueOf(long val) {
        return BigDecimal.valueOf(val);
    }

    @Override
    public BigDecimal valueOf(byte value) {
        return BigDecimal.valueOf(value);
    }

    @Override
    public BigDecimal valueOf(short value) {
        return BigDecimal.valueOf(value);
    }

    @Override
    public BigDecimal valueOf(float value) {
        return BigDecimal.valueOf(value);
    }

    @Override
    public BigDecimal valueOf(char value) {
        return BigDecimal.valueOf(value);
    }

    @Override
    public BigDecimal valueOf(boolean value) {
        return BigDecimal.valueOf(value ? 1 : 0);
    }
}
