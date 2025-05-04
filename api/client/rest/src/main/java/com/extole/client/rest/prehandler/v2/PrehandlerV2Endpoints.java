package com.extole.client.rest.prehandler.v2;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO to be removed in ENG-13399
@Path("/v2/prehandlers")
public interface PrehandlerV2Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PrehandlerV2Response> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @QueryParam("condition_type") String conditionType,
        @Nullable @QueryParam("condition") String condition,
        @Nullable @QueryParam("action_type") String actionType,
        @Nullable @QueryParam("action") String action)
        throws UserAuthorizationRestException, PrehandlerValidationV2RestException;

    @GET
    @Path("/{prehandlerId}")
    @Produces(MediaType.APPLICATION_JSON)
    PrehandlerV2Response get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("prehandlerId") String prehandlerId)
        throws UserAuthorizationRestException, PrehandlerV2RestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PrehandlerV2Response create(@UserAccessTokenParam String accessToken,
        PrehandlerCreateV2Request request)
        throws UserAuthorizationRestException, PrehandlerValidationV2RestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{prehandlerId}")
    PrehandlerV2Response update(@UserAccessTokenParam String accessToken,
        @PathParam("prehandlerId") String prehandlerId,
        PrehandlerUpdateV2Request updateRequest) throws UserAuthorizationRestException,
        PrehandlerValidationV2RestException, PrehandlerV2RestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{prehandlerId}")
    PrehandlerV2Response delete(@UserAccessTokenParam String accessToken,
        @PathParam("prehandlerId") String prehandlerId)
        throws UserAuthorizationRestException, PrehandlerV2RestException;
}
