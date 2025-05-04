package com.extole.consumer.rest.impl.optout;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.security.EncrypterException;
import com.extole.common.security.SecureEmailService;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.EventProcessorConfigurator;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.optout.OptoutEndpoints;
import com.extole.consumer.rest.optout.OptoutRequest;
import com.extole.consumer.rest.optout.OptoutResponse;
import com.extole.consumer.rest.optout.OptoutRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.id.Id;
import com.extole.optout.client.OptoutUpdateClient;
import com.extole.optout.client.OptoutUpdateClient.OptoutType;
import com.extole.optout.external.ExternalOptoutClientException;
import com.extole.optout.external.OptoutService;

@Provider
public class OptoutEndpointsImpl implements OptoutEndpoints {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^([^<>]+)@([^<>]+)(>\\s*)?$");
    private static final Logger LOG = LoggerFactory.getLogger(OptoutEndpointsImpl.class);

    private final SecureEmailService secureEmailService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final HttpServletRequest servletRequest;
    private final OptoutService optoutService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final OptoutUpdateClient optoutUpdateClient;

    @Autowired
    public OptoutEndpointsImpl(
        SecureEmailService secureEmailService,
        ConsumerRequestContextService consumerRequestContextService,
        @Context HttpServletRequest servletRequest,
        OptoutService optoutService,
        ConsumerEventSenderService consumerEventSenderService,
        OptoutUpdateClient optoutUpdateClient) {
        this.secureEmailService = secureEmailService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.servletRequest = servletRequest;
        this.optoutService = optoutService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.optoutUpdateClient = optoutUpdateClient;
    }

    @Override
    public OptoutResponse updateOptout(String accessToken, String secureEmail, OptoutRequest request)
        throws OptoutRestException {
        Id<ClientHandle> clientId = consumerRequestContextService.extractProgramDomain(servletRequest).getClientId();
        try {
            String email;
            if (isPlainEmail(secureEmail)) {
                LOG.warn("Update un-secure optout email, client_id={}", clientId);
                email = secureEmail;
            } else {
                email = secureEmailService.readSecureEmail(secureEmail);
            }
            OptoutAction optoutAction;
            if (request.getOptout().booleanValue()) {
                optoutUpdateClient.addOptout(clientId, email, remapOptoutType(request));
                optoutAction = OptoutAction.UNSUBSCRIBED;
            } else {
                optoutUpdateClient.removeOptout(clientId, email);
                optoutAction = OptoutAction.SUBSCRIBED;
            }
            produceOptoutEvent(accessToken, clientId, optoutAction, email, request);
            return new OptoutResponse(request.getOptout());
        } catch (EncrypterException e) {
            throw RestExceptionBuilder.newBuilder(OptoutRestException.class)
                .withErrorCode(OptoutRestException.INVALID_SECURE_EMAIL)
                .addParameter("secure_email", secureEmail)
                .withCause(e)
                .build();
        }
    }

    private static OptoutType remapOptoutType(OptoutRequest request) {
        return Optional.ofNullable(request.getType())
            .map(optoutType -> OptoutType.valueOf(optoutType.name()))
            .orElse(OptoutType.NOT_APPLICABLE);
    }

    private void produceOptoutEvent(String accessToken, Id<ClientHandle> clientId,
        OptoutAction optoutAction, String email, OptoutRequest request) {
        try {
            ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
                .withEventName(optoutAction.getEventName())
                .withReplaceableAccessToken(accessToken)
                .withEventProcessing(configurator -> {
                    addDataIfValueNotNull(configurator, "email", email);
                    addDataIfValueNotNull(configurator, "list_type", request.getListType());
                    addDataIfValueNotNull(configurator, "list_name", request.getListName());
                    addDataIfValueNotNull(configurator, "source", request.getSource());
                })
                .build();

            consumerEventSenderService
                .createInputEvent(requestContext.getAuthorization(), requestContext.getProcessedRawEvent(),
                    requestContext.getAuthorization().getIdentity())
                .send();
        } catch (AuthorizationRestException | AuthorizationException e) {
            LOG.warn("Failed to produce optout event={} for clientId={}", optoutAction, clientId, e);
        }
    }

    @Override
    public OptoutResponse getOptout(String secureEmail) throws OptoutRestException {
        Id<ClientHandle> clientId = consumerRequestContextService.extractProgramDomain(servletRequest).getClientId();

        try {
            String email;
            if (isPlainEmail(secureEmail)) {
                LOG.warn("Read un-secure optout email, client_id={}", clientId);
                email = secureEmail;
            } else {
                email = secureEmailService.readSecureEmail(secureEmail);
            }
            Boolean optout = isOptout(clientId, email);
            return new OptoutResponse(optout);
        } catch (EncrypterException e) {
            throw RestExceptionBuilder.newBuilder(OptoutRestException.class)
                .withErrorCode(OptoutRestException.INVALID_SECURE_EMAIL)
                .addParameter("secure_email", secureEmail)
                .withCause(e)
                .build();
        }
    }

    private boolean isPlainEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private Boolean isOptout(Id<ClientHandle> clientId, String email) {
        try {
            return Boolean.valueOf(optoutService.isOptout(clientId, email));
        } catch (ExternalOptoutClientException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private static void addDataIfValueNotNull(EventProcessorConfigurator configurator, String key, String value) {
        if (value != null) {
            configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
        }
    }

    enum OptoutAction {
        SUBSCRIBED("list_optin"),
        UNSUBSCRIBED("list_optout");

        private final String eventName;

        OptoutAction(String eventName) {
            this.eventName = eventName;
        }

        String getEventName() {
            return eventName;
        }
    }

}
