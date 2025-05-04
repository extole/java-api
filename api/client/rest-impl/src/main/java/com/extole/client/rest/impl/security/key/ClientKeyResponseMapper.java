package com.extole.client.rest.impl.security.key;

import java.time.ZoneId;

import com.extole.client.rest.security.key.ClientKeyResponse;
import com.extole.model.entity.client.security.key.ClientKey;

public interface ClientKeyResponseMapper<KEY extends ClientKey, RESPONSE extends ClientKeyResponse> {

    RESPONSE toResponse(KEY clientKey, ZoneId timeZone);

    ClientKey.Algorithm getAlgorithm();

}
