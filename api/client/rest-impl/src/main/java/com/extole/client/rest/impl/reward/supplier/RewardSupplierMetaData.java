package com.extole.client.rest.impl.reward.supplier;

import javax.annotation.Nullable;

import com.extole.model.service.reward.supplier.tango.TangoBrand;
import com.extole.model.service.reward.supplier.tango.TangoBrandItem;

public final class RewardSupplierMetaData {

    private String brandName = null;
    private String brandDescription = null;
    private String brandDisclaimer = null;
    private String brandImageUrl = null;

    private RewardSupplierMetaData() {
    }

    private RewardSupplierMetaData(TangoBrand brand, TangoBrandItem item) {
        this.brandName = brand.getBrandName();
        this.brandDescription = brand.getDescription();
        this.brandImageUrl = brand.getImageUrl().orElse(null);
        this.brandDisclaimer = brand.getDisclaimer();
    }

    @Nullable
    public String getBrandName() {
        return brandName;
    }

    @Nullable
    public String getBrandDescription() {
        return brandDescription;
    }

    @Nullable
    public String getBrandDisclaimer() {
        return brandDisclaimer;
    }

    @Nullable
    public String getBrandImageUrl() {
        return brandImageUrl;
    }

    public static RewardSupplierMetaData create(TangoBrand brand, TangoBrandItem item) {
        return new RewardSupplierMetaData(brand, item);
    }

    public static RewardSupplierMetaData empty() {
        return new RewardSupplierMetaData();
    }
}
