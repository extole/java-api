package com.extole.api.impl.event.reward;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import com.extole.event.webhook.reward.CanceledPublicReward;
import com.extole.event.webhook.reward.EarnedPublicReward;
import com.extole.event.webhook.reward.FailedFulfilledPublicReward;
import com.extole.event.webhook.reward.FailedPublicReward;
import com.extole.event.webhook.reward.FulfilledPublicReward;
import com.extole.event.webhook.reward.PublicReward;
import com.extole.event.webhook.reward.RedeemedPublicReward;
import com.extole.event.webhook.reward.RevokedPublicReward;
import com.extole.event.webhook.reward.SentPublicReward;

@Component
public class PublicRewardSerializer {
    private final Map<Class<? extends PublicReward>,
        Function<PublicReward, ? extends PublicRewardImpl>> eventConverters;

    PublicRewardSerializer() {
        Map<Class<? extends PublicReward>, Function<PublicReward, ? extends PublicRewardImpl>> map =
            Maps.newHashMap();
        map.put(EarnedPublicReward.class,
            rewardEvent -> new EarnedPublicRewardImpl((EarnedPublicReward) rewardEvent));
        map.put(FulfilledPublicReward.class,
            rewardEvent -> new FulfilledPublicRewardImpl((FulfilledPublicReward) rewardEvent));
        map.put(FailedFulfilledPublicReward.class,
            rewardEvent -> new FailedFulfilledPublicRewardImpl((FailedFulfilledPublicReward) rewardEvent));
        map.put(SentPublicReward.class,
            rewardEvent -> new SentPublicRewardImpl((SentPublicReward) rewardEvent));
        map.put(FailedPublicReward.class,
            rewardEvent -> new FailedPublicRewardImpl((FailedPublicReward) rewardEvent));
        map.put(CanceledPublicReward.class,
            rewardEvent -> new CanceledPublicRewardImpl((CanceledPublicReward) rewardEvent));
        map.put(RedeemedPublicReward.class,
            rewardEvent -> new RedeemedPublicRewardImpl((RedeemedPublicReward) rewardEvent));
        map.put(RevokedPublicReward.class,
            rewardEvent -> new RevokedPublicRewardImpl((RevokedPublicReward) rewardEvent));
        eventConverters = Collections.unmodifiableMap(map);
    }

    public com.extole.api.webhook.reward.event.PublicReward serialize(PublicReward publicReward) {
        return eventConverters.get(publicReward.getClass()).apply(publicReward);
    }
}
