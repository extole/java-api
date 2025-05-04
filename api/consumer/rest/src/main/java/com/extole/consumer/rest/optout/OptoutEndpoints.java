package com.extole.consumer.rest.optout;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;

@Path("/v4/optout/{secure_email}")
public interface OptoutEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    OptoutResponse updateOptout(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("secure_email") String secureEmail, OptoutRequest request)
        throws OptoutRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    OptoutResponse getOptout(@PathParam("secure_email") String secureEmail) throws OptoutRestException;

}
