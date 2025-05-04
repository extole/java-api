package com.extole.client.rest.reward.supplier.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CouponStatsResponse {

    private static final String NUMBER_OF_AVAILABLE_COUPONS = "number_of_available_coupons";
    private static final String NUMBER_OF_ISSUED_COUPONS = "number_of_issued_coupons";

    private final int numberOfAvailableCoupons;
    private final int numberOfIssuedCoupons;

    public CouponStatsResponse(
        @JsonProperty(NUMBER_OF_AVAILABLE_COUPONS) int numberOfAvailableCoupons,
        @JsonProperty(NUMBER_OF_ISSUED_COUPONS) int numberOfIssuedCoupons) {
        this.numberOfAvailableCoupons = numberOfAvailableCoupons;
        this.numberOfIssuedCoupons = numberOfIssuedCoupons;
    }

    @JsonProperty(NUMBER_OF_AVAILABLE_COUPONS)
    public int getNumberOfAvailableCoupons() {
        return numberOfAvailableCoupons;
    }

    @JsonProperty(NUMBER_OF_ISSUED_COUPONS)
    public int getNumberOfIssuedCoupons() {
        return numberOfIssuedCoupons;
    }

}
