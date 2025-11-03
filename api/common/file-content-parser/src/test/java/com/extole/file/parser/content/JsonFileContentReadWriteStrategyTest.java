package com.extole.file.parser.content;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import com.extole.common.lang.ObjectMapperProvider;

class JsonFileContentReadWriteStrategyTest {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance()
        .copy()
        .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    private static final JsonFileContentReadWriteStrategy JSON_FILE_CONTENT_READ_WRITE_STRATEGY =
        new JsonFileContentReadWriteStrategy(OBJECT_MAPPER);
    private static final String JSON_EXAMPLE = "[{\n" +
        "    \"str\": \"test\",\n" +
        "    \"numAsStr\": \"2\",\n" +
        "    \"num\": 400,\n" +
        "    \"arr\": [\"ab\", \"cd\"],\n" +
        "    \"obj\": {\n" +
        "        \"a\": 1,\n" +
        "        \"b\": 2\n" +
        "    }\n" +
        "}]";

    @Test
    void testWriteAndReadFileContent() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("str", "test");
        map.put("numAsStr", "2");
        map.put("num", Integer.valueOf(400));
        map.put("arr", List.of("ab", "cd"));
        map.put("obj", Map.of("a", Integer.valueOf(1), "b", Integer.valueOf(2)));
        map.put("null_key", null);

        String json = new String(
            JSON_FILE_CONTENT_READ_WRITE_STRATEGY.writeFileContent(List.of(map)).getContentAsBytes());

        assertThat(JSON_FILE_CONTENT_READ_WRITE_STRATEGY
            .readFileContent(new ByteArrayInputStream(json.getBytes()), new TypeReference<Map<String, Object>>() {}))
                .isEqualTo(singletonList(map));
    }

    @Test
    void testReadFileContent() throws Exception {
        Map<String, Object> element = JSON_FILE_CONTENT_READ_WRITE_STRATEGY.readFileContent(
            new ByteArrayInputStream(JSON_EXAMPLE.getBytes(StandardCharsets.UTF_8)),
            new TypeReference<Map<String, Object>>() {}).get(0);

        assertThat(element).isEqualTo(OBJECT_MAPPER.readValue(JSON_EXAMPLE,
            new TypeReference<List<Map<String, Object>>>() {}).get(0));

        element = JSON_FILE_CONTENT_READ_WRITE_STRATEGY.readFileContent(
            new ByteArrayInputStream(JSON_EXAMPLE.getBytes(StandardCharsets.UTF_8)),
            new TypeReference<Map<String, Object>>() {}, (objectNode) -> {
                objectNode.set("num", OBJECT_MAPPER.getNodeFactory().textNode("hi"));
                objectNode.set("arr", OBJECT_MAPPER.getNodeFactory().numberNode(909));
            }).get(0);

        Map<String, Object> changedElement = new HashMap<>(OBJECT_MAPPER.readValue(
            JSON_EXAMPLE,
            new TypeReference<List<Map<String, Object>>>() {}).get(0));
        changedElement.put("num", "hi");
        changedElement.put("arr", Integer.valueOf(909));

        assertThat(element).isEqualTo(changedElement);
    }
}
