package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;

@SuppressWarnings("rawtypes")
@Component
public class SettingUploaderRegistry {

    private final Map<SettingType, SettingUploader> settingUploaders;
    private final DefaultSettingUploader defaultSettingUploader;

    public SettingUploaderRegistry(List<SettingUploader<?>> settingUploaders,
        DefaultSettingUploader defaultSettingUploader) {
        this.settingUploaders = uploadersAsMap(settingUploaders);
        this.defaultSettingUploader = defaultSettingUploader;
    }

    public Optional<SettingUploader> getUploader(SettingType settingType) {
        return Optional.ofNullable(settingUploaders.get(settingType));

    }

    public DefaultSettingUploader getDefaultUploader() {
        return defaultSettingUploader;
    }

    private Map<SettingType, SettingUploader> uploadersAsMap(
        List<SettingUploader<?>> settingUploaders) {
        ImmutableMap.Builder<SettingType, SettingUploader> builder = ImmutableMap.builder();
        settingUploaders.forEach(item -> item.getSettingTypes()
            .forEach(settingType -> builder.put(settingType, item)));
        return builder.build();
    }
}
