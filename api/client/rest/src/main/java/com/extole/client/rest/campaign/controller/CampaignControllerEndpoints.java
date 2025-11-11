package com.extole.client.rest.campaign.controller;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

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
import com.extole.client.rest.campaign.built.controller.BuiltCampaignStepResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.create.CampaignStepCreateRequest;
import com.extole.client.rest.campaign.controller.response.CampaignStepResponse;
import com.extole.client.rest.campaign.controller.update.CampaignStepUpdateRequest;
import com.extole.client.rest.campaign.step.data.CampaignStepDataValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers")
public interface CampaignControllerEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    <REQUEST extends CampaignStepCreateRequest, RESPONSE extends CampaignStepResponse> RESPONSE create(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        REQUEST createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignStepDataValidationRestException, CampaignJourneyEntryValidationRestException,
        CampaignStepValidationRestException, OmissibleRestException, CampaignFrontendControllerValidationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stepId}")
    <REQUEST extends CampaignStepUpdateRequest, RESPONSE extends CampaignStepResponse> RESPONSE update(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        REQUEST updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerValidationRestException,
        CampaignControllerRestException, CampaignComponentValidationRestException, BuildCampaignRestException,
        OmissibleRestException, CampaignUpdateRestException, CampaignStepDataValidationRestException,
        CampaignJourneyEntryValidationRestException, CampaignStepValidationRestException,
        CampaignFrontendControllerValidationRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stepId}")
    <RESPONSE extends CampaignStepResponse> RESPONSE delete(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("stepId") String stepId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stepId}")
    <RESPONSE extends CampaignStepResponse> RESPONSE get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("stepId") String stepId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    <RESPONSE extends CampaignStepResponse> List<RESPONSE> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @QueryParam("type") Optional<StepType> type,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stepId}/built")
    <RESPONSE extends BuiltCampaignStepResponse> RESPONSE getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("stepId") String stepId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/built")
    <RESPONSE extends BuiltCampaignStepResponse> List<RESPONSE> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @QueryParam("type") Optional<StepType> type,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

}
