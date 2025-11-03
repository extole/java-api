package com.extole.common.jwt.decode;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

public final class PayloadClaimsParser {

    private PayloadClaimsParser() {
    }

    public static Instant parseNumericDate(Object numericDate) throws PayloadClaimParseException {
        long epochSeconds;

        if (numericDate instanceof Number) {
            return Instant.ofEpochSecond(((Number) numericDate).longValue());
        } else if (numericDate instanceof String) {
            try {
                epochSeconds = NumberUtils.createNumber((String) numericDate).longValue();
            } catch (NumberFormatException e) {
                throw new PayloadClaimParseException("Could not parse: " + numericDate + " to long", e);
            }
        } else {
            throw new PayloadClaimParseException("Could not parse: " + numericDate + " to long");
        }
        try {
            return Instant.ofEpochSecond(epochSeconds);
        } catch (DateTimeException e) {
            throw new PayloadClaimParseException("Could not parse: " + numericDate + " to " + Instant.class.getName());
        }
    }

    public static List<String> parseList(Object collectionClaim) {
        if (collectionClaim instanceof Collection) {
            return ((Collection<?>) collectionClaim).stream()
                .map(value -> value.toString())
                .collect(Collectors.toList());
        } else {
            return List.of(collectionClaim.toString());
        }
    }
}
