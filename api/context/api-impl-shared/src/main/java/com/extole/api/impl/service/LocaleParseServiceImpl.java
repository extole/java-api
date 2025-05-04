package com.extole.api.impl.service;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.extole.api.service.LocaleParseService;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.variant.InvalidLocaleException;

public class LocaleParseServiceImpl implements LocaleParseService {
    private final com.extole.common.variant.LocaleParseService localeParseService;
    private final ExecutionLogger executionLogger;

    public LocaleParseServiceImpl(com.extole.common.variant.LocaleParseService localeParseService,
        ExecutionLogger executionLogger) {
        this.localeParseService = localeParseService;
        this.executionLogger = executionLogger;
    }

    @Nullable
    @Override
    public String[] parse(String localeRanges) {
        if (localeRanges == null) {
            executionLogger.trace("No locale ranges provided for parsing.");
            return null;
        }
        try {
            String[] result = localeParseService.parse(localeRanges).toArray(new String[0]);
            executionLogger.trace("Parsed locale ranges: " + localeRanges + " to language tags: " +
                Arrays.toString(result));
            return result;
        } catch (InvalidLocaleException e) {
            executionLogger.trace("Failed to parse locale ranges: " + localeRanges + " to language tags.");
            return null;
        }
    }
}
