package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignRestException extends ExtoleRestException {
    public static final ErrorCode<CampaignRestException> INVALID_CAMPAIGN_ID = new ErrorCode<>(
        "invalid_campaign_id", 400, "Invalid Campaign Id", "campaign_id");

    public static final ErrorCode<CampaignRestException> INVALID_CAMPAIGN_VERSION = new ErrorCode<>(
        "invalid_campaign_version", 400, "Invalid Campaign Version", "version");

    public static final ErrorCode<CampaignRestException> INVALID_DATE = new ErrorCode<>(
        "date_invalid", 400, "Date is invalid or not ISO-8601 compliant");

    public static final ErrorCode<CampaignRestException> UNLOCK_MISSING_LOCK_TYPES = new ErrorCode<>(
        "lock_missing_lock_types", 400, "Unable to unlock, lock types should be specified", "campaign_id");

    public static final ErrorCode<CampaignRestException> MISSING_LOCK_TYPES = new ErrorCode<>(
        "unlock_missing_lock_types", 400, "Unable to lock, lock types should be specified", "campaign_id");

    public static final ErrorCode<CampaignRestException> CAMPAIGN_LOCKED =
        new ErrorCode<>("campaign_locked", 400, "Could not perform the operation. The campaign is locked",
            "campaign_id", "campaign_locks");

    public static final ErrorCode<CampaignRestException> CAMPAIGN_VERSION_MALFORMED =
        new ErrorCode<>("campaign_version_malformed", 400, "Campaign version is malformed");

    public static final ErrorCode<CampaignRestException> CONCURRENT_UPDATE =
        new ErrorCode<>("campaign_concurrent_update", 400,
            "Could not perform the operation. Campaign is currently updated by other process/user",
            "campaign_id", "version");

    public static final ErrorCode<CampaignRestException> CAMPAIGN_HAS_PENDING_CHANGES =
        new ErrorCode<>("campaign_has_pending_changes", 400,
            "Could not perform the operation. Campaign has pending changes", "campaign_id");

    public static final ErrorCode<CampaignRestException> INVALID_CAMPAIGN_STATE = new ErrorCode<>(
        "invalid_campaign_state", 400, "Invalid campaign state", "campaign_id", "expected_state", "current_state");

    public CampaignRestException(String uniqueId, ErrorCode<CampaignRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
