package com.extole.consumer.rest.web.zone;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.response.DropsAccessTokenCookie;

@Deprecated // TODO use ZonesEndpoints ENG-9760
@Path("/zone")
@DropsAccessTokenCookie
public interface ZoneEndpoints {

    /**
     * Zone endpoint will serve creative or HTTP 302 to landing page or return HTTP 200.
     *
     * In case of an error
     * - an empty HTTP 200 will be returned
     * - X_EXTOLE_ERROR header will be set to a JSON representation of ZoneRequestException
     */
    @GET
    @Path("/{zone_name}")
    @Consumes(MediaType.WILDCARD)
    Response render(@AccessTokenParam String accessToken, @PathParam("zone_name") String zoneName)
        throws AuthorizationRestException;

    @GET
    @Consumes(MediaType.WILDCARD)
    Response render(@AccessTokenParam String accessToken);
}
