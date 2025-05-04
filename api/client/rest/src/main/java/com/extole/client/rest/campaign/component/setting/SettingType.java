package com.extole.client.rest.campaign.component.setting;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum SettingType {

    STRING,
    BOOLEAN,
    INTEGER,
    STRING_LIST,
    INTEGER_LIST,
    DELAY_LIST,
    JSON,
    COLOR,
    IMAGE,
    FONT,
    URL,
    CLIENT_KEY_FLOW,
    CLIENT_KEY,
    EXTOLE_CLIENT_KEY,
    REWARD_SUPPLIER_ID,
    AUDIENCE_ID,
    ENUM,
    ENUM_LIST,
    MULTI_SOCKET,
    PARTNER_ENUM_LIST,
    HTML,
    REWARD_SUPPLIER_ID_LIST

}
