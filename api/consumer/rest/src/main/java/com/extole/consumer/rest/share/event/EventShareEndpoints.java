package com.extole.consumer.rest.share.event;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Deprecated // TODO use /v6/email/share instead ENG-14783
@Path("/v4/events/share")
public interface EventShareEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    EventShareResponse shareEvent(@AccessTokenParam(readCookie = false) String accessToken,
        EventShareRequest emailShareRequest)
        throws AuthorizationRestException, EventRestException;

    @GET
    @Path("/status/{pollingId}")
    @Produces(MediaType.APPLICATION_JSON)
    EventSharePollingResponse shareEventStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("pollingId") String pollingId) throws AuthorizationRestException;
}
