package com.extole.file.parser.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface FileContentReadStrategy {

    <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference) throws IOException;

    <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference,
        Consumer<ObjectNode> valueModifier) throws IOException;

}
