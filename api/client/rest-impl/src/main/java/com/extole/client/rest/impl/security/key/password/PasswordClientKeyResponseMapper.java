package com.extole.client.rest.impl.security.key.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.security.key.GenericClientKeyResponseMapper;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class PasswordClientKeyResponseMapper extends GenericClientKeyResponseMapper {

    @Autowired
    public PasswordClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
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
