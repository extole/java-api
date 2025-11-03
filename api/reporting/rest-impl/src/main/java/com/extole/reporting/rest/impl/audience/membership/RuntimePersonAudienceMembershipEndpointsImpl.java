package com.extole.reporting.rest.impl.audience.membership;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.audience.membership.service.AudienceMembershipService;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.person.service.audience.membership.AudienceMembershipNotFoundException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonNotIdentifiedException;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;
import com.extole.reporting.rest.audience.membership.PersonMembershipRestException;
import com.extole.reporting.rest.audience.membership.PersonMembershipValidationRestException;
import com.extole.reporting.rest.audience.membership.PersonRestException;
import com.extole.reporting.rest.audience.membership.RuntimePersonAudienceMembershipEndpoints;
import com.extole.reporting.rest.audience.membership.v4.PersonAudienceMembershipV4CreateRequest;
import com.extole.reporting.rest.audience.membership.v4.PersonAudienceMembershipV4Response;

@Provider
public class RuntimePersonAudienceMembershipEndpointsImpl implements RuntimePersonAudienceMembershipEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceMembershipService audienceMembershipService;

    @Autowired
    public RuntimePersonAudienceMembershipEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AudienceMembershipService audienceMembershipService) {
        this.authorizationProvider = authorizationProvider;
        this.audienceMembershipService = audienceMembershipService;
    }

    @Override
    public List<PersonAudienceMembershipV4Response> list(String accessToken, Id<com.extole.api.person.Person> personId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return audienceMembershipService.list(authorization, Id.valueOf(personId.getValue()))
                .stream()
                .map(audienceMembership -> toPersonAudienceMembershipResponse(audienceMembership, timeZone))
                .collect(Collectors.toList());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
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
    public PersonAudienceMembershipV4Response create(String accessToken, Id<com.extole.api.person.Person> personId,
        PersonAudienceMembershipV4CreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipValidationRestException,
        PersonMembershipRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            if (createRequest.getAudienceId() == null) {
                throw RestExceptionBuilder.newBuilder(PersonMembershipValidationRestException.class)
                    .withErrorCode(PersonMembershipValidationRestException.MISSING_AUDIENCE_ID)
                    .build();
            }
            PersonAudienceMembership personAudienceMembership =
                audienceMembershipService.create(authorization, Id.valueOf(createRequest.getAudienceId().getValue()),
                    Id.valueOf(personId.getValue()));
            return toPersonAudienceMembershipResponse(personAudienceMembership, timeZone);
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonMembershipRestException.class)
                .withErrorCode(PersonMembershipRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e)
                .build();
        } catch (PersonNotIdentifiedException e) {
            throw RestExceptionBuilder.newBuilder(PersonMembershipRestException.class)
                .withErrorCode(PersonMembershipRestException.PERSON_NOT_IDENTIFIED)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PersonAudienceMembershipV4Response delete(String accessToken, Id<com.extole.api.person.Person> personId,
        Id<com.extole.api.audience.Audience> audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonAudienceMembership audienceMembership = audienceMembershipService.delete(authorization,
                Id.valueOf(audienceId.getValue()), Id.valueOf(personId.getValue()));
            return toPersonAudienceMembershipResponse(audienceMembership, timeZone);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonMembershipRestException.class)
                .withErrorCode(PersonMembershipRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AudienceMembershipNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonMembershipRestException.class)
                .withErrorCode(PersonMembershipRestException.MEMBERSHIP_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    private PersonAudienceMembershipV4Response
        toPersonAudienceMembershipResponse(PersonAudienceMembership audienceMembership, ZoneId timeZone) {
        return new PersonAudienceMembershipV4Response(Id.valueOf(audienceMembership.getAudienceId().getValue()),
            audienceMembership.getCreatedDate().atZone(timeZone),
            audienceMembership.getUpdatedDate().atZone(timeZone));
    }
}
