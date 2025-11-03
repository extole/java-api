package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignArchiveRestException> INVALID_CAMPAIGN_ARCHIVE = new ErrorCode<>(
        "invalid_campaign_archive", 403, "Invalid campaign archive", "details");

    public static final ErrorCode<CampaignArchiveRestException> MISSING_CAMPAIGN_JSON = new ErrorCode<>(
        "missing_campaign_json", 403, "Missing campaign.json file in campaign archive");

    public CampaignArchiveRestException(String uniqueId, ErrorCode<CampaignArchiveRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
