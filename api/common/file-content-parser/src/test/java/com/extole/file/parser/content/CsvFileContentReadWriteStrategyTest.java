package com.extole.file.parser.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import com.extole.common.lang.ObjectMapperProvider;

class CsvFileContentReadWriteStrategyTest {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();
    private static final CsvFileContentReadWriteStrategy CSV_FILE_CONTENT_READ_WRITE_STRATEGY =
        new CsvFileContentReadWriteStrategy(OBJECT_MAPPER);
    private static final String CSV_EXAMPLE = "" +
        "Name,Age,Languages,Number,NumberAsString\n" +
        "Alice,30,\"[\"\"ro\"\", \"\"en\"\"]\",,\"1\"\n" +
        "Bob,25,\"[\"\"it\"\", \"\"en\"\"]\",2,\n" +
        "Charlie,35,\"[\"\"ro\"\", \"\"fr\"\"]\",,\"3\"";

    @Test
    void testWriteFileContent() throws Exception {
        List<Map<String, Object>> elements = List.of(
            Map.of(
                "Name", "Alice",
                "Age", Integer.valueOf(30),
                "Languages", List.of("ro", "en"),
                "NumberAsString", "1"),
            Map.of(
                "Name", "Bob",
                "Age", Integer.valueOf(25),
                "Languages", List.of("it", "en"),
                "Number", Integer.valueOf(2)),
            Map.of(
                "Name", "Charlie",
                "Age", Integer.valueOf(35),
                "Languages", List.of("ro", "fr"),
                "NumberAsString", "3"));
        String csvContent = new String(
            CSV_FILE_CONTENT_READ_WRITE_STRATEGY.writeFileContent(elements).getContentAsBytes());

        List<Map<String, Object>> deserializedElements = CSV_FILE_CONTENT_READ_WRITE_STRATEGY.readFileContent(
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)),
            new TypeReference<>() {});
        assertThat(deserializedElements).hasSize(3)
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Alice")
                .containsEntry("Age", Integer.valueOf(30))
                .containsEntry("Languages", List.of("ro", "en"))
                .containsEntry("NumberAsString", Integer.valueOf(1)))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Bob")
                .containsEntry("Age", Integer.valueOf(25))
                .containsEntry("Languages", List.of("it", "en"))
                .containsEntry("Number", Integer.valueOf(2)))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Charlie")
                .containsEntry("Age", Integer.valueOf(35))
                .containsEntry("Languages", List.of("ro", "fr"))
                .containsEntry("NumberAsString", Integer.valueOf(3)));

        deserializedElements = CSV_FILE_CONTENT_READ_WRITE_STRATEGY.readFileContent(
            new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)),
            new TypeReference<>() {}, objectNode -> {
                if (objectNode.hasNonNull("NumberAsString")) {
                    objectNode.set("NumberAsString",
                        OBJECT_MAPPER.getNodeFactory().textNode(objectNode.get("NumberAsString").asText()));
                }
            });
        assertThat(deserializedElements).hasSize(3)
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Alice")
                .containsEntry("Age", Integer.valueOf(30))
                .containsEntry("Languages", List.of("ro", "en"))
                .containsEntry("NumberAsString", "1"))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Bob")
                .containsEntry("Age", Integer.valueOf(25))
                .containsEntry("Languages", List.of("it", "en"))
                .containsEntry("Number", Integer.valueOf(2)))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Charlie")
                .containsEntry("Age", Integer.valueOf(35))
                .containsEntry("Languages", List.of("ro", "fr"))
                .containsEntry("NumberAsString", "3"));
    }

    @Test
    void testReadFileContentWithExplicitValueModifier() throws Exception {
        List<Map<String, Object>> elements = CSV_FILE_CONTENT_READ_WRITE_STRATEGY.readFileContent(
            new ByteArrayInputStream(CSV_EXAMPLE.getBytes(StandardCharsets.UTF_8)),
            new TypeReference<>() {}, objectNode -> {
                if (objectNode.hasNonNull("NumberAsString")) {
                    objectNode.set("NumberAsString",
                        OBJECT_MAPPER.getNodeFactory().textNode(objectNode.get("NumberAsString").asText()));
                }
            });
        assertThat(elements).hasSize(3)
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Alice")
                .containsEntry("Age", Integer.valueOf(30))
                .containsEntry("Languages", List.of("ro", "en"))
                .containsEntry("NumberAsString", "1"))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Bob")
                .containsEntry("Age", Integer.valueOf(25))
                .containsEntry("Languages", List.of("it", "en"))
                .containsEntry("Number", Integer.valueOf(2)))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Charlie")
                .containsEntry("Age", Integer.valueOf(35))
                .containsEntry("Languages", List.of("ro", "fr"))
                .containsEntry("NumberAsString", "3"));
    }

    @Test
    void testReadFileContentByDefaultInterpretStringNumbersAsRegularNumbers() throws Exception {
        List<Map<String, Object>> elements = CSV_FILE_CONTENT_READ_WRITE_STRATEGY.readFileContent(
            new ByteArrayInputStream(CSV_EXAMPLE.getBytes(StandardCharsets.UTF_8)),
            new TypeReference<>() {});
        assertThat(elements).hasSize(3)
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Alice")
                .containsEntry("Age", Integer.valueOf(30))
                .containsEntry("Languages", List.of("ro", "en"))
                .containsEntry("NumberAsString", Integer.valueOf(1)))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Bob")
                .containsEntry("Age", Integer.valueOf(25))
                .containsEntry("Languages", List.of("it", "en"))
                .containsEntry("Number", Integer.valueOf(2)))
            .anySatisfy(element -> assertThat(element).hasSize(4)
                .containsEntry("Name", "Charlie")
                .containsEntry("Age", Integer.valueOf(35))
                .containsEntry("Languages", List.of("ro", "fr"))
                .containsEntry("NumberAsString", Integer.valueOf(3)));
    }
}
