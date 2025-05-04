package com.extole.consumer.rest.impl.redirect;

import java.util.Optional;

import com.extole.id.Id;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonShare;

public class ShareKey {

    private final Optional<Id<PersonShare>> shareId;
    private final Optional<PartnerEventId> partnerShareId;
    private final Optional<String> channel;

    public ShareKey(Optional<Id<PersonShare>> shareId,
        Optional<PartnerEventId> partnerShareId,
        Optional<String> channel) {
        this.shareId = shareId;
        this.partnerShareId = partnerShareId;
        this.channel = channel;
    }

    public Optional<Id<PersonShare>> getShareId() {
        return shareId;
    }

    public Optional<PartnerEventId> getPartnerShareId() {
        return partnerShareId;
    }

    public Optional<String> getChannel() {
        return channel;
    }

}
