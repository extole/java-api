package com.extole.consumer.rest.email;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.producer.DefaultApplicationJSON;

@Path("/v4/email")
public interface EmailProviderEndpoints {

    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    EmailProviderValidationResponse validate(EmailProviderValidationRequest request)
        throws EmailProviderRestException;

}
