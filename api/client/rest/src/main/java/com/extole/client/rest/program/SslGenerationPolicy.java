package com.extole.client.rest.program;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum SslGenerationPolicy {
    GENERATE, GENERATE_FORCE, PROVIDED, DISABLED
}
