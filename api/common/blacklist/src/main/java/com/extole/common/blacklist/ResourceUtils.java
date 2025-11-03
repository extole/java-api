package com.extole.common.blacklist;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

final class ResourceUtils {
    private ResourceUtils() {
        throw new AssertionError();
    }

    static List<String> readLinesOfResourceFile(String resourcePath) {
        try {
            byte[] resourceContent = readResource(resourcePath);
            return Arrays.stream(new String(resourceContent).split("\n"))
                .filter(value -> !StringUtils.isWhitespace(value))
                .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] readResource(String path) throws IOException {
        try (InputStream inputStream = Objects.requireNonNull(ResourceUtils.class.getResourceAsStream(path))) {
            return IOUtils.toByteArray(inputStream);
        }
    }
}
