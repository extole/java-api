package com.extole.client.rest.campaign.flow.step.app;

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
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepAppResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/flow-steps/{flowStepId}/apps")
public interface CampaignFlowStepAppEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignFlowStepAppResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        CampaignFlowStepAppCreateRequest createRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepAppValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{flowStepAppId}")
    CampaignFlowStepAppResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepAppId") String flowStepAppId,
        CampaignFlowStepAppUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepAppValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException, CampaignFlowStepAppRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{flowStepAppId}")
    CampaignFlowStepAppResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepAppId") String flowStepAppId)
        throws CampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignFlowStepRestException, CampaignFlowStepAppRestException,
        UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{flowStepAppId}")
    CampaignFlowStepAppResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepAppId") String flowStepAppId)
        throws CampaignRestException, UserAuthorizationRestException, CampaignFlowStepRestException,
        CampaignFlowStepAppRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CampaignFlowStepAppResponse> getAll(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{flowStepAppId}/built")
    BuiltCampaignFlowStepAppResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepAppId") String flowStepAppId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException, CampaignFlowStepAppRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/built")
    List<BuiltCampaignFlowStepAppResponse> getAllBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException;

}
