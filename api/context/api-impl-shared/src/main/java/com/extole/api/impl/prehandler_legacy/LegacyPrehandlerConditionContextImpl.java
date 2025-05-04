package com.extole.api.impl.prehandler_legacy;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.event.InputConsumerEvent;
import com.extole.api.impl.person.AuthorizationImpl;
import com.extole.api.impl.person.PersonImpl;
import com.extole.api.person.Authorization;
import com.extole.api.person.Person;
import com.extole.api.prehandler_legacy.LegacyPrehandlerConditionContext;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.sandbox.SandboxService;

@Deprecated // TODO to be removed in ENG-13399
public final class LegacyPrehandlerConditionContextImpl implements LegacyPrehandlerConditionContext {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyPrehandlerConditionContextImpl.class);

    private final SandboxService sandboxService;
    private final PersonService personService;
    private final com.extole.authorization.service.Authorization backendAuthorization;
    private final Authorization authorization;
    private final InputConsumerEvent inputEvent;
    private final ExecutionLogger logger;

    public LegacyPrehandlerConditionContextImpl(
        SandboxService sandboxService,
        PersonService personService,
        com.extole.authorization.service.Authorization baseAuthorization,
        com.extole.authorization.service.Authorization backendAuthorization, InputConsumerEvent inputEvent,
        ExecutionLogger logger) {
        this.sandboxService = sandboxService;
        this.personService = personService;
        this.backendAuthorization = backendAuthorization;
        this.inputEvent = inputEvent;
        this.logger = logger;
        this.authorization = buildContextAuthorization(baseAuthorization);
    }

    @Override
    public Authorization getAuthorization() {
        return authorization;
    }

    @Override
    public InputConsumerEvent getEvent() {
        return inputEvent;
    }

    @Deprecated // TODO remove it because it is available in InputEvent, ENG-10118
    @Override
    public String getEventName() {
        return inputEvent.getEventName();
    }

    @Override
    @Nullable
    public Person findPersonById(String personId) {
        return getPersonProfile(personId).map(value -> new PersonImpl(value, sandboxService)).orElse(null);
    }

    @Override
    @Nullable
    public Person findPersonByLookupKey(String lookupType, String key) {
        return getPersonByLookupKey(lookupType, key).map(value -> new PersonImpl(value, sandboxService)).orElse(null);
    }

    @Override
    @Nullable
    public Object jsonPath(Object object, String path) {
        try {
            return JsonPath.read(object, path);
        } catch (PathNotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            LOG.error("Unable to evaluate JSON path: {} on the object of type: {} (eventId: {})", path,
                object.getClass(), inputEvent.getId(), e);
            logger.log("Unable to evaluate JSON path: " + path + " due to: " + e.toString());
            return null;
        }
    }

    private Optional<com.extole.person.service.profile.Person> getPersonByLookupKey(String lookupType,
        String key) {
        try {
            return personService.getPersonByProfileLookupKey(backendAuthorization, PersonKey.ofType(lookupType, key));
        } catch (AuthorizationException e) {
            String logMessage = String.format("Authorization exception, lookupType=%s, key=%s clientId=%s",
                lookupType, key, authorization.getClientId());
            LOG.error(logMessage, e);
        }

        return Optional.empty();
    }

    private Optional<com.extole.person.service.profile.Person> getPersonProfile(String personId) {
        try {
            return Optional.of(personService.getPerson(backendAuthorization, Id.valueOf(personId)));
        } catch (PersonNotFoundException | AuthorizationException e) {
            String logMessage = String.format("PersonProfile not found, personId=%s, clientId=%s",
                personId, authorization.getClientId());
            LOG.error(logMessage, e);
        }

        return Optional.empty();
    }

    private Authorization buildContextAuthorization(com.extole.authorization.service.Authorization authorization) {
        return new AuthorizationImpl(authorization.getAccessToken(),
            authorization.getScopes().stream().map(com.extole.authorization.service.Authorization.Scope::name)
                .collect(Collectors.toSet()),
            authorization.getClientId().getValue(), authorization.getIdentityId().getValue());
    }
}
