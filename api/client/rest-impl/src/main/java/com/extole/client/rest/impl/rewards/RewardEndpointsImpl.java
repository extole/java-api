package com.extole.client.rest.impl.rewards;

import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.ANY;
import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.CANCELED;
import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.FAILED;
import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.FULFILLED;
import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.REDEEMED;
import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.REVOKED;
import static com.extole.rewards.service.state.RewardStateQueryBuilder.Type.SENT;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.rewards.CancelRewardRequest;
import com.extole.client.rest.rewards.CanceledRewardStateResponse;
import com.extole.client.rest.rewards.FailedRewardStateResponse;
import com.extole.client.rest.rewards.FulfilledRewardStateResponse;
import com.extole.client.rest.rewards.Period;
import com.extole.client.rest.rewards.RedeemedRewardStateResponse;
import com.extole.client.rest.rewards.RevokeRewardRequest;
import com.extole.client.rest.rewards.RevokedRewardStateResponse;
import com.extole.client.rest.rewards.RewardEndpoints;
import com.extole.client.rest.rewards.RewardListRequest;
import com.extole.client.rest.rewards.RewardQueryRestException;
import com.extole.client.rest.rewards.RewardResponse;
import com.extole.client.rest.rewards.RewardRestException;
import com.extole.client.rest.rewards.RewardStateResponse;
import com.extole.client.rest.rewards.RewardStateSummaryResponse;
import com.extole.client.rest.rewards.RewardUpdateRequest;
import com.extole.client.rest.rewards.SentRewardStateResponse;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.RewardSupplierType;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.shared.reward.supplier.RewardSupplierCache;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.rewards.service.ClaimedRewardRetryNotSupportedException;
import com.extole.rewards.service.LockCouldNotBeAcquiredException;
import com.extole.rewards.service.Reward;
import com.extole.rewards.service.RewardIllegalStateTransitionException;
import com.extole.rewards.service.RewardNotFoundException;
import com.extole.rewards.service.RewardNotRetryableStateException;
import com.extole.rewards.service.RewardQueryBuilder;
import com.extole.rewards.service.RewardRetryNotSupportedException;
import com.extole.rewards.service.RewardService;
import com.extole.rewards.service.state.RewardState;
import com.extole.rewards.service.state.RewardStateQueryBuilder;
import com.extole.rewards.service.state.RewardStateType;
import com.extole.rewards.service.state.cancel.CanceledRewardState;
import com.extole.rewards.service.state.cancel.RewardStateCancelBuilder;
import com.extole.rewards.service.state.fail.FailedRewardState;
import com.extole.rewards.service.state.fulfillment.FulfilledRewardState;
import com.extole.rewards.service.state.redeem.RedeemedRewardState;
import com.extole.rewards.service.state.revoke.RevokedRewardState;
import com.extole.rewards.service.state.send.SentRewardState;
import com.extole.rewards.service.state.summary.RewardStateSummaryQueryBuilder;
import com.extole.sandbox.SandboxModel;
import com.extole.sandbox.SandboxNotFoundException;
import com.extole.sandbox.SandboxService;

@Provider
public class RewardEndpointsImpl implements RewardEndpoints {
    private static final LockDescription LOCK_DESCRIPTION_FOR_REWARD_UPDATE =
        new LockDescription("profile-reward-endpoints-lock");
    private static final String EVENT_NAME_FOR_REWARD_UPDATE = "extole.profile.reward.update";

    private static final int MAX_REWARD_FETCH_SIZE = 1000;
    private static final String COMMA = ",";
    private static final int DEFAULT_PERIOD_COUNT_MULTIPLIER = 8;
    private static final String ISO_8601_INTERVAL_DATES_SEPARATOR = "/";

