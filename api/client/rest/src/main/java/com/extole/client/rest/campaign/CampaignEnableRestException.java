package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignEnableRestException extends ExtoleRestException {
    public static final ErrorCode<CampaignEnableRestException> CAMPAIGN_WITH_PENDING_CHANGES = new ErrorCode<>(
        "campaign_has_pending_changes", 403, "Campaign can't be enabled since it has pending changes that are not "
            + "discarded or published",
        "campaign_id");

    public CampaignEnableRestException(String uniqueId, ErrorCode<CampaignEnableRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
