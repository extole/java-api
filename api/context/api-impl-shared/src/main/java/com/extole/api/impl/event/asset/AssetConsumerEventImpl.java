package com.extole.api.impl.event.asset;

import com.extole.api.event.asset.Asset;
import com.extole.api.event.asset.AssetConsumerEvent;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class AssetConsumerEventImpl extends ConsumerEventImpl implements AssetConsumerEvent {

    private final String assetId;
    private final String assetName;
    private final Asset asset;

    private AssetConsumerEventImpl(com.extole.event.consumer.asset.AssetConsumerEvent event, Person person) {
        super(event, person);
        this.assetId = event.getAssetId().getValue();
        this.assetName = event.getAssetName();
        this.asset = new AssetImpl(event.getAssetPojo());
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public String getAssetName() {
        return assetName;
    }

    @Override
    public Asset getAsset() {
        return asset;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static AssetConsumerEvent newInstance(com.extole.event.consumer.asset.AssetConsumerEvent event,
        Person person) {
        return new AssetConsumerEventImpl(event, person);
    }

}
