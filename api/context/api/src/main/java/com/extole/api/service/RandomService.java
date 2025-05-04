package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface RandomService {

    String randomString(int count, String allowedCharacters);

    String randomStringAlphabetic(int count);

    Integer randomInt(int startInclusive, int endExclusive);

}