    private final ClientAuthorizationProvider authorizationProvider;
    private final RewardService rewardService;
    private final PersonService personService;
    private final RewardRestMapper rewardRestMapper;
    private final RewardSupplierCache rewardSupplierRewardSupplierCache;
    private final SandboxService sandboxService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public RewardEndpointsImpl(RewardService rewardService,
        ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        RewardRestMapper rewardRestMapper,
        RewardSupplierCache rewardSupplierRewardSupplierCache,
        SandboxService sandboxService,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.authorizationProvider = authorizationProvider;
        this.rewardService = rewardService;
        this.personService = personService;
        this.rewardRestMapper = rewardRestMapper;
        this.rewardSupplierRewardSupplierCache = rewardSupplierRewardSupplierCache;
        this.sandboxService = sandboxService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public List<RewardResponse> listRewards(String accessToken, RewardListRequest rewardListRequest)
        throws UserAuthorizationRestException, RewardQueryRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        validateLimits(rewardListRequest.getLimit(), rewardListRequest.getOffset());

        try {
            RewardQueryBuilder rewardQueryBuilder = rewardService.createRewardQueryBuilder(authorization)
                .withPartnerRewardIds(parsePartnerIds(rewardListRequest.getPartnerRewardIds()))
                .withPartnerRewardSupplierIds(parsePartnerIds(rewardListRequest.getRewardSupplierIds()))
                .withStateTypes(parseRewardStateTypes(rewardListRequest.getStates().orElse(null)))
                .withRewardSupplierIds(parseIds(rewardListRequest.getRewardSupplierIds()))
                .withPersonIds(parseIds(rewardListRequest.getPersonIds()))
                .withActionIds(Lists.newArrayList(parseIds(rewardListRequest.getActionIds())))
                .withRootEventIds(Lists.newArrayList(parseIds(rewardListRequest.getRootEventIds())))
                .withRewardSupplierTypes(parseRewardSupplierTypes(rewardListRequest.getRewardTypes()));
            String timeInterval = rewardListRequest.getTimeInterval();
            parseFromDate(timeInterval, rewardListRequest.getTimeZone()).ifPresent(rewardQueryBuilder::withCreatedFrom);
            parseToDate(timeInterval, rewardListRequest.getTimeZone()).ifPresent(rewardQueryBuilder::withCreatedTo);

            List<Reward> rewards = rewardQueryBuilder
                .withSuccessfulOnly(rewardListRequest.getSuccessOnly())
                .withLimit(rewardListRequest.getLimit())
                .withOffset(rewardListRequest.getOffset())
                .list();

            RewardMappingFunction rewardMappingFunction =
                newRewardResponseMappingFunction(authorization, rewardListRequest.getTimeZone());
            List<RewardResponse> rewardResponses = Lists.newArrayListWithCapacity(rewards.size());

            for (Reward reward : rewards) {
                rewardResponses.add(rewardMappingFunction.mapReward(reward));
            }
            return rewardResponses;

        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<RewardStateSummaryResponse> stateSummary(String accessToken,
        Optional<Period> period,
        Optional<String> periodCount,
        Optional<ZonedDateTime> endDate,
        Optional<Boolean> successOnly,
        Optional<String> multipleRewardSupplierIds,
        Optional<String> multipleRewardTypes,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardQueryRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            RewardStateSummaryQueryBuilder rewardStateSummaryQueryBuilder =
                rewardService.createRewardStateSummaryQueryBuilder(authorization);

            ZonedDateTime dateEnd = endDate.orElse(ZonedDateTime.now(timeZone));

            return rewardStateSummaryQueryBuilder
                .withRewardSupplierIds(parseIds(multipleRewardSupplierIds))
                .withRewardSupplierTypes(parseRewardSupplierTypes(multipleRewardTypes))
                .withSuccessfulOnly(successOnly.orElse(true))
                .withEndDate(dateEnd)
                .withPeriodCount(parsePeriodCount(periodCount).orElse(DEFAULT_PERIOD_COUNT_MULTIPLIER))
                .withPeriod(period.map(value -> com.extole.rewards.service.state.summary.Period.valueOf(value.name()))
                    .orElse(com.extole.rewards.service.state.summary.Period.WEEK))
                .list()
                .stream()
                .map(rewardStateSummary -> rewardRestMapper.toRewardStateSummaryResponse(rewardStateSummary,
                    dateEnd.getZone()))
                .collect(Collectors.toList());

        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardResponse getReward(String accessToken, String rewardId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Reward reward = rewardService.getReward(authorization, Id.valueOf(rewardId));

            return newRewardResponseMappingFunction(authorization, timeZone).mapReward(reward);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<RewardStateResponse> getRewardStateHistory(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            RewardStateQueryBuilder<RewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), ANY);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardState -> rewardRestMapper.toRewardStateResponse(rewardState, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<FulfilledRewardStateResponse> getRewardFulfillments(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Reward reward = rewardService.getReward(authorization, Id.valueOf(rewardId));
            RewardStateQueryBuilder<FulfilledRewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), FULFILLED);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardStateFulfillment -> rewardRestMapper.toRewardStateFulfillmentResponse(reward,
                    rewardStateFulfillment,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<SentRewardStateResponse> getRewardSends(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            RewardStateQueryBuilder<SentRewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), SENT);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardStateSend -> rewardRestMapper.toRewardStateSendResponse(rewardStateSend, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<RedeemedRewardStateResponse> getRewardRedeems(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            RewardStateQueryBuilder<RedeemedRewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), REDEEMED);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardStateRedeem -> rewardRestMapper.toRewardStateRedeemResponse(rewardStateRedeem,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<FailedRewardStateResponse> getRewardFails(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            RewardStateQueryBuilder<FailedRewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), FAILED);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardStateFail -> rewardRestMapper.toRewardStateFailResponse(rewardStateFail, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<CanceledRewardStateResponse> getRewardCancels(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            RewardStateQueryBuilder<CanceledRewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), CANCELED);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardStateCancel -> rewardRestMapper.toRewardStateCancelResponse(rewardStateCancel,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<RevokedRewardStateResponse> getRewardRevokes(String accessToken, String rewardId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            RewardStateQueryBuilder<RevokedRewardState> rewardStateQueryBuilder =
                rewardService.createRewardStateQueryBuilder(authorization, Id.valueOf(rewardId), REVOKED);

            return rewardStateQueryBuilder.list().stream()
                .map(rewardStateRevoke -> rewardRestMapper.toRewardStateRevokeResponse(rewardStateRevoke,
                    timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardResponse retryReward(String accessToken, String rewardId, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            rewardService.retryReward(authorization, Id.valueOf(rewardId));

            return newRewardResponseMappingFunction(authorization, timeZone)
                .mapReward(rewardService.getReward(authorization, Id.valueOf(rewardId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        } catch (RewardRetryNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_RETRY_NOT_SUPPORTED)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        } catch (RewardNotRetryableStateException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_RETRYABLE_STATE)
                .addParameter("reward_id", rewardId)
                .addParameter("current_reward_state", e.getCurrentRewardState())
                .addParameter("retryable_states", e.getRetryableStates())
                .withCause(e)
                .build();
        } catch (ClaimedRewardRetryNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.CLAIMED_REWARD_RETRY)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        } catch (RewardIllegalStateTransitionException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_ILLEGAL_STATE_TRANSITION)
                .addParameter("current_state", e.getCurrentState())
                .withCause(e)
                .build();
        } catch (RewardSupplierNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_SUPPLIER_NOT_FOUND)
                .addParameter("reward_supplier_id", e.getRewardSupplierId())
                .withCause(e)
                .build();
        } catch (LockCouldNotBeAcquiredException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardResponse cancel(String accessToken, String rewardId, Optional<CancelRewardRequest> cancelRewardRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            rewardService.updateReward(authorization, Id.valueOf(rewardId),
                (existingRewardBuilder -> {
                    RewardStateCancelBuilder rewardStateCancelBuilder = existingRewardBuilder.addCancel();
                    if (cancelRewardRequest.isPresent() && cancelRewardRequest.get().getMessage() != null) {
                        rewardStateCancelBuilder.withMessage(cancelRewardRequest.get().getMessage());
                    }
                    existingRewardBuilder.save();
                }));
            return newRewardResponseMappingFunction(authorization, timeZone)
                .mapReward(rewardService.getReward(authorization, Id.valueOf(rewardId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        } catch (RewardIllegalStateTransitionException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_ILLEGAL_STATE_TRANSITION)
                .addParameter("current_state", e.getCurrentState())
                .withCause(e)
                .build();
        } catch (LockCouldNotBeAcquiredException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardResponse revoke(String accessToken, String rewardId, Optional<RevokeRewardRequest> revokeRewardRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            rewardService.revokeReward(authorization, Id.valueOf(rewardId),
                revokeRewardRequest.map(RevokeRewardRequest::getMessage).orElse(null));
            return newRewardResponseMappingFunction(authorization, timeZone)
                .mapReward(rewardService.getReward(authorization, Id.valueOf(rewardId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        } catch (RewardIllegalStateTransitionException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_ILLEGAL_STATE_TRANSITION)
                .addParameter("current_state", e.getCurrentState())
                .withCause(e)
                .build();
        }
    }

    @Override
    public RewardResponse updateReward(String accessToken, String rewardId,
        RewardUpdateRequest rewardUpdateRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, RewardRestException, PersonRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Id<ClientHandle> clientId = authorization.getClientId();
        try {
            Reward reward = rewardService.getReward(authorization, Id.valueOf(rewardId));
            Id<PersonHandle> personId = reward.getPersonId();

            if (isNoopUpdateRequest(rewardUpdateRequest)) {
                return newRewardResponseMappingFunction(authorization, timeZone)
                    .mapReward(rewardService.getReward(authorization, Id.valueOf(rewardId)));
            }

            SandboxModel sandboxModel = sandboxService.getById(clientId,
                Id.valueOf(rewardUpdateRequest.getSandbox().getValue()));
            rewardService.updateReward(authorization, Id.valueOf(rewardId),
                rewardBuilder -> rewardBuilder.updateSandbox(sandboxModel));

            Person person = personService.getPerson(authorization, personId);
            ProcessedRawEvent processedRawEvent =
                clientRequestContextService.createBuilder(authorization, servletRequest)
                    .withEventName(EVENT_NAME_FOR_REWARD_UPDATE)
                    .withEventProcessing(processor -> {
                        String message = "Profile reward sandbox update via reward endpoints."
                            + " Reward id: " + rewardId + ", old sandbox: " + reward.getSandbox()
                            + ", new sandbox: " + rewardUpdateRequest.getSandbox();
                        processor.addLogMessage(message);
                    }).build().getProcessedRawEvent();

            consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person)
                .withLockDescription(LOCK_DESCRIPTION_FOR_REWARD_UPDATE)
                .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                    return new InputEventLockClosureResult<>(originalPerson);
                })
                .getPreEventSendingResult();
            return newRewardResponseMappingFunction(authorization, timeZone)
                .mapReward(rewardService.getReward(authorization, Id.valueOf(rewardId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", rewardId)
                .withCause(e)
                .build();
        } catch (SandboxNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.SANDBOX_NOT_FOUND)
                .addParameter("sandbox", rewardUpdateRequest.getSandbox())
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e)
                .build();
        } catch (EventProcessorException | LockClosureException | LockCouldNotBeAcquiredException
            | RewardIllegalStateTransitionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Optional<Integer> parsePeriodCount(Optional<String> periodCountAsString) throws RewardQueryRestException {
        try {
            return periodCountAsString
                .map(periodCount -> Optional.of(Integer.valueOf(periodCount)))
                .orElse(Optional.empty());
        } catch (NumberFormatException e) {
            throw RestExceptionBuilder.newBuilder(RewardQueryRestException.class)
                .withErrorCode(RewardQueryRestException.INVALID_PERIOD_COUNT)
                .withCause(e)
                .build();
        }
    }

    private List<RewardStateType> parseRewardStateTypes(String multipleState) throws RewardQueryRestException {
        if (StringUtils.isBlank(multipleState)) {
            return Collections.emptyList();
        } else {
            List<RewardStateType> rewardStateTypes = Lists.newArrayList();
            try {
                for (String rewardStateType : StringUtils.split(multipleState, COMMA)) {
                    rewardStateTypes.add(RewardStateType.valueOf(rewardStateType.toUpperCase()));
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                throw RestExceptionBuilder.newBuilder(RewardQueryRestException.class)
                    .withErrorCode(RewardQueryRestException.INVALID_REWARD_STATE)
                    .addParameter("reward_state_type", multipleState)
                    .withCause(e)
                    .build();
            }
            return rewardStateTypes;
        }
    }

    private List<String> parsePartnerIds(Optional<String> partnerRewardIds) {
        return partnerRewardIds.map(ids -> StringUtils.split(ids, COMMA))
            .map(ids -> Stream.of(ids))
            .orElse(Stream.empty())
            .collect(Collectors.toList());
    }

    private <T> List<Id<T>> parseIds(Optional<String> multipleIds) {
        return multipleIds.map(ids -> StringUtils.split(ids, COMMA))
            .map(ids -> Stream.of(ids))
            .orElse(Stream.empty())
            .map(id -> Id.<T>valueOf(id))
            .collect(Collectors.toList());
    }

    private List<RewardSupplierType> parseRewardSupplierTypes(Optional<String> multipleState)
        throws RewardQueryRestException {
        if (!multipleState.isPresent()) {
            return Collections.emptyList();
        } else {
            List<RewardSupplierType> rewardSupplierTypes = Lists.newArrayList();
            try {
                for (String rewardSupplierType : StringUtils.split(multipleState.get(), COMMA)) {
                    rewardSupplierTypes.add(RewardSupplierType.valueOf(rewardSupplierType.toUpperCase()));
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                throw RestExceptionBuilder.newBuilder(RewardQueryRestException.class)
                    .withErrorCode(RewardQueryRestException.UNSUPPORTED_REWARD_TYPE)
                    .withCause(e)
                    .build();
            }
            return rewardSupplierTypes;
        }
    }

    private RewardMappingFunction newRewardResponseMappingFunction(Authorization authorization, ZoneId timeZone) {
        return (reward) -> {
            Person person = null;
            try {
                person = personService.getPerson(authorization, reward.getPersonId());
            } catch (PersonNotFoundException ignored) {
                // ignored
            }
            return rewardRestMapper.toRewardResponse(reward, person, timeZone,
                getRewardSupplier(authorization, reward.getRewardSupplierId())
                    .map(RewardSupplier::getPartnerRewardSupplierId).filter(Optional::isPresent)
                    .map(Optional::get).orElse(null));
        };
    }

    private Optional<RewardSupplier> getRewardSupplier(Authorization authorization,
        Id<RewardSupplier> rewardSupplierId) {
        try {
            return Optional.of(rewardSupplierRewardSupplierCache.getActiveRewardSupplier(authorization.getClientId(),
                rewardSupplierId));
        } catch (RewardSupplierNotFoundException e) {
            return Optional.empty();
        }
    }

    private void validateLimits(int limit, int offset) throws QueryLimitsRestException {
        if (limit < 0) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_LIMIT)
                .addParameter("limit", limit)
                .build();
        }

        if (offset < 0) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_OFFSET)
                .addParameter("offset", offset)
                .build();
        }

        if (limit - offset > MAX_REWARD_FETCH_SIZE) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.MAX_FETCH_SIZE_1000)
                .addParameter("limit", limit)
                .addParameter("offset", offset)
                .build();
        }
    }

    private Optional<Instant> parseFromDate(@Nullable String timeInterval, ZoneId defaultTimeZone)
        throws RewardQueryRestException {
        if (timeInterval == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new DateTimeBuilder()
                .withDateString(StringUtils.substringBefore(timeInterval, ISO_8601_INTERVAL_DATES_SEPARATOR))
                .withDefaultTimezone(defaultTimeZone)
                .build().toInstant());
        } catch (DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(RewardQueryRestException.class)
                .withErrorCode(RewardQueryRestException.INVALID_TIME_INTERVAL)
                .withCause(e)
                .build();
        }
    }

    private Optional<Instant> parseToDate(@Nullable String timeInterval, ZoneId defaultTimeZone)
        throws RewardQueryRestException {
        if (timeInterval == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new DateTimeBuilder()
                .withDateString(StringUtils.substringAfter(timeInterval, ISO_8601_INTERVAL_DATES_SEPARATOR))
                .withDefaultTimezone(defaultTimeZone)
                .build().toInstant());
        } catch (DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(RewardQueryRestException.class)
                .withErrorCode(RewardQueryRestException.INVALID_TIME_INTERVAL)
                .withCause(e)
                .build();
        }
    }

    private boolean isNoopUpdateRequest(RewardUpdateRequest rewardUpdateRequest) {
        return rewardUpdateRequest.getSandbox().isOmitted();
    }

    @FunctionalInterface
    private interface RewardMappingFunction {
        RewardResponse mapReward(Reward reward) throws AuthorizationException;
    }
}
