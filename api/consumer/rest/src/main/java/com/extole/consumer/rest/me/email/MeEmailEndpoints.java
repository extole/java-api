package com.extole.consumer.rest.me.email;

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

@Deprecated // TODO remove endpoint as it doesn't follow the event pattern ENG-13187
@Path("/v4/me/email")
public interface MeEmailEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    SendEmailResponse sendEmail(@AccessTokenParam(readCookie = false) String accessToken,
        SendEmailRequest request)
        throws AuthorizationRestException, SendEmailRestException;

    @GET
    @Path("/status/{polling_id}")
    @Produces(MediaType.APPLICATION_JSON)
    SendEmailPollingResponse getSendEmailStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("polling_id") String pollingId) throws AuthorizationRestException;

}
