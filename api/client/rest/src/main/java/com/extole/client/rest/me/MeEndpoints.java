package com.extole.client.rest.me;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/me")
public interface MeEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    MeResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @GET
    @Path("/clients")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<MeClientResponse> listClients(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @Path("/zendesk-single-sign-on")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    UserZendeskSingleSignOnResponse
        createZendeskSingleSignOnUrl(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
            throws UserAuthorizationRestException;

    @GET
    @Path("/programs")
    @Produces(MediaType.APPLICATION_JSON)
    List<MeClientProgramResponse>
        listPrograms(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
            throws UserAuthorizationRestException;
}
