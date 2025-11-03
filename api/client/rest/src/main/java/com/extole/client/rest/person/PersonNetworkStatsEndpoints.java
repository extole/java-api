package com.extole.client.rest.person;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/persons/{person_id}/network-stats")
@Tag(name = "/v4/persons", description = "PersonNetworkStats")
public interface PersonNetworkStatsEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(summary = "Gets calculated statistics for the network of a Person")
    PersonNetworkStatsResponse getNetworkStats(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("person_id") String personId,
        @Parameter(description = "Optional container filter, defaults to production container. " +
            "Pass \"*\" to include steps for all containers") @Nullable @QueryParam("container") String container,
        @Parameter(
            description = "Optional flag to exclude relationships with anonymous persons.") @Nullable @QueryParam("exclude_anonymous") Boolean excludeAnonymous)
        throws UserAuthorizationRestException, PersonRestException;

}
