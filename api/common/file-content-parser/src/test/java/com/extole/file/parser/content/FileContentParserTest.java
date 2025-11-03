package com.extole.file.parser.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.extole.common.lang.ObjectMapperProvider;

class FileContentParserTest {
    private static final List<
        PojoExampleForFileParserContentTest<Map<String, Object>>> LIST_OF_POJOS_WITH_NESTED_STRUCTURE =
            ImmutableList.of(
                new PojoExampleForFileParserContentTest<>("name1,_,.q", 1, ImmutableMap.of("val1",
                    ImmutableMap.of(
                        "inner_val1",
                        ImmutableMap.of(
                            "inner_inner_val1", Integer.valueOf(22),
                            "inner_inner_val2", "test",
                            "inner_val2", "A32")))),
                new PojoExampleForFileParserContentTest<>("name2_?|!@#$%%", 2, ImmutableMap.of("val2",
                    ImmutableMap.of(
                        "inner_val1",
                        ImmutableMap.of(
                            "inner_inner_val1", Integer.valueOf(220),
                            "inner_inner_val2", "test1",
                            "inner_val2", "A321")))));
    private static final byte[] ALL_BYTES_VALUES = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            ALL_BYTES_VALUES[i] = (byte) i;
        }
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testReadAndWriteOperationsAreSymmetricForEmptyStrings(FileContentParseFormat format) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "");
        map.put("b", null);
        List<Map<String, String>> objects = ImmutableList.of(
            map,
            ImmutableMap.of("c", " ", "b", "bvc"));
        ParsedFileContent parsedContent = FileContentParser.getWriteStrategy(format).writeFileContent(objects);

        List<Map<String, String>> result = readFileContent(format,
            parsedContent.getContentAsBytes(),
            new TypeReference<>() {});
        assertThat(result).isEqualTo(objects);
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testReadAndWriteOperationsAreSymmetric(FileContentParseFormat format) throws Exception {
        List<Map<String, Object>> objects = ImmutableList.of(
            ImmutableMap.of("a", Long.valueOf(Long.MIN_VALUE), "b", Long.valueOf(Long.MIN_VALUE)),
            ImmutableMap.of("a", Long.valueOf(Long.MAX_VALUE), "b", "123"));
        ParsedFileContent parsedContent = FileContentParser.getWriteStrategy(format).writeFileContent(objects);
        assertThat(parsedContent.getFileParserContentFormat()).isEqualTo(format);
        List<Map<String, Object>> result = readFileContent(format, parsedContent.getContentAsBytes(),
            new TypeReference<>() {}, objectNode -> {
                if (objectNode.hasNonNull("b") && objectNode.get("b").asInt() == 123) {
                    objectNode.set("b",
                        ObjectMapperProvider.getConfiguredInstance().getNodeFactory().textNode("123"));
                }
            });
        assertThat(result).isEqualTo(objects);

        parsedContent = FileContentParser.getWriteStrategy(format).writeFileContent(objects);
        assertThat(parsedContent.getFileParserContentFormat()).isEqualTo(format);
        result = readFileContent(format, parsedContent.getContentAsBytes(),
            new TypeReference<>() {}, objectNode -> {
                if (objectNode.hasNonNull("b") && objectNode.get("b").asInt() == 123) {
                    objectNode.set("b",
                        ObjectMapperProvider.getConfiguredInstance().getNodeFactory().textNode("123"));
                }
            });
        assertThat(result).isEqualTo(objects);
    }

    @Test
    void testCsvStringValuesAreSurroundedByQuotesInAllCases() throws Exception {
        List<Map<String, Object>> objects = ImmutableList.of(
            ImmutableMap.of("a", Long.valueOf(Long.MIN_VALUE), "b", "text,\"123", "c", ""),
            ImmutableMap.of("a", Long.valueOf(Long.MAX_VALUE), "b", "text123"));
        byte[] parsedContent = writeFileContent(FileContentParseFormat.CSV, objects);
        List<Map<String, Object>> result = readFileContent(FileContentParseFormat.CSV, parsedContent,
            new TypeReference<>() {});
        assertThat(objects).isEqualTo(result);
        assertThat(new String(parsedContent)).isEqualTo(new String(readResource("/parser/example1.csv")));
    }

    @Test
    void testCsvReadLogicWithEmptiesAndNulls() throws Exception {
        byte[] csvContent = readResource("/parser/example2.csv");

        List<Map<String, Object>> result = readFileContent(FileContentParseFormat.CSV, csvContent,
            new TypeReference<>() {});

        assertThat(result).isEqualTo(List.of(Map.of("a", ""), new HashMap<String, Object>() {
            {
                put("a", null);
            }
        }, Map.of()));
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testParseAndReadPojoWithNestedList(FileContentParseFormat format) throws Exception {
        List<Animal> animals = ImmutableList.of(
            new Animal("Bob", List.of("tag1", "tag2,")),
            new Animal("Jim", List.of("tag5", "tag4,")),
            new Animal("Marley", List.of("tag4", "tag5", "tag6", "tag7")));
        byte[] parsedContent = writeFileContent(format, animals);

        List<Animal> result = readFileContent(format, parsedContent, new TypeReference<>() {});

        assertThat(result)
            .hasSize(3)
            .anySatisfy(value -> {
                assertThat(value.getName()).isEqualTo("Bob");
                assertThat(value.getTags()).isEqualTo(List.of("tag1", "tag2,"));
            })
            .anySatisfy(value -> {
                assertThat(value.getName()).isEqualTo("Jim");
                assertThat(value.getTags()).isEqualTo(List.of("tag5", "tag4,"));
            })
            .anySatisfy(value -> {
                assertThat(value.getName()).isEqualTo("Marley");
                assertThat(value.getTags()).isEqualTo(List.of("tag4", "tag5", "tag6", "tag7"));
            });
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testReadAllBytesContent(FileContentParseFormat format) {
        assertThrows(IOException.class,
            () -> readFileContent(format, ALL_BYTES_VALUES, new TypeReference<List<Map<String, Object>>>() {}));
    }

    @ValueSource(strings = {"CSV", "XLSX"})
    @ParameterizedTest
    void testXlsxAndCsvShouldNotContainQuotesForStrings(FileContentParseFormat format)
        throws Exception {
        Map<String, Object> nestedMap = Map.of(
            "field1", "value1",
            "field2", Integer.valueOf(9),
            "field3", Set.of("aaa", "999"));

        byte[] parsedContent = writeFileContent(format, ImmutableList.of(
            Map.of("col1", "a , bc", "col2", Double.valueOf(Double.MAX_VALUE), "col3", nestedMap),
            Map.of("col1", "abc, , , , ,, , 1", "col2", Double.valueOf(Double.MIN_VALUE), "col3", nestedMap)));

        List<Map<String, Object>> listWithMaps = readFileContent(format, parsedContent, new TypeReference<>() {});

        assertThat(listWithMaps).hasSize(2)
            .anySatisfy(map -> assertThat(map).hasSize(3)
                .containsEntry("col1", "a , bc")
                .containsEntry("col2", Double.valueOf(Double.MAX_VALUE))
                .hasEntrySatisfying("col3", value -> {
                    assertThat(value).isInstanceOf(Map.class)
                        .asInstanceOf(InstanceOfAssertFactories
                            .map(String.class, Object.class))
                        .containsEntry("field1", "value1")
                        .containsEntry("field2", Integer.valueOf(9))
                        .hasEntrySatisfying("field3", list -> assertThat(list).isInstanceOf(List.class)
                            .asInstanceOf(InstanceOfAssertFactories.list(Object.class))
                            .contains("aaa", "999"));
                }))
            .anySatisfy(map -> assertThat(map).hasSize(3)
                .containsEntry("col1", "abc, , , , ,, , 1")
                .containsEntry("col2", Double.valueOf(Double.MIN_VALUE))
                .hasEntrySatisfying("col3", value -> {
                    assertThat(value).isInstanceOf(Map.class)
                        .asInstanceOf(InstanceOfAssertFactories
                            .map(String.class, Object.class))
                        .containsEntry("field1", "value1")
                        .containsEntry("field2", Integer.valueOf(9))
                        .hasEntrySatisfying("field3", list -> assertThat(list).isInstanceOf(List.class)
                            .asInstanceOf(InstanceOfAssertFactories.list(Object.class))
                            .contains("aaa", "999"));
                }));
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testReadEmptyContent(FileContentParseFormat format) throws Exception {
        byte[] parsedContent = new byte[] {};
        List<PojoExampleForFileParserContentTest<Map<String, Object>>> result = readFileContent(format, parsedContent,
            new TypeReference<>() {});
        assertThat(result).isEmpty();
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testFileFormatParseAndReadEmptyList(FileContentParseFormat format) throws Exception {
        byte[] parsedContent = writeFileContent(format, Collections.emptyList());
        List<PojoExampleForFileParserContentTest<Map<String, Object>>> result = readFileContent(format,
            parsedContent, new TypeReference<>() {});
        assertThat(result).isEmpty();
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testFileFormatParseAndReadPojoWithRichNestedStructureFromFile(FileContentParseFormat format)
        throws Exception {
        byte[] parsedContent = readResource("/parser/nested_structure." + format.getExtension());
        List<PojoExampleForFileParserContentTest<Map<String, Object>>> result = readFileContent(format, parsedContent,
            new TypeReference<>() {});

        assertThat(result).isEqualTo(LIST_OF_POJOS_WITH_NESTED_STRUCTURE);
    }

    @MethodSource("fileFormats")
    @ParameterizedTest
    void testFileFormatParseAndReadPojoWithRichNestedStructure(FileContentParseFormat format)
        throws Exception {
        byte[] parsedContent = writeFileContent(format, LIST_OF_POJOS_WITH_NESTED_STRUCTURE);
        List<PojoExampleForFileParserContentTest<Map<String, Object>>> result = readFileContent(format,
            parsedContent, new TypeReference<>() {});

        assertThat(result).isEqualTo(LIST_OF_POJOS_WITH_NESTED_STRUCTURE);
    }

    private <T> List<T> readFileContent(FileContentParseFormat format, byte[] rawFileContent,
        TypeReference<T> typeReference) throws IOException {
        FileContentReadStrategy readStrategy = FileContentParser.getReadStrategy(format);
        return readStrategy.readFileContent(new ByteArrayInputStream(rawFileContent), typeReference);
    }

    private <T> List<T> readFileContent(FileContentParseFormat format, byte[] rawFileContent,
        TypeReference<T> typeReference, Consumer<ObjectNode> valueModifier) throws IOException {
        FileContentReadStrategy readStrategy = FileContentParser.getReadStrategy(format);
        return readStrategy.readFileContent(new ByteArrayInputStream(rawFileContent), typeReference, valueModifier);
    }

    private <T> byte[] writeFileContent(FileContentParseFormat format, List<T> listOfPojos) throws IOException {
        FileContentWriteStrategy writeStrategy = FileContentParser.getWriteStrategy(format);
        return writeStrategy.writeFileContent(listOfPojos).getContentAsBytes();
    }

    private byte[] readResource(String path) throws IOException {
        try (InputStream inputStream = Objects.requireNonNull(this.getClass().getResourceAsStream(path))) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    private static Arguments[] fileFormats() {
        return EnumSet.allOf(FileContentParseFormat.class)
            .stream()
            .map(fileFormat -> Arguments.of(fileFormat))
            .toArray(Arguments[]::new);
    }

    static class Animal {
        private final String name;
        private final List<String> tags;

        @JsonCreator
        Animal(@JsonProperty("name") String name, @JsonProperty("tags") List<String> tags) {
            this.name = name;
            this.tags = tags;
        }

        @JsonProperty("name")
        String getName() {
            return name;
        }

        @JsonProperty("tags")
        List<String> getTags() {
            return tags;
        }
    }
}
