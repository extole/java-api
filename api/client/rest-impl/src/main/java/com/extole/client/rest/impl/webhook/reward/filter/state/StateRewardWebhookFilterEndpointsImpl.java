package com.extole.client.rest.impl.webhook.reward.filter.state;

import static java.util.stream.Collectors.toList;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.webhook.reward.RewardWebhookRestException;
import com.extole.client.rest.webhook.reward.filter.state.DetailedRewardState;
import com.extole.client.rest.webhook.reward.filter.state.StateRewardWebhookFilterCreateRequest;
import com.extole.client.rest.webhook.reward.filter.state.StateRewardWebhookFilterEndpoints;
import com.extole.client.rest.webhook.reward.filter.state.StateRewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.state.StateRewardWebhookFilterRestException;
import com.extole.client.rest.webhook.reward.filter.state.StateRewardWebhookFilterUpdateRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.webhook.reward.filter.state.StateRewardWebhookFilter;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookControllerActionException;
import com.extole.model.service.webhook.WebhookAssociatedWithWebhookUserSubscriptionChannelException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.service.webhook.reward.RewardWebhookNotFoundException;
import com.extole.model.service.webhook.reward.RewardWebhookService;
import com.extole.model.service.webhook.reward.filter.RewardWebhookFilterBuilderType;
import com.extole.model.service.webhook.reward.filter.state.StateRewardWebhookFilterBuilder;
import com.extole.model.service.webhook.reward.filter.state.StateRewardWebhookFilterNotFoundException;
import com.extole.model.service.webhook.reward.filter.state.StateRewardWebhookFilterQueryBuilder;
import com.extole.model.service.webhook.reward.filter.state.StateRewardWebhookFilterService;

@Provider
public class StateRewardWebhookFilterEndpointsImpl implements StateRewardWebhookFilterEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardWebhookService rewardWebhookService;
    private final ComponentService componentService;
    private final StateRewardWebhookFilterService stateRewardWebhookFilterService;
    private final StateRewardWebhookFilterRestMapper stateRewardWebhookFilterRestMapper;

    @Autowired
    public StateRewardWebhookFilterEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        RewardWebhookService rewardWebhookService,
        ComponentService componentService,
        StateRewardWebhookFilterService stateRewardWebhookFilterService,
        StateRewardWebhookFilterRestMapper stateRewardWebhookFilterRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.rewardWebhookService = rewardWebhookService;
        this.componentService = componentService;
        this.stateRewardWebhookFilterService = stateRewardWebhookFilterService;
        this.stateRewardWebhookFilterRestMapper = stateRewardWebhookFilterRestMapper;
    }

    @Override
    public List<StateRewardWebhookFilterResponse> listStateRewardWebhookFilters(String accessToken, String webhookId,
        ZoneId timeZone) throws UserAuthorizationRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            StateRewardWebhookFilterQueryBuilder stateRewardWebhookFilterQueryBuilder =
                stateRewardWebhookFilterService.createRewardWebhookStateFilterQueryBuilder(authorization,
                    Id.valueOf(webhookId));

            return stateRewardWebhookFilterQueryBuilder.list().stream()
                .map(rewardWebhookStateFilter -> stateRewardWebhookFilterRestMapper
                    .toRewardWebhookStateFilterResponse(rewardWebhookStateFilter, timeZone))
                .collect(toList());
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
    public StateRewardWebhookFilterResponse getStateRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, ZoneId timeZone) throws UserAuthorizationRestException, RewardWebhookRestException,
        StateRewardWebhookFilterRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            StateRewardWebhookFilter stateRewardWebhookFilter =
                stateRewardWebhookFilterService.getRewardWebhookStateFilter(authorization,
                    Id.valueOf(webhookId), Id.valueOf(filterId));
            return stateRewardWebhookFilterRestMapper.toRewardWebhookStateFilterResponse(stateRewardWebhookFilter,
                timeZone);

        } catch (StateRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
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
    public StateRewardWebhookFilterResponse createStateRewardWebhookFilter(String accessToken, String webhookId,
        StateRewardWebhookFilterCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, StateRewardWebhookFilterRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            if (createRequest.getStates() == null
                || createRequest.getStates().stream().filter(Objects::nonNull).collect(toList()).isEmpty()) {
                throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                    .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_MISSING_STATES)
                    .build();
            }

            Set<com.extole.model.entity.webhook.reward.filter.state.DetailedRewardState> detailedRewardStates =
                createRequest.getStates().stream()
                    .filter(Objects::nonNull)
                    .map(DetailedRewardState::name)
                    .map(com.extole.model.entity.webhook.reward.filter.state.DetailedRewardState::valueOf)
                    .collect(Collectors.toSet());

            StateRewardWebhookFilter savedFilter =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .addFilter(RewardWebhookFilterBuilderType.STATE)
                    .withStates(detailedRewardStates)
                    .save();

            return stateRewardWebhookFilterRestMapper.toRewardWebhookStateFilterResponse(savedFilter, timeZone);
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
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public StateRewardWebhookFilterResponse updateStateRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, StateRewardWebhookFilterUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, StateRewardWebhookFilterRestException, RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            StateRewardWebhookFilter filter = stateRewardWebhookFilterService.getRewardWebhookStateFilter(authorization,
                Id.valueOf(webhookId), Id.valueOf(filterId));

            StateRewardWebhookFilterBuilder<StateRewardWebhookFilter> webhookFilterBuilder =
                rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                    .updateFilter(filter);

            if (updateRequest.getStates() != null) {
                if (updateRequest.getStates().stream().filter(Objects::nonNull).collect(toList()).isEmpty()) {
                    throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                        .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_MISSING_STATES)
                        .build();
                }
                webhookFilterBuilder.withStates(updateRequest.getStates().stream()
                    .map(DetailedRewardState::name)
                    .map(com.extole.model.entity.webhook.reward.filter.state.DetailedRewardState::valueOf)
                    .collect(Collectors.toSet()));
            }

            return stateRewardWebhookFilterRestMapper.toRewardWebhookStateFilterResponse(
                webhookFilterBuilder.save(), timeZone);

        } catch (StateRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
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
        } catch (BuildWebhookException e) {
            throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_BUILD_FAILED)
                .addParameter("webhook_id", e.getWebhookId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        }
    }

    @Override
    public StateRewardWebhookFilterResponse archiveStateRewardWebhookFilter(String accessToken, String webhookId,
        String filterId, ZoneId timeZone) throws UserAuthorizationRestException, StateRewardWebhookFilterRestException,
        RewardWebhookRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            StateRewardWebhookFilter filter = stateRewardWebhookFilterService.getRewardWebhookStateFilter(authorization,
                Id.valueOf(webhookId), Id.valueOf(filterId));

            rewardWebhookService.updateWebhook(authorization, Id.valueOf(webhookId))
                .removeFilter(filter)
                .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return stateRewardWebhookFilterRestMapper.toRewardWebhookStateFilterResponse(filter, timeZone);
        } catch (StateRewardWebhookFilterNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(StateRewardWebhookFilterRestException.class)
                .withErrorCode(StateRewardWebhookFilterRestException.STATE_REWARD_WEBHOOK_FILTER_NOT_FOUND)
                .addParameter("webhook_id", webhookId)
                .addParameter("filter_id", filterId)
                .withCause(e)
                .build();
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
        } catch (InvalidComponentReferenceException | BuildWebhookException
            | WebhookAssociatedWithWebhookControllerActionException
            | WebhookAssociatedWithWebhookUserSubscriptionChannelException | MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
