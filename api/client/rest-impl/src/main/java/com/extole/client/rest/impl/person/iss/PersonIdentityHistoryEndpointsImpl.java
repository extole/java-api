package com.extole.client.rest.impl.person.iss;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.iss.IdentityLogResponse;
import com.extole.client.rest.person.iss.PersonIdentityHistoryEndpoints;
import com.extole.client.rest.person.iss.PersonIdentityHistoryResponse;
import com.extole.client.rest.person.iss.PersonIdentityShapeShiftResponse;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.iss.IdentityShapeShift;
import com.extole.person.service.profile.iss.IdentityShapeShiftType;

@Provider
public class PersonIdentityHistoryEndpointsImpl implements PersonIdentityHistoryEndpoints {
    private static final Set<IdentityShapeShiftType> SPLIT_LOSER_SHAPE_SHIFT_TYPES = ImmutableSet
        .<IdentityShapeShiftType>builder()
        .add(IdentityShapeShiftType.DEVICE_AND_IDENTITY_WITH_DIFFERENT_KEY)
        .add(IdentityShapeShiftType.DEVICE_WITH_DIFFERENT_KEY)
        .build();

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;

    @Autowired
    public PersonIdentityHistoryEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonService personService) {

        this.authorizationProvider = authorizationProvider;
        this.personService = personService;
    }

    @Override
    public List<IdentityLogResponse> getIdentityHistory(String accessToken,
        String personId,
        ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            return person.getIdentityShapeShifts()
                .stream()
                .map(value -> mapIdentityShapeShift(value, timeZone))
                .collect(Collectors.toUnmodifiableList());
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

    private IdentityLogResponse mapIdentityShapeShift(IdentityShapeShift identityShapeShift,
        ZoneId timeZone) {
        Optional<ZonedDateTime> zonedCreatedDate = Optional.of(identityShapeShift.getCreatedAt().atZone(timeZone));
        if (isSplitLoser(identityShapeShift)) {
            return new PersonIdentityHistoryResponse(
                Optional.of(identityShapeShift.getOldIdentityKey().getName()),
                identityShapeShift.getOldIdentityKeyValue(),
                zonedCreatedDate,
                Optional.of(identityShapeShift.getNewIdentityKey().getName()),
                identityShapeShift.getNewIdentityKeyValue(),
                identityShapeShift.getNewIdentityId().map(Id::getValue),
                identityShapeShift.getNewIdentityKeyValueCandidate());
        }

        com.extole.client.rest.person.iss.IdentityShapeShiftType shapeShiftType =
            com.extole.client.rest.person.iss.IdentityShapeShiftType
                .valueOf(identityShapeShift.getType().name());
        return new PersonIdentityShapeShiftResponse(
            Optional.of(identityShapeShift.getOldIdentityKey().getName()),
            identityShapeShift.getOldIdentityKeyValue(),
            zonedCreatedDate,
            shapeShiftType,
            identityShapeShift.getNewIdentityProfileUpdateVersion());
    }

    private boolean isSplitLoser(IdentityShapeShift identityShapeShift) {
        return SPLIT_LOSER_SHAPE_SHIFT_TYPES.contains(identityShapeShift.getType());
    }

}
