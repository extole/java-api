package com.extole.consumer.rest.impl.redirect;

import java.util.Optional;

import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.share.PersonShare;
import com.extole.person.service.shareable.Shareable;

public class ShortShareLink {

    private final Optional<Shareable> shareable;
    private final Optional<Id<PersonShare>> shareId;
    private final Optional<PartnerEventId> partnerShareId;
    private final Optional<String> channel;
    private final String resolvingPath;

    public ShortShareLink(Optional<Shareable> shareable,
        Optional<Id<PersonShare>> shareId,
        Optional<PartnerEventId> partnerShareId,
        Optional<String> channel,
        String resolvingPath) {
        this.shareable = shareable;
        this.shareId = shareId;
        this.partnerShareId = partnerShareId;
        this.channel = channel;
        this.resolvingPath = resolvingPath;
    }

    public Optional<Shareable> getShareable() {
        return shareable;
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

    public String getResolvingPath() {
        return resolvingPath;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
