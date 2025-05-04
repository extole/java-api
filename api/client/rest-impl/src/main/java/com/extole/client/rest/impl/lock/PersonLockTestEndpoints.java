package com.extole.client.rest.impl.lock;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.impl.memcached.TestMemcachedFactory;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.memcached.ExtoleMemcachedException;
import com.extole.common.memcached.OutdatedCasVersionException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.ProfileStoreException;
import com.extole.person.service.impl.lock.PersonLock;
import com.extole.person.service.impl.lock.PersonLockKey;
import com.extole.person.service.impl.lock.PersonLockPojo;
import com.extole.person.service.impl.lock.PersonLockService;
import com.extole.person.service.profile.NoOpPersonOperations;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonLockAcquireRuntimeException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ReadOnlyPersonDataException;

@Provider
@Singleton
@Beta
@Path("/test/profile-lock")
public class PersonLockTestEndpoints {
    private static final int LOCK_AWAIT_SECONDS = 15;
    private static final int MAX_LOCK_ATTEMPTS = 20;
    private static final LockDescription LOCK_DESCRIPTION = new LockDescription("lock-test-endpoint");
    private static final String PROFILE_LOCK_STORE_NAME_FOR_TEST = "profile-lock-test";

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonLockService personLockService;
    private final PersonService personService;
    private final ExtoleMemcachedClient<String, PersonLockPojo> memcachedClient;

    @Autowired
    public PersonLockTestEndpoints(ClientAuthorizationProvider authorizationProvider,
        PersonLockService personLockService, TestMemcachedFactory memcachedFactory, PersonService personService) {
        this.authorizationProvider = authorizationProvider;
        this.personLockService = personLockService;
        this.personService = personService;
        this.memcachedClient =
            memcachedFactory.createMemcachedClient(PROFILE_LOCK_STORE_NAME_FOR_TEST, PersonLockPojo.class);
    }

