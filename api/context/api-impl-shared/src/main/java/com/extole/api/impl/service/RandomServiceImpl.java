package com.extole.api.impl.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.extole.api.service.RandomService;

public class RandomServiceImpl implements RandomService {

    @Override
    public String randomString(int count, String allowedCharacters) {
        if (count < 1 || StringUtils.isEmpty(allowedCharacters)) {
            throw new IllegalArgumentException();
        }

        StringBuilder resultBuilder = new StringBuilder(count);
        char[] characters = allowedCharacters.toCharArray();

        for (int i = 0; i < count; i++) {
            int characterIndex = RandomUtils.nextInt(0, characters.length);
            resultBuilder.append(characters[characterIndex]);
        }
        return resultBuilder.toString();
    }

    @Override
    public String randomStringAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    @Override
    public Integer randomInt(int startInclusive, int endExclusive) {
        return Integer.valueOf(RandomUtils.nextInt(startInclusive, endExclusive));
    }
}
