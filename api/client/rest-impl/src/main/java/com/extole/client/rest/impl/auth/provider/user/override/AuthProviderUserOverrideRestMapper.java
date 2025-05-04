package com.extole.client.rest.impl.auth.provider.user.override;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.auth.provider.user.override.AuthProviderUserOverrideResponse;
import com.extole.model.entity.client.auth.provider.user.override.AuthProviderUserOverride;

@Component
public class AuthProviderUserOverrideRestMapper {

    public AuthProviderUserOverrideResponse
        toAuthProviderUserOverrideResponse(AuthProviderUserOverride authProviderUserOverride, ZoneId timeZone) {
        return new AuthProviderUserOverrideResponse(authProviderUserOverride.getId().getValue(),
            authProviderUserOverride.getName(),
            authProviderUserOverride.getUserId().getValue(),
            authProviderUserOverride.isAuthProviderEnabledForUser().booleanValue(),
            authProviderUserOverride.getDescription().orElse(null),
            authProviderUserOverride.getCreatedAt().atZone(timeZone),
            authProviderUserOverride.getUpdatedAt().atZone(timeZone));
    }

}
