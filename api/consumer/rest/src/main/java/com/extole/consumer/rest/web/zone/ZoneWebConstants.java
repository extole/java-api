package com.extole.consumer.rest.web.zone;

public final class ZoneWebConstants {

    public static final String EMAIL_HEADER_TO = "X-Extole-Email-To";
    public static final String EMAIL_HEADER_FROM = "X-Extole-Email-From";
    public static final String EMAIL_HEADER_SUBJECT = "X-Extole-Email-Subject";
    public static final String EMAIL_HEADER_CC = "X-Extole-Email-Cc";
    public static final String EMAIL_HEADER_BCC = "X-Extole-Email-Bcc";
    public static final String EMAIL_HEADER_REPLY_TO = "X-Extole-Email-Reply-To";
    public static final String EMAIL_HEADER_DO_NOT_SEND = "X-Extole-Email-Do-Not-Send";
    public static final String EMAIL_HEADER_OPTOUT_LIST_NAME = "X-Extole-Email-Optout-List";
    public static final String EMAIL_HEADER_CUSTOM_PREFIX = "X-Extole-Email-Header-";

    public static final String EMAIL_CUSTOM_HEADER_PARTNER_CODE = "Partner-Code";

    public static final String EMAIL_PARAMETER_SHARE_ID = "share_id";
    public static final String EMAIL_PARAMETER_REWARD_ID = "reward_id";
    public static final String EMAIL_PARAMETER_VERIFICATION_CODE = "code";
    public static final String EMAIL_PARAMETER_CAUSE_EVENT_ID = "cause_event_id";

    public static final String ACCESS_TOKEN_PARAMETER_NAME = "access_token";

    @Deprecated // TODO will remove zone_id, and x-zone-id, ENG-9515
    public static final String ZONE_ID_PARAMETER_NAME = "zone_id";

    private ZoneWebConstants() {
    }
}
