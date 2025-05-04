package com.extole.client.rest.impl.webhook.reward.filter;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterEndpoints;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.RewardWebhookFilterRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.webhook.reward.filter.RewardWebhookFilter;
import com.extole.model.entity.webhook.reward.filter.RewardWebhookFilterType;
import com.extole.model.service.webhook.reward.RewardWebhookNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookService;

@Provider
public class RewardWebhookFilterEndpointsImpl implements RewardWebhookFilterEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardWebhookService rewardWebhookService;
    private final RewardWebhookFilterRestMapper rewardWebhookFilterRestMapper;

    @Autowired
    public RewardWebhookFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardWebhookService rewardWebhookService,
        RewardWebhookFilterRestMapper rewardWebhookFilterRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.rewardWebhookService = rewardWebhookService;
        this.rewardWebhookFilterRestMapper = rewardWebhookFilterRestMapper;
    }

    @Override
    public List<RewardWebhookFilterResponse> listRewardWebhookFilters(String accessToken, String webhookId,
        Optional<String> type, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, RewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            RewardWebhookFilterType rewardWebhookFilterType = getRewardWebhookFilterType(type);

            return rewardWebhookService.getWebhook(authorization, Id.valueOf(webhookId))
                .getFilters()
                .stream()
                .filter(rewardWebhookFilter -> !type.isPresent()
                    || rewardWebhookFilter.getType().equals(rewardWebhookFilterType))
                .map(rewardWebhook -> rewardWebhookFilterRestMapper.toRewardWebhookFilterResponse(rewardWebhook,
                    timeZone))
                .collect(Collectors.toList());
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardWebhookFilterResponse getRewardWebhookFilter(String accessToken, String webhookId, String filterId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardWebhookRestException, RewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<RewardWebhookFilter> optionalFilter =
                rewardWebhookService.getWebhook(authorization, Id.valueOf(webhookId))
                    .getFilters()
                    .stream()
                    .filter(rewardWebhookFilter -> rewardWebhookFilter.getId().equals(Id.valueOf(filterId)))
                    .findFirst();

            if (optionalFilter.isPresent()) {
                return rewardWebhookFilterRestMapper.toRewardWebhookFilterResponse(optionalFilter.get(), timeZone);
            } else {
                throw RestExceptionBuilder.newBuilder(RewardWebhookFilterRestException.class)
                    .withErrorCode(RewardWebhookFilterRestException.REWARD_WEBHOOK_FILTER_NOT_FOUND)
                    .addParameter("webhook_id", webhookId)
                    .addParameter("filter_id", filterId)
                    .build();
            }
        } catch (RewardWebhookNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookRestException.class)
                .withErrorCode(RewardWebhookRestException.REWARD_WEBHOOK_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Nullable
    private RewardWebhookFilterType getRewardWebhookFilterType(Optional<String> type)
        throws RewardWebhookFilterRestException {
        if (!type.isPresent()) {
            return null;
        }
        try {
            return RewardWebhookFilterType.valueOf(type.get().toUpperCase());
        } catch (Exception e) {
            throw RestExceptionBuilder.newBuilder(RewardWebhookFilterRestException.class)
                .withErrorCode(RewardWebhookFilterRestException.REWARD_WEBHOOK_FILTER_UNKNOWN_TYPE)
                .addParameter("type", type)
                .withCause(e)
                .build();
        }
    }
}
