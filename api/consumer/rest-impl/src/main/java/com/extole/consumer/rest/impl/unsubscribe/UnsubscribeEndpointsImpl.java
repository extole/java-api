package com.extole.consumer.rest.impl.unsubscribe;

import static com.extole.consumer.event.service.processor.EventData.Source.BACKEND;
import static com.extole.optout.client.OptoutUpdateClient.OptoutType.NOT_APPLICABLE;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.security.EncrypterException;
import com.extole.common.security.SecureEmailService;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.unsubscribe.UnsubscribeEndpoints;
import com.extole.consumer.rest.unsubscribe.UnsubscribeRequest;
import com.extole.consumer.rest.unsubscribe.UnsubscribeResponse;
import com.extole.consumer.rest.unsubscribe.UnsubscribeRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.id.Id;
import com.extole.optout.client.OptoutUpdateClient;

@Provider
public class UnsubscribeEndpointsImpl implements UnsubscribeEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeEndpointsImpl.class);

    public static final String EVENT_NAME = "list_optout";

    private final SecureEmailService secureEmailService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final HttpServletRequest servletRequest;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final OptoutUpdateClient optoutUpdateClient;

    @Autowired
    public UnsubscribeEndpointsImpl(
        SecureEmailService secureEmailService,
        ConsumerRequestContextService consumerRequestContextService,
        @Context HttpServletRequest servletRequest,
        ConsumerEventSenderService consumerEventSenderService,
        OptoutUpdateClient optoutUpdateClient) {
        this.secureEmailService = secureEmailService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.servletRequest = servletRequest;
        this.consumerEventSenderService = consumerEventSenderService;
        this.optoutUpdateClient = optoutUpdateClient;
    }

    @Override
    public UnsubscribeResponse unsubscribe(UnsubscribeRequest request) throws UnsubscribeRestException {
        if (StringUtils.isBlank(request.getEncryptedEmail())) {
            throw RestExceptionBuilder.newBuilder(UnsubscribeRestException.class)
                .withErrorCode(UnsubscribeRestException.MISSING_ENCRYPTED_EMAIL)
                .build();
        }
        if (StringUtils.isBlank(request.getListName())) {
            throw RestExceptionBuilder.newBuilder(UnsubscribeRestException.class)
                .withErrorCode(UnsubscribeRestException.MISSING_LIST_NAME)
                .build();
        }

        Id<ClientHandle> clientId = consumerRequestContextService.extractProgramDomain(servletRequest).getClientId();
        try {
            String email = secureEmailService.readSecureEmail(request.getEncryptedEmail());

            Boolean unsubscribed = optoutUpdateClient.addOptout(clientId, email, NOT_APPLICABLE);
            if (unsubscribed) {
                fireEvent(clientId, email);
            }
            return new UnsubscribeResponse(unsubscribed);
        } catch (EncrypterException e) {
            throw RestExceptionBuilder.newBuilder(UnsubscribeRestException.class)
                .withErrorCode(UnsubscribeRestException.INVALID_ENCRYPTED_EMAIL)
                .addParameter("encrypted_email", request.getEncryptedEmail())
                .withCause(e)
                .build();
        }
    }

    private void fireEvent(Id<ClientHandle> clientId, String email) {
        try {
            ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withEventName(EVENT_NAME)
                .withEventProcessing(configurator -> {
                    configurator.addData(new EventData("email", email, BACKEND, true, false));
                })
                .build();
            consumerEventSenderService
                .createInputEvent(requestContext.getAuthorization(), requestContext.getProcessedRawEvent(),
                    requestContext.getAuthorization().getIdentity())
                .send();
        } catch (Exception e) {
            LOG.error("Failed to produce {} event for clientId={}", EVENT_NAME, clientId, e);
        }
    }

}
