package com.extole.common.rest.exception;

import java.util.Map;

public class FileFormatRestException extends ExtoleRestException {

    public static final ErrorCode<FileFormatRestException> UNSUPPORTED_FILE_FORMAT =
        new ErrorCode<>("unsupported_file_format", 415, "Request had an unsupported file format", "file_extension");

    public static final ErrorCode<FileFormatRestException> INVALID_FILE_CONTENT =
        new ErrorCode<>("invalid_file_content", 400, "File content is invalid");

    public FileFormatRestException(String uniqueId,
        ErrorCode<FileFormatRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
