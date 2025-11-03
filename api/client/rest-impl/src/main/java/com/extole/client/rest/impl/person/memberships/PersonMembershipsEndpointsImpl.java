package com.extole.client.rest.impl.person.memberships;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.memberships.PersonMembershipResponse;
import com.extole.client.rest.person.memberships.PersonMembershipRestException;
import com.extole.client.rest.person.memberships.PersonMembershipsEndpoints;
import com.extole.client.rest.person.memberships.PersonMembershipsListRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.audience.built.BuiltAudience;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.model.shared.audience.BuiltAudienceCache;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.PersonAudienceMembershipQueryBuilder;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.audience.membership.AudienceHandle;
import com.extole.person.service.profile.audience.membership.PersonAudienceMembership;

@Provider
public class PersonMembershipsEndpointsImpl implements PersonMembershipsEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final FullPersonService fullPersonService;
    private final BuiltAudienceCache builtAudienceCache;

    @Autowired
    public PersonMembershipsEndpointsImpl(
        FullPersonService fullPersonService,
        BuiltAudienceCache builtAudienceCache,
        ClientAuthorizationProvider authorizationProvider) {
        this.fullPersonService = fullPersonService;
        this.builtAudienceCache = builtAudienceCache;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<PersonMembershipResponse> list(String accessToken, String personId,
        PersonMembershipsListRequest listRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonMembershipRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonAudienceMembershipQueryBuilder builder =
                fullPersonService.createAudienceMembershipsQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withIds(listRequest.getAudienceIds().stream().map(Id::<AudienceHandle>valueOf)
                        .collect(Collectors.toList()))
                    .withNames(listRequest.getAudienceNames())
                    .withOffset(listRequest.getOffset().intValue())
                    .withLimit(listRequest.getLimit().intValue());

            List<PersonMembershipResponse> result = Lists.newArrayList();
            for (PersonAudienceMembership audienceMembership : builder.list()) {
                result.add(toResponse(userAuthorization, audienceMembership, timeZone));
            }
            return result;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonMembershipRestException.class)
                .withErrorCode(PersonMembershipRestException.AUDIENCE_NOT_FOUND)
                .addParameter("person_id", personId)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    private PersonMembershipResponse toResponse(Authorization authorization,
        PersonAudienceMembership audienceMembership, ZoneId timeZone)
        throws AuthorizationException, AudienceNotFoundException {
        BuiltAudience builtAudience =
            builtAudienceCache.getById(authorization, Id.valueOf(audienceMembership.getAudienceId().getValue()));
        return PersonMembershipResponse.builder()
            .withAudienceId(audienceMembership.getAudienceId().getValue())
            .withAudienceName(builtAudience.getName())
            .withCreatedDate(ZonedDateTime.ofInstant(audienceMembership.getCreatedDate(), timeZone))
            .withUpdatedDate(ZonedDateTime.ofInstant(audienceMembership.getUpdatedDate(), timeZone))
            .build();
    }
}
