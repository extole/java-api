package com.extole.client.rest.person;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum PersonReferralReason {
    SHARE_RECIPIENT,
    SHARE_ID,
    SHAREABLE_ID,
    COUPON_CODE,
    ADVOCATE,
    ADVOCATE_CODE,
    ADVOCATE_PARTNER_USER_ID,
    @Deprecated // TBD - OPEN TICKET needed in enum to support DB read of referrals that were created with this reason
    DISPLACED_PARTNER_USER_ID,
    @Deprecated // TBD - OPEN TICKET
    UNKNOWN_REASON,
    INCENTIVIZED_SHARE,
    PARTNER_EVENT_ID,
    PARTNER_SHARE_ID
}
