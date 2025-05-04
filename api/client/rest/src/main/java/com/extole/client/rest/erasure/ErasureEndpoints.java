package com.extole.client.rest.erasure;

import java.time.ZoneId;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/erasures")
public interface ErasureEndpoints {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ErasureResponse erase(@UserAccessTokenParam String accessToken, ErasureRequest request,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ErasureValidationRestException;
}
