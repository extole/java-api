package com.extole.client.rest.impl.rewards.custom;

import java.time.ZoneId;
import java.util.Optional;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.rewards.RewardRestMapper;
import com.extole.client.rest.rewards.RewardResponse;
import com.extole.client.rest.rewards.RewardRestException;
import com.extole.client.rest.rewards.custom.CustomRewardEndpoints;
import com.extole.client.rest.rewards.custom.FailedRewardRequest;
import com.extole.client.rest.rewards.custom.FulfilledAndSentRewardRequest;
import com.extole.client.rest.rewards.custom.FulfilledRewardRequest;
import com.extole.client.rest.rewards.custom.RedeemedRewardRequest;
import com.extole.client.rest.rewards.custom.SentRewardRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.reward.supplier.RewardSupplier;
import com.extole.model.entity.reward.supplier.built.BuiltRewardSupplier;
import com.extole.model.entity.reward.supplier.built.custom.reward.BuiltCustomRewardSupplier;
import com.extole.model.service.reward.supplier.RewardSupplierNotFoundException;
import com.extole.model.shared.reward.supplier.BuiltRewardSupplierCache;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.rewards.service.CustomRewardService;
import com.extole.rewards.service.LockCouldNotBeAcquiredException;
import com.extole.rewards.service.Reward;
import com.extole.rewards.service.RewardIllegalStateTransitionException;
import com.extole.rewards.service.RewardNotFoundException;
import com.extole.rewards.service.state.fail.FailedRewardStateBuilder;
import com.extole.rewards.service.state.fulfillment.FulfilledRewardStateBuilder;
import com.extole.rewards.service.state.redeem.RedeemedRewardStateBuilder;
import com.extole.rewards.service.state.send.SentRewardStateBuilder;

