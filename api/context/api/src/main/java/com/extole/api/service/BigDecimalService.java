package com.extole.api.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface BigDecimalService {

    BigDecimal valueOfOrDefault(String val, long defaultValue);

    BigDecimal valueOf(String val) throws InvalidNumberException;

    BigDecimal valueOf(double val);

    BigDecimal valueOf(BigInteger val);

    BigDecimal valueOf(int val);

    BigDecimal valueOf(long val);

    BigDecimal valueOf(byte value);

    BigDecimal valueOf(short value);

    BigDecimal valueOf(float value);

    BigDecimal valueOf(char value);

    BigDecimal valueOf(boolean value);

}
