package com.extole.client.rest.impl.security.key.rsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.security.key.ClientKeyConversionRuntimeException;
import com.extole.client.rest.impl.security.key.GenericClientKeyResponseMapper;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.GenericClientKeyResponse;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.ClientKey.Type;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class RSAClientKeyResponseMapper extends GenericClientKeyResponseMapper {

    private static final String OPENSSH_PUBLIC_KEY_PREFIX = "ssh-rsa ";

    private final EncryptionService<byte[]> encryptionService;

    @Autowired
    public RSAClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        super(encryptionService);
        this.encryptionService = encryptionService;
    }

    @Override
    public GenericClientKeyResponse toResponse(ClientKey clientKey, ZoneId timeZone) {
        byte[] decryptedKey = encryptionService.decrypt(clientKey.getEncryptedKey());
        String key = encodeToString(decryptedKey);

        if (clientKey.getType() == Type.SSH) {
            key = maskSshKey(key);
        } else if (clientKey.getType() == Type.PGP_EXTOLE) {
            key = toPemFormat(decryptedKey);
        }

        return new GenericClientKeyResponse(
            clientKey.getId().getValue(),
            clientKey.getName(),
            ClientKeyAlgorithm.valueOf(clientKey.getAlgorithm().name()),
            key,
            ClientKeyType.valueOf(clientKey.getType().name()),
            clientKey.getDescription(),
            clientKey.getPartnerKeyId(),
            ZonedDateTime.ofInstant(clientKey.getCreatedAt(), timeZone),
            ZonedDateTime.ofInstant(clientKey.getUpdatedAt(), timeZone),
            clientKey.getTags(),
            clientKey.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            clientKey.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public ClientKey.Algorithm getAlgorithm() {
        return ClientKey.Algorithm.RSA;
    }

    private String toPemFormat(byte[] plainKey) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            try (
                ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream)) {
                armoredOutputStream.write(plainKey);
            }
            return outputStream.toString(StandardCharsets.ISO_8859_1.name());
        } catch (IOException e) {
            throw new ClientKeyConversionRuntimeException("Couldn't get PEM formatted key", e);
        }
    }

    private String maskSshKey(String plainKey) {
        if (plainKey.startsWith(OPENSSH_PUBLIC_KEY_PREFIX)) {
            return plainKey;
        }
        return mask(plainKey);
    }
}
