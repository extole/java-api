package com.extole.client.rest.events.v4;

import java.time.ZoneId;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Deprecated // TODO REMOVE IN ENG-10566
@Path("/v4/events")
public interface ClientConsumerEventV4Endpoints {

    @Deprecated // TODO REMOVE IN ENG-10566
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientConsumerEventV4Response submit(@UserAccessTokenParam String accessToken,
        ClientConsumerEventV4Request request, @TimeZoneParam ZoneId timeZone)
        throws ClientConsumerEventV4RestException, UserAuthorizationRestException;

}
