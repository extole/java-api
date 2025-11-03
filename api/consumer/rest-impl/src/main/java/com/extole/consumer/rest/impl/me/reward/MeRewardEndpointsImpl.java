package com.extole.consumer.rest.impl.me.reward;

import static com.extole.consumer.rest.impl.me.reward.MeRewardEndpointsImpl.RewardFilter.partnerEventIdFilter;
import static com.extole.consumer.rest.impl.me.reward.MeRewardEndpointsImpl.RewardFilter.pollingIdFilter;
import static com.extole.consumer.rest.impl.me.reward.MeRewardEndpointsImpl.RewardFilter.rewardNameFilter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestErrorBuilder;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.JourneyKey;
import com.extole.consumer.rest.me.reward.ClaimRewardError;
import com.extole.consumer.rest.me.reward.ClaimRewardPollingResponse;
import com.extole.consumer.rest.me.reward.ClaimRewardRequest;
import com.extole.consumer.rest.me.reward.ClaimRewardResponse;
import com.extole.consumer.rest.me.reward.ClaimRewardRestException;
import com.extole.consumer.rest.me.reward.MeRewardEndpoints;
import com.extole.consumer.rest.me.reward.PollingRewardResponse;
import com.extole.consumer.rest.me.reward.PollingRewardResponse.Status;
import com.extole.consumer.rest.me.reward.RewardResponse;
import com.extole.consumer.rest.me.reward.RewardResponse.FaceValueType;
import com.extole.consumer.rest.me.reward.RewardRestException;
import com.extole.consumer.rest.signal.step.QualityResults;
import com.extole.consumer.rest.signal.step.QualityRuleResult;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.ConsumerRequestMetadata;
import com.extole.consumer.service.ConsumerRequestMetadataService;
import com.extole.consumer.service.reward.ConsumerRewardPendingOperation;
import com.extole.consumer.service.reward.ConsumerRewardService;
import com.extole.consumer.service.reward.InvalidCampaignException;
import com.extole.consumer.service.reward.RewarderNameInvalidException;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.event.pending.operation.signal.StepSignalPendingOperationEvent;
import com.extole.event.pending.operation.signal.step.StepSignal;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.shared.reward.supplier.ArchivedRewardSupplierCache;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.reward.PersonReward;
import com.extole.signal.service.event.StepSignalPendingOperationReadService;

