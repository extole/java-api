package com.extole.client.rest.impl.security.key.built;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.built.BuiltSslPkcs12ClientKeyResponse;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.built.BuiltSslPkcs12ClientKey;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class BuiltSslPkcs12ClientKeyResponseMapper
    extends BuiltBaseClientKeyResponseMapper<BuiltSslPkcs12ClientKey, BuiltSslPkcs12ClientKeyResponse> {

    private final EncryptionService<byte[]> encryptionService;

    public BuiltSslPkcs12ClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        super(encryptionService);
        this.encryptionService = encryptionService;
    }

    @Override
    public BuiltSslPkcs12ClientKeyResponse toResponse(BuiltSslPkcs12ClientKey clientKey, ZoneId timeZone) {
        return new BuiltSslPkcs12ClientKeyResponse(clientKey.getId().getValue(),
            clientKey.getName(),
            ClientKeyAlgorithm.valueOf(clientKey.getAlgorithm().name()),
            maskClientKey(clientKey),
            maskPassword(clientKey),
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
        return ClientKey.Algorithm.SSL_PKCS_12;
    }

    private Optional<String> maskPassword(BuiltSslPkcs12ClientKey clientKey) {
        Optional<byte[]> decryptedPassword =
            clientKey.getEncryptedPassword().map(password -> encryptionService.decrypt(password));
        return decryptedPassword.map(bytes -> mask(new String(bytes, StandardCharsets.ISO_8859_1)));

    }
}
