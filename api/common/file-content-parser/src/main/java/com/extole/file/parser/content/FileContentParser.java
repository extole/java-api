package com.extole.file.parser.content;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.evaluateable.ValidEvaluatableModule;

public final class FileContentParser {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance()
        .copy()
        .registerModule(new ValidEvaluatableModule())
        .setSerializationInclusion(JsonInclude.Include.ALWAYS);

    private static final Map<FileContentParseFormat, FileContentWriteStrategy> WRITE_STRATEGIES_BY_FILE_FORMATS =
        ImmutableMap
            .<FileContentParseFormat, FileContentWriteStrategy>builder()
            .put(FileContentParseFormat.JSON, new JsonFileContentReadWriteStrategy(OBJECT_MAPPER))
            .put(FileContentParseFormat.XLSX, new XlsxFileContentReadWriteStrategy(OBJECT_MAPPER))
            .put(FileContentParseFormat.CSV, new CsvFileContentReadWriteStrategy(OBJECT_MAPPER))
            .build();
    private static final Map<FileContentParseFormat, FileContentReadStrategy> READ_STRATEGIES_BY_FILE_FORMATS =
        ImmutableMap
            .<FileContentParseFormat, FileContentReadStrategy>builder()
            .put(FileContentParseFormat.JSON, new JsonFileContentReadWriteStrategy(OBJECT_MAPPER))
            .put(FileContentParseFormat.XLSX, new XlsxFileContentReadWriteStrategy(OBJECT_MAPPER))
            .put(FileContentParseFormat.CSV, new CsvFileContentReadWriteStrategy(OBJECT_MAPPER))
            .build();

    private FileContentParser() {
    }

    public static FileContentWriteStrategy getWriteStrategy(FileContentParseFormat fileContentParseFormat) {
        return WRITE_STRATEGIES_BY_FILE_FORMATS.get(fileContentParseFormat);
    }

    public static FileContentReadStrategy getReadStrategy(FileContentParseFormat fileContentParseFormat) {
        return READ_STRATEGIES_BY_FILE_FORMATS.get(fileContentParseFormat);
    }

}
