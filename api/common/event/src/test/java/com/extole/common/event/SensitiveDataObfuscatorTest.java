package com.extole.common.event;

import static com.extole.common.event.SensitiveDataObfuscator.forAlgorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.extole.common.security.HashAlgorithm;

public class SensitiveDataObfuscatorTest {

    private static final String AUTHORIZATION = "authorization";
    private static final String AUTHORIZATION_TOKEN = "authorization-token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXTOLE_ACCESS_TOKEN = "extole_access_token";
    private static final String HEADER_X_EXTOLE_INCOMING_URL = "x-extole-incoming-url";
    private static final String HEADER_X_INCOMING_URL = "x-incoming-url";
    private static final String HEADER_COOKIE = "cookie";
    private static final String CUSTOM_HEADER = "CUSTOM_HEADER";
    private static final String EMPTY_VALUE = "EMPTY_VALUE";
    private static final String NULL_VALUE = "NULL_VALUE";
    private static final String EMPTY = "EMPTY";

    @Test
    public void testObfuscateMap() {
        obfuscateMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)",
            HashAlgorithm.SHA1);
        obfuscateMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)",
            HashAlgorithm.SHA256);

        obfuscateAuthorizationHeaderCaseInsensitiveMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveMap(
            "aHVnZV9odWdlX2h1Z2VfaHVnZV91c2VybmFtZWh1Z2VfaHVnZV9odWdlX2h1Z2VfcGFzc3dvcmQ=",
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMap(
            "aHVnZV9odWdlX2h1Z2VfaHVnZV91c2VybmFtZWh1Z2VfaHVnZV9odWdlX2h1Z2VfcGFzc3dvcmQ=",
            HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveMap("aHV=", HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMap("aHV=", HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveMap(
            RandomStringUtils.randomAlphanumeric(250)
                + "." + RandomStringUtils.randomAlphanumeric(250)
                + "-" + RandomStringUtils.randomAlphanumeric(250),
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMap(
            RandomStringUtils.randomAlphanumeric(250)
                + "." + RandomStringUtils.randomAlphanumeric(250)
                + "-" + RandomStringUtils.randomAlphanumeric(250),
            HashAlgorithm.SHA256);

        obfuscateAlreadyHashedDataMap(
            "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)",
            HashAlgorithm.SHA1);
        obfuscateAlreadyHashedDataMap(
            "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)",
            HashAlgorithm.SHA256);
    }

    @Test
    public void testObfuscateMultiMap() {
        obfuscateMultiMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)",
            HashAlgorithm.SHA1);
        obfuscateMultiMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)",
            HashAlgorithm.SHA256);

        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(
            "aHVnZV9odWdlX2h1Z2VfaHVnZV91c2VybmFtZWh1Z2VfaHVnZV9odWdlX2h1Z2VfcGFzc3dvcmQ=",
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(
            "aHVnZV9odWdlX2h1Z2VfaHVnZV91c2VybmFtZWh1Z2VfaHVnZV9odWdlX2h1Z2VfcGFzc3dvcmQ=",
            HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap("aHV=", HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap("aHV=", HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(
            RandomStringUtils.randomAlphanumeric(250)
                + "." + RandomStringUtils.randomAlphanumeric(250)
                + "-" + RandomStringUtils.randomAlphanumeric(250),
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(
            RandomStringUtils.randomAlphanumeric(250)
                + "." + RandomStringUtils.randomAlphanumeric(250)
                + "-" + RandomStringUtils.randomAlphanumeric(250),
            HashAlgorithm.SHA256);

        obfuscateAlreadyHashedDataMultiMap(
            "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)",
            HashAlgorithm.SHA1);
        obfuscateAlreadyHashedDataMultiMap(
            "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)",
            HashAlgorithm.SHA256);
    }

    @Test
    public void testObfuscateListMap() {
        obfuscateListMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)",
            HashAlgorithm.SHA1);
        obfuscateListMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)",
            HashAlgorithm.SHA256);

        obfuscateAuthorizationHeaderCaseInsensitiveListMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap(
            "1S5AAL0JO8SSTVLFR5KIDB2L1L",
            HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap(
            "aHVnZV9odWdlX2h1Z2VfaHVnZV91c2VybmFtZWh1Z2VfaHVnZV9odWdlX2h1Z2VfcGFzc3dvcmQ=",
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap(
            "aHVnZV9odWdlX2h1Z2VfaHVnZV91c2VybmFtZWh1Z2VfaHVnZV9odWdlX2h1Z2VfcGFzc3dvcmQ=",
            HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap("aHV=", HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap("aHV=", HashAlgorithm.SHA256);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap(
            RandomStringUtils.randomAlphanumeric(250)
                + "." + RandomStringUtils.randomAlphanumeric(250)
                + "-" + RandomStringUtils.randomAlphanumeric(250),
            HashAlgorithm.SHA1);
        obfuscateAuthorizationHeaderCaseInsensitiveListMap(
            RandomStringUtils.randomAlphanumeric(250)
                + "." + RandomStringUtils.randomAlphanumeric(250)
                + "-" + RandomStringUtils.randomAlphanumeric(250),
            HashAlgorithm.SHA256);

        obfuscateAlreadyHashedDataListMap(
            "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)",
            HashAlgorithm.SHA1);
        obfuscateAlreadyHashedDataListMap(
            "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)",
            HashAlgorithm.SHA256);
    }

    @Test
    public void testObfuscateWithNoneHashAlgorithm() {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(HashAlgorithm.NONE.name());
        String accessToken = "1S5AAL0JO8SSTVLFR5KIDB2L1L";

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put(AUTHORIZATION, accessToken);
        inputMap.put(AUTHORIZATION_TOKEN, accessToken);
        inputMap.put(ACCESS_TOKEN, accessToken);
        inputMap.put(EXTOLE_ACCESS_TOKEN, accessToken);
        inputMap.put(HEADER_X_EXTOLE_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        inputMap.put(HEADER_X_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        inputMap.put(HEADER_COOKIE, "extole_access_token=" + accessToken + "; access_token=" + accessToken);
        inputMap.put(CUSTOM_HEADER, "access_token=" + accessToken);

        Map<String, String> outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap).isEqualTo(outputMap);

        Multimap<String, String> inputMultiMap = ArrayListMultimap.create();
        inputMultiMap.put(AUTHORIZATION, accessToken);
        inputMultiMap.put(AUTHORIZATION_TOKEN, accessToken);
        inputMultiMap.put(ACCESS_TOKEN, accessToken);
        inputMultiMap.put(EXTOLE_ACCESS_TOKEN, accessToken);
        inputMultiMap.put(HEADER_X_EXTOLE_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        inputMultiMap.put(HEADER_X_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        inputMultiMap.put(HEADER_COOKIE, "access_token=" + accessToken);
        inputMultiMap.put(HEADER_COOKIE,
            "access_token=" + accessToken + "; extole_access_token=" + accessToken);
        inputMultiMap.put(CUSTOM_HEADER, "access_token=" + accessToken);
        inputMultiMap.put(CUSTOM_HEADER, "extole_access_token=" + accessToken);

        Map<String, List<String>> outputMultiMap =
            sensitiveDataObfuscator.multimapToSafeMap(inputMultiMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMultiMap.asMap()).isEqualTo(outputMultiMap);

        Map<String, List<String>> inputListMap = new HashMap<>();
        inputListMap.put(AUTHORIZATION, Collections.singletonList(accessToken));
        inputListMap.put(AUTHORIZATION_TOKEN, Collections.singletonList(accessToken));
        inputListMap.put(ACCESS_TOKEN, Collections.singletonList(accessToken));
        inputListMap.put(EXTOLE_ACCESS_TOKEN, Collections.singletonList(accessToken));
        inputListMap.put(HEADER_X_EXTOLE_INCOMING_URL,
            Collections.singletonList("http://127.0.0.1/?access_token=" + accessToken));
        inputListMap.put(HEADER_X_INCOMING_URL,
            Collections.singletonList("http://127.0.0.1/?access_token=" + accessToken));
        inputListMap.put(HEADER_COOKIE, Arrays.asList("extole_access_token=" + accessToken,
            "extole_access_token=" + accessToken + "; access_token=" + accessToken));
        inputListMap.put(CUSTOM_HEADER,
            Arrays.asList("extole_access_token=" + accessToken, "access_token=" + accessToken));

        Map<String, List<String>> outputListMap = sensitiveDataObfuscator.listMapToSafeMap(inputListMap,
            ObfuscationStrategy.ALL);
        Assertions.assertThat(inputListMap).isEqualTo(outputListMap);
    }

    @Test
    public void testObfuscateRequestBody() {
        String accessToken = "1S5AAL0JO8SSTVLFR5KIDB2L1L";
        String sha1AccessToken = "HASHED_SHA1(66f5e13333e67f5f6e3d504475604d0ad6778b09)";
        String sha256AccessToken = "HASHED_SHA256(148938dda199cfb0df8f68ad5825dbdee49e7e9224a764113485d20338852a55)";
        String bodyTemplate = "::headers.Accept=text/javascript&::headers.X-Extole-App=javascript_sdk&::" +
            "headers.Authorization=Bearer+" + "%s" + "&first_name=Extole&last_name=Extole" +
            "&email=example.com&partner_user_id=1111&partner_conversion_id=1111" +
            "&cart_value=100.00&coupon_code=&zone_id=1111";

        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(HashAlgorithm.SHA1.name());

        String output = sensitiveDataObfuscator.hashRequestBody(String.format(bodyTemplate, accessToken));
        Assertions.assertThat(output).isEqualTo(String.format(bodyTemplate, sha1AccessToken));
        String output2 = sensitiveDataObfuscator.hashRequestBody(output);
        Assertions.assertThat(output2).isEqualTo(output);

        sensitiveDataObfuscator = forAlgorithm(HashAlgorithm.SHA256.name());

        output = sensitiveDataObfuscator.hashRequestBody(String.format(bodyTemplate, accessToken));
        Assertions.assertThat(output).isEqualTo(String.format(bodyTemplate, sha256AccessToken));
        output2 = sensitiveDataObfuscator.hashRequestBody(output);
        Assertions.assertThat(output2).isEqualTo(output);

        sensitiveDataObfuscator = forAlgorithm(HashAlgorithm.NONE.name());

        output = sensitiveDataObfuscator.hashRequestBody(String.format(bodyTemplate, accessToken));
        Assertions.assertThat(output).isEqualTo(String.format(bodyTemplate, accessToken));
    }

    private void obfuscateMap(String accessToken, String expectedHash, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());

        Map<String, String> input = new HashMap<>();
        input.put(AUTHORIZATION, " " + accessToken);
        input.put(AUTHORIZATION_TOKEN, " " + accessToken);
        input.put(ACCESS_TOKEN, " " + accessToken);
        input.put(EXTOLE_ACCESS_TOKEN, accessToken);
        input.put(HEADER_X_EXTOLE_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        input.put(HEADER_X_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        input.put(HEADER_COOKIE, " extole_access_token=" + accessToken + "; access_token=" + accessToken);
        input.put(CUSTOM_HEADER, " access_token=" + accessToken);
        input.put(EMPTY_VALUE, "");
        input.put(NULL_VALUE, null);

        Map<String, String> output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.ALL);

        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(ACCESS_TOKEN)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(EXTOLE_ACCESS_TOKEN)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(HEADER_X_EXTOLE_INCOMING_URL)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(HEADER_X_INCOMING_URL)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(HEADER_COOKIE)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(CUSTOM_HEADER)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(AUTHORIZATION_TOKEN)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(AUTHORIZATION)).isEqualTo(StringUtils.trim(output.get(AUTHORIZATION)));
        Assertions.assertThat(output.get(AUTHORIZATION_TOKEN))
            .isEqualTo(StringUtils.trim(output.get(AUTHORIZATION_TOKEN)));
        Assertions.assertThat(output.get(ACCESS_TOKEN)).isEqualTo(StringUtils.trim(output.get(ACCESS_TOKEN)));
        Assertions.assertThat(output.get(HEADER_COOKIE)).isEqualTo(StringUtils.trim(output.get(HEADER_COOKIE)));
        Assertions.assertThat(output.get(CUSTOM_HEADER)).isNotEqualTo(StringUtils.trim(output.get(CUSTOM_HEADER)));
        Assertions.assertThat(output).doesNotContainKey(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKey(NULL_VALUE);
        Assertions.assertThat(output.get(AUTHORIZATION))
            .isEqualTo(expectedHash);

        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);

        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(AUTHORIZATION_TOKEN)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(HEADER_COOKIE)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(HEADER_X_EXTOLE_INCOMING_URL)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(HEADER_X_INCOMING_URL)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(ACCESS_TOKEN)).contains(accessToken);
        Assertions.assertThat(output.get(EXTOLE_ACCESS_TOKEN)).contains(accessToken);
        Assertions.assertThat(output.get(CUSTOM_HEADER)).contains(accessToken);
        Assertions.assertThat(output).doesNotContainKey(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKey(NULL_VALUE);

        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.PARAMETERS);

        Assertions.assertThat(output.get(ACCESS_TOKEN)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(EXTOLE_ACCESS_TOKEN)).doesNotContain(accessToken);
        Assertions.assertThat(output.get(AUTHORIZATION)).contains(accessToken);
        Assertions.assertThat(output.get(AUTHORIZATION_TOKEN)).contains(accessToken);
        Assertions.assertThat(output.get(HEADER_X_EXTOLE_INCOMING_URL)).contains(accessToken);
        Assertions.assertThat(output.get(HEADER_X_INCOMING_URL)).contains(accessToken);
        Assertions.assertThat(output.get(HEADER_COOKIE)).contains(accessToken);
        Assertions.assertThat(output.get(CUSTOM_HEADER)).contains(accessToken);
        Assertions.assertThat(output).doesNotContainKey(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKey(NULL_VALUE);
    }

    private void obfuscateMultiMap(String accessToken, String expectedHash, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Multimap<String, String> input = ArrayListMultimap.create();
        input.put(AUTHORIZATION, accessToken);
        input.put(AUTHORIZATION_TOKEN, accessToken);
        input.put(ACCESS_TOKEN, accessToken);
        input.put(EXTOLE_ACCESS_TOKEN, accessToken);
        input.put(HEADER_X_EXTOLE_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        input.put(HEADER_X_INCOMING_URL, "http://127.0.0.1/?access_token=" + accessToken);
        input.put(HEADER_COOKIE, "access_token=" + accessToken);
        input.put(HEADER_COOKIE,
            "access_token=" + accessToken + "; extole_access_token=" + accessToken);
        input.put(CUSTOM_HEADER, "access_token=" + accessToken);
        input.put(CUSTOM_HEADER, "extole_access_token=" + accessToken);
        input.put(EMPTY_VALUE, "");
        input.put(EMPTY_VALUE, "");
        input.put(NULL_VALUE, null);
        input.put(NULL_VALUE, null);

        Map<String, List<String>> output = sensitiveDataObfuscator.multimapToSafeMap(input, ObfuscationStrategy.ALL);

        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(AUTHORIZATION_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(EXTOLE_ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_EXTOLE_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_COOKIE)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(CUSTOM_HEADER)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value)
                .contains(expectedHash));
        Assertions.assertThat(output).doesNotContainKeys(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(NULL_VALUE);

        output = sensitiveDataObfuscator.multimapToSafeMap(input, ObfuscationStrategy.HEADERS);

        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(AUTHORIZATION_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_COOKIE)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_EXTOLE_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(EXTOLE_ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(CUSTOM_HEADER)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        Assertions.assertThat(output).doesNotContainKeys(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(NULL_VALUE);

        output = sensitiveDataObfuscator.multimapToSafeMap(input, ObfuscationStrategy.PARAMETERS);

        output.get(ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(EXTOLE_ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_EXTOLE_INCOMING_URL)

            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(HEADER_X_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(AUTHORIZATION_TOKEN)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(HEADER_COOKIE)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(CUSTOM_HEADER)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        Assertions.assertThat(output).doesNotContainKeys(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(NULL_VALUE);
    }

    private void obfuscateListMap(String accessToken, String expectedHash, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Map<String, List<String>> input = new HashMap<>();
        input.put(AUTHORIZATION, Collections.singletonList(accessToken));
        input.put(AUTHORIZATION_TOKEN, Collections.singletonList(accessToken));
        input.put(ACCESS_TOKEN, Collections.singletonList(accessToken));
        input.put(EXTOLE_ACCESS_TOKEN, Collections.singletonList(accessToken));
        input.put(HEADER_X_EXTOLE_INCOMING_URL,
            Collections.singletonList("http://127.0.0.1/?access_token=" + accessToken));
        input.put(HEADER_X_INCOMING_URL,
            Collections.singletonList("http://127.0.0.1/?access_token=" + accessToken));
        input.put(HEADER_COOKIE, Arrays.asList("extole_access_token=" + accessToken,
            "extole_access_token=" + accessToken + "; access_token=" + accessToken));
        input.put(CUSTOM_HEADER,
            Arrays.asList("extole_access_token=" + accessToken, "access_token=" + accessToken));
        input.put(EMPTY_VALUE, Arrays.asList("", ""));
        input.put(NULL_VALUE, Arrays.asList(null, null));
        input.put(EMPTY, Collections.emptyList());

        Map<String, List<String>> output = sensitiveDataObfuscator.listMapToSafeMap(input, ObfuscationStrategy.ALL);

        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(AUTHORIZATION_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(EXTOLE_ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_EXTOLE_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_COOKIE)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(CUSTOM_HEADER)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value)
                .contains(expectedHash));
        Assertions.assertThat(output).doesNotContainKeys(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(NULL_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(EMPTY);

        output = sensitiveDataObfuscator.listMapToSafeMap(input, ObfuscationStrategy.HEADERS);

        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(AUTHORIZATION_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_COOKIE)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_EXTOLE_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(EXTOLE_ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(CUSTOM_HEADER)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        Assertions.assertThat(output).doesNotContainKeys(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(NULL_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(EMPTY);

        output = sensitiveDataObfuscator.listMapToSafeMap(input, ObfuscationStrategy.PARAMETERS);

        output.get(ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(EXTOLE_ACCESS_TOKEN)
            .forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
        output.get(HEADER_X_EXTOLE_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(HEADER_X_INCOMING_URL)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(AUTHORIZATION)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(AUTHORIZATION_TOKEN)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(HEADER_COOKIE)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        output.get(CUSTOM_HEADER)
            .forEach(value -> Assertions.assertThat(value).contains(accessToken));
        Assertions.assertThat(output).doesNotContainKeys(EMPTY_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(NULL_VALUE);
        Assertions.assertThat(output).doesNotContainKeys(EMPTY);
    }

    private void obfuscateAuthorizationHeaderCaseInsensitiveMap(String accessToken, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());

        Map<String, String> input = new HashMap<>();
        input.put(AUTHORIZATION, "Basic " + accessToken);
        Map<String, String> output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "basic " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "bAsIc " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "BaSiC " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "Bearer " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "bearer " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "bEaReR " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);

        input.put(AUTHORIZATION, "BeArEr " + accessToken);
        output = sensitiveDataObfuscator.mapToSafeMap(input, ObfuscationStrategy.HEADERS);
        Assertions.assertThat(output.get(AUTHORIZATION)).doesNotContain(accessToken);
    }

    private void obfuscateAuthorizationHeaderCaseInsensitiveMultiMap(String accessToken, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Multimap<String, String> input = ArrayListMultimap.create();
        input.put(AUTHORIZATION, accessToken);
        input.put(AUTHORIZATION, "Basic " + accessToken);
        input.put(AUTHORIZATION, "basic " + accessToken);
        input.put(AUTHORIZATION, "bAsIc " + accessToken);
        input.put(AUTHORIZATION, "BaSiC " + accessToken);

        input.put(AUTHORIZATION, "Bearer " + accessToken);
        input.put(AUTHORIZATION, "bearer " + accessToken);
        input.put(AUTHORIZATION, "bEaReR " + accessToken);
        input.put(AUTHORIZATION, "BeArEr " + accessToken);

        Map<String, List<String>> output = sensitiveDataObfuscator.multimapToSafeMap(input,
            ObfuscationStrategy.HEADERS);
        output.get(AUTHORIZATION).forEach(value -> Assertions.assertThat(value).doesNotContain(accessToken));
    }

    private void obfuscateAuthorizationHeaderCaseInsensitiveListMap(String accessToken, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Map<String, List<String>> input = new HashMap<>();
        input.put(AUTHORIZATION,
            Arrays.asList(
                accessToken,
                "Basic " + accessToken,
                "basic " + accessToken,
                "bAsIc " + accessToken,
                "BaSiC " + accessToken,
                "Bearer " + accessToken,
                "bearer " + accessToken,
                "bEaReR " + accessToken,
                "BeArEr " + accessToken));
        Map<String, List<String>> output = sensitiveDataObfuscator.listMapToSafeMap(input, ObfuscationStrategy.HEADERS);
        output.get(AUTHORIZATION).forEach(value -> Assertions.assertThat(value)
            .doesNotContain(accessToken));
    }

    private void obfuscateAlreadyHashedDataMap(String hashedData, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put(CUSTOM_HEADER, " " + hashedData);
        inputMap.put(HEADER_COOKIE, " extole_access_token=" + hashedData + "; " + "access_token=" + hashedData);

        Map<String, String> outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);

        Assertions.assertThat(inputMap.get(CUSTOM_HEADER)).isEqualTo(outputMap.get(CUSTOM_HEADER));
        Assertions.assertThat(inputMap.get(HEADER_COOKIE)).isEqualTo(outputMap.get(HEADER_COOKIE));

        inputMap.put(AUTHORIZATION, " Bearer " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " bearer " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " bEaReR " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " BeArEr " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " Basic " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " basic " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " bAsIc " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));

        inputMap.put(AUTHORIZATION, " BaSiC " + hashedData);
        outputMap = sensitiveDataObfuscator.mapToSafeMap(inputMap, ObfuscationStrategy.ALL);
        Assertions.assertThat(inputMap.get(AUTHORIZATION)).isEqualTo(outputMap.get(AUTHORIZATION));
    }

    private void obfuscateAlreadyHashedDataMultiMap(String hashedData, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Multimap<String, String> inputMultiMap = ArrayListMultimap.create();
        inputMultiMap.put(AUTHORIZATION, hashedData);
        inputMultiMap.put(AUTHORIZATION, "Basic " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "basic " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "bAsIc " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "BaSiC " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "Bearer " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "bearer " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "bEaReR " + hashedData);
        inputMultiMap.put(AUTHORIZATION, "BeArEr " + hashedData);
        inputMultiMap.put(CUSTOM_HEADER, hashedData);
        inputMultiMap.put(HEADER_COOKIE, "access_token=" + hashedData);
        inputMultiMap.put(HEADER_COOKIE, "extole_access_token=" + hashedData);

        Map<String, List<String>> outputMultiMap =
            sensitiveDataObfuscator.multimapToSafeMap(inputMultiMap, ObfuscationStrategy.ALL);

        outputMultiMap.get(AUTHORIZATION).forEach(
            value -> Assertions.assertThat(value).contains(hashedData));
        outputMultiMap.get(CUSTOM_HEADER).forEach(value -> Assertions.assertThat(value).contains(hashedData));
        outputMultiMap.get(HEADER_COOKIE).forEach(value -> Assertions.assertThat(value).contains(hashedData));
    }

    private void obfuscateAlreadyHashedDataListMap(String hashedData, HashAlgorithm hashAlgorithm) {
        SensitiveDataObfuscator sensitiveDataObfuscator = forAlgorithm(hashAlgorithm.name());
        Map<String, List<String>> inputList = new HashMap<>();
        inputList.put(AUTHORIZATION,
            Arrays.asList(
                " Bearer " + hashedData,
                " bearer " + hashedData,
                " bEaReR " + hashedData,
                " BeArEr " + hashedData,
                " Basic " + hashedData,
                " basic " + hashedData,
                " bAsIc " + hashedData,
                " BaSiC " + hashedData,
                " " + hashedData));
        inputList.put(CUSTOM_HEADER,
            Collections.singletonList(" " + hashedData));
        inputList.put(HEADER_COOKIE,
            Arrays.asList(" extole_access_token=" + hashedData, " access_token=" + hashedData));

        Map<String, List<String>> outputList =
            sensitiveDataObfuscator.listMapToSafeMap(inputList, ObfuscationStrategy.ALL);

        Assertions.assertThat(inputList.get(AUTHORIZATION)).isEqualTo(outputList.get(AUTHORIZATION));
        Assertions.assertThat(inputList.get(CUSTOM_HEADER)).isEqualTo(outputList.get(CUSTOM_HEADER));
        Assertions.assertThat(inputList.get(HEADER_COOKIE)).isEqualTo(outputList.get(HEADER_COOKIE));
    }
}
