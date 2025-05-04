package com.extole.client.rest.reward.supplier.v2;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManualCouponUploadResponse {
    public static final String COUPON_CODE = "coupon_code";
    public static final String EXPIRES_AT = "expires_at";
    private final String couponCode;
    private final ZonedDateTime expiresAt;

    public ManualCouponUploadResponse(
        @JsonProperty(COUPON_CODE) String couponCode,
        @Nullable @JsonProperty(EXPIRES_AT) ZonedDateTime expiresAt) {
        this.couponCode = couponCode;
        this.expiresAt = expiresAt;
    }

    @JsonProperty(COUPON_CODE)
    public String getCouponCode() {
        return couponCode;
    }

    @JsonProperty(EXPIRES_AT)
    public Optional<ZonedDateTime> getExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String couponCode;
        private ZonedDateTime expiresAt;

        private Builder() {
        }

        public Builder withCouponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public Builder withExpiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public ManualCouponUploadResponse build() {
            return new ManualCouponUploadResponse(couponCode, expiresAt);
        }
    }
}
