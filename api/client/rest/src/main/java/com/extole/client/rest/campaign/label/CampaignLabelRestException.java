package com.extole.client.rest.campaign.label;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignLabelRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignLabelRestException> INVALID_CAMPAIGN_LABEL_NAME =
        new ErrorCode<>("invalid_campaign_label_name", 403, "Invalid campaign label name", "campaign_id", "label_name");

    public CampaignLabelRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
