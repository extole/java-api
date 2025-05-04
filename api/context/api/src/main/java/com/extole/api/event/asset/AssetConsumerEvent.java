package com.extole.api.event.asset;

import com.extole.api.event.ConsumerEvent;

public interface AssetConsumerEvent extends ConsumerEvent {

    String getAssetId();

    String getAssetName();

    Asset getAsset();

}
