package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignComponentRootValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignComponentRootValidationRestException> ROOT_RENAME =
        new ErrorCode<>("campaign_component_root_rename", 400,
            "Campaign component root rename is restricted");

    public CampaignComponentRootValidationRestException(String uniqueId,
        ErrorCode<CampaignComponentRootValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
