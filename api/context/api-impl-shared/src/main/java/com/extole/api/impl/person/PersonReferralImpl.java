package com.extole.api.impl.person;

import javax.annotation.Nullable;

import com.extole.api.person.PersonReferral;
import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.profile.ProfileHandle;
import com.extole.profile.referral.ProfileReferralPojo;

public class PersonReferralImpl implements PersonReferral {

    private final String clientId;
    private final String otherPersonId;
    private final String mySide;
    private final String createdDate;
    private final String updatedDate;
    private final String reason;
    private final boolean isDisplaced;
    private final String container;

    public PersonReferralImpl(com.extole.person.service.profile.referral.PersonReferral personReferral) {
        this.clientId = personReferral.getClientId().getValue();
        this.otherPersonId = personReferral.getOtherPersonId().getValue();
        this.mySide = personReferral.getMySide().name();
        this.createdDate = personReferral.getCreatedDate().toString();
        this.updatedDate = personReferral.getUpdatedDate().toString();
        this.reason = personReferral.getReason().name();
        this.isDisplaced = personReferral.isDisplaced();
        this.container = personReferral.getContainer().getName();
    }

    public PersonReferralImpl(com.extole.person.service.profile.referral.PersonReferral.Side mySide,
        Id<ProfileHandle> otherPersonId,
        ProfileReferralPojo personReferral) {
        this.clientId = personReferral.getClientId().getValue();
        this.otherPersonId = otherPersonId.getValue();
        this.mySide = mySide.name();
        this.createdDate = personReferral.getCreatedDate().toString();
        this.updatedDate = personReferral.getUpdatedDate().toString();
        this.reason = personReferral.getReason().name();
        this.isDisplaced = personReferral.getDisplacedDate().isPresent();
        this.container = personReferral.getContainer().getName();
    }

    public PersonReferralImpl(
        String clientId,
        String otherPersonId,
        String mySide,
        String createdDate,
        String updatedDate,
        String reason,
        boolean isDisplaced,
        String container) {
        this.clientId = clientId;
        this.otherPersonId = otherPersonId;
        this.mySide = mySide;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.reason = reason;
        this.isDisplaced = isDisplaced;
        this.container = container;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getOtherPersonId() {
        return otherPersonId;
    }

    @Override
    public String getMySide() {
        return mySide;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public boolean isDisplaced() {
        return isDisplaced;
    }

    @Nullable
    @Override
    public String getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
