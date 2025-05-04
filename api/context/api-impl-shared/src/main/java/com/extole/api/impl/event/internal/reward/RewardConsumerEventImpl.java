package com.extole.api.impl.event.internal.reward;

import java.util.Optional;

import javax.annotation.Nullable;

import com.extole.api.event.ReferralContext;
import com.extole.api.event.internal.reward.RewardContext;
import com.extole.api.impl.event.ReferralContextImpl;
import com.extole.api.impl.event.internal.InternalConsumerEventImpl;
import com.extole.api.person.Person;

public abstract class RewardConsumerEventImpl extends InternalConsumerEventImpl {

    private final RewardContext reward;
    private final Optional<ReferralContext> referralContext;

    protected RewardConsumerEventImpl(
        com.extole.event.consumer.internal.reward.RewardConsumerEvent event,
        Person person) {
        super(event, person);
        this.reward = new RewardContextImpl(
            event.getReward().getRewardId().getValue(),
            event.getReward().getRewardName(),
            event.getReward().getPartnerRewardId(),
            event.getReward().getTags(),
            event.getReward().getFaceValue(),
            event.getReward().getFaceValueType(),
            event.getReward().getData(),
            event.getReward().getState().name(),
            event.getReward().getProgramLabel(),
            event.getReward().getSupplierType());
        this.referralContext = createReferralContext(event);
    }

    public RewardContext getReward() {
        return reward;
    }

    @Nullable
    public ReferralContext getReferralContext() {
        return referralContext.orElse(null);
    }

    private static Optional<ReferralContext>
        createReferralContext(com.extole.event.consumer.internal.reward.RewardConsumerEvent event) {
        return event.getReferralContext().map(context -> new ReferralContextImpl(context));
    }

}
