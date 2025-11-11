package com.extole.client.rest.campaign.controller.trigger.targeting;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.BuildCampaignControllerRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerTargetingResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/triggers/targeting")
public interface CampaignControllerTriggerTargetingEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignControllerTriggerTargetingResponse create(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        CampaignControllerTriggerTargetingCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerTargetingResponse update(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId,
        CampaignControllerTriggerTargetingUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerTargetingResponse delete(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerTargetingResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}/built")
    BuiltCampaignControllerTriggerTargetingResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

}
