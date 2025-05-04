package com.extole.client.rest.campaign.controller.action.data.intelligence;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/actions/data-intelligences")
public interface CampaignControllerActionDataIntelligenceEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CampaignControllerActionDataIntelligenceResponse> get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionDataIntelligenceResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("actionId") String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignControllerActionDataIntelligenceResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        CampaignControllerActionDataIntelligenceRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionDataIntelligenceValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionDataIntelligenceResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId,
        CampaignControllerActionDataIntelligenceRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionDataIntelligenceValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionDataIntelligenceResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException;
}
