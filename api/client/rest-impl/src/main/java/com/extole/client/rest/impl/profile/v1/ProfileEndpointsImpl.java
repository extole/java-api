package com.extole.client.rest.impl.profile.v1;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.client.rest.profile.v1.ProfileEndpoints;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;

@Provider
@Path("/v1")
public class ProfileEndpointsImpl implements ProfileEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public ProfileEndpointsImpl(ClientAuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    @GET
    @Path(ProfileEndpoints.PROFILES_SEGMENTS_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> listBySegment(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("segment") String segment, @QueryParam("time_frame") String timeFrame,
        @QueryParam("campaign") String campaign) throws UserAuthorizationRestException {
        authorizationProvider.getClientAuthorization(accessToken);
        return Collections.emptyList();
    }
}
