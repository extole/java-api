package com.extole.client.rest.campaign.migration;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class GlobalCampaignMigrationRestException extends ExtoleRestException {

    public static final ErrorCode<GlobalCampaignMigrationRestException> GLOBAL_NOT_PUBLISHED =
        new ErrorCode<>("global_campaign_not_published", 400,
            "Global campaign should have no draft changes. Publish or discard all changes before migrating");
    public static final ErrorCode<GlobalCampaignMigrationRestException> SOURCE_CAMPAIGN_NOT_FOUND =
        new ErrorCode<>("source_campaign_not_found", 400, "Source campaign not found");

    public GlobalCampaignMigrationRestException(String uniqueId, ErrorCode<GlobalCampaignMigrationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
