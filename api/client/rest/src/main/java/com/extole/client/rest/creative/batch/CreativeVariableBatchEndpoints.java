package com.extole.client.rest.creative.batch;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.creative.CreativeVariableResponse;
import com.extole.client.rest.creative.CreativeVariableRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.request.FileInputStreamRequest;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/creative/variable/batch")
public interface CreativeVariableBatchEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, List<CreativeVariableResponse>> getVariables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @QueryParam("zone_state") Optional<String> zoneState,
        @DefaultValue("creative") @QueryParam("group_by") String groupBy)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CreativeVariableBatchRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, List<CreativeVariableResponse>> updateVariables(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        List<CreativeVariableUpdateRequest> request)
        throws CampaignRestException, UserAuthorizationRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CreativeVariableRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/zone")
    Map<String, List<CreativeVariableResponse>> updateZoneVariables(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        List<ZoneCreativeVariableUpdateRequest> request)
        throws CampaignRestException, UserAuthorizationRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CreativeVariableRestException;

    @GET
    @Path("/values{format :(\\.csv|\\.json|\\.xlsx)?}")
    Response getVariablesValues(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @Nullable @PathParam("format") String format,
        @Nullable @QueryParam("type") String type,
        @Nullable @QueryParam("tags") String tags,
        @QueryParam("zone_state") Optional<String> zoneState)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException;

    @PUT
    @Path("/values")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
    Map<String, List<CreativeVariableResponse>> updateVariableValues(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        FileInputStreamRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CreativeVariableRestException;

}
