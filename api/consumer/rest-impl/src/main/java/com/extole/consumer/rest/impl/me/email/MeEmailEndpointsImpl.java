package com.extole.consumer.rest.impl.me.email;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestErrorBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.email.MeEmailEndpoints;
import com.extole.consumer.rest.me.email.SendEmailError;
import com.extole.consumer.rest.me.email.SendEmailPollingResponse;
import com.extole.consumer.rest.me.email.SendEmailRequest;
import com.extole.consumer.rest.me.email.SendEmailResponse;
import com.extole.consumer.rest.me.email.SendEmailRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.ConsumerRequestMetadata;
import com.extole.consumer.service.ConsumerRequestMetadataService;
import com.extole.consumer.service.email.person.InvalidZoneNameException;
import com.extole.consumer.service.email.person.MissingZoneNameException;
import com.extole.consumer.service.email.person.SendPersonEmailPendingOperation;
import com.extole.consumer.service.email.person.SendPersonEmailService;
import com.extole.id.Id;
import com.extole.person.service.shareable.PersonNotRewardableException;

@Provider
public class MeEmailEndpointsImpl implements MeEmailEndpoints {
    private final HttpServletRequest servletRequest;
    private final SendPersonEmailService sendPersonEmailService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerRequestMetadataService consumerRequestMetadataService;

    @Inject
    public MeEmailEndpointsImpl(@Context HttpServletRequest servletRequest,
        SendPersonEmailService sendPersonEmailService,
        ConsumerRequestContextService consumerRequestContextService,
        ConsumerRequestMetadataService consumerRequestMetadataService) {
        this.servletRequest = servletRequest;
        this.sendPersonEmailService = sendPersonEmailService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.consumerRequestMetadataService = consumerRequestMetadataService;
    }

    @Override
    public SendEmailResponse sendEmail(String accessToken, SendEmailRequest request)
        throws AuthorizationRestException, SendEmailRestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withEventProcessing(configurator -> {
                for (Map.Entry<String, String> entry : request.getData().entrySet()) {
                    configurator.addData(
                        new EventData(entry.getKey(), entry.getValue(), EventData.Source.REQUEST_BODY, false, true));
                }
            })
            .withAccessToken(accessToken)
            .build();
        PersonAuthorization authorization = requestContext.getAuthorization();
        if (request.getData() == null || !request.getData().containsKey("campaign_id")) {
            throw RestExceptionBuilder.newBuilder(SendEmailRestException.class)
                .withErrorCode(SendEmailRestException.MISSING_CAMPAIGN_ID).build();
        }
        ConsumerRequestMetadata metadata =
            consumerRequestMetadataService.createBuilder(requestContext).withData(request.getData()).build();
        try {
            Id<SendPersonEmailPendingOperation> pollingId =
                sendPersonEmailService.sendEmail(authorization, request.getZoneName(),
                    Id.valueOf(request.getData().get("campaign_id")), metadata,
                    requestContext.getProcessedRawEvent().getSandbox());
            return new SendEmailResponse(pollingId.getValue());
        } catch (MissingZoneNameException e) {
            throw RestExceptionBuilder.newBuilder(SendEmailRestException.class)
                .withErrorCode(SendEmailRestException.MISSING_ZONE_NAME)
                .withCause(e).build();
        } catch (InvalidZoneNameException e) {
            throw RestExceptionBuilder.newBuilder(SendEmailRestException.class)
                .withErrorCode(SendEmailRestException.INVALID_ZONE_NAME)
                .addParameter("zone_name", request.getZoneName())
                .withCause(e).build();
        } catch (PersonNotRewardableException e) {
            throw RestExceptionBuilder.newBuilder(SendEmailRestException.class)
                .withErrorCode(SendEmailRestException.PROFILE_NOT_SET)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        }
    }

    @Override
    public SendEmailPollingResponse getSendEmailStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        SendPersonEmailPendingOperation pendingOperation =
            sendPersonEmailService.getSendEmailStatus(authorization, Id.valueOf(pollingId));

        ErrorCode<?> errorCode = null;
        if (pendingOperation.getStatus().isFailure()) {
            switch (pendingOperation.getError()) {
                case EMAIL_SEND_FAILED_RETRYING:
                    errorCode = SendEmailError.EMAIL_SEND_FAILED_RETRYING;
                    break;
                case EMAIL_SEND_FAILED:
                    errorCode = SendEmailError.EMAIL_SEND_FAILED;
                    break;
                case EMAIL_TARGETING_FAILED:
                    errorCode = SendEmailError.EMAIL_TARGETING_FAILED;
                    break;
                default:
                    errorCode = SendEmailError.SOFTWARE_ERROR;
                    break;
            }
        }

        SendEmailError error = Optional.ofNullable(errorCode)
            .map(code -> new SendEmailError(
                new RestErrorBuilder()
                    .withUniqueId(pollingId)
                    .withHttpStatusCode(code.getHttpCode())
                    .withCode(code.getName())
                    .withMessage(code.getMessage())
                    .withParameters(Collections.singletonMap("polling_id", pollingId))
                    .build()))
            .orElse(null);
        return new SendEmailPollingResponse(pollingId, PollingStatus.valueOf(pendingOperation.getStatus().name()),
            Optional.ofNullable(pendingOperation.getEmailId()).map(Id::getValue).orElse(null),
            Optional.ofNullable(pendingOperation.getActionId()).map(Id::getValue).orElse(null), error);
    }
}
