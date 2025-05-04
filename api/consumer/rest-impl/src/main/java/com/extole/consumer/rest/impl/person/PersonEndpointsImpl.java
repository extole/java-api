package com.extole.consumer.rest.impl.person;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.ConsumerResponseMapper;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.PublicPersonStepResponse;
import com.extole.consumer.rest.person.PersonEndpoints;
import com.extole.consumer.rest.person.PersonRestException;
import com.extole.consumer.rest.person.PublicPersonResponse;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.PublicPerson;
import com.extole.person.service.profile.step.PublicPersonStep;

@Provider
public class PersonEndpointsImpl implements PersonEndpoints {

    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonService personService;
    private final HttpServletRequest servletRequest;
    private final ConsumerResponseMapper consumerResponseMapper;

    @Inject
    public PersonEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        PersonService personService,
        @Context HttpServletRequest servletRequest,
        ConsumerResponseMapper consumerResponseMapper) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.personService = personService;
        this.servletRequest = servletRequest;
        this.consumerResponseMapper = consumerResponseMapper;
    }

    @Override
    public PublicPersonResponse getPublicPerson(String accessToken, String personId)
        throws AuthorizationRestException, PersonRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            PublicPerson person = personService.getPublicPerson(authorization.getClientId(), Id.valueOf(personId));
            return new PublicPersonResponse(person.getId().getValue(), person.getFirstName(),
                Optional.ofNullable(person.getProfilePictureUrl()).map(URI::toString).orElse(null),
                person.getPublicData());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.INVALID_PERSON_ID)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public String getPersonProfilePictureUrl(String accessToken, String personId)
        throws AuthorizationRestException, PersonRestException {
        return getPublicPerson(accessToken, personId).getProfilePictureUrl();
    }

    @Override
    public List<PublicPersonStepResponse> getPublicPersonSteps(String accessToken, String personId,
        String campaignId, String programLabel, String stepName)
        throws AuthorizationRestException, PersonRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();

        try {
            List<PublicPersonStep> steps = personService
                .getPublicPerson(authorization.getClientId(), Id.valueOf(personId))
                .getPublicSteps()
                .stream()
                .filter(step -> StringUtils.isBlank(campaignId) ||
                    Objects.equal(campaignId, step.getCampaignId().map(Id::getValue).orElse(null)))
                .filter(step -> StringUtils.isBlank(programLabel)
                    || Objects.equal(programLabel, step.getProgramLabel().orElse(null)))
                .filter(step -> StringUtils.isBlank(stepName) || Objects.equal(stepName, step.getStepName()))
                .collect(Collectors.toList());
            return consumerResponseMapper.toPublicPersonStepResponse(steps);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.INVALID_PERSON_ID)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        }
    }
}
