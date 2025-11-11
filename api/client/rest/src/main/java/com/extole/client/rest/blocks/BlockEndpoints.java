package com.extole.client.rest.blocks;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2")
public interface BlockEndpoints {

    @POST
    @Path("/blocks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    BlockResponse create(@UserAccessTokenParam String accessToken,
        BlockCreateRequest blockCreateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BlockRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/blocks/{blockId}")
    BlockResponse getBlockById(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("blockId") String blockId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BlockRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/blocks")
    List<BlockResponse> getBlocks(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam BlockListRequest blockListRequest) throws UserAuthorizationRestException, QueryLimitsRestException;

    @GET
    @Path("/blocks/global")
    @Produces(MediaType.APPLICATION_JSON)
    List<BlockResponse> getGlobalBlocks(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam BlockListRequest blockListRequest) throws UserAuthorizationRestException, QueryLimitsRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/blocks/{blockId}")
    BlockResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("blockId") String blockId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BlockRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/blocks/test")
    BlockCheckResponse test(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        BlockCheckRequest blockCheckRequest)
        throws UserAuthorizationRestException, BlockRestException;
}
