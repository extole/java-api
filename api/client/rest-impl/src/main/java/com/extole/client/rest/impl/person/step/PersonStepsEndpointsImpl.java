package com.extole.client.rest.impl.person.step;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonStepDataResponse;
import com.extole.client.rest.person.PersonStepsListRestException;
import com.extole.client.rest.person.StepQuality;
import com.extole.client.rest.person.StepScope;
import com.extole.client.rest.person.step.PersonStepResponse;
import com.extole.client.rest.person.step.PersonStepsEndpoints;
import com.extole.client.rest.person.step.PersonStepsListRequest;
import com.extole.common.journey.JourneyName;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.StepName;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.InvalidFlowPersonStepQueryException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.StepQueryBuilder;
import com.extole.person.service.profile.step.PersonStep;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.sandbox.Container;

@Provider
public class PersonStepsEndpointsImpl implements PersonStepsEndpoints {
    private static final String ALL_CONTAINERS = "*";

    private final ClientAuthorizationProvider authorizationProvider;
    private final FullPersonService fullPersonService;

    @Autowired
    public PersonStepsEndpointsImpl(FullPersonService fullPersonService,
        ClientAuthorizationProvider authorizationProvider) {
        this.fullPersonService = fullPersonService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<PersonStepResponse> getSteps(String accessToken, String personId,
        PersonStepsListRequest personStepsListRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonStepsListRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Multimap<String, String> journeyKeyValues =
                parseKeyValuePairs(personStepsListRequest.getJourneyKeyValues(), "journey_key_values");

            StepQueryBuilder builder = fullPersonService.createStepQueryBuilder(userAuthorization, Id.valueOf(personId))
                .withStepNames(personStepsListRequest.getNames().stream()
                    .map(StepName::valueOf)
                    .collect(Collectors.toSet()))
                .withCampaignIds(personStepsListRequest.getCampaignIds().stream()
                    .map(id -> Id.<CampaignHandle>valueOf(id))
                    .collect(Collectors.toSet()))
                .withProgramLabels(new HashSet<>(personStepsListRequest.getPrograms()))
                .withJourneyNames(personStepsListRequest.getJourneyNames().stream()
                    .map(JourneyName::valueOf)
                    .collect(Collectors.toSet()))
                .withDataKeys(new HashSet<>(personStepsListRequest.getDataKeys()))
                .withEventIds(personStepsListRequest.getEventIds().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(Id::valueOf)
                    .collect(Collectors.toSet()))
                .withCauseEventIds(personStepsListRequest.getCauseEventIds().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(Id::valueOf)
                    .collect(Collectors.toSet()))
                .withRootEventIds(personStepsListRequest.getRootEventIds().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(Id::valueOf)
                    .collect(Collectors.toSet()))
                .withJourneyKeyNames(personStepsListRequest.getJourneyKeyNames())
                .withJourneyKeyValues(journeyKeyValues);

            if (!personStepsListRequest.getContainers().contains(ALL_CONTAINERS)
                && !personStepsListRequest.getContainers().isEmpty()) {
                builder.withContainers(personStepsListRequest.getContainers().stream()
                    .map(Container::new)
                    .collect(Collectors.toSet()));
            }

            personStepsListRequest.getIsPrimary().ifPresent(builder::withPrimary);
            builder.withOffset(personStepsListRequest.getOffset()
                .orElse(Integer.valueOf(PersonStepsListRequest.DEFAULT_OFFSET)).intValue());
            builder.withLimit(personStepsListRequest.getLimit()
                .orElse(Integer.valueOf(PersonStepsListRequest.DEFAULT_LIMIT)).intValue());
            return builder.list().stream()
                .map(step -> toPersonStepResponse(step, timeZone))
                .collect(Collectors.toList());
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
        } catch (InvalidFlowPersonStepQueryException e) {
            throw RestExceptionBuilder.newBuilder(PersonStepsListRestException.class)
                .withErrorCode(PersonStepsListRestException.INVALID_FLOW_QUERY)
                .withCause(e)
                .build();
        }
    }

    private PersonStepResponse toPersonStepResponse(PersonStep step, ZoneId timeZone) {
        Map<String, PersonStepDataResponse> data = Maps.newHashMap();
        step.getPrivateData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue))
            .forEach((name, value) -> data.put(name, new PersonStepDataResponse(name, PersonDataScope.PRIVATE, value)));
        step.getPublicData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue))
            .forEach((name, value) -> data.put(name, new PersonStepDataResponse(name, PersonDataScope.PUBLIC, value)));
        step.getClientData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue))
            .forEach((name, value) -> data.put(name, new PersonStepDataResponse(name, PersonDataScope.CLIENT, value)));

        PersonStepResponse.Builder builder = PersonStepResponse.builder();
        builder
            .withId(step.getId().getValue())
            .withName(step.getStepName())
            .withEventId(step.getEventId().getValue())
            .withQuality(StepQuality.valueOf(step.getQuality().name()))
            .withData(data)
            .withScope(StepScope.valueOf(step.getScope().name()))
            .withEventDate(step.getEventDate().atZone(timeZone))
            .withCreatedDate(step.getCreatedDate().atZone(timeZone))
            .withUpdatedDate(step.getUpdatedDate().atZone(timeZone))
            .withContainer(step.getContainer().getName())
            .withIsPrimary(!step.isAliasName())
            .withCauseEventId(step.getCauseEventId().getValue())
            .withRootEventId(step.getRootEventId().getValue());

        step.getProgramLabel().ifPresent(builder::withProgram);
        step.getCampaignId().ifPresent(id -> builder.withCampaignId(id.getValue()));
        step.getJourneyName().ifPresent(name -> builder.withJourneyName(name.getValue()));
        step.getJourneyKey().ifPresent(
            journeyKey -> builder.withJourneyKey(new JourneyKey(journeyKey.getName(), journeyKey.getValue())));

        return builder.build();
    }

    private Multimap<String, String> parseKeyValuePairs(List<String> valuesToParse, String parameterName)
        throws PersonStepsListRestException {
        ImmutableListMultimap.Builder<String, String> resultBuilder = ImmutableListMultimap.<String, String>builder();

        for (String valueToParse : valuesToParse) {
            int delimiterLocation = valueToParse.indexOf(":");
            if (delimiterLocation > 0) {
                resultBuilder.put(valueToParse.substring(0, delimiterLocation),
                    valueToParse.substring(delimiterLocation + 1));
            } else {
                throw RestExceptionBuilder.newBuilder(PersonStepsListRestException.class)
                    .withErrorCode(PersonStepsListRestException.VALUE_DOES_NOT_FOLLOW_PATTERN)
                    .addParameter("parameter", parameterName)
                    .addParameter("value", valueToParse)
                    .build();
            }
        }

        return resultBuilder.build();
    }

}
