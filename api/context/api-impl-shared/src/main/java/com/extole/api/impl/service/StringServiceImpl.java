package com.extole.api.impl.service;

import java.util.Arrays;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import com.extole.api.service.StringService;

public class StringServiceImpl implements StringService {

    @Override
    public String[] split(String value) {
        String[] strings = new String[] {};
        if (value == null) {
            return strings;
        }
        return Arrays.stream(value.split(","))
            .filter(string -> !Strings.isNullOrEmpty(string))
            .toArray(String[]::new);
    }

    @Override
    public String stripAccents(String value) {
        return StringUtils.stripAccents(value);
    }

    @Override
    public String removeEnd(String str, String remove) {
        return StringUtils.removeEnd(str, remove);
    }

    @Override
    public boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }

}
