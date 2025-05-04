package com.extole.consumer.rest.events.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v6/events")
public interface EventEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    SubmitEventResponse submit(@AccessTokenParam(readCookie = false) String accessToken,
        SubmitEventRequest submitEventRequest)
        throws AuthorizationRestException, SubmitEventRestException;
}
