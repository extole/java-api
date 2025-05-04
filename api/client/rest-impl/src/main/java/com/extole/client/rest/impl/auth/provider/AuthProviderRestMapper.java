package com.extole.client.rest.impl.auth.provider;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.auth.provider.AuthProviderResponse;
import com.extole.model.entity.client.auth.provider.AuthProvider;

@Component
public class AuthProviderRestMapper {

    public AuthProviderResponse toAuthProviderResponse(AuthProvider authProvider, ZoneId timeZone) {
        return new AuthProviderResponse(authProvider.getId().getValue(),
            authProvider.getName(),
            authProvider.getAuthProviderType().getId().getValue(),
            authProvider.isDefaultEnabledForAllUsers(),
            authProvider.getDescription().orElse(null),
            authProvider.getCreatedAt().atZone(timeZone),
            authProvider.getUpdatedAt().atZone(timeZone));
    }

}
