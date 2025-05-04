package com.extole.event.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.TooManyRequestsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v5/events")
public interface EventDispatcherEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    EventDispatcherResponse submit(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        EventDispatcherRequest request)
        throws EventDispatcherRestException, UserAuthorizationRestException, TooManyRequestsRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{event_name}")
    EventDispatcherResponse submit(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("event_name") String eventName, UnnamedEventDispatcherRequest request)
        throws EventDispatcherRestException, UserAuthorizationRestException, TooManyRequestsRestException;

}
