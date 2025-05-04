package com.extole.reporting.rest.file.assets;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FileAssetEncryptionRestException extends ExtoleRestException {

    public static final ErrorCode<FileAssetEncryptionRestException> MISSING_PGP_EXTOLE_KEY =
        new ErrorCode<>("missing_extole_public_key", 400, "Cannot decrypt file as extole public key is not created");

    public static final ErrorCode<FileAssetEncryptionRestException> DECRYPTION_ERROR =
        new ErrorCode<>("decryption_error", 400, "Could not decrypt file");

    public FileAssetEncryptionRestException(String uniqueId, ErrorCode<FileAssetEncryptionRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
