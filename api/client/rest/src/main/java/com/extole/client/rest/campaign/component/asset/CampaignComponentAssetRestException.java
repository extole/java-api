package com.extole.client.rest.campaign.component.asset;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignComponentAssetRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignComponentAssetRestException> CAMPAIGN_COMPONENT_ASSET_NOT_FOUND =
        new ErrorCode<>(
            "campaign_component_asset_not_found", 400, "Campaign Component asset not found", "campaign_id",
            "campaign_component_id", "asset_id");

    public CampaignComponentAssetRestException(String uniqueId, ErrorCode<CampaignComponentAssetRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
