package com.extole.client.rest.reward.supplier.v2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManualCouponRewardSupplierUploadCouponsRequest {

    private static final String COUPONS = "coupons";

    private final List<String> coupons;

    public ManualCouponRewardSupplierUploadCouponsRequest(@JsonProperty(COUPONS) List<String> coupons) {
        this.coupons = coupons;
    }

    public List<String> getCoupons() {
        return coupons;
    }

}
