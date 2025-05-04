package com.extole.client.rest.events;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO only a single client uses (healthyPaws) - to be removed in ENG-12938
@Path("/")
public interface ConversionEndpoints {

    @GET
    @Path("/v3/events/convert")
    @Produces(MediaType.APPLICATION_JSON)
    Response v3Convert(@UserAccessTokenParam String accessToken, @Context HttpServletRequest httpRequest)
        throws UserAuthorizationRestException;

}
