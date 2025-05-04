package com.extole.api.impl.campaign;

import java.util.Map;
import java.util.Optional;

import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Socket;

public interface VariablesSupplier {
    Variables supplyDefault();

    Variables supplyForComponent(ComponentWithVersion component);

    interface Variables {
        Map<String, SettingWithComponent> getResolved();

        Map<VariableKey, FlatVariable> getFlat();
    }

    interface SettingWithComponent {

        Setting getSetting();

        ComponentWithVersion getOwner();

        Optional<Socket> getSocket();

    }
}
