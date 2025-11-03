package com.extole.client.rest.impl.events;

import static com.extole.event.consumer.ConsumerEventDecorator.PARAMETER_NAME_TARGET;
import static com.extole.event.consumer.ConsumerEventDecorator.TARGET_PREFIX_CAMPAIGN_ID;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.actions.Action;
import com.extole.actions.ActionService;
import com.extole.actions.ActionType;
import com.extole.actions.review.ReviewStatusCauseType;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.events.ApprovalEndpoints;
import com.extole.client.rest.events.ConversionResponse;
import com.extole.client.rest.impl.events.ApprovalRequest.ApprovalException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.event.consumer.input.InputConsumerEvent;
import com.extole.event.pending.operation.PendingOperationStatus;
import com.extole.event.pending.operation.signal.SignalPendingOperationEvent;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.signal.service.event.SignalPendingOperationReadService;

@Provider
public class ApprovalEndpointsImpl implements ApprovalEndpoints {

    private static final String OPERATION_STATUS_SUCCEEDED = "SUCCEEDED";

    private static final String EVENT_PARAMETER_POLLING_ID = "polling_id";
    private static final String EVENT_PARAMETER_ACTION_ID = "action_id";
    private static final String EVENT_PARAMETER_NOTE = "note";
    private static final String EVENT_PARAMETER_FORCE = "force";
    private static final String EVENT_PARAMETER_CAUSE_TYPE = "cause_type";

    private static final String RESPONSE_STATUS_SUCCESS = "success";
    private static final String RESPONSE_STATUS_FAILURE = "failure";

    private static final String EVENT_NAME_FOR_APPROVAL = "legacy_approve";
    private static final String EVENT_NAME_FOR_DECLINE = "legacy_decline";

    private static final Logger LOG = LoggerFactory.getLogger(ApprovalEndpointsImpl.class);

    private final ActionService actionService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final ClientRequestContextService clientRequestContextService;
    private final HttpServletRequest servletRequest;
    private final ProgramDomainCache programDomainCache;
    private final SignalPendingOperationReadService signalReadService;

    private final int numberOfSignalPollingRetries;
    private final int signalPollingInterval;

