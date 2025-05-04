package com.extole.client.rest.campaign.migration;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignMigrationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignMigrationRestException> MIGRATION_FAILED =
        new ErrorCode<>("migration_failed", 400, "Migration failed", "client_id", "campaign_id", "stacktrace");

    public static final ErrorCode<CampaignMigrationRestException> UNIDENTIFIED_ASSET =
        new ErrorCode<>("unidentified_asset", 400, "Unidentified asset", "filename", "step_mapping_name");

    public static final ErrorCode<CampaignMigrationRestException> MIGRATION_WAS_ALREADY_STARTED =
        new ErrorCode<>("migration_was_already_started", 400,
            "Migration for this campaign was started in the past and is still running", "campaign_id");

    public CampaignMigrationRestException(String uniqueId, ErrorCode<CampaignMigrationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
