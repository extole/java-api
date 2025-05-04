package com.extole.client.rest.reward.supplier.v2;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ManualCouponRewardSupplierDownloadCouponsResponse {

    private static final String COUPONS = "coupons";
    private static final String UPLOADED_COUPONS = "uploaded_coupons";

    private final List<ManualCouponUploadResponse> uploadedCoupons;

    public ManualCouponRewardSupplierDownloadCouponsResponse(
        @JsonProperty(UPLOADED_COUPONS) List<ManualCouponUploadResponse> uploadedCoupons) {
        this.uploadedCoupons = uploadedCoupons;
    }

    @Deprecated // TODO remove deprecated field ENG-12921
    @JsonProperty(COUPONS)
    public List<String> getCoupons() {
        return uploadedCoupons.stream().map(ManualCouponUploadResponse::getCouponCode)
            .collect(Collectors.toList());
    }

    @JsonProperty(UPLOADED_COUPONS)
    public List<ManualCouponUploadResponse> getUploadedCoupons() {
        return uploadedCoupons;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
