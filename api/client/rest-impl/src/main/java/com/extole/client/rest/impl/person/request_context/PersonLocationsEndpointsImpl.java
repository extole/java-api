package com.extole.client.rest.impl.person.request_context;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.person.RequestContextResponseMapper;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.request_context.PersonLocationResponse;
import com.extole.client.rest.person.request_context.PersonLocationsEndpoints;
import com.extole.client.rest.person.request_context.PersonLocationsListRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.PersonLocationQueryBuilder;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.request.context.Location;

@Provider
public class PersonLocationsEndpointsImpl implements PersonLocationsEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final FullPersonService fullPersonService;
    private final RequestContextResponseMapper requestContextMapper;

    @Autowired
    public PersonLocationsEndpointsImpl(FullPersonService fullPersonService,
        ClientAuthorizationProvider authorizationProvider,
        RequestContextResponseMapper requestContextMapper) {
        this.fullPersonService = fullPersonService;
        this.authorizationProvider = authorizationProvider;
        this.requestContextMapper = requestContextMapper;
    }

    @Override
    public List<PersonLocationResponse> list(String accessToken, String personId,
        PersonLocationsListRequest listRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonLocationQueryBuilder builder =
                fullPersonService.createLocationQueryBuilder(userAuthorization, Id.valueOf(personId))
                    .withCountries(listRequest.getCountries());

            builder.withOffset(listRequest.getOffset())
                .withLimit(listRequest.getLimit());
            List<PersonLocationResponse> responses = Lists.newArrayList();
            for (Location location : builder.list()) {
                responses.add(requestContextMapper.toLocationResponse(location, timeZone));
            }
            return responses;
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
