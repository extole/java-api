package com.extole.client.rest.reward.supplier.v2;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ManualCouponRequest {
    private static final String COUPON_CODE = "coupon_code";
    private static final String EXPIRES_AT = "expires_at";
    private final String couponCode;
    private final ZonedDateTime expiresAt;

    public ManualCouponRequest(@JsonProperty(COUPON_CODE) String couponCode,
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

    @Override
    public String toString() {
        return ToString.create(this);
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

        public ManualCouponRequest build() {
            return new ManualCouponRequest(couponCode, expiresAt);
        }
    }
}
