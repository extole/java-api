package com.extole.api.client.security.key;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ClientKey {

    String getAlgorithm();

    String getKey() throws ClientKeyApiException;

}
