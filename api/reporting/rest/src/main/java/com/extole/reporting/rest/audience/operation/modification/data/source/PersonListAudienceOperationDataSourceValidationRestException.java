package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceValidationRestException;

public class PersonListAudienceOperationDataSourceValidationRestException
    extends AudienceOperationDataSourceValidationRestException {

    public static final ErrorCode<
        PersonListAudienceOperationDataSourceValidationRestException> INVALID_IDENTITY_KEY_VALUE =
            new ErrorCode<>("invalid_identity_key_value", 400, "Invalid identity key value", "identity_key_value");

    public PersonListAudienceOperationDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
