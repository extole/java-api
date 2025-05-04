package com.extole.client.rest.impl.person;

import java.util.Optional;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.google.common.annotations.Beta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;

@Provider
@Singleton
@Beta
@Path("/v5/test/persons")
public class PersonVersionEndpointsImpl {
    private static final Logger LOG = LoggerFactory.getLogger(PersonVersionEndpointsImpl.class);

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final FullPersonService fullPersonService;

    @Autowired
    public PersonVersionEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        FullPersonService fullPersonService) {
        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
        this.fullPersonService = fullPersonService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{person_id}/versions")
    public PersonVersionResponse get(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("person_id") String personId)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person runtimePerson = personService.getPerson(authorization, Id.valueOf(personId));
            Optional<String> persistedVersion = Optional.empty();
            try {
                Person fullPerson = fullPersonService.getPerson(authorization, Id.valueOf(personId));
                persistedVersion = Optional.of(fullPerson.getVersion());
            } catch (PersonNotFoundException e) {
                LOG.debug("Person not found in full person service for client {} and person {} with runtime version " +
                    "{}", authorization.getClientId(), personId, runtimePerson.getVersion());
            }
            return new PersonVersionResponse(runtimePerson.getVersion(), persistedVersion);
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

}
