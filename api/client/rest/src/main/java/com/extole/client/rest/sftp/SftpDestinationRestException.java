package com.extole.client.rest.sftp;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SftpDestinationRestException extends ExtoleRestException {

    public static final ErrorCode<SftpDestinationRestException> NOT_FOUND =
        new ErrorCode<>("sftp_destination_not_found", 400, "Could not find sftp destination", "sftp_destination_id");

    public static final ErrorCode<SftpDestinationRestException> UNSUPPORTED_OPERATION =
        new ErrorCode<>("unsupported_operation", 400, "Operation is not supported for given sftp destination type",
            "sftp_destination_id", "type");

    public static final ErrorCode<SftpDestinationRestException> USERNAME_EXISTS_ON_SFTP_SERVER = new ErrorCode<>(
        "username_exists_on_sftp_server", 400, "Username already exists on sftp server", "username");

    public SftpDestinationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
