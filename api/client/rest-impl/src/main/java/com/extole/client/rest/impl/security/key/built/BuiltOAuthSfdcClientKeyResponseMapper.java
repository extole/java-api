package com.extole.client.rest.impl.security.key.built;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyType;
import com.extole.client.rest.security.key.built.BuiltOAuthSfdcClientKeyResponse;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.built.BuiltOAuthSfdcClientKey;
import com.extole.model.service.encryption.EncryptionService;

@Component
public class BuiltOAuthSfdcClientKeyResponseMapper
    extends BuiltBaseClientKeyResponseMapper<BuiltOAuthSfdcClientKey, BuiltOAuthSfdcClientKeyResponse> {

    @Autowired
    public BuiltOAuthSfdcClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        super(encryptionService);
    }

    @Override
    public BuiltOAuthSfdcClientKeyResponse toResponse(BuiltOAuthSfdcClientKey clientKey, ZoneId timeZone) {
        return new BuiltOAuthSfdcClientKeyResponse(clientKey.getId().getValue(),
            clientKey.getName(),
            ClientKeyAlgorithm.valueOf(clientKey.getAlgorithm().name()),
            maskClientKey(clientKey),
            ClientKeyType.valueOf(clientKey.getType().name()),
            clientKey.getDescription(),
            clientKey.getPartnerKeyId(),
            ZonedDateTime.ofInstant(clientKey.getCreatedAt(), timeZone),
            ZonedDateTime.ofInstant(clientKey.getUpdatedAt(), timeZone),
            clientKey.getAuthorizationUrl().toString(),
            clientKey.getOAuthClientId(),
            clientKey.getScope(),
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
        return ClientKey.Algorithm.OAUTH_SFDC;
    }

}
