package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface DoubleService {
    Double valueOfOrDefault(String value, double defaultValue);

    Double valueOf(String value) throws InvalidNumberException;

    Double valueOf(double value);

    Double valueOf(byte value);

    Double valueOf(short value);

    Double valueOf(int value);

    Double valueOf(long value);

    Double valueOf(float value);

    Double valueOf(char value);

    Double valueOf(boolean value);
}
