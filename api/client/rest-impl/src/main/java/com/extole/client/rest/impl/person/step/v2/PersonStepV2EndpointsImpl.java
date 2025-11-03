package com.extole.client.rest.impl.person.step.v2;

import static com.extole.authorization.service.Authorization.Scope.CLIENT_SUPERUSER;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.StepScope;
import com.extole.client.rest.person.step.v2.PersonStepV2Endpoints;
import com.extole.client.rest.person.step.v2.PersonStepV2RestException;
import com.extole.client.rest.person.step.v2.PersonStepV2UpdateRequest;
import com.extole.client.rest.person.step.v2.PersonStepValidationV2RestException;
import com.extole.client.rest.person.v2.PartnerEventIdV2Request;
import com.extole.client.rest.person.v2.PartnerEventIdV2Response;
import com.extole.client.rest.person.v2.PersonStepV2Response;
import com.extole.client.rest.person.v4.PersonDataV4Response;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.step.PartnerEventId;
import com.extole.person.service.profile.step.PersonStep;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.person.service.profile.step.PersonStepNotFoundException;
import com.extole.sandbox.Container;

@Provider
public class PersonStepV2EndpointsImpl implements PersonStepV2Endpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PersonStepV2EndpointsImpl.class);

    private static final LockDescription LOCK_DESCRIPTION = new LockDescription("person-step-v2-endpoints-lock");
    private static final String EVENT_NAME_FOR_STEP_UPDATE = "extole.person.step.update";

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonStepV2EndpointsImpl(
        PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public List<PersonStepV2Response> getSteps(String accessToken, String personId, String campaignId,
        String programLabel, String stepName, StepQuality quality, PartnerEventIdV2Request partnerEventId,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personService.getPerson(authorization, Id.valueOf(personId))
                .getSteps().stream()
                .filter(step -> StringUtils.isBlank(campaignId)
                    || Objects.equal(campaignId, step.getCampaignId().map(Id::getValue).orElse(null)))
                .filter(step -> StringUtils.isBlank(programLabel)
                    || Objects.equal(programLabel, step.getProgramLabel().orElse(null)))
                .filter(step -> StringUtils.isBlank(stepName) || Objects.equal(stepName, step.getStepName()))
                .filter(
                    step -> quality == null || Objects.equal(quality, StepQuality.valueOf(step.getQuality().name())))
                .filter(step -> partnerEventId == null || StringUtils.isBlank(partnerEventId.getName()) ||
                    (step.getPartnerEventId().isPresent() &&
                        StringUtils.equalsIgnoreCase(PartnerEventId.sanitizeName(partnerEventId.getName()),
                            step.getPartnerEventId().get().getName())))
                .filter(step -> partnerEventId == null || StringUtils.isBlank(partnerEventId.getValue()) ||
                    (step.getPartnerEventId().isPresent()
                        && StringUtils.equalsIgnoreCase(PartnerEventId.sanitizeValue(partnerEventId.getValue()),
                            step.getPartnerEventId().get().getValue())))
                .map(personStep -> toPersonStepResponse(personStep, timeZone))
                .collect(Collectors.toList());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public PersonStepV2Response getStep(String accessToken, String personId, String stepId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonStepV2RestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<PersonStepV2Response> stepResponse =
                personService.getPerson(authorization, Id.valueOf(personId))
                    .getSteps().stream()
                    .filter(step -> step.getId().getValue().equals(stepId))
                    .map(personStep -> toPersonStepResponse(personStep, timeZone))
                    .findFirst();

            if (stepResponse.isPresent()) {
                return stepResponse.get();
            }
            throw RestExceptionBuilder.newBuilder(PersonStepV2RestException.class)
                .withErrorCode(PersonStepV2RestException.STEP_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("step_id", stepId)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
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
    public void updateStep(String accessToken, String personId, String stepId,
        PersonStepV2UpdateRequest stepUpdateRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonStepV2RestException,
        PersonStepValidationV2RestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        verifySuperUserAuthorization(authorization);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            PersonStep stepToUpdate = person.getSteps().stream()
                .filter(step -> step.getId().getValue().equals(stepId))
                .findFirst().orElse(null);

            if (stepToUpdate == null) {
                throw RestExceptionBuilder.newBuilder(PersonStepV2RestException.class)
                    .withErrorCode(PersonStepV2RestException.STEP_NOT_FOUND)
                    .addParameter("person_id", personId)
                    .addParameter("step_id", stepId)
                    .build();
            }

            if (isNoopUpdateRequest(stepUpdateRequest)) {
                return;
            }

            ProcessedRawEvent processedRawEvent =
                clientRequestContextService.createBuilder(authorization, servletRequest)
                    .withEventName(EVENT_NAME_FOR_STEP_UPDATE)
                    .withEventProcessing(processor -> {
                        processor.addLogMessage("Person step updated via client step v2 endpoints."
                            + " Step id: " + stepToUpdate.getId() + "."
                            + " Old container: " + stepToUpdate.getContainer() + "."
                            + " New container: " + stepUpdateRequest.getContainer() + ".");
                    }).build().getProcessedRawEvent();

            consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person.getId())
                .withLockDescription(LOCK_DESCRIPTION)
                .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                    try {
                        personBuilder.updateStep(Id.valueOf(stepId))
                            .withContainer(new Container(stepUpdateRequest.getContainer()));
                    } catch (PersonStepNotFoundException e) {
                        throw new LockClosureException(e);
                    }
                    Person updatedPerson = personBuilder.save();
                    return new InputEventLockClosureResult<>(updatedPerson);
                });
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (LockClosureException e) {
            LOG.error("Unable to update person in lock context for clientId={}, personId={}",
                authorization.getClientId(), personId, e);
            throw RestExceptionBuilder.newBuilder(PersonStepValidationV2RestException.class)
                .withErrorCode(PersonStepValidationV2RestException.PERSON_STEP_UPDATE_ERROR)
                .addParameter("person_id", personId)
                .addParameter("step_id", stepId)
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private static boolean isNoopUpdateRequest(PersonStepV2UpdateRequest stepUpdateRequest) {
        return StringUtils.isEmpty(stepUpdateRequest.getContainer());
    }

    private PersonStepV2Response toPersonStepResponse(PersonStep step, ZoneId timeZone) {
        Map<String, Object> privateData = step.getPrivateData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue));
        Map<String, Object> publicData = step.getPublicData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue));
        Map<String, Object> clientData = step.getClientData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue));
        List<PersonDataV4Response> data = Lists.newArrayList();
        privateData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PRIVATE, value)));
        publicData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.PUBLIC, value)));
        clientData.forEach((name, value) -> data.add(new PersonDataV4Response(name, PersonDataScope.CLIENT, value)));

        return new PersonStepV2Response(
            step.getId().getValue(),
            step.getCampaignId().map(Id::getValue).orElse(null),
            step.getProgramLabel().orElse(null),
            step.getContainer().getName(),
            step.getStepName(),
            step.getEventId().getValue(),
            step.getEventDate().atZone(timeZone),
            step.getValue().map(this::formatFaceValue).orElse(null),
            step.getPartnerEventId()
                .map(value -> new PartnerEventIdV2Response(value.getName(), value.getValue()))
                .orElse(null),
            StepQuality.valueOf(step.getQuality().name()),
            data,
            StepScope.valueOf(step.getScope().name()),
            step.getCauseEventId().getValue(),
            step.getRootEventId().getValue());
    }

    private String formatFaceValue(BigDecimal faceValue) {
        return faceValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private static void verifySuperUserAuthorization(Authorization authorization)
        throws UserAuthorizationRestException {
        if (!authorization.getScopes().contains(CLIENT_SUPERUSER)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }

}
