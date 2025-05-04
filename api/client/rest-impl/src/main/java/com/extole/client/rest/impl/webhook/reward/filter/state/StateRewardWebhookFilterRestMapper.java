package com.extole.client.rest.impl.webhook.reward.filter.state;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.webhook.reward.filter.state.DetailedRewardState;
import com.extole.client.rest.webhook.reward.filter.state.StateRewardWebhookFilterResponse;
import com.extole.model.entity.webhook.reward.filter.state.StateRewardWebhookFilter;

@Component
public class StateRewardWebhookFilterRestMapper {

    public StateRewardWebhookFilterResponse toRewardWebhookStateFilterResponse(StateRewardWebhookFilter filter,
        ZoneId timeZone) {
        return new StateRewardWebhookFilterResponse(filter.getId().getValue(),
            filter.getStates().stream().map(Enum::name).map(DetailedRewardState::valueOf).collect(Collectors.toSet()),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }
}
