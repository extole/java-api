package com.extole.client.rest.profile.v1;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO remove in ENG-13035
@Path("/v1")
public interface ProfileEndpoints {
    String PROFILES_SEGMENTS_URI = "/profiles/segments";

    @GET
    @Path(ProfileEndpoints.PROFILES_SEGMENTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    List<Object> listBySegment(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("segment") String segment, @QueryParam("time_frame") String timeFrame,
        @QueryParam("campaign") String campaign) throws UserAuthorizationRestException;
}
