package com.extole.client.rest.blocks;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum BlockCheckResult {
    BLACKLISTED, WHITELISTED, NOT_LISTED
}
