package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface IntegerService {
    Integer valueOfOrDefault(String value, int defaultValue);

    Integer valueOf(String value) throws InvalidNumberException;

    Integer valueOf(String value, int radix) throws InvalidNumberException;

    Integer valueOf(byte value);

    Integer valueOf(short value);

    Integer valueOf(int value);

    Integer valueOf(long value) throws NumberOverflowException;

    Integer valueOf(float value) throws NumberOverflowException;

    Integer valueOf(double value) throws NumberOverflowException;

    Integer valueOf(char value);

    Integer valueOf(boolean value);
}
