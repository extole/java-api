package com.extole.block;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserializes invalid json, e.g. ["abc", "cde"
 */
final class JsonArrayDeserializer {
    private static final Pattern PATTERN = Pattern.compile("\"([^\"]*)\"");

    List<String> deserialize(String jsonArray) {
        List<String> result = new LinkedList<>();
        Matcher matcher = PATTERN.matcher(jsonArray);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return Collections.unmodifiableList(result);
    }
}
