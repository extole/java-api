package com.extole.client.rest.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum TokenTransmissionChannel {
    HEADER, QUERY_PARAM, COOKIE, REQUEST_BODY
}
