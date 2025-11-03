package com.extole.consumer.rest.redirect;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.response.DropsAccessTokenCookie;

@Path("/v2/link")
@DropsAccessTokenCookie
public interface LinkFollowingEndpoint {

    @GET
    Response fetch(@AccessTokenParam String accessToken, @HeaderParam("X-Extole-Incoming-Url") String incomingUrl)
        throws LinkFollowingRestException;
}
