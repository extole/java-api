package com.extole.common.variant;

import java.util.List;

public interface LocaleParseService {

    List<String> parse(String localeRanges) throws InvalidLocaleException;
}
