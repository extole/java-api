package com.extole.consumer.rest.me.shareable.v5;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v5/shareables")
public interface ShareableV5Endpoints {

    @GET
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareableV5Response get(@AccessTokenParam(readCookie = false) String accessToken, @PathParam("code") String code)
        throws AuthorizationRestException, ShareableV5RestException;

}
