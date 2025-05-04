package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignUpdateRestException extends ExtoleRestException {
    public CampaignUpdateRestException(String uniqueId, ErrorCode<CampaignRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

    public static final ErrorCode<CampaignUpdateRestException> CONCURRENT_UPDATE =
        new ErrorCode<>("campaign_concurrent_update", 400,
            "Could not perform the operation. Campaign is currently updated by other process/user",
            "campaign_id", "version");

    public static final ErrorCode<CampaignUpdateRestException> STALE_VERSION =
        new ErrorCode<>("campaign_stale_version", 400,
            "You can only modify the latest version of the campaign",
            "expected_version", "actual_version", "campaign_id");
}