@Provider
public class MeRewardEndpointsImpl implements MeRewardEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(MeRewardEndpointsImpl.class);

    private static final String POLLING_ID = "polling_id";
    private static final String REWARD_NAME = "reward_name";
    private static final String PARTNER_EVENT_ID = "partner_event_id";

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerRewardService consumerRewardService;
    private final ConsumerRequestMetadataService consumerRequestMetadataService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final ArchivedRewardSupplierCache archivedRewardSupplierCache;
    private final StepSignalPendingOperationReadService stepSignalPendingOperationReadService;
    private final Map<String, Function<String, Predicate<PersonReward>>> rewardFilters;

    @Autowired
    public MeRewardEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        ConsumerRewardService consumerRewardService,
        ConsumerRequestMetadataService consumerRequestMetadataService,
        ConsumerEventSenderService consumerEventSenderService,
        ArchivedRewardSupplierCache archivedRewardSupplierCache,
        StepSignalPendingOperationReadService stepSignalPendingOperationReadService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.consumerRewardService = consumerRewardService;
        this.consumerRequestMetadataService = consumerRequestMetadataService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.archivedRewardSupplierCache = archivedRewardSupplierCache;
        this.stepSignalPendingOperationReadService = stepSignalPendingOperationReadService;
        this.rewardFilters = ImmutableMap.of(POLLING_ID, pollingIdFilter(), REWARD_NAME, rewardNameFilter(),
            PARTNER_EVENT_ID, partnerEventIdFilter());
    }

    @Override
    public ClaimRewardResponse claimReward(String accessToken, ClaimRewardRequest request)
        throws AuthorizationRestException, ClaimRewardRestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withEventName(ConsumerEventName.EXTOLE_REWARD_CLAIM.getEventName())
            .withAccessToken(accessToken)
            .build();

        PersonAuthorization authorization = requestContext.getAuthorization();
        try {
            consumerEventSenderService
                .createInputEvent(authorization, requestContext.getProcessedRawEvent())
                .send();
        } catch (AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }

        try {
            ConsumerRequestMetadata metadata = consumerRequestMetadataService.createBuilder(requestContext)
                .withCampaignId(request.getCampaignId()).build();

            Id<ConsumerRewardPendingOperation> pollingId = consumerRewardService.claimReward(authorization,
                request.getRewardeeName(), metadata);

            return new ClaimRewardResponse(pollingId.getValue());
        } catch (InvalidCampaignException e) {
            throw RestExceptionBuilder.newBuilder(ClaimRewardRestException.class)
                .withErrorCode(ClaimRewardRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", request.getCampaignId())
                .withCause(e).build();
        } catch (RewarderNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClaimRewardRestException.class)
                .withErrorCode(ClaimRewardRestException.INVALID_REWARDER_NAME)
                .addParameter("rewarder_name", request.getRewardeeName())
                .withCause(e).build();
        }
    }

    @Override
    public ClaimRewardPollingResponse getClaimRewardStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        Authorization authorization = getAuthorizationFromRequest(accessToken);
        ConsumerRewardPendingOperation pendingOperation =
            consumerRewardService.getClaimRewardStatus(authorization, Id.valueOf(pollingId));
        ClaimRewardError error = Optional.ofNullable(getClaimRewardErrorCode(pendingOperation))
            .map(code -> new ClaimRewardError(
                new RestErrorBuilder()
                    .withUniqueId(pollingId)
                    .withHttpStatusCode(code.getHttpCode())
                    .withCode(code.getName())
                    .withMessage(code.getMessage())
                    .withParameters(Collections.singletonMap("polling_id", pollingId))
                    .build()))
            .orElse(null);
        return new ClaimRewardPollingResponse(pollingId, PollingStatus.valueOf(pendingOperation.getStatus().name()),
            pendingOperation.getRewardId(), error);
    }

    @Override
    public RewardResponse getReward(String accessToken, String rewardId)
        throws AuthorizationRestException, RewardRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        PersonReward reward = authorization.getIdentity().getRewards().stream()
            .filter(personReward -> Id.valueOf(rewardId).equals(personReward.getRewardId()))
            .findFirst().orElseThrow(() -> RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.INVALID_REWARD_ID)
                .addParameter("reward_id", rewardId).build());
        try {
            RewardSupplier rewardSupplier = archivedRewardSupplierCache.getRewardSupplier(
                authorization.getClientId(),
                Id.valueOf(reward.getRewardSupplierId().getValue()));
            return toRewardResponse(authorization, reward, rewardSupplier);
        } catch (RewardSupplierNotFoundException e) {
            LOG.error("Unable to resolve RewardSupplier for client_id : " + authorization.getClientId()
                + ", reward_id : " + rewardId, e);
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.INVALID_REWARD_ID)
                .addParameter("reward_id", rewardId).build();
        }
    }

    @Override
    public List<RewardResponse> getRewards(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        List<RewardResponse> rewardResponses = new ArrayList<>();
        for (PersonReward reward : authorization.getIdentity().getRewards()) {
            try {
                RewardSupplier rewardSupplier = archivedRewardSupplierCache.getRewardSupplier(
                    authorization.getClientId(),
                    Id.valueOf(reward.getRewardSupplierId().getValue()));
                rewardResponses.add(toRewardResponse(authorization, reward, rewardSupplier));
            } catch (RewardSupplierNotFoundException e) {
                // TODO ENG-9089, replace with error
                LOG.warn("Unable to resolve RewardSupplier for client_id : " + authorization.getClientId()
                    + ", reward_id : " + reward.getRewardId(), e);
            }
        }
        return rewardResponses;
    }

    private PersonAuthorization getAuthorizationFromRequest(String accessToken) throws AuthorizationRestException {
        return consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
    }

    private RewardResponse toRewardResponse(Authorization authorization, PersonReward personReward,
        RewardSupplier rewardSupplier) {
        List<String> tags;
        switch (personReward.getPersonRole()) {
            case ADVOCATE:
                tags = Collections.singletonList(RewardResponse.TAG_ADVOCATE);
                break;
            case FRIEND:
                tags = Collections.singletonList(RewardResponse.TAG_FRIEND);
                break;
            default:
                tags = Collections.emptyList();
        }

        return new RewardResponse(personReward.getRewardId().getValue(),
            personReward.getState().name().toLowerCase(), personReward.getPartnerRewardId().orElse(null),
            personReward.getPartnerRewardId().orElse(null),
            personReward.getFaceValue().setScale(2, BigDecimal.ROUND_HALF_UP).toString(),
            personReward.getFaceValue().setScale(2, BigDecimal.ROUND_HALF_UP).toString(),
            FaceValueType.valueOf(personReward.getFaceValueType().name()),
            personReward.getRewardedDate().toString(),
            personReward.getRewardedDate().toString(),
            personReward.getRewardedDate().toString(),
            personReward.getCampaignId().getValue(),
            personReward.getProgramLabel(),
            personReward.getSandbox(),
            personReward.getRewardSlots().stream().collect(Collectors.toList()),
            tags,
            getType(authorization.getClientId(), rewardSupplier),
            Optional.ofNullable(personReward.getPartnerRewardKeyType())
                .map(partnerRewardKeyType -> RewardResponse.RewardType.valueOf(partnerRewardKeyType))
                .orElse(RewardResponse.RewardType.ID),
            personReward.getRewardSupplierId().getValue(),
            rewardSupplier.getPartnerRewardSupplierId().orElse(null),
            personReward.getRewardName().orElse(null),
            personReward.getExpiryDate().map(expiryDate -> expiryDate.toString()).orElse(null),
            personReward.getRedeemedDate().map(redeemedDate -> redeemedDate.toString()).orElse(null),
            personReward.getJourneyName().getValue(),
            personReward.getJourneyKey().map(value -> new JourneyKey(value.getName(), value.getValue())));
    }

    private RewardResponse.Type getType(Id<ClientHandle> clientId, RewardSupplier rewardSupplier) {
        switch (rewardSupplier.getRewardSupplierType()) {
            case CUSTOM_REWARD:
                return RewardResponse.Type.CUSTOM_REWARD;
            case TANGO_V2:
                return RewardResponse.Type.TANGO_V2;
            case PAYPAL_PAYOUTS:
                return RewardResponse.Type.PAYPAL_PAYOUTS;
            case MANUAL_COUPON:
            case SALESFORCE_COUPON:
                return RewardResponse.Type.COUPON;
            default:
                throw new RewardSupplierRuntimeException("Invalid rewardType given for rewardSupplier: "
                    + rewardSupplier + " client: " + clientId);
        }
    }

    public static class RewardSupplierRuntimeException extends RuntimeException {

        public RewardSupplierRuntimeException(String message) {
            super(message);
        }
    }

    @Override
    public PollingRewardResponse getRewardStatus(String accessToken, Optional<String> pollingId,
        Optional<String> rewardName, Optional<String> partnerEventId) throws AuthorizationRestException,
        RewardRestException {

        PersonAuthorization authorization = getAuthorizationFromRequest(accessToken);
        Optional<Predicate<PersonReward>> rewardFilter =
            determineRewardFilter(ImmutableMap.<String, Optional<String>>builder()
                .put(POLLING_ID, pollingId)
                .put(REWARD_NAME, rewardName)
                .put(PARTNER_EVENT_ID, partnerEventId)
                .build());

        if (!rewardFilter.isPresent()) {
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.INVALID_REWARD_FILTER_EXCEPTION).build();
        }

        Optional<PersonReward> personReward = authorization.getIdentity().getRewards().stream()
            .filter(rewardFilter.get())
            .findFirst();
        Optional<ClaimRewardError> error = getClaimRewardError(pollingId, authorization, personReward);

        List<QualityResults> qualityResults = Collections.emptyList();
        if (pollingId.isPresent() || personReward.isPresent()) {
            Optional<String> rewardPollingId =
                !pollingId.isPresent() ? personReward.map(reward -> reward.getData().get(POLLING_ID)) : pollingId;
            if (rewardPollingId.isPresent()) {
                qualityResults = getQualityResults(rewardPollingId.get(), authorization);
            }
        }
        Status status = determineRewardStatus(personReward, error, qualityResults);
        return new PollingRewardResponse(personReward.map(reward -> toRewardResponse(authorization, reward)),
            status, qualityResults);
    }

    private Status determineRewardStatus(Optional<PersonReward> personReward, Optional<ClaimRewardError> error,
        List<QualityResults> qualityResults) {
        Status status = Status.PENDING;
        if (personReward.isPresent()) {
            status = Status.SUCCEEDED;
        }
        if (error.isPresent() || (!qualityResults.isEmpty() && qualityResults.stream()
            .noneMatch(result -> result.getScore().equals(QualityResults.QualityScore.HIGH)))) {
            status = Status.FAILED;
        }
        return status;
    }

    private List<QualityResults> getQualityResults(String pollingId, Authorization authorization) {
        List<StepSignalPendingOperationEvent> pendingOperations =
            stepSignalPendingOperationReadService.get(authorization, Id.valueOf(pollingId));

        return pendingOperations.stream()
            .filter(pendingOperation -> pendingOperation.getSignal() != null)
            .map(pendingOperation -> toQualityRuleResult(pendingOperation.getSignal()))
            .collect(Collectors.toList());
    }

    private QualityResults toQualityRuleResult(StepSignal signal) {
        List<QualityRuleResult> qualityRuleResults = signal
            .getQualityResults()
            .getQualityRuleResults()
            .stream()
            .map(item -> new QualityRuleResult(item.getRuleType(),
                QualityResults.QualityScore.valueOf(item.getQualityScore().name())))
            .collect(Collectors.toList());

        return new QualityResults(QualityResults.QualityScore.valueOf(signal.getQualityResults().getScore().name()),
            qualityRuleResults);
    }

    private Optional<ClaimRewardError> getClaimRewardError(Optional<String> pollingId, Authorization authorization,
        Optional<PersonReward> personReward) {
        Optional<ClaimRewardError> error = Optional.empty();
        if (!personReward.isPresent() && pollingId.isPresent()) {
            ConsumerRewardPendingOperation pendingOperation =
                consumerRewardService.getClaimRewardStatus(authorization, Id.valueOf(pollingId.get()));
            ErrorCode<?> errorCode = getClaimRewardErrorCode(pendingOperation);
            error = Optional.ofNullable(errorCode).map(code -> new ClaimRewardError(new RestErrorBuilder()
                .withUniqueId(pollingId.get())
                .withHttpStatusCode(code.getHttpCode())
                .withCode(code.getName())
                .withMessage(code.getMessage())
                .withParameters(Collections.singletonMap(POLLING_ID, pollingId))
                .build()));
        }
        return error;
    }

    @Nullable
    private ErrorCode<ClaimRewardError> getClaimRewardErrorCode(ConsumerRewardPendingOperation pendingOperation) {
        if (pendingOperation.getStatus().isFailure()) {
            switch (pendingOperation.getError()) {
                case NO_REWARD:
                    return ClaimRewardError.NO_REWARD;
                default:
                    return ClaimRewardError.SOFTWARE_ERROR;
            }
        }
        return null;
    }

    private RewardResponse toRewardResponse(Authorization authorization, PersonReward reward) {
        try {
            RewardSupplier rewardSupplier = archivedRewardSupplierCache.getRewardSupplier(
                authorization.getClientId(),
                Id.valueOf(reward.getRewardSupplierId().getValue()));
            return toRewardResponse(authorization, reward, rewardSupplier);
        } catch (RewardSupplierNotFoundException e) {
            throw new RuntimeException(String.format("RewardSupplier not found, id=%s, clientId=%s, rewardId=%s",
                reward.getRewardSupplierId(), authorization.getClientId(), reward.getId()), e);
        }
    }

    private Optional<Predicate<PersonReward>> determineRewardFilter(Map<String, Optional<String>> inputFilters) {
        Optional<Pair<Function<String, Predicate<PersonReward>>, String>> rewardFilterClass = inputFilters.entrySet()
            .stream().filter(item -> item.getValue().isPresent())
            .findFirst()
            .map(item -> Pair.of(rewardFilters.get(item.getKey()), item.getValue().get()));

        if (rewardFilterClass.isPresent()) {
            Function<String, Predicate<PersonReward>> filter = rewardFilterClass.get().getKey();
            String filterValue = rewardFilterClass.get().getValue();
            return Optional.of(filter.apply(filterValue));
        }
        return Optional.empty();
    }

    static class RewardFilter {

        static Function<String, Predicate<PersonReward>> pollingIdFilter() {
            return (pollingId) -> personReward -> personReward.getData().containsKey(POLLING_ID) &&
                personReward.getData().get(POLLING_ID).equals(pollingId);
        }

        static Function<String, Predicate<PersonReward>> rewardNameFilter() {
            return (rewardName) -> personReward -> personReward.getRewardName().isPresent()
                && personReward.getRewardName().get().equals(rewardName);
        }

        static Function<String, Predicate<PersonReward>> partnerEventIdFilter() {
            return (partnerEventId) -> personReward -> personReward.getData().containsKey(PARTNER_EVENT_ID) &&
                personReward.getData().get(PARTNER_EVENT_ID).equals(partnerEventId);
        }
    }
}
