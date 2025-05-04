package com.extole.client.rest.impl.rewards.paypal.payouts;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.rewards.RewardRestException;
import com.extole.client.rest.rewards.paypal.payouts.PayPalPayoutsListenerEndpoints;
import com.extole.client.rest.rewards.paypal.payouts.item.PayPalPayoutsItemChangedRequest;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.rewards.service.LockCouldNotBeAcquiredException;
import com.extole.rewards.service.RewardIllegalStateTransitionException;
import com.extole.rewards.service.RewardNotFoundException;
import com.extole.rewards.service.paypal.PayPalPayoutsRewardService;
import com.extole.rewards.service.paypal.WebhookPayPalPayoutsRewardUpdate;

@Provider
public class PayPalPayoutsListenerEndpointsImpl implements PayPalPayoutsListenerEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PayPalPayoutsListenerEndpointsImpl.class);

    private final PayPalPayoutsRewardService payPalPayoutsRewardService;

    @Autowired
    public PayPalPayoutsListenerEndpointsImpl(PayPalPayoutsRewardService payPalPayoutsRewardService) {
        this.payPalPayoutsRewardService = payPalPayoutsRewardService;
    }

    @Override
    public Response handle(PayPalPayoutsItemChangedRequest request)
        throws UserAuthorizationRestException, RewardRestException {

        try {
            payPalPayoutsRewardService.updateReward(buildServiceRequest(request));
            return Response.ok().build();
        } catch (LockCouldNotBeAcquiredException e) {
            LOG.warn("Could not acquire lock on reward {} in order to handle event {}", request.getRewardId(), request,
                e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            LOG.error("Access denied to reward {} client {} while handling event {} ", request.getRewardId(),
                request.getClientId(), request, e);
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (RewardNotFoundException e) {
            LOG.warn("Reward {} client {} not found not found while handling event {} ", request.getRewardId(),
                request.getClientId(), request, e);
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_NOT_FOUND)
                .addParameter("reward_id", request.getRewardId())
                .withCause(e)
                .build();
        } catch (RewardIllegalStateTransitionException e) {
            LOG.warn("Reward {} client {} not transitioned to another state during handling event {} ",
                request.getRewardId(), request.getClientId(), request, e);
            throw RestExceptionBuilder.newBuilder(RewardRestException.class)
                .withErrorCode(RewardRestException.REWARD_ILLEGAL_STATE_TRANSITION)
                .addParameter("current_state", e.getCurrentState())
                .withCause(e)
                .build();
        }
    }

    private WebhookPayPalPayoutsRewardUpdate buildServiceRequest(PayPalPayoutsItemChangedRequest request) {
        return WebhookPayPalPayoutsRewardUpdate.Builder.newBuilder()
            .withEventId(request.getEventId())
            .withBatchId(request.getBatchId())
            .withReceiver(request.getReceiver())
            .withRewardId(Id.valueOf(request.getRewardId()))
            .withClientId(Id.valueOf(request.getClientId()))
            .withTransactionStatus(
                WebhookPayPalPayoutsRewardUpdate.TransactionStatus.valueOf(request.getTransactionStatus().name()))
            .withSummary(request.getSummary())
            .withErrorCode(request.getErrorCode())
            .withErrorMessage(request.getErrorMessage())
            .withAuthenticationAlgorithm(request.getAuthenticationAlgorithm())
            .withPublicKeyCertificateUrl(request.getPublicKeyCertificateUrl())
            .withTransmissionId(request.getTransmissionId())
            .withTransmissionSignature(request.getTransmissionSignature())
            .withTransmissionTime(request.getTransmissionTime())
            .withPayload(request.getPayload())
            .build();
    }

}
