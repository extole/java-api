package com.extole.client.rest.campaign.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignProgramRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignProgramRestException> INVALID_PROGRAM_TYPE =
        new ErrorCode<>("invalid_program_type", 400,
            "Invalid program type, length must be between 1 and 128", "program_type");

    public static final ErrorCode<CampaignProgramRestException> PROGRAM_TYPE_EMPTY =
        new ErrorCode<>("program_type_empty", 400,
            "Program type cannot be empty or null");

    public CampaignProgramRestException(String uniqueId, ErrorCode<CampaignProgramRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
