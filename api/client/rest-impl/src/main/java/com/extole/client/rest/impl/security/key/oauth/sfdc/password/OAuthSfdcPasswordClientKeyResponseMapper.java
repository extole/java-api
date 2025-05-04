package com.extole.client.rest.impl.security.key.oauth.sfdc.password;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.security.key.BaseClientKeyResponseMapper;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyResponse;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.OAuthSfdcPasswordClientKey;
import com.extole.model.entity.encryption.Encrypted;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class OAuthSfdcPasswordClientKeyResponseMapper
    extends BaseClientKeyResponseMapper<OAuthSfdcPasswordClientKey, OAuthSfdcPasswordClientKeyResponse> {

    private final EncryptionService<byte[]> encryptionService;

    @Autowired
    public OAuthSfdcPasswordClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        super(encryptionService);
        this.encryptionService = encryptionService;
    }

    @Override
    public OAuthSfdcPasswordClientKeyResponse toResponse(OAuthSfdcPasswordClientKey clientKey,
        ZoneId timeZone) {
        return new OAuthSfdcPasswordClientKeyResponse(clientKey.getId().getValue(),
            clientKey.getName(),
            ClientKeyAlgorithm.valueOf(clientKey.getAlgorithm().name()),
            maskClientKey(clientKey),
            ClientKeyType.valueOf(clientKey.getType().name()),
            clientKey.getDescription(),
            clientKey.getPartnerKeyId(),
            ZonedDateTime.ofInstant(clientKey.getCreatedAt(), timeZone),
            ZonedDateTime.ofInstant(clientKey.getUpdatedAt(), timeZone),
            clientKey.getAuthorizationUrl(),
            clientKey.getOAuthClientId(),
            clientKey.getScope(),
            clientKey.getUsername(),
            maskPassword(clientKey.getEncryptedPassword()),
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
        return ClientKey.Algorithm.OAUTH_SFDC_PASSWORD;
    }

    private String maskPassword(Encrypted<byte[]> encryptedPassword) {
        return mask(new String(encryptionService.decrypt(encryptedPassword), StandardCharsets.ISO_8859_1));
    }
}
