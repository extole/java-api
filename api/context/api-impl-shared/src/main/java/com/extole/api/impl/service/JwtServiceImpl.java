package com.extole.api.impl.service;

import com.extole.api.service.JwtBuilder;
import com.extole.api.service.JwtService;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.key.provider.service.KeyProviderService;
import com.extole.model.shared.client.security.key.jwt.JwtClientKeyCache;

public class JwtServiceImpl implements JwtService {

    private final Id<ClientHandle> clientId;
    private final JwtClientKeyCache jwtClientKeyCache;
    private final KeyProviderService keyProviderService;

    public JwtServiceImpl(Id<ClientHandle> clientId, JwtClientKeyCache jwtClientKeyCache,
        KeyProviderService keyProviderService) {
        super();
        this.clientId = clientId;
        this.jwtClientKeyCache = jwtClientKeyCache;
        this.keyProviderService = keyProviderService;
    }

    @Override
    public JwtBuilder createJwtBuilder() {
        return new JwtBuilderImpl(clientId, jwtClientKeyCache, keyProviderService);
    }
}
