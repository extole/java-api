package com.extole.client.rest.impl.logo;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.logo.LogoResponse;
import com.extole.model.entity.logo.Logo;

@Component
public class LogoRestMapper {
    public LogoResponse toLogoResponse(Logo logo, ZoneId timeZone) {
        return new LogoResponse(logo.getId().getValue(), logo.getCreatedDate().atZone(timeZone),
            logo.getUpdatedDate().atZone(timeZone));
    }
}
