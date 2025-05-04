package com.extole.client.rest.campaign.controller;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignJourneyEntryValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignJourneyEntryValidationRestException> DUPLICATE_JOURNEY_ENTRY =
        new ErrorCode<>("campaign_journey_entry_duplicate_journey_entry", 400,
            "Two journey entries can't have the same journey name", "journey_name");

    public static final ErrorCode<CampaignJourneyEntryValidationRestException> INVALID_TRIGGER_PHASE =
        new ErrorCode<>("campaign_journey_entry_invalid_trigger_phase", 400,
            "Only triggers with matching phase are allowed");

    public static final ErrorCode<CampaignJourneyEntryValidationRestException> NULL_JOURNEY_KEY_NAME =
        new ErrorCode<>("campaign_journey_entry_null_journey_key_name", 400, "Journey key name cannot be null");

    public static final ErrorCode<CampaignJourneyEntryValidationRestException> JOURNEY_KEY_NAME_INVALID_LENGTH =
        new ErrorCode<>("campaign_journey_entry_journey_key_name_invalid_length", 400,
            "Journey key name cannot be blank or exceed maximum length", "max_length", "name");

    public static final ErrorCode<CampaignJourneyEntryValidationRestException> NULL_JOURNEY_KEY_VALUE =
        new ErrorCode<>("campaign_journey_entry_null_journey_key_value", 400, "Journey key value cannot be null");

    public CampaignJourneyEntryValidationRestException(
        String uniqueId,
        ErrorCode<CampaignJourneyEntryValidationRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
