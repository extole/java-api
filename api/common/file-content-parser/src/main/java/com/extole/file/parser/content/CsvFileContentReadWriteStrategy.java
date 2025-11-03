package com.extole.file.parser.content;

import static java.util.Collections.emptyList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

final class CsvFileContentReadWriteStrategy implements FileContentWriteStrategy, FileContentReadStrategy {

    private static final String LSEP_CHAR = "\u2028";
    private static final GridConverter GRID_CONVERTER = GridConverter.getInstance();
    private static final CsvProcessor CSV_PROCESSOR = CsvProcessor.INSTANCE;
    private final ObjectMapper objectMapper;

    CsvFileContentReadWriteStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ParsedFileContent writeFileContent(List<T> list) throws IOException {
        List<Map<String, String>> grid = GRID_CONVERTER.convertListToGrid(list);
        try {
            return ParsedFileContent.ofCsv(CSV_PROCESSOR.write(grid, true).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference)
        throws IOException {

        return readFileContent(fileContentInputStream, typeReference, Function.identity()::apply);
    }

    @Override
    public <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference,
        Consumer<ObjectNode> valueModifier) throws IOException {
        String fileContent = IOUtils.toString(fileContentInputStream, StandardCharsets.UTF_8)
            .replace(LSEP_CHAR, StringUtils.EMPTY);
        IOUtils.closeQuietly(fileContentInputStream);

        if (StringUtils.isEmpty(fileContent)) {
            return emptyList();
        }

        List<ObjectNode> csvContent = extractGridFromCsvContent(fileContent);

        List<T> results = new ArrayList<>(csvContent.size());

        for (ObjectNode objectNode : csvContent) {

            valueModifier.accept(objectNode);
            T object = objectMapper.convertValue(objectNode, typeReference);
            results.add(object);
        }

        return Collections.unmodifiableList(results);
    }

    private List<ObjectNode> extractGridFromCsvContent(String csvContent) throws IOException {
        List<Map<String, String>> elements;

        if (StringUtils.isWhitespace(csvContent)) {
            return emptyList();
        }

        try {
            elements = CSV_PROCESSOR.read(csvContent);
        } catch (CsvProcessException e) {
            throw new IOException(e);
        }

        List<ObjectNode> rowObjectNodes = elements.stream()
            .map(value -> {
                ObjectNode objectNode = objectMapper.createObjectNode();
                value.forEach((k, v) -> {
                    if (v != null) {
                        objectNode.put(k, v);
                    }
                });
                return objectNode;
            }).collect(Collectors.toUnmodifiableList());

        ImmutableList.Builder<ObjectNode> resultListBuilder = ImmutableList.builder();
        for (ObjectNode rowObjectNode : rowObjectNodes) {
            ObjectNode newObjectNode = convertRowObjectNodeToPojoObjectNode(rowObjectNode);
            resultListBuilder.add(newObjectNode);
        }

        return resultListBuilder.build();
    }

    private ObjectNode convertRowObjectNodeToPojoObjectNode(ObjectNode csvRowObjectNode)
        throws JsonProcessingException {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = csvRowObjectNode.fields();
        ObjectNode newObjectNode = objectMapper.createObjectNode();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            JsonNode newNode = convertCsvCellNodeToPojoFieldNode(fieldValue);

            newObjectNode.set(fieldName, newNode);
        }
        return newObjectNode;
    }

    private JsonNode convertCsvCellNodeToPojoFieldNode(JsonNode fieldValue) throws JsonProcessingException {
        JsonNode result;
        try {
            result = objectMapper.readValue(fieldValue.asText(), JsonNode.class);
            if (result instanceof NumericNode && !NumberUtils.isCreatable(fieldValue.asText())) {
                result = objectMapper.readValue(fieldValue.toString(), JsonNode.class);
            }
        } catch (JsonProcessingException e) {
            result = objectMapper.readValue(fieldValue.toString(), JsonNode.class);
        }
        return result;
    }

}
