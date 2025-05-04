package com.extole.client.rest.security.key;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ClientKeyType {
    JWT, PGP, PGP_EXTOLE, SSH, PASSWORD, WEBHOOK
}
