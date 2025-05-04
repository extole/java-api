package com.extole.client.rest.campaign.controller.action.incentivize;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum IncentivizeActionOverrideType {
    CART_VALUE,
    CHANNEL,
    MESSAGE,
    EMAIL,
    EVENT_TYPE,
    HTTP_HEADERS,
    DATA,
    PARTNER_EVENT_ID,
    PARTNER_USER_ID,
    PERSON_ID,
    RECIPIENT,
    SOURCE,
    SOURCE_URL,
    SHARE_ID,
    VIA_ZONE,
    ZONE,
    API_VERSION,
    EVENT_ID
}
