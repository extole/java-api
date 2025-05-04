package com.extole.consumer.rest.me.journey;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.common.annotations.Beta;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Beta
@Path("/v4/me/journeys")
public interface MeJourneyEndpoints {

    // TODO Rename type to journey_name - ENG-18547
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<JourneyResponse> getJourneys(@AccessTokenParam(readCookie = false) String accessToken,
        @Nullable @QueryParam("container") String container,
        @Nullable @QueryParam("type") String journeyName) throws AuthorizationRestException;

}
