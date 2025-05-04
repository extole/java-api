package com.extole.client.rest.signal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v3/signals")
public interface ClientSignalEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status/{pollingId}")
    SignalResponse getStatus(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("pollingId") String pollingId) throws UserAuthorizationRestException;
}
