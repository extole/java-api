package com.extole.api.impl.campaign;

import java.util.Map;

import com.extole.api.campaign.ComponentAsset;

public interface AssetsSupplier {
    Map<String, ComponentAsset> supplyDefault();

    Map<String, ComponentAsset> supplyForComponent(ComponentWithVersion component);
}
