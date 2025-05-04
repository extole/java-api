package com.extole.client.rest.person;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v5/persons/{person_id}/relationships")
@Tag(name = "/v5/persons/{person_id}/relationships", description = "PersonRelationship")
public interface PersonRelationshipsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with relationships", description = "Returns relationships for a person.")
    List<PersonRelationshipResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @BeanParam PersonRelationshipsListRequest listRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipsListRestException;
}