@Provider
public class CustomRewardEndpointsImpl implements CustomRewardEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final CustomRewardService customRewardService;
    private final RewardRestMapper rewardRestMapper;
    private final BuiltRewardSupplierCache builtRewardSupplierCache;

    @Autowired
    public CustomRewardEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        CustomRewardService customRewardService,
        RewardRestMapper rewardRestMapper,
        BuiltRewardSupplierCache builtRewardSupplierCache) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
        this.customRewardService = customRewardService;
        this.rewardRestMapper = rewardRestMapper;
        this.builtRewardSupplierCache = builtRewardSupplierCache;
    }

    @Override
    public RewardResponse fulfilled(String accessToken, String rewardId,
        FulfilledRewardRequest fulfilledRewardRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Reward reward = customRewardService.getReward(authorization, Id.valueOf(rewardId));
            customRewardService.updateReward(authorization, Id.valueOf(rewardId),
                (existingCustomRewardBuilder -> {
                    FulfilledRewardStateBuilder fulfilledRewardStateBuilder =
                        existingCustomRewardBuilder.addFulfillment()
                            .withSendEmail(rewardSupplierAutoSendEmail(reward));

                    if (fulfilledRewardRequest.getPartnerRewardId() != null) {
                        fulfilledRewardStateBuilder.withPartnerRewardId(fulfilledRewardRequest.getPartnerRewardId());
                    }

                    if (fulfilledRewardRequest.getCostCode() != null) {
                        fulfilledRewardStateBuilder.withCostCode(fulfilledRewardRequest.getCostCode());
                    }

                    if (fulfilledRewardRequest.getSuccess() != null) {
                        fulfilledRewardStateBuilder
                            .withSuccessful(fulfilledRewardRequest.getSuccess().booleanValue());
                    }

                    if (fulfilledRewardRequest.getMessage() != null) {
                        fulfilledRewardStateBuilder.withMessage(fulfilledRewardRequest.getMessage());
                    }

                    existingCustomRewardBuilder.save();
                }));

            return newRewardResponseMappingFunction(authorization, timeZone).mapReward(Id.valueOf(rewardId));
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
    public RewardResponse sent(String accessToken, String rewardId, SentRewardRequest sentRewardRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            customRewardService.updateReward(authorization, Id.valueOf(rewardId),
                (existingCustomRewardBuilder -> {
                    SentRewardStateBuilder sentRewardStateBuilder = existingCustomRewardBuilder.addSend();

                    if (sentRewardRequest.getPartnerRewardSentId() != null) {
                        sentRewardStateBuilder.withPartnerRewardSentId(sentRewardRequest.getPartnerRewardSentId());
                    }

                    if (sentRewardRequest.getSuccess() != null) {
                        sentRewardStateBuilder.withSuccessful(sentRewardRequest.getSuccess().booleanValue());
                    }

                    if (sentRewardRequest.getMessage() != null) {
                        sentRewardStateBuilder.withMessage(sentRewardRequest.getMessage());
                    }

                    existingCustomRewardBuilder.save();

                }));

            return newRewardResponseMappingFunction(authorization, timeZone).mapReward(Id.valueOf(rewardId));
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
    public RewardResponse fulfilledAndSent(String accessToken, String rewardId,
        FulfilledAndSentRewardRequest fulfilledAndSentRewardRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {

            customRewardService.updateReward(authorization, Id.valueOf(rewardId),
                (existingCustomRewardBuilder -> {
                    FulfilledRewardStateBuilder fulfilledRewardStateBuilder =
                        existingCustomRewardBuilder.addFulfillment()
                            .withSendEmail(false);

                    if (fulfilledAndSentRewardRequest.getPartnerRewardId() != null) {
                        fulfilledRewardStateBuilder
                            .withPartnerRewardId(fulfilledAndSentRewardRequest.getPartnerRewardId());
                    }

                    if (fulfilledAndSentRewardRequest.getCostCode() != null) {
                        fulfilledRewardStateBuilder
                            .withCostCode(fulfilledAndSentRewardRequest.getCostCode());
                    }

                    if (fulfilledAndSentRewardRequest.getMessage() != null) {
                        fulfilledRewardStateBuilder.withMessage(fulfilledAndSentRewardRequest.getMessage());
                    }

                    SentRewardStateBuilder rewardSentStateBuilder =
                        existingCustomRewardBuilder.addSend();

                    if (fulfilledAndSentRewardRequest.getPartnerRewardSentId() != null) {
                        rewardSentStateBuilder
                            .withPartnerRewardSentId(fulfilledAndSentRewardRequest.getPartnerRewardSentId());
                    }

                    if (fulfilledAndSentRewardRequest.getEmail() != null) {
                        rewardSentStateBuilder.withEmail(fulfilledAndSentRewardRequest.getEmail());
                    }

                    existingCustomRewardBuilder.save();

                }));

            return newRewardResponseMappingFunction(authorization, timeZone).mapReward(Id.valueOf(rewardId));

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
    public RewardResponse redeemed(String accessToken, String rewardId,
        RedeemedRewardRequest redeemedRewardRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            customRewardService.updateReward(authorization, Id.valueOf(rewardId),
                (existingCustomRewardBuilder -> {
                    RedeemedRewardStateBuilder redeemedRewardStateBuilder =
                        existingCustomRewardBuilder.addRedeem();

                    if (redeemedRewardRequest.getPartnerRewardRedeemId() != null) {
                        redeemedRewardStateBuilder
                            .withPartnerRewardRedeemId(redeemedRewardRequest.getPartnerRewardRedeemId());
                    }

                    if (redeemedRewardRequest.getMessage() != null) {
                        redeemedRewardStateBuilder.withMessage(redeemedRewardRequest.getMessage());
                    }

                    existingCustomRewardBuilder.save();

                }));

            return newRewardResponseMappingFunction(authorization, timeZone).mapReward(Id.valueOf(rewardId));
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
    public RewardResponse failed(String accessToken, String rewardId, FailedRewardRequest failedRewardRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {

            customRewardService.updateReward(authorization, Id.valueOf(rewardId),
                (existingCustomRewardBuilder -> {
                    FailedRewardStateBuilder failedRewardStateBuilder = existingCustomRewardBuilder.addFail();

                    if (failedRewardRequest.getMessage() != null) {
                        failedRewardStateBuilder.withMessage(failedRewardRequest.getMessage());
                    }

                    existingCustomRewardBuilder.save();

                }));

            return newRewardResponseMappingFunction(authorization, timeZone).mapReward(Id.valueOf(rewardId));
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

    private RewardMappingFunction newRewardResponseMappingFunction(Authorization authorization, ZoneId timeZone) {
        return (rewardId) -> {
            Reward reward = customRewardService.getReward(authorization, rewardId);
            Person person = null;
            try {
                person = personService.getPerson(authorization, reward.getPersonId());
            } catch (PersonNotFoundException ignored) {
                // ignored
            }
            return rewardRestMapper.toRewardResponse(reward,
                person,
                timeZone,
                getRewardSupplier(authorization, reward.getRewardSupplierId())
                    .map(BuiltRewardSupplier::getPartnerRewardSupplierId).filter(Optional::isPresent)
                    .map(Optional::get).orElse(null));
        };
    }

    private Optional<BuiltRewardSupplier> getRewardSupplier(Authorization authorization,
        Id<RewardSupplier> rewardSupplierId) {
        try {
            return Optional.of(builtRewardSupplierCache.getActiveRewardSupplier(authorization.getClientId(),
                rewardSupplierId));
        } catch (RewardSupplierNotFoundException e) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    private interface RewardMappingFunction {
        RewardResponse mapReward(Id<Reward> rewardId) throws RewardNotFoundException, AuthorizationException;
    }

    private boolean rewardSupplierAutoSendEmail(Reward reward) {
        try {
            return ((BuiltCustomRewardSupplier) builtRewardSupplierCache.getActiveRewardSupplier(reward.getClientId(),
                reward.getRewardSupplierId()))
                    .isRewardEmailAutoSendEnabled();
        } catch (RewardSupplierNotFoundException e) {
            return false;
        }
    }

}
