package com.extole.consumer.rest.share.email.v5;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.share.custom.AdvocateCodeRestException;

@Deprecated // TODO kept for zazzle-client only - ENG-18976
@Path("/v5/email/share")
public interface EmailShareV5Endpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Path("/batch")
    List<EmailShareV5Response> batchEmailShare(@AccessTokenParam(readCookie = false) String accessToken,
        BatchEmailShareV5Request shareRequest)
        throws AuthorizationRestException, EmailRecipientV5RestException, EmailShareContentV5RestException,
        AdvocateCodeRestException, BatchEmailRecipientV5RestException;

}
