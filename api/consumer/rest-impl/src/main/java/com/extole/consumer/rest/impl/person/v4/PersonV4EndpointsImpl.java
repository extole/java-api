package com.extole.consumer.rest.impl.person.v4;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.person.v4.PersonRestV4Exception;
import com.extole.consumer.rest.person.v4.PersonV4Endpoints;
import com.extole.consumer.rest.person.v4.PublicPersonV4Response;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.PublicPerson;

@Provider
public class PersonV4EndpointsImpl implements PersonV4Endpoints {

    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonService personService;
    private final HttpServletRequest servletRequest;

    @Inject
    public PersonV4EndpointsImpl(PersonService personService,
        ConsumerRequestContextService consumerRequestContextService,
        @Context HttpServletRequest servletRequest) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.personService = personService;
        this.servletRequest = servletRequest;
    }

    @Override
    public PublicPersonV4Response getPublicPerson(String accessToken, String personId)
        throws AuthorizationRestException, PersonRestV4Exception {

        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            PublicPerson person =
                personService.getPublicPerson(authorization.getClientId(), Id.valueOf(personId));
            return new PublicPersonV4Response(person.getId().getValue(), person.getFirstName(),
                Optional.ofNullable(person.getProfilePictureUrl()).map(URI::toString).orElse(null),
                person.getPublicData());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestV4Exception.class)
                .withErrorCode(PersonRestV4Exception.INVALID_PERSON_ID)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public String getPersonProfilePictureUrl(String accessToken, String personId)
        throws AuthorizationRestException, PersonRestV4Exception {
        return getPublicPerson(accessToken, personId).getProfilePictureUrl();
    }

}
