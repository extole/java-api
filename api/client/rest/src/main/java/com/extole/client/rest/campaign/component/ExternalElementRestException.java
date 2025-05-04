package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ExternalElementRestException extends ExtoleRestException {

    public static final ErrorCode<ExternalElementRestException> EXTERNAL_ELEMENT_IN_USE = new ErrorCode<>(
        "external_element_in_use", 400, "External element is in use",
        "external_element_id", "entity_type", "associations");

    public ExternalElementRestException(String uniqueId, ErrorCode<BuildClientKeyRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
