package com.extole.consumer.rest.signal.step;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v3/signals/step")
public interface StepSignalEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status/{pollingId}")
    StepSignalResponse getStatus(
        @AccessTokenParam(readCookie = false) String accessToken, @PathParam("pollingId") String pollingId)
        throws AuthorizationRestException;

}
