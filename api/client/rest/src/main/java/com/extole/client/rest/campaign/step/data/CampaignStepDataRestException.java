package com.extole.client.rest.campaign.step.data;

import java.util.Map;

import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignStepDataRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignStepDataRestException> INVALID_STEP_DATA_ID =
        new ErrorCode<>("invalid_step_data_id", 400, "Invalid step data id", "step_id", "step_data_id");

    public CampaignStepDataRestException(String uniqueId, ErrorCode<CampaignRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
