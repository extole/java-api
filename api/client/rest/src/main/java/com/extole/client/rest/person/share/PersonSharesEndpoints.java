package com.extole.client.rest.person.share;

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

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v5/persons/{person_id}/shares")
@Tag(name = "/v5/persons/{person_id}/shares", description = "Person shares")
public interface PersonSharesEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{share_id}")
    @Operation(summary = "Get a share by id",
        description = "Returns share for a person identified by id.")
    PersonShareResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @Parameter(description = "Share id parameter") @PathParam("share_id") String shareId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with shares",
        description = "Returns shares for a person, sorted by created date in descending order.")
    List<PersonShareResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @BeanParam PersonSharesListRequest sharesListRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonSharesListRestException;
}
