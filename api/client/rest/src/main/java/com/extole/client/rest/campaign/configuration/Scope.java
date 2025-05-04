package com.extole.client.rest.campaign.configuration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum Scope {
    CLIENT_SUPERUSER,
    CLIENT_ADMIN,
    USER_SUPPORT,
    VERIFIED_CONSUMER,
    UPDATE_PROFILE,
    ONE_TIME,
    PASSWORD_RESET,
    BACKEND,
    CLIENT_REPORT_DOWNLOAD
}
