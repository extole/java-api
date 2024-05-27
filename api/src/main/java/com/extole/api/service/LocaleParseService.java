package com.extole.api.service;

import javax.annotation.Nullable;

public interface LocaleParseService {

    @Nullable
    String[] parse(String localeRanges);
}
