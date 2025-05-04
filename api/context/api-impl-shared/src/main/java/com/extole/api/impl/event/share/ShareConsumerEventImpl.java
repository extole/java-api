package com.extole.api.impl.event.share;

import java.util.Optional;

import javax.annotation.Nullable;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.ReferralContext;
import com.extole.api.event.share.ShareConsumerEvent;
import com.extole.api.impl.event.ConsumerEventImpl;
import com.extole.api.impl.event.ReferralContextImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class ShareConsumerEventImpl extends ConsumerEventImpl implements ShareConsumerEvent {

    private final String shareId;
    private final String quality;
    private final Optional<ReferralContext> referralContext;

    private ShareConsumerEventImpl(com.extole.event.consumer.share.ShareConsumerEvent event, Person person) {
        super(event, person);
        this.shareId = event.getShareId().getValue();
        this.quality = event.getQuality().name();
        this.referralContext = createReferralContext(event);
    }

    @Override
    public String getShareId() {
        return shareId;
    }

    @Override
    public String getQuality() {
        return quality;
    }

    @Nullable
    @Override
    public ReferralContext getReferralContext() {
        return referralContext.orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static ConsumerEvent newInstance(com.extole.event.consumer.share.ShareConsumerEvent event, Person person) {
        return new ShareConsumerEventImpl(event, person);
    }

    private static Optional<ReferralContext>
        createReferralContext(com.extole.event.consumer.share.ShareConsumerEvent event) {
        return event.getReferralContext().map(context -> new ReferralContextImpl(context));
    }

}
