package com.extole.client.rest.reward.supplier;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum RewardSupplierType {
    MANUAL_COUPON, SALESFORCE_COUPON, TANGO_V2, CUSTOM_REWARD, PAYPAL_PAYOUTS
}
