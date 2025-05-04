package com.extole.client.rest.impl.consumer;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.authorization.service.person.PersonAuthorizationService;
import com.extole.client.rest.consumer.ConsumerEndpoints;
import com.extole.client.rest.consumer.ConsumerTokenResponse;
import com.extole.client.rest.consumer.UpgradeAuthorizationRestException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;

@Provider
public class ConsumerEndpointsImpl implements ConsumerEndpoints {
    private final PersonService personService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonAuthorizationService personAuthorizationService;

    @Inject
    public ConsumerEndpointsImpl(PersonService personService,
        ClientAuthorizationProvider authorizationProvider,
        PersonAuthorizationService personAuthorizationService) {
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.personAuthorizationService = personAuthorizationService;
    }

    @Override
    public ConsumerTokenResponse upgradeAuthorization(String accessToken, String consumerAccessToken)
        throws UserAuthorizationRestException, UpgradeAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        PersonAuthorization consumerAuthorization;
        try {
            consumerAuthorization =
                personAuthorizationService.getAuthorization(consumerAccessToken, authorization.getClientId());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UpgradeAuthorizationRestException.class)
                .withErrorCode(UpgradeAuthorizationRestException.INVALID_CONSUMER_TOKEN).withCause(e).build();
        }

        try {
            consumerAuthorization = personAuthorizationService.upgrade(authorization, consumerAuthorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UpgradeAuthorizationRestException.class)
                .withErrorCode(UpgradeAuthorizationRestException.UPGRADE_NOT_ALLOWED).withCause(e).build();
        }
        return createConsumerTokenResponse(consumerAuthorization);
    }

    @Override
    public ConsumerTokenResponse createAuthorization(String accessToken, Long personId)
        throws UserAuthorizationRestException, PersonRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId.toString()));
            Authorization consumerAuthorization =
                personAuthorizationService.authorizeVerifiedConsumer(authorization, person);
            return createConsumerTokenResponse(consumerAuthorization);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).withCause(e)
                .addParameter("person_id", personId.toString()).build();
        }
    }

    private ConsumerTokenResponse createConsumerTokenResponse(Authorization consumerAuthorization) {
        Set<String> scopes =
            consumerAuthorization.getScopes().stream().map(Authorization.Scope::name).collect(Collectors.toSet());
        return new ConsumerTokenResponse(consumerAuthorization.getAccessToken(),
            consumerAuthorization.getClientId().getValue(), scopes);
    }

}
