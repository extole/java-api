package com.extole.client.rest.campaign.flow.step.metric;

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
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepMetricResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/flow-steps/{flowStepId}/metrics")
public interface CampaignFlowStepMetricEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignFlowStepMetricResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        CampaignFlowStepMetricCreateRequest createRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepMetricValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{flowStepMetricId}")
    CampaignFlowStepMetricResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepMetricId") String flowStepMetricId,
        CampaignFlowStepMetricUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepMetricValidationRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, BuildCampaignRestException, CampaignFlowStepMetricRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{flowStepMetricId}")
    CampaignFlowStepMetricResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepMetricId") String flowStepMetricId)
        throws CampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignFlowStepRestException, CampaignFlowStepMetricRestException,
        UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{flowStepMetricId}")
    CampaignFlowStepMetricResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepMetricId") String flowStepMetricId)
        throws CampaignRestException, UserAuthorizationRestException, CampaignFlowStepRestException,
        CampaignFlowStepMetricRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CampaignFlowStepMetricResponse> getAll(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{flowStepMetricId}/built")
    BuiltCampaignFlowStepMetricResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId,
        @PathParam("flowStepMetricId") String flowStepMetricId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException, CampaignFlowStepMetricRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/built")
    List<BuiltCampaignFlowStepMetricResponse> getAllBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion,
        @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignFlowStepRestException;

}
