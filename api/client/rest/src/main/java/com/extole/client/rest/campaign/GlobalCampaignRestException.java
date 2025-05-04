package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class GlobalCampaignRestException extends ExtoleRestException {

    public static final ErrorCode<GlobalCampaignRestException> GLOBAL_RENAME =
        new ErrorCode<>("campaign_global_rename", 400, "Global campaign renaming is restricted");

    public static final ErrorCode<GlobalCampaignRestException> GLOBAL_ARCHIVE =
        new ErrorCode<>("campaign_global_archive", 400, "Global campaign archiving is restricted");

    public static final ErrorCode<GlobalCampaignRestException> GLOBAL_STATE_CHANGE_EXCEPTION =
        new ErrorCode<>("campaign_global_state_change", 400,
            "Global campaign state change is restricted. It can only be LIVE");

    public GlobalCampaignRestException(String uniqueId, ErrorCode<GlobalCampaignRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
