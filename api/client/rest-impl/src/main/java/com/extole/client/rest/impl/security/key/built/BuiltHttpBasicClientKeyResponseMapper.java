package com.extole.client.rest.impl.security.key.built;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.built.BuiltClientKey;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class BuiltHttpBasicClientKeyResponseMapper extends BuiltGenericClientKeyResponseMapper {

    private final EncryptionService<byte[]> encryptionService;

    @Autowired
    public BuiltHttpBasicClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        super(encryptionService);
        this.encryptionService = encryptionService;
    }

    @Override
    protected String maskClientKey(BuiltClientKey clientKey) {
        String encodedKey = encodeToString(encryptionService.decrypt(clientKey.getEncryptedKey()));
        String[] keyParts = encodedKey.split(":", 2);
        return keyParts[0] + ":" + MASK;
    }

    @Override
    public ClientKey.Algorithm getAlgorithm() {
        return ClientKey.Algorithm.HTTP_BASIC;
    }

}
