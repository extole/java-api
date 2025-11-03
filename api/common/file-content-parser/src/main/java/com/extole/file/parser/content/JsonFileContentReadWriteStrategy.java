package com.extole.file.parser.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.commons.io.IOUtils;

final class JsonFileContentReadWriteStrategy implements FileContentWriteStrategy, FileContentReadStrategy {

    private final ObjectMapper objectMapper;

    JsonFileContentReadWriteStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ParsedFileContent writeFileContent(List<T> list) throws IOException {
        return ParsedFileContent.ofJson(objectMapper.writeValueAsBytes(list));
    }

    @Override
    public <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference)
        throws IOException {
        byte[] bytes = IOUtils.toByteArray(fileContentInputStream);
        if (bytes.length == 0) {
            return Collections.emptyList();
        }
        return objectMapper.readValue(bytes, convertToTypeReferenceList(typeReference));
    }

    @Override
    public <T> List<T> readFileContent(InputStream fileContentInputStream,
        TypeReference<T> typeReference, Consumer<ObjectNode> valueModifier) throws IOException {

        ArrayNode jsonElements = objectMapper.readValue(fileContentInputStream, ArrayNode.class);
        List<T> results = new ArrayList<>(jsonElements.size());

        for (JsonNode element : jsonElements) {
            ObjectNode objectNode = (ObjectNode) element;
            valueModifier.accept(objectNode);

            T object = objectMapper.convertValue(objectNode, typeReference);
            results.add(object);
        }

        return Collections.unmodifiableList(results);
    }

    private <T> TypeReference<List<T>> convertToTypeReferenceList(TypeReference<T> elementTypeReference) {
        JavaType elementType = objectMapper.constructType(elementTypeReference.getType());

        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);

        return new TypeReference<>() {
            @Override
            public JavaType getType() {
                return collectionType;
            }
        };
    }

}
