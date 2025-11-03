package com.extole.common.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.extole.common.security.HashAlgorithm;

public final class SensitiveDataObfuscator {

    private static final Pattern ACCESS_TOKEN_PATTERN =
        Pattern.compile("access_token=(?<accessToken>[a-zA-Z0-9]{15,26})");
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXTOLE_ACCESS_TOKEN = "extole_access_token";
    private static final String AUTHORIZATION = "authorization";
    private static final String AUTHORIZATION_TOKEN = "authorization-token";
    private static final String HEADER_COOKIE = "cookie";
    private static final String HEADER_X_INCOMING_URL = "x-incoming-url";
    private static final String HEADER_X_EXTOLE_INCOMING_URL = "x-extole-incoming-url";

    private final HashAlgorithm hashAlgorithm;
    private final Pattern headerAuthorizationPattern;
    private final Pattern bodyAuthorizationPattern;

    private SensitiveDataObfuscator(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        this.headerAuthorizationPattern =
            Pattern.compile("^(Bearer |Basic )?(?!" + hashAlgorithm.getHashMarker()
                + "|Basic|Bearer)(?<accessToken>[a-zA-Z0-9=.-]+)", Pattern.CASE_INSENSITIVE);
        this.bodyAuthorizationPattern =
            Pattern.compile("(Bearer([ +])|Basic([ +]))(?!" + hashAlgorithm.getHashMarker()
                + "|Basic|Bearer)(?<accessToken>[a-zA-Z0-9=.-]+)", Pattern.CASE_INSENSITIVE);
    }

    public static SensitiveDataObfuscator forAlgorithm(String hashAlgorithm) {
        HashAlgorithm algorithm = HashAlgorithm.NONE;
        if (Stream.of(HashAlgorithm.values()).anyMatch(item -> item.name().equals(hashAlgorithm))) {
            algorithm = HashAlgorithm.valueOf(hashAlgorithm);
        }
        return new SensitiveDataObfuscator(algorithm);
    }

    public String hashRequestBody(String value) {
        return hashAccessToken(value, bodyAuthorizationPattern);
    }

    public String hashRequestUrl(String url) {
        try {
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(url)
                .build()
                .getQueryParams();

            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);

            for (Entry<String, List<String>> entry : queryParams.entrySet()) {
                if (entry.getKey().equals("access_token") ||
                    entry.getKey().equals("extole_access_token")) {
                    List<String> hashedValues = entry.getValue().stream()
                        .map(value -> {
                            if (value.startsWith(hashAlgorithm.getHashMarker())) {
                                return value;
                            } else {
                                return hashAlgorithm.hashString(value);
                            }
                        })
                        .collect(Collectors.toUnmodifiableList());

                    urlBuilder.replaceQueryParam(entry.getKey(), hashedValues);
                }
            }

            return urlBuilder.build()
                .toUriString();
        } catch (RuntimeException ignored) {
            // Intentionally ignored. Batch jobs don't put valid URLs.
        }

        return url;
    }

    public Map<String, String> mapToSafeMap(Map<String, String> map, ObfuscationStrategy strategy) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<String, String> safeMapBuilder = ImmutableMap.builder();
        map.entrySet().stream().filter(entry -> !Strings.isNullOrEmpty(entry.getValue())).forEach(
            (entry) -> safeMapBuilder.put(entry.getKey(),
                hashSensitiveData(entry.getKey(), entry.getValue(), strategy)));
        return safeMapBuilder.build();
    }

    public Map<String, List<String>> multimapToSafeMap(Multimap<String, String> multimap,
        ObfuscationStrategy strategy) {
        if (multimap == null || multimap.isEmpty()) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<String, List<String>> safeMapBuilder = ImmutableMap.builder();
        multimap.asMap().forEach((key, value) -> {
            List<String> hashedData = hashSensitiveData(key, value, strategy);
            if (!hashedData.isEmpty()) {
                safeMapBuilder.put(key, hashedData);
            }
        });
        return safeMapBuilder.build();
    }

    public Map<String, List<String>> listMapToSafeMap(Map<String, List<String>> listMap, ObfuscationStrategy strategy) {
        if (listMap == null || listMap.isEmpty()) {
            return Collections.emptyMap();
        }
        ImmutableMap.Builder<String, List<String>> safeMapBuilder = ImmutableMap.builder();
        listMap.forEach((key, value) -> {
            List<String> hashedData = hashSensitiveData(key, value, strategy);
            if (!hashedData.isEmpty()) {
                safeMapBuilder.put(key, hashedData);
            }
        });
        return safeMapBuilder.build();
    }

    private List<String> hashSensitiveData(String key, Collection<String> values, ObfuscationStrategy strategy) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (!Strings.isNullOrEmpty(value)) {
                result.add(hashSensitiveData(key, value, strategy));
            }
        }
        return result;
    }

    private String hashSensitiveData(String key, String value, ObfuscationStrategy strategy) {
        String newValue = value;
        if (strategy == ObfuscationStrategy.PARAMETERS) {
            newValue = hashSensitiveParameters(key, newValue);
        } else if (strategy == ObfuscationStrategy.HEADERS) {
            newValue = hashSensitiveHeaders(key, newValue);
        } else if (strategy == ObfuscationStrategy.ALL) {
            newValue = hashSensitiveParameters(key, newValue);
            newValue = hashSensitiveHeaders(key, newValue);
            if (newValue.equals(value)) {
                newValue = hashAccessTokenInUrl(newValue);
            }
        }
        return newValue;
    }

    private String hashSensitiveParameters(String key, String value) {
        String newValue = value;
        if (ACCESS_TOKEN.equalsIgnoreCase(key) || EXTOLE_ACCESS_TOKEN.equalsIgnoreCase(key)) {
            newValue = hashAlgorithm.hashString(StringUtils.trim(newValue));
        }
        return newValue;
    }

    private String hashSensitiveHeaders(String key, String value) {
        String newValue = value;
        if (AUTHORIZATION.equalsIgnoreCase(key)) {
            newValue = hashAccessToken(newValue, headerAuthorizationPattern);
        } else if (AUTHORIZATION_TOKEN.equalsIgnoreCase(key)) {
            newValue = hashAlgorithm.hashString(newValue);
        } else if (HEADER_COOKIE.equalsIgnoreCase(key)) {
            newValue = hashAccessToken(newValue, ACCESS_TOKEN_PATTERN);
        } else if (HEADER_X_INCOMING_URL.equalsIgnoreCase(key)
            || HEADER_X_EXTOLE_INCOMING_URL.equalsIgnoreCase(key)) {
            newValue = hashAccessTokenInUrl(newValue);
        }
        return newValue;
    }

    private String hashAccessToken(String value, Pattern pattern) {
        String result = value;
        String trimmedResult = StringUtils.trim(result);
        Matcher matcher = pattern.matcher(trimmedResult);
        while (matcher.find()) {
            String accessToken = matcher.group("accessToken");
            result = trimmedResult.replace(accessToken, hashAlgorithm.hashString(accessToken));
        }
        return result;
    }

    private String hashAccessTokenInUrl(String value) {
        String marker = ACCESS_TOKEN + "=";
        int start = value.indexOf(marker);
        if (start == -1) {
            return value;
        }
        int end = value.indexOf('&', start);
        if (end == -1) {
            end = value.length();
        }
        String token = value.substring(start + marker.length(), end);
        if (!token.contains(hashAlgorithm.name())) {
            value = new StringBuilder(value)
                .replace(start + marker.length(), end, hashAlgorithm.hashString(token))
                .toString();
        }
        return value;
    }

}
