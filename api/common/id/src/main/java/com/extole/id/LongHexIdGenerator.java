package com.extole.id;

import java.util.Random;

import com.google.common.base.Strings;

/**
 * Produces a random 24-character hex string, in the style of a Mongo Object ID
 */
public class LongHexIdGenerator implements DeprecatedIdGenerator<String> {

    private final int intHexStringLength = 8;
    private final int longHexStringLength = 16;
    private final char hexPadding = '0';
    private final Random randomGenerator = new Random();

    @Override
    public String generateId() {
        return Strings.padStart(Integer.toHexString(randomGenerator.nextInt()), intHexStringLength, hexPadding) +
            Strings.padStart(Long.toHexString(randomGenerator.nextLong()), longHexStringLength, hexPadding);
    }

}
