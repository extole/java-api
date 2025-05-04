package com.extole.api.impl.event.data_intelligence;

import java.util.Optional;

import javax.annotation.Nullable;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.ReferralContext;
import com.extole.api.event.data_intelligence.DataIntelligenceConsumerEvent;
import com.extole.api.impl.event.ReferralContextImpl;
import com.extole.api.impl.event.internal.InternalConsumerEventImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.ToString;

public final class DataIntelligenceConsumerEventImpl extends InternalConsumerEventImpl
    implements DataIntelligenceConsumerEvent {

    private final Optional<ReferralContext> referralContext;

    private DataIntelligenceConsumerEventImpl(
        com.extole.event.consumer.internal.data_intelligence.DataIntelligenceConsumerEvent event, Person person) {
        super(event, person);
        this.referralContext = createReferralContext(event);
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

    public static ConsumerEvent newInstance(
        com.extole.event.consumer.internal.data_intelligence.DataIntelligenceConsumerEvent event, Person person) {
        return new DataIntelligenceConsumerEventImpl(event, person);
    }

    private static Optional<ReferralContext> createReferralContext(
        com.extole.event.consumer.internal.data_intelligence.DataIntelligenceConsumerEvent event) {
        return event.getReferralContext().map(context -> new ReferralContextImpl(context));
    }

}
