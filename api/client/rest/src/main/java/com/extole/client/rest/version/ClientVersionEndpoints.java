package com.extole.client.rest.version;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/client-version")
@Tag(name = "/v2/client-version", description = "ClientVersion")
public interface ClientVersionEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "client version information",
        description = "Returns current client version and whether all instances have received the versions changes")
    ClientVersionResponse get(@UserAccessTokenParam String accessToken,
        @Parameter(hidden = true,
            description = "use when client has been deleted") @Nullable @QueryParam("client_id") String clientId,
        @Parameter(
            description = "queries pending changes for a specific client version") @Nullable @QueryParam("version") Integer version)
        throws UserAuthorizationRestException, ClientVersionRestException;

}
