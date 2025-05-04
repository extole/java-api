package com.extole.client.rest.impl.auth.provider.type.extole;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.auth.provider.type.extole.ExtoleAuthProviderTypeResponse;
import com.extole.client.rest.client.Scope;
import com.extole.model.service.auth.provider.type.extole.ExtoleAuthProviderType;

@Component
public class ExtoleAuthProviderTypeRestMapper {

    public ExtoleAuthProviderTypeResponse toExtoleAuthProviderTypeResponse(
        ExtoleAuthProviderType extoleAuthProviderType,
        ZoneId timeZone) {
        return new ExtoleAuthProviderTypeResponse(extoleAuthProviderType.getId().getValue(),
            extoleAuthProviderType.getName(),
            extoleAuthProviderType.getScopes().stream().map(Enum::name).map(Scope::valueOf)
                .collect(Collectors.toSet()),
            extoleAuthProviderType.getDescription().orElse(null),
            extoleAuthProviderType.getCreatedAt().atZone(timeZone),
            extoleAuthProviderType.getUpdatedAt().atZone(timeZone));
    }

}
