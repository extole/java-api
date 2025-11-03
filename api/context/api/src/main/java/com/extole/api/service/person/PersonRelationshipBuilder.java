package com.extole.api.service.person;

import java.util.Map;

public interface PersonRelationshipBuilder {

    String DATA_NAME_SHAREABLE_CODE = "shareable_code";
    String DATA_NAME_SHARE_ID = "share_id";
    String DATA_NAME_EVENT_NAME = "event_name";
    String DATA_NAME_COUPON_CODE = "coupon_code";

    enum PersonRelationshipRole {
        FRIEND,
        ADVOCATE
    }

    enum PersonRelationshipReason {
        UNKNOWN_REASON,
        ADVOCATE,
        ADVOCATE_PARTNER_USER_ID,
        DISPLACED_PARTNER_USER_ID,
        PARTNER_EVENT_ID,
        SHAREABLE_ID,
        ADVOCATE_CODE,
        SHARE_RECIPIENT,
        PARTNER_SHARE_ID,
        SHARE_ID,
        INCENTIVIZED_SHARE,
        COUPON_CODE
    }

    PersonRelationshipBuilder withMyRole(String myRole);

    PersonRelationshipBuilder withOtherPersonRole(String otherPersonRole);

    PersonRelationshipBuilder withOtherPersonId(String otherPersonId);

    PersonRelationshipBuilder withReason(String reason);

    PersonRelationshipBuilder withProgram(String program);

    PersonRelationshipBuilder withCampaignId(String campaignId);

    PersonRelationshipBuilder withData(Map<String, Object> data);

    PersonRelationshipBuilder addData(String name, Object value);

    PersonRelationshipBuilder removeData(String name);

    PersonRelationshipBuilder clearData();

    PersonBuilder done();

}
