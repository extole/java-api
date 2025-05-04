package com.extole.client.rest.blocks;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ListType {
    USER_AGENT, NORMALIZED_EMAIL, EMAIL_DOMAIN, CIDR
}
