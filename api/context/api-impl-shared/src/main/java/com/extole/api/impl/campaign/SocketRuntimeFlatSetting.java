package com.extole.api.impl.campaign;

import java.util.List;

import com.extole.id.Id;
import com.extole.model.entity.campaign.Component;

public interface SocketRuntimeFlatSetting extends RuntimeFlatSetting {

    List<Id<Component>> getInstalledIntoComponents();
}
