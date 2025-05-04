package com.extole.client.rest.impl.webhook;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.webhook.ClientWebhookResponse;
import com.extole.client.rest.webhook.GenericWebhookResponse;
import com.extole.client.rest.webhook.PartnerWebhookResponse;
import com.extole.client.rest.webhook.RewardWebhookResponse;
import com.extole.client.rest.webhook.WebhookResponse;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterType;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.webhook.Webhook;
import com.extole.model.entity.webhook.partner.PartnerWebhook;
import com.extole.model.entity.webhook.reward.RewardWebhook;
import com.extole.model.entity.webhook.reward.filter.RewardWebhookFilter;

@Component
public class WebhookRestMapper {

    public WebhookResponse toWebhookResponse(Webhook webhook, ZoneId timeZone) {
        switch (webhook.getType()) {
            case GENERIC:
                return toGenericWebhookResponse(webhook, timeZone);
            case REWARD:
                return toRewardWebhookResponse((RewardWebhook) webhook, timeZone);
            case CLIENT:
                return toClientWebhookResponse(webhook, timeZone);
            case PARTNER:
                return toPartnerWebhookResponse((PartnerWebhook) webhook, timeZone);

            default:
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(new RuntimeException("Unknown webhook type: " + webhook.getType()))
                    .build();
        }
    }

    private ClientWebhookResponse toClientWebhookResponse(Webhook clientWebhook, ZoneId timeZone) {
        return new ClientWebhookResponse(clientWebhook.getId().getValue(),
            clientWebhook.getName(),
            clientWebhook.getUrl(),
            clientWebhook.getClientKeyId(),
            clientWebhook.getTags(),
            clientWebhook.getRequest(),
            clientWebhook.getResponseHandler(),
            clientWebhook.getEnabled(),
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

    private WebhookResponse toGenericWebhookResponse(Webhook webhook, ZoneId timeZone) {
        return new GenericWebhookResponse(webhook.getId().getValue(),
            webhook.getName(),
            webhook.getUrl(),
            webhook.getClientKeyId(),
            webhook.getTags(),
            webhook.getRequest(),
            webhook.getResponseHandler(),
            webhook.getEnabled(),
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

    private WebhookResponse toRewardWebhookResponse(RewardWebhook rewardWebhook, ZoneId timeZone) {
        List<RewardWebhookFilterResponse> filters = rewardWebhook.getFilters().stream()
            .map(filter -> toRewardWebhookFilterResponse(filter, timeZone))
            .collect(Collectors.toList());

        return new RewardWebhookResponse(rewardWebhook.getId().getValue(),
            rewardWebhook.getName(),
            rewardWebhook.getUrl(),
            rewardWebhook.getClientKeyId(),
            rewardWebhook.getTags(),
            rewardWebhook.getRequest(),
            rewardWebhook.getResponseHandler(),
            rewardWebhook.getEnabled(),
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

    private WebhookResponse toPartnerWebhookResponse(PartnerWebhook rewardWebhook, ZoneId timeZone) {
        return new PartnerWebhookResponse(rewardWebhook.getId().getValue(),
            rewardWebhook.getName(),
            rewardWebhook.getUrl(),
            rewardWebhook.getClientKeyId(),
            rewardWebhook.getTags(),
            rewardWebhook.getRequest(),
            rewardWebhook.getResponseHandler(),
            rewardWebhook.getEnabled(),
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

    private RewardWebhookFilterResponse toRewardWebhookFilterResponse(RewardWebhookFilter filter, ZoneId timeZone) {
        return new RewardWebhookFilterResponse(filter.getId().getValue(),
            RewardWebhookFilterType.valueOf(filter.getType().name()),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }
}