    @POST
    @Path("/is-locked/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonLockStatusResponse isLocked(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("clientId") String clientId, PersonLockStatusRequest lockStatusRequest)
        throws UserAuthorizationRestException {
        validateAccess(accessToken);

        PersonLock personLock = buildPersonLockFromStatusRequest(clientId, lockStatusRequest);

        boolean isLocked = personLockService.isLockedBy(personLock);

        return new PersonLockStatusResponse(isLocked);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PersonLockResponse getLock(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @QueryParam("key") String key)
        throws PersonLockRestException, UserAuthorizationRestException {
        validateAccess(accessToken);
        try {
            Optional<PersonLockPojo> valueOptional = memcachedClient.get(key);
            if (valueOptional.isPresent()) {
                PersonLockPojo lock = valueOptional.get();
                return buildPersonLockResponse(lock);
            }
            return PersonLockResponse.EMPTY_LOCK;
        } catch (ExtoleMemcachedException e) {
            throw RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                .addParameter("message", e.getMessage())
                .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                .addParameter("message", e.getMessage())
                .build();
        }
    }

    @POST
    @Path("/lock/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonLockResponse lock(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("clientId") String clientId,
        PersonLockRequest lockRequest) throws UserAuthorizationRestException {
        validateAccess(accessToken);
        PersonLock personLock = personLockService
            .lock(new PersonLockKey(Id.valueOf(clientId), lockRequest.getLockingId()), LOCK_DESCRIPTION);
        return buildPersonLockResponse(personLock);
    }

    @POST
    @Path("/lock-twice-distinct-threads/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonLockResponse> lockTwiceDistinctThreads(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("clientId") String clientId, @QueryParam("person_id") String personId)
        throws PersonLockRestException, UserAuthorizationRestException {
        validateAccess(accessToken);

        CountDownLatch latch = new CountDownLatch(1);
        Id<ClientHandle> clientHandle = Id.valueOf(clientId);
        Id<PersonHandle> personHandle = Id.valueOf(personId);

        AtomicReference<PersonLock> firstPersonLock = new AtomicReference<>();
        AtomicReference<PersonLockRestException> internalException = new AtomicReference<>();
        new Thread(() -> {
            try {
                firstPersonLock
                    .set(personLockService.lock(new PersonLockKey(clientHandle, personHandle), LOCK_DESCRIPTION));
                latch.countDown();
            } catch (RuntimeException e) {
                internalException.set(RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                    .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                    .addParameter("message", e.getMessage())
                    .build());
            }
        }).start();

        if (internalException.get() != null) {
            throw internalException.get();
        }

        try {
            if (!latch.await(LOCK_AWAIT_SECONDS, TimeUnit.SECONDS)) {
                throw RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                    .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                    .addParameter("message", "Unable to acquire first lock")
                    .build();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                .addParameter("message", e.getMessage())
                .build();
        }

        PersonLock secondPersonLock = null;
        try {
            PersonLockAcquireRuntimeException lockAcquireException;
            int attempt = 0;
            do {
                attempt++;
                try {
                    secondPersonLock =
                        personLockService.lock(new PersonLockKey(clientHandle, personHandle), LOCK_DESCRIPTION);
                    lockAcquireException = null;
                } catch (PersonLockAcquireRuntimeException e) {
                    lockAcquireException = e;
                }
            } while (lockAcquireException != null && attempt <= MAX_LOCK_ATTEMPTS);
        } catch (RuntimeException e) {
            throw RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                .addParameter("message", e.getMessage())
                .build();
        }

        if (secondPersonLock == null) {
            throw RestExceptionBuilder.newBuilder(PersonLockRestException.class)
                .withErrorCode(PersonLockRestException.LOCK_SERVICE_UNAVAILABLE)
                .addParameter("message", "Unable to acquire second lock")
                .build();
        }
        return Lists.newArrayList(buildPersonLockResponse(firstPersonLock.get()),
            buildPersonLockResponse(secondPersonLock));
    }

    @POST
    @Path("/update-person/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updatePersonIncorrectly(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("personId") String personId,
        PersonUpdateRequest updateRequest) throws UserAuthorizationRestException, PersonLockUpdateRestException {
        Authorization authorization = validateAccess(accessToken);
        try {
            personService.updatePerson(authorization, Id.valueOf(personId), new LockDescription("update-person-sleep"),
                (personBuilder, person) -> {
                    try {
                        Thread.sleep(updateRequest.getLockDurationMs());
                        for (Map.Entry<String, String> entry : updateRequest.getData().entrySet()) {
                            personBuilder.addOrReplaceData(entry.getKey()).withValue(entry.getValue());
                        }
                        return personBuilder.save();
                    } catch (InterruptedException | PersonDataInvalidValueException | PersonDataValueLengthException
                        | PersonDataNameLengthException | PersonDataInvalidNameException
                        | ReadOnlyPersonDataException e) {
                        throw new LockClosureException(e);
                    }
                },
                new NoOpPersonOperations());
        } catch (PersonLockAcquireRuntimeException e) {
            throw e;
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonLockUpdateRestException.class)
                .withErrorCode(PersonLockUpdateRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof ProfileStoreException && cause.getCause() instanceof OutdatedCasVersionException) {
                throw RestExceptionBuilder.newBuilder(PersonLockUpdateRestException.class)
                    .withErrorCode(PersonLockUpdateRestException.CONCURRENT_MODIFICATION_DETECTED)
                    .addParameter("person_id", personId)
                    .withCause(cause.getCause())
                    .build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @POST
    @Path("/lock-twice/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonLockResponse> lockTwice(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("clientId") String clientId, @QueryParam("person_id") String personId)
        throws UserAuthorizationRestException {
        validateAccess(accessToken);

        PersonLock personLock;
        Id<ClientHandle> clientHandle = Id.valueOf(clientId);
        Id<PersonHandle> personHandle = Id.valueOf(personId);
        personLock = personLockService.lock(new PersonLockKey(clientHandle, personHandle), LOCK_DESCRIPTION);

        PersonLock secondPersonLock;
        secondPersonLock = personLockService.lock(new PersonLockKey(clientHandle, personHandle), LOCK_DESCRIPTION);

        return Lists.newArrayList(buildPersonLockResponse(personLock), buildPersonLockResponse(secondPersonLock));
    }

    private PersonLockResponse buildPersonLockResponse(PersonLock personLock) {
        PersonLockResponse personLockResponse;
        if (personLock != null) {
            personLockResponse =
                new PersonLockResponse(personLock.getLockId(), personLock.getLockDescription().getDescription(),
                    Long.valueOf(personLock.getThreadId()), personLock.getHost(),
                    Long.valueOf(personLock.getExpirationTime().toEpochMilli()),
                    Long.valueOf(personLock.getCreationTime().toEpochMilli()), personLock.isReLock());
        } else {
            personLockResponse = PersonLockResponse.EMPTY_LOCK;
        }
        return personLockResponse;
    }

    private PersonLockResponse buildPersonLockResponse(PersonLockPojo lock) {
        PersonLockResponse personLockResponse;
        personLockResponse =
            new PersonLockResponse(lock.getLockId(), lock.getLockDescription(),
                Long.valueOf(lock.getThreadId()), lock.getHost(),
                Long.valueOf(lock.getLockExpirationTime()),
                Long.valueOf(lock.getLockCreationTime()), false);
        return personLockResponse;
    }

    @POST
    @Path("/unlock/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonLockReleaseResponse unlockPerson(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("clientId") String clientId, PersonLockReleaseRequest lockReleaseRequest)
        throws UserAuthorizationRestException {
        validateAccess(accessToken);

        PersonLock personLock = buildPersonLockFromReleaseRequest(clientId, lockReleaseRequest);

        boolean isLockReleased = false;
        personLockService.releaseLock(personLock);
        isLockReleased = true;
        return new PersonLockReleaseResponse(isLockReleased);
    }

    private Authorization validateAccess(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
        return authorization;
    }

    private PersonLock buildPersonLockFromStatusRequest(String clientId,
        PersonLockStatusRequest lockStatusRequest) {
        PersonLockPojo storeValue =
            new PersonLockPojo(lockStatusRequest.getLockId(), lockStatusRequest.getLockDescription(),
                lockStatusRequest.getThreadId(), lockStatusRequest.getHost(), lockStatusRequest.getLockExpirationTime(),
                lockStatusRequest.getLockCreationTime());
        return buildPersonLock(clientId, lockStatusRequest.getLockingId(), storeValue);
    }

    private PersonLock buildPersonLockFromReleaseRequest(String clientId,
        PersonLockReleaseRequest lockReleaseRequest) {
        PersonLockPojo storeValue = new PersonLockPojo(lockReleaseRequest.getLockId(),
            lockReleaseRequest.getLockDescription(),
            lockReleaseRequest.getThreadId(), lockReleaseRequest.getHost(), lockReleaseRequest.getLockExpirationTime(),
            lockReleaseRequest.getLockCreationTime());
        return buildPersonLock(clientId, lockReleaseRequest.getLockingId(), storeValue);
    }

    private PersonLock buildPersonLock(String clientId, String lockingId, PersonLockPojo storeValue) {
        Id<ClientHandle> clientHandleId = Id.valueOf(clientId);
        return new PersonLock(new PersonLockKey(clientHandleId, lockingId), storeValue, false);
    }
}
