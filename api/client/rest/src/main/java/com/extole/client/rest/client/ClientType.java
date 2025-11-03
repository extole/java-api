package com.extole.client.rest.client;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ClientType {
    CUSTOMER, EX_CUSTOMER, TEST, UNCLASSIFIED, PROSPECT, COMPONENT_LIBRARY
}
