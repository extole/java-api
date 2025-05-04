package com.extole.client.rest.impl.auth.provider.type;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.auth.provider.type.AuthProviderTypeProtocol;
import com.extole.client.rest.auth.provider.type.AuthProviderTypeResponse;
import com.extole.client.rest.client.Scope;
import com.extole.model.entity.auth.provider.type.AuthProviderType;

@Component
public class AuthProviderTypeRestMapper {

    public AuthProviderTypeResponse toAuthProviderTypeResponse(AuthProviderType authProviderType, ZoneId timeZone) {
        return new AuthProviderTypeResponse(authProviderType.getId().getValue(),
            authProviderType.getName(),
            AuthProviderTypeProtocol.valueOf(authProviderType.getAuthProviderTypeProtocol().name()),
            authProviderType.getScopes().stream().map(Enum::name).map(Scope::valueOf).collect(Collectors.toSet()),
            authProviderType.getDescription().orElse(null),
            authProviderType.getCreatedAt().atZone(timeZone),
            authProviderType.getUpdatedAt().atZone(timeZone));
    }

}
