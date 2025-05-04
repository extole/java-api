package com.extole.client.rest.person.iss;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("v5/persons/{person_id}/identity-history")
@Tag(name = "v5/persons/{person_id}/identity-history", description = "Person identity history")
public interface PersonIdentityHistoryEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<IdentityLogResponse> getIdentityHistory(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, PersonRestException;

}
