package com.extole.consumer.rest.share.email;

import java.util.List;

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

@Path("/v6/email/share")
public interface EmailShareEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    EmailShareResponse emailShare(@AccessTokenParam(readCookie = false) String accessToken,
        EmailShareRequest shareRequest) throws AuthorizationRestException, EmailShareRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Path("/batch")
    List<EmailShareResponse> batchEmailShare(@AccessTokenParam(readCookie = false) String accessToken,
        BatchEmailShareRequest shareRequest)
        throws AuthorizationRestException, EmailShareRestException, BatchEmailRecipientRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Path("/advocate-code")
    EmailShareResponse emailShareAdvocateCode(@AccessTokenParam(readCookie = false) String accessToken,
        EmailShareWithAdvocateCodeRequest shareRequest)
        throws AuthorizationRestException, EmailShareRestException, AdvocateCodeRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Path("/advocate-code/batch")
    List<EmailShareResponse> batchEmailShareAdvocateCode(@AccessTokenParam(readCookie = false) String accessToken,
        BatchEmailShareWithAdvocateCodeRequest shareRequest) throws AuthorizationRestException,
        EmailShareRestException, BatchEmailRecipientRestException, AdvocateCodeRestException;

    @GET
    @Path("/status/{pollingId}")
    @Produces(MediaType.APPLICATION_JSON)
    EmailSharePollingResponse emailShareStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("pollingId") String pollingId) throws AuthorizationRestException;

}
