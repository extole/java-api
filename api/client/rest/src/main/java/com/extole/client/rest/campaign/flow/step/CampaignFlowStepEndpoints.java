package com.extole.client.rest.campaign.flow.step;

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

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppValidationRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/flow-steps")
public interface CampaignFlowStepEndpoints {

    // TODO Remove CampaignFlowStepMetricValidationRestException after that UI will be adjusted ENG-18841-2
    // TODO Remove CampaignFlowStepAppValidationRestException after that UI will be adjusted ENG-18842
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignFlowStepResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignFlowStepCreateRequest createRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignFlowStepMetricValidationRestException, CampaignFlowStepAppValidationRestException;

    // TODO Remove CampaignFlowStepMetricValidationRestException after that UI will be adjusted ENG-18841-2
    // TODO Remove CampaignFlowStepAppValidationRestException after that UI will be adjusted ENG-18842
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{flowStepId}")
    CampaignFlowStepResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId,
        CampaignFlowStepUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        CampaignFlowStepValidationRestException, CampaignComponentValidationRestException, BuildCampaignRestException,
        OmissibleRestException, CampaignUpdateRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{flowStepId}")
    CampaignFlowStepResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{flowStepId}")
    CampaignFlowStepResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion, @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CampaignFlowStepResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String campaignVersion)
        throws UserAuthorizationRestException, CampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{flowStepId}/built")
    BuiltCampaignFlowStepResponse getBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String campaignVersion, @PathParam("flowStepId") String flowStepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignFlowStepRestException,
        BuildCampaignRestException;

    @GET
    @Path("/built")
    @Produces(MediaType.APPLICATION_JSON)
    List<BuiltCampaignFlowStepResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String campaignVersion,
        @Nullable @QueryParam("flow_path") String flowPath)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException;

}
