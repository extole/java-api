package com.extole.consumer.rest.share.custom;

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

@Deprecated // TODO use /v6/event/share instead ENG-14783
@Path("/v5/custom/share")
public interface CustomShareEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    CustomShareResponse customShare(@AccessTokenParam(readCookie = false) String accessToken,
        CustomShareRequest customShareRequest)
        throws AuthorizationRestException, CustomShareRestException, AdvocateCodeRestException;

    @GET
    @Path("/status/{pollingId}")
    @Produces(MediaType.APPLICATION_JSON)
    CustomSharePollingResponse customShareStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("pollingId") String pollingId) throws AuthorizationRestException;
}
