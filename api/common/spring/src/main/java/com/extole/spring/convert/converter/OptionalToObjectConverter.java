package com.extole.spring.convert.converter;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

public class OptionalToObjectConverter implements Converter<Optional, Object> {

    @Override
    public Object convert(Optional source) {
        if (source == null || !source.isPresent()) {
            return null;
        } else {
            return source.get();
        }
    }

}
