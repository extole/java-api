package com.extole.file.parser.content;

import java.io.IOException;
import java.util.Objects;

import com.google.common.io.ByteSource;

public final class ParsedFileContent {
    private final ByteSource contentByteSource;
    private final FileContentParseFormat fileContentParseFormat;

    static ParsedFileContent ofCsv(byte[] contentAsBytes) {
        return new ParsedFileContent(Objects.requireNonNull(contentAsBytes), FileContentParseFormat.CSV);
    }

    static ParsedFileContent ofJson(byte[] contentAsBytes) {
        return new ParsedFileContent(Objects.requireNonNull(contentAsBytes), FileContentParseFormat.JSON);
    }

    static ParsedFileContent ofXlsx(byte[] contentAsBytes) {
        return new ParsedFileContent(Objects.requireNonNull(contentAsBytes), FileContentParseFormat.XLSX);
    }

    private ParsedFileContent(byte[] contentAsBytes, FileContentParseFormat fileContentParseFormat) {
        this.contentByteSource = ByteSource.wrap(contentAsBytes);
        this.fileContentParseFormat = fileContentParseFormat;
    }

    public byte[] getContentAsBytes() {
        try {
            return contentByteSource.read();
        } catch (IOException e) {
            throw new RuntimeException("Should never happen", e);
        }
    }

    public FileContentParseFormat getFileParserContentFormat() {
        return fileContentParseFormat;
    }
}
