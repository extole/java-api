package com.extole.common.variant.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import com.extole.common.variant.InvalidLocaleException;
import com.extole.common.variant.LocaleParseService;

@Component
class LocaleParseServiceImpl implements LocaleParseService {

    @Override
    public List<String> parse(String localeRanges) throws InvalidLocaleException {
        try {
            List<Locale.LanguageRange> parsedLanguageRanges = Collections.emptyList();
            if (!Strings.isNullOrEmpty(localeRanges)) {
                parsedLanguageRanges = Locale.LanguageRange.parse(localeRanges.replace('_', '-'));
            }

            return parsedLanguageRanges.stream().map(range -> Locale.forLanguageTag(range.getRange()).toString())
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new InvalidLocaleException("Failed to parse locale ranges: " + localeRanges + " to language tags.",
                e);
        }
    }
}
