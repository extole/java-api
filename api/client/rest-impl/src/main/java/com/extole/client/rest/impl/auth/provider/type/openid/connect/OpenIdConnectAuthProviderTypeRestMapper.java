package com.extole.client.rest.impl.auth.provider.type.openid.connect;

import java.time.ZoneId;
import java.util.Base64;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.auth.provider.type.openid.connect.Category;
import com.extole.client.rest.auth.provider.type.openid.connect.OpenIdConnectAuthProviderTypeResponse;
import com.extole.client.rest.client.Scope;
import com.extole.model.service.auth.provider.type.openid.connect.OpenIdConnectAuthProviderType;

@Component
public class OpenIdConnectAuthProviderTypeRestMapper {

    private static final int LAST_VISIBLE_CHARACTERS = 3;
    private static final String MASK = "*****";

    public OpenIdConnectAuthProviderTypeResponse
        toOpenIdConnectAuthProviderTypeResponse(OpenIdConnectAuthProviderType openIdConnectAuthProviderType,
            ZoneId timeZone) {
        return new OpenIdConnectAuthProviderTypeResponse(openIdConnectAuthProviderType.getId().getValue(),
            openIdConnectAuthProviderType.getName(),
            openIdConnectAuthProviderType.getDomain(),
            openIdConnectAuthProviderType.getApplicationId(),
            mask(Base64.getEncoder().encodeToString(openIdConnectAuthProviderType.getApplicationSecret())),
            openIdConnectAuthProviderType.getCustomParams(),
            openIdConnectAuthProviderType.getScopes().stream().map(Enum::name).map(Scope::valueOf)
                .collect(Collectors.toSet()),
            Category.valueOf(openIdConnectAuthProviderType.getCategory().name()),
            openIdConnectAuthProviderType.getDescription().orElse(null),
            openIdConnectAuthProviderType.getCreatedAt().atZone(timeZone),
            openIdConnectAuthProviderType.getUpdatedAt().atZone(timeZone));
    }

    private String mask(String secret) {
        return secret.charAt(0) + MASK + secret.substring(secret.length() - LAST_VISIBLE_CHARACTERS);
    }

}
