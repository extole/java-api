package com.extole.client.rest.person.share;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonSharesListRestException extends ExtoleRestException {

    public static final ErrorCode<PersonSharesListRestException> INVALID_DATA_VALUE = new ErrorCode<>(
        "data_values_invalid", 400, "Data values parameter format is invalid", "data_values");
    public static final ErrorCode<PersonSharesListRestException> INVALID_PARTNER_IDS = new ErrorCode<>(
        "partner_ids_invalid", 400, "Partner ids parameter format is invalid", "partner_ids");

    public PersonSharesListRestException(String uniqueId, ErrorCode<PersonSharesListRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
