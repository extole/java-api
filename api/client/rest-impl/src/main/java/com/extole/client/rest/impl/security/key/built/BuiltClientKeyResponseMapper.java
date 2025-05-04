package com.extole.client.rest.impl.security.key.built;

import java.time.ZoneId;

import com.extole.client.rest.security.key.built.BuiltClientKeyResponse;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.built.BuiltClientKey;

public interface BuiltClientKeyResponseMapper<KEY extends BuiltClientKey, RESPONSE extends BuiltClientKeyResponse> {

    RESPONSE toResponse(KEY clientKey, ZoneId timeZone);

    ClientKey.Algorithm getAlgorithm();

}
