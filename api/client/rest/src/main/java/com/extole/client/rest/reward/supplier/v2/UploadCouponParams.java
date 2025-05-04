package com.extole.client.rest.reward.supplier.v2;

import javax.ws.rs.QueryParam;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.lang.ToString;

public class UploadCouponParams {

    private final boolean allowRestrictedCharacters;
    private final boolean allowExpired;
    private final boolean discardDuplicated;

    public static Builder newParams() {
        return new Builder();
    }

    public UploadCouponParams(
        @Parameter(
            description = "Allows coupon codes containing restricted characters")
        @QueryParam("allow_restricted_characters") boolean allowRestrictedCharacters,
        @Parameter(description = "Allows expired coupons") @QueryParam("allow_expired") boolean allowExpired,
        @Parameter(
            description = "Discards duplicated coupons")
        @QueryParam("discard_duplicated") boolean discardDuplicated) {
        this.allowRestrictedCharacters = allowRestrictedCharacters;
        this.allowExpired = allowExpired;
        this.discardDuplicated = discardDuplicated;
    }

    @QueryParam("allow_restricted_characters")
    public boolean isAllowRestrictedCharacters() {
        return allowRestrictedCharacters;
    }

    @QueryParam("allow_expired")
    public boolean isAllowExpired() {
        return allowExpired;
    }

    @QueryParam("discard_duplicated")
    public boolean isDiscardDuplicated() {
        return discardDuplicated;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private boolean allowRestrictedCharacters = false;
        private boolean allowExpired = false;
        private boolean discardDuplicated = false;

        public Builder withAllowRestrictedCharacters() {
            this.allowRestrictedCharacters = true;
            return this;
        }

        public Builder withAllowExpired() {
            this.allowExpired = true;
            return this;
        }

        public Builder withDiscardDuplicated() {
            this.discardDuplicated = true;
            return this;
        }

        public UploadCouponParams build() {
            return new UploadCouponParams(
                allowRestrictedCharacters, allowExpired, discardDuplicated);
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
