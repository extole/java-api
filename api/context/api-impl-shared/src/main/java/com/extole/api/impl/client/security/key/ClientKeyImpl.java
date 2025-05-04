package com.extole.api.impl.client.security.key;

import java.util.Map;

import com.extole.api.client.security.key.ClientKey;
import com.extole.api.client.security.key.ClientKeyApiException;
import com.extole.key.provider.service.KeyProviderException;
import com.extole.key.provider.service.KeyProviderService;
import com.extole.model.entity.client.security.key.built.BuiltClientKey;

public class ClientKeyImpl implements ClientKey {

    private final KeyProviderService keyProviderService;
    private final com.extole.model.entity.client.security.key.ClientKey clientKey;
    private final String algorithm;

    public ClientKeyImpl(
        KeyProviderService keyProviderService,
        com.extole.model.entity.client.security.key.ClientKey clientKey) {
        this.keyProviderService = keyProviderService;
        this.clientKey = clientKey;
        this.algorithm = clientKey.getAlgorithm().name();
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String getKey() throws ClientKeyApiException {
        try {
            return keyProviderService.getKey(clientKey);
        } catch (KeyProviderException e) {
            BuiltClientKey key = e.getBuiltClientKey();
            Map<String, String> parameters = Map.of("client_key_id", key.getId().getValue(),
                "client_key_name", key.getName(),
                "client_key_algorithm", key.getAlgorithm().name());
            throw new ClientKeyApiException(ClientKeyApiException.ErrorCode.ACCESS_TOKEN_UNAVAILABLE, e, parameters);
        }
    }

}
