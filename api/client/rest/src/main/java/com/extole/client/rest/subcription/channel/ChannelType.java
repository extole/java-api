package com.extole.client.rest.subcription.channel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ChannelType {
    SLACK, EMAIL, EXTOLE_CLIENT_SLACK, WEBHOOK, THIRD_PARTY_EMAIL
}