    @Autowired
    public ApprovalEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ActionService actionService,
        PersonService personService,
        ConsumerEventSenderService consumerEventSenderService,
        ClientRequestContextService clientRequestContextService,
        @Context HttpServletRequest servletRequest,
        ProgramDomainCache programDomainCache,
        SignalPendingOperationReadService signalReadService,
        @Value("${signal.poll.number.of.retries:60}") int numberOfSignalPollingRetries,
        @Value("${signal.poll.interval.in.millis:1000}") int signalPollingInterval) {
        this.authorizationProvider = authorizationProvider;
        this.actionService = actionService;
        this.personService = personService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.clientRequestContextService = clientRequestContextService;
        this.servletRequest = servletRequest;
        this.programDomainCache = programDomainCache;
        this.signalReadService = signalReadService;
        this.numberOfSignalPollingRetries = numberOfSignalPollingRetries;
        this.signalPollingInterval = signalPollingInterval;
    }

    @Override
    public Response v3Approve(String accessToken, Long eventId, String partnerConversionId,
        String approvalEvent, String note, boolean force) throws UserAuthorizationRestException {
        return v2Approve(accessToken, eventId, partnerConversionId, approvalEvent, note, force);
    }

    @Override
    public Response v2Approve(String accessToken, Long eventId, String partnerConversionId,
        String approvalEvent, String note, boolean force) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(authorization, Authorization.Scope.USER_SUPPORT);
        try {
            ConversionResponse response =
                process(authorization, eventId, partnerConversionId, approvalEvent, note, force);
            if (response != null) {
                LOG.warn("success approval " + response + " for accessToken " + accessToken + " eventId "
                    + eventId + " partnerConversionId " + partnerConversionId + " approvalEvent " + approvalEvent
                    + " note " + note + " force " + force);
                return Response.ok()
                    .entity(response)
                    .header("Cache-Control", "no-cache")
                    .build();
            } else {
                LOG.warn("no content approval for accessToken " + accessToken + " eventId " + eventId
                    + " partnerConversionId " + partnerConversionId + " approvalEvent " + approvalEvent + " note "
                    + note + " force " + force);
                return Response.noContent()
                    .header("Cache-Control", "no-cache")
                    .build();
            }
        } catch (ApprovalException e) {
            LOG.warn("approval endpoints received ApprovalException for accessToken {} eventId {} " +
                "partnerConversionId {} approvalEvent {} note {} force {}",
                accessToken, eventId, partnerConversionId, approvalEvent, note, Boolean.valueOf(force), e);
            return Response.ok()
                .entity(new ConversionResponse(e.getMessage()))
                .header("Cache-Control", "no-cache")
                .build();
        } catch (AuthorizationException e) {
            LOG.warn("approval endpoints received AuthorizationException for accessToken {} eventId {} " +
                "partnerConversionId {} approvalEvent {} note {} force {}",
                accessToken, eventId, partnerConversionId, approvalEvent, note, Boolean.valueOf(force), e);
            return Response.ok()
                .entity(new ConversionResponse(e.getMessage()))
                .header("Cache-Control", "no-cache")
                .build();
        } catch (EventProcessorException e) {
            LOG.warn("approval endpoints received EventProcessorException for accessToken {} eventId {} " +
                "partnerConversionId {} approvalEvent {} note {} force {}",
                accessToken, eventId, partnerConversionId, approvalEvent, note, Boolean.valueOf(force), e);
            return Response.ok()
                .entity(new ConversionResponse(e.getMessage()))
                .header("Cache-Control", "no-cache")
                .build();
        } catch (PersonNotFoundException e) {
            LOG.warn("approval endpoints received PersonNotFoundException for accessToken {} eventId {} " +
                "partnerConversionId {} approvalEvent {} note {} force {}",
                accessToken, eventId, partnerConversionId, approvalEvent, note, Boolean.valueOf(force), e);
            return Response.ok()
                .entity(new ConversionResponse(e.getMessage()))
                .header("Cache-Control", "no-cache")
                .build();
        }
    }

    private ConversionResponse process(ClientAuthorization authorization, Long eventId, String partnerConversionId,
        String approvalEvent, String note, boolean force)
        throws ApprovalException, AuthorizationException, EventProcessorException, PersonNotFoundException {
        Id<ClientHandle> clientId = authorization.getClientId();
        Long clientIdAsLong = Long.valueOf(clientId.getValue());
        ApprovalRequest request =
            new ApprovalRequest(clientIdAsLong, eventId, partnerConversionId, approvalEvent, note, force);
        request.validate();
        Action targetAction = lookupAction(authorization, request);

        PublicProgram programDomain;
        Id<ProgramHandle> programId = Id.valueOf(targetAction.getSiteId());
        try {
            programDomain = programDomainCache.getById(programId, clientId);
        } catch (ProgramNotFoundException e) {
            throw new ApprovalException(
                "Unable to find program with id: " + programId + " for client: " + clientId + " for request: "
                    + request);
        }

        Person person;
        try {
            person = personService.getPerson(authorization, targetAction.getPersonId());
        } catch (PersonNotFoundException e) {
            throw new ApprovalException(
                "Unable to find person with id: " + targetAction.getPersonId() + " for client: " + clientId
                    + " for request: " + request);
        }

        switch (request.getApproveType()) {
            case APPROVE:
                return approveAction(authorization, person.getId(), targetAction, programDomain, request);
            case DECLINE:
                return declineAction(authorization, person.getId(), targetAction, programDomain, request);
            default:
                return null;
        }
    }

    private ConversionResponse approveAction(ClientAuthorization authorization, Id<PersonHandle> personId,
        Action action,
        PublicProgram programDomain, ApprovalRequest request)
        throws AuthorizationException, EventProcessorException, PersonNotFoundException {

        String pollingId = UUID.randomUUID().toString();

        Builder<String, String> approveParametersBuilder = ImmutableMap.<String, String>builder()
            .put(EVENT_PARAMETER_POLLING_ID, pollingId)
            .put(EVENT_PARAMETER_ACTION_ID, action.getActionId().getValue())
            .put(EVENT_PARAMETER_FORCE, Boolean.toString(request.getForce()))
            .put(EVENT_PARAMETER_CAUSE_TYPE, ReviewStatusCauseType.ADMIN_USER.name())
            .put(PARAMETER_NAME_TARGET, TARGET_PREFIX_CAMPAIGN_ID + action.getCampaignId().getValue());

        if (StringUtils.isNotBlank(request.getNote())) {
            approveParametersBuilder.put(EVENT_PARAMETER_NOTE, request.getNote());
        }
        Map<String, String> approveParameters = approveParametersBuilder.build();

        ClientRequestContext requestContext =
            clientRequestContextService.createBuilder(authorization, servletRequest)
                .withEventName(EVENT_NAME_FOR_APPROVAL)
                .withEventProcessing(configurator -> {
                    configurator.withClientDomain(programDomain);
                    approveParameters.forEach((key, value) -> {
                        configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, true, true));
                    });
                })
                .build();

        InputConsumerEvent inputConsumerEvent = consumerEventSenderService
            .createInputEvent(authorization, requestContext.getProcessedRawEvent(), personId)
            .send();

        Id<ClientHandle> clientId = authorization.getClientId();
        Id<?> inputEventId = inputConsumerEvent.getId();

        LOG.warn("Sent approval event for event_id: {} event type: {} client: {} input event id: {}",
            action.getActionId(), action.getActionType(), clientId, inputEventId);

        Optional<SignalPendingOperationEvent> approveStatus = getPollingStatus(authorization, inputEventId, pollingId);
        return approveStatus.map(signal -> toConversionResponse(signal, action))
            .orElse(new ConversionResponse(RESPONSE_STATUS_FAILURE, "Polling failed for id: " + pollingId,
                action.getActionId().getValue()));
    }

    private ConversionResponse declineAction(ClientAuthorization authorization, Id<PersonHandle> personId,
        Action action, PublicProgram programDomain, ApprovalRequest request)
        throws AuthorizationException, EventProcessorException, PersonNotFoundException {

        String pollingId = UUID.randomUUID().toString();

        Builder<String, String> declineParametersBuilder = ImmutableMap.<String, String>builder()
            .put(EVENT_PARAMETER_POLLING_ID, pollingId)
            .put(EVENT_PARAMETER_ACTION_ID, action.getActionId().getValue())
            .put(EVENT_PARAMETER_CAUSE_TYPE, ReviewStatusCauseType.ADMIN_USER.name())
            .put(PARAMETER_NAME_TARGET, TARGET_PREFIX_CAMPAIGN_ID + action.getCampaignId().getValue());

        if (StringUtils.isNotBlank(request.getNote())) {
            declineParametersBuilder.put(EVENT_PARAMETER_NOTE, request.getNote());
        }
        Map<String, String> declineParameters = declineParametersBuilder.build();

        ClientRequestContext requestContext =
            clientRequestContextService.createBuilder(authorization, servletRequest)
                .withEventName(EVENT_NAME_FOR_DECLINE)
                .withEventProcessing(configurator -> {
                    configurator.withClientDomain(programDomain);
                    declineParameters.forEach((key, value) -> {
                        configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, true, true));
                    });
                })
                .build();

        InputConsumerEvent inputConsumerEvent = consumerEventSenderService
            .createInputEvent(authorization, requestContext.getProcessedRawEvent(), personId)
            .send();

        Id<ClientHandle> clientId = authorization.getClientId();
        Id<?> inputEventId = inputConsumerEvent.getId();

        LOG.warn("Sent decline event for event_id: {} event type: {} client: {} input event id: {}",
            action.getActionId(), action.getActionType(), clientId, inputEventId);

        Optional<SignalPendingOperationEvent> declineStatus = getPollingStatus(authorization, inputEventId, pollingId);
        return declineStatus.map(signal -> toConversionResponse(signal, action))
            .orElse(new ConversionResponse(RESPONSE_STATUS_FAILURE, "Polling failed for id: " + pollingId,
                action.getActionId().getValue()));
    }

    private Action lookupAction(Authorization authorization, ApprovalRequest request) throws ApprovalException {
        if (request.getEventId() != null) {
            Optional<Action> targetAction =
                actionService.findById(authorization, Id.valueOf(String.valueOf(request.getEventId())));
            if (targetAction.isEmpty()) {
                throw new ApprovalException("Cannot locate action using event_id " + request.getEventId());
            }
            return targetAction.get();
        }
        if (request.getPartnerConversionId() != null) {
            Optional<Action> targetAction = actionService.findLatestByActionTypeAndPartnerConversionId(authorization,
                ActionType.PURCHASE, request.getPartnerConversionId());
            if (targetAction.isEmpty()) { // PURCHASE
                LOG.warn(
                    "TRIAG-1281 Cannot locate approval action using partnerConversiondId: {}," +
                        " client: {}, request: {}",
                    request.getPartnerConversionId(), authorization.getClientId().getValue(), request);
                throw new ApprovalException(
                    "Cannot locate action using partner_conversion_id " + request.getPartnerConversionId());
            }
            return targetAction.get();
        }
        throw new ApprovalException("Missing the event_id and partner_conversion_id - both are empty");
    }

    private Optional<SignalPendingOperationEvent> getPollingStatus(Authorization authorization, Id<?> inputEventId,
        String pollingId) {
        try {
            for (int i = 0; i < numberOfSignalPollingRetries; i++) {
                TimeUnit.MILLISECONDS.sleep(signalPollingInterval);

                List<SignalPendingOperationEvent> signalEvents =
                    signalReadService.get(authorization, Id.valueOf(pollingId));
                if (signalEvents.size() > 0 && PendingOperationStatus.SUCCEEDED == signalEvents.get(0).getStatus()) {
                    return Optional.of(signalEvents.get(0));
                }
            }
            LOG.warn("Polling failed for client: {} input event id: {} polling id: {}", authorization.getClientId(),
                inputEventId, pollingId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Polling interrupted for client: {} input event id: {} polling id: {}",
                authorization.getClientId(),
                inputEventId, pollingId, e);
        }
        return Optional.empty();
    }

    private ConversionResponse toConversionResponse(SignalPendingOperationEvent signal, Action action) {
        String operationStatus = signal.getData().get("status").toString();
        String message = signal.getData().get("message").toString();
        String actionId = action.getActionId().getValue();

        if (OPERATION_STATUS_SUCCEEDED.equals(operationStatus)) {
            return new ConversionResponse(RESPONSE_STATUS_SUCCESS, message, actionId);
        } else {
            return new ConversionResponse(RESPONSE_STATUS_FAILURE, message, actionId);
        }
    }

    private void checkAccessRights(Authorization authorization, Authorization.Scope scope)
        throws UserAuthorizationRestException {
        if (!authorization.isAuthorized(authorization.getClientId(), scope)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

}
