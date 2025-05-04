package com.extole.api.impl.person;

import com.extole.api.person.PartnerEventId;

public class PartnerEventIdImpl implements PartnerEventId {

    private final com.extole.person.service.profile.step.PartnerEventId partnerEventId;

    public PartnerEventIdImpl(com.extole.person.service.profile.step.PartnerEventId partnerEventId) {
        this.partnerEventId = partnerEventId;
    }

    @Override
    public String getName() {
        return partnerEventId.getName();
    }

    @Override
    public String getValue() {
        return partnerEventId.getValue();
    }
}
