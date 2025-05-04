package com.extole.event.api.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.TooManyRequestsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v5/batch-events")
public interface BatchEventDispatcherEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<BatchEventDispatcherResponse> batchSubmit(@UserAccessTokenParam String accessToken,
        List<EventDispatcherRequest> requests)
        throws UserAuthorizationRestException, TooManyRequestsRestException;

}
