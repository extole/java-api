package com.extole.client.rest.impl.webhook.built;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.webhook.built.BuiltClientWebhookResponse;
import com.extole.client.rest.webhook.built.BuiltGenericWebhookResponse;
import com.extole.client.rest.webhook.built.BuiltPartnerWebhookResponse;
import com.extole.client.rest.webhook.built.BuiltRewardWebhookResponse;
import com.extole.client.rest.webhook.built.BuiltWebhookResponse;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterType;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.webhook.built.BuiltWebhook;
import com.extole.model.entity.webhook.built.partner.BuiltPartnerWebhook;
import com.extole.model.entity.webhook.built.reward.BuiltRewardWebhook;
import com.extole.model.entity.webhook.built.reward.filter.BuiltRewardWebhookFilter;

@Component
public class BuiltWebhookRestMapper {

    public BuiltWebhookResponse toBuiltWebhookResponse(BuiltWebhook webhook, ZoneId timeZone) {
        switch (webhook.getType()) {
            case GENERIC:
                return toBuiltGenericWebhookResponse(webhook, timeZone);
            case REWARD:
                return toBuiltRewardWebhookResponse((BuiltRewardWebhook) webhook, timeZone);
            case CLIENT:
                return toBuiltClientWebhookResponse(webhook, timeZone);
            case PARTNER:
                return toBuiltPartnerWebhookResponse((BuiltPartnerWebhook) webhook, timeZone);
            default:
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(new RuntimeException("Unknown webhook type: " + webhook.getType()))
                    .build();
        }
    }

    private BuiltClientWebhookResponse toBuiltClientWebhookResponse(BuiltWebhook clientWebhook, ZoneId timeZone) {
        return new BuiltClientWebhookResponse(clientWebhook.getId().getValue(),
            clientWebhook.getName(),
            clientWebhook.getUrl(),
            clientWebhook.getClientKeyId().map(value -> Id.valueOf(value.getValue())),
            clientWebhook.getTags(),
            clientWebhook.getRequest(),
            clientWebhook.getResponseHandler(),
            Boolean.valueOf(clientWebhook.isEnabled()),
            clientWebhook.getDescription(),
            clientWebhook.getCreatedAt().atZone(timeZone),
            clientWebhook.getUpdatedAt().atZone(timeZone),
            clientWebhook.getDefaultMethod(),
            clientWebhook.getRetryIntervals(),
            clientWebhook.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            clientWebhook.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    private BuiltGenericWebhookResponse toBuiltGenericWebhookResponse(BuiltWebhook webhook, ZoneId timeZone) {
        return new BuiltGenericWebhookResponse(webhook.getId().getValue(),
            webhook.getName(),
            webhook.getUrl(),
            webhook.getClientKeyId().map(value -> Id.valueOf(value.getValue())),
            webhook.getTags(),
            webhook.getRequest(),
            webhook.getResponseHandler(),
            Boolean.valueOf(webhook.isEnabled()),
            webhook.getDescription(),
            webhook.getCreatedAt().atZone(timeZone),
            webhook.getUpdatedAt().atZone(timeZone),
            webhook.getDefaultMethod(),
            webhook.getRetryIntervals(),
            webhook.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            webhook.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    private BuiltRewardWebhookResponse toBuiltRewardWebhookResponse(BuiltRewardWebhook rewardWebhook, ZoneId timeZone) {
        List<RewardWebhookFilterResponse> filters = rewardWebhook.getFilters().stream()
            .map(filter -> toRewardWebhookFilterResponse(filter, timeZone))
            .collect(Collectors.toList());

        return new BuiltRewardWebhookResponse(rewardWebhook.getId().getValue(),
            rewardWebhook.getName(),
            rewardWebhook.getUrl(),
            rewardWebhook.getClientKeyId().map(value -> Id.valueOf(value.getValue())),
            rewardWebhook.getTags(),
            rewardWebhook.getRequest(),
            rewardWebhook.getResponseHandler(),
            Boolean.valueOf(rewardWebhook.isEnabled()),
            rewardWebhook.getDescription(),
            rewardWebhook.getCreatedAt().atZone(timeZone),
            rewardWebhook.getUpdatedAt().atZone(timeZone),
            rewardWebhook.getDefaultMethod(),
            rewardWebhook.getRetryIntervals(),
            rewardWebhook.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardWebhook.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            filters);
    }

    private BuiltPartnerWebhookResponse toBuiltPartnerWebhookResponse(BuiltPartnerWebhook rewardWebhook,
        ZoneId timeZone) {

        return new BuiltPartnerWebhookResponse(rewardWebhook.getId().getValue(),
            rewardWebhook.getName(),
            rewardWebhook.getUrl(),
            rewardWebhook.getClientKeyId().map(value -> Id.valueOf(value.getValue())),
            rewardWebhook.getTags(),
            rewardWebhook.getRequest(),
            rewardWebhook.getResponseHandler(),
            Boolean.valueOf(rewardWebhook.isEnabled()),
            rewardWebhook.getDescription(),
            rewardWebhook.getCreatedAt().atZone(timeZone),
            rewardWebhook.getUpdatedAt().atZone(timeZone),
            rewardWebhook.getDefaultMethod(),
            rewardWebhook.getRetryIntervals(),
            rewardWebhook.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            rewardWebhook.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            rewardWebhook.getResponseBodyHandler());
    }

    private RewardWebhookFilterResponse toRewardWebhookFilterResponse(BuiltRewardWebhookFilter filter,
        ZoneId timeZone) {
        return new RewardWebhookFilterResponse(filter.getId().getValue(),
            RewardWebhookFilterType.valueOf(filter.getType().name()),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }
}
