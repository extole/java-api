package com.extole.client.rest.campaign.step.data;

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
import com.extole.client.rest.campaign.built.step.data.BuiltStepDataResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/steps/{stepId}/data")
public interface CampaignStepDataEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    StepDataResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        StepDataCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignUpdateRestException, BuildCampaignRestException, CampaignStepDataValidationRestException,
        CampaignComponentValidationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stepDataId}")
    StepDataResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        @PathParam("stepDataId") String stepDataId,
        StepDataUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignStepDataRestException, CampaignUpdateRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignStepDataValidationRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stepDataId}")
    StepDataResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        @PathParam("stepDataId") String stepDataId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignUpdateRestException, CampaignStepDataRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignStepDataValidationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stepDataId}")
    StepDataResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        @PathParam("stepDataId") String stepDataId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignStepDataRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<StepDataResponse> getAll(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stepDataId}/built")
    BuiltStepDataResponse getBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        @PathParam("stepDataId") String stepDataId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignControllerRestException, CampaignStepDataRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/built")
    List<BuiltStepDataResponse> getAllBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignControllerRestException;

}
