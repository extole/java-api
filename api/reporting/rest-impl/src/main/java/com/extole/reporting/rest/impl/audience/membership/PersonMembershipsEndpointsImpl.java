package com.extole.reporting.rest.impl.audience.membership;

import java.time.ZoneId;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.audience.membership.service.AudienceMembershipService;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.audience.built.BuiltAudience;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.model.shared.audience.BuiltAudienceCache;
import com.extole.person.service.audience.membership.AudienceMembershipNotFoundException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonNotIdentifiedException;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;
import com.extole.reporting.rest.audience.membership.PersonMembershipCreateRequest;
import com.extole.reporting.rest.audience.membership.PersonMembershipResponse;
import com.extole.reporting.rest.audience.membership.PersonMembershipRestException;
import com.extole.reporting.rest.audience.membership.PersonMembershipValidationRestException;
import com.extole.reporting.rest.audience.membership.PersonMembershipsEndpoints;
import com.extole.reporting.rest.audience.membership.PersonRestException;

@Provider
public class PersonMembershipsEndpointsImpl implements PersonMembershipsEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceMembershipService audienceMembershipService;
    private final BuiltAudienceCache builtAudienceCache;

    @Autowired
    public PersonMembershipsEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AudienceMembershipService audienceMembershipService,
        BuiltAudienceCache builtAudienceCache) {
        this.authorizationProvider = authorizationProvider;
        this.audienceMembershipService = audienceMembershipService;
        this.builtAudienceCache = builtAudienceCache;
    }

    @Override
    public PersonMembershipResponse create(String accessToken, String personId,
        PersonMembershipCreateRequest createRequest, ZoneId timeZone)
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
                    Id.valueOf(personId));
            return toPersonAudienceMembershipResponse(authorization, personAudienceMembership, timeZone);
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
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonMembershipRestException.class)
                .withErrorCode(PersonMembershipRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public PersonMembershipResponse delete(String accessToken, String personId, String audienceId,
        ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonAudienceMembership audienceMembership = audienceMembershipService.delete(authorization,
                Id.valueOf(audienceId), Id.valueOf(personId));
            return toPersonAudienceMembershipResponse(authorization, audienceMembership, timeZone);
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

    private PersonMembershipResponse
        toPersonAudienceMembershipResponse(Authorization authorization, PersonAudienceMembership audienceMembership,
            ZoneId timeZone) throws AuthorizationException, AudienceNotFoundException {
        BuiltAudience builtAudience =
            builtAudienceCache.getById(authorization, Id.valueOf(audienceMembership.getAudienceId().getValue()));
        return new PersonMembershipResponse(audienceMembership.getAudienceId().getValue(),
            builtAudience.getName(),
            audienceMembership.getCreatedDate().atZone(timeZone),
            audienceMembership.getUpdatedDate().atZone(timeZone));
    }
}
