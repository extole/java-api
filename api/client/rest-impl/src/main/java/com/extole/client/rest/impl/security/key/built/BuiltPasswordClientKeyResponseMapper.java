package com.extole.client.rest.impl.security.key.built;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class BuiltPasswordClientKeyResponseMapper extends BuiltGenericClientKeyResponseMapper {

    @Autowired
    public BuiltPasswordClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        super(encryptionService);
    }

    protected String maskClientKey(ClientKey clientKey) {
        return MASK;
    }

    @Override
    public ClientKey.Algorithm getAlgorithm() {
        return ClientKey.Algorithm.PASSWORD;
    }

}
