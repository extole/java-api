package com.extole.api.impl.model;

import com.extole.api.model.ClientSettings;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.client.ClientSettingsPojo;

final class ClientSettingsImpl implements ClientSettings {
    private final ClientSettingsPojo clientSettings;

    ClientSettingsImpl(ClientSettingsPojo clientSettings) {
        this.clientSettings = clientSettings;
    }

    @Override
    public String getId() {
        return clientSettings.getId().getValue();
    }

    @Override
    public String getTimezone() {
        return clientSettings.getTimeZone().getId();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
