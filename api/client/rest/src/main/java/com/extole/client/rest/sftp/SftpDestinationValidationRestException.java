package com.extole.client.rest.sftp;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SftpDestinationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_NAME = new ErrorCode<>(
        "sftp_destination_invalid_name", 400,
        "Name is not valid. Name is valid if it has a length of max 255 characters and contains only ASCII " +
            "printable characters.",
        "name");

    public static final ErrorCode<SftpDestinationValidationRestException> DUPLICATED_NAME = new ErrorCode<>(
        "sftp_destination_duplicated_name", 400, "Sftp destination with such a name already exists", "name");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_EXTERNAL_USERNAME = new ErrorCode<>(
        "external_sftp_destination_invalid_username", 400,
        "Username is not valid for external sftp destination. A valid username must not be empty or blank, not " +
            "longer than 255 characters and contain only ASCII printable characters",
        "username");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_LOCAL_USERNAME = new ErrorCode<>(
        "local_sftp_destination_invalid_username", 400,
        "Username is not valid for local sftp destination. A valid username must start with the client shortname. " +
            "It can only contain letters, numbers and the following chars: \"_\", \"@\", \"-\", \".\". " +
            "The length must be between 3 and 100 characters.",
        "username");

    public static final ErrorCode<SftpDestinationValidationRestException> DUPLICATED_USERNAME = new ErrorCode<>(
        "sftp_destination_duplicated_username", 400, "Sftp destination with such username already exists", "username");

    public static final ErrorCode<SftpDestinationValidationRestException> MISSING_USERNAME = new ErrorCode<>(
        "sftp_destination_missing_username", 400, "Username is mandatory");

    public static final ErrorCode<SftpDestinationValidationRestException> MISSING_KEY_ID =
        new ErrorCode<>("sftp_destination_missing_key_id", 400, "Key id is mandatory");

    public static final ErrorCode<SftpDestinationValidationRestException> CLIENT_KEY_NOT_FOUND =
        new ErrorCode<>("sftp_destination_client_key_not_found", 400, "Client key not found", "client_key_id");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_KEY_TYPE =
        new ErrorCode<>("sftp_destination_invalid_key_type", 400, "Invalid key type", "client_key_id", "type",
            "supported_types");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_KEY_CONTENT =
        new ErrorCode<>("sftp_destination_invalid_key_content", 400,
            "Provided client key is not a valid ssh public key. It must start with \"ssh-rsa\" and be base64 encoded",
            "client_key_id");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_DROPBOX_PATH =
        new ErrorCode<>("sftp_destination_invalid_dropbox_path", 400,
            "Dropbox path is not valid. Dropbox path is valid if not empty or blank, not longer than 255 characters" +
                " and is a valid system path",
            "path");

    public static final ErrorCode<SftpDestinationValidationRestException> MISSING_HOST =
        new ErrorCode<>("sftp_destination_missing_host", 400, "Host is mandatory");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_HOST =
        new ErrorCode<>("sftp_destination_invalid_host", 400, "Host is not a valid domain", "host");

    public static final ErrorCode<SftpDestinationValidationRestException> INVALID_PORT = new ErrorCode<>(
        "sftp_destination_invalid_port", 400, "Port is not valid", "port");

    public SftpDestinationValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
