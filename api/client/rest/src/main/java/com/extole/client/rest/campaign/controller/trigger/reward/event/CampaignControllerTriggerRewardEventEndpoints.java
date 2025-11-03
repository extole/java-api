package com.extole.client.rest.campaign.controller.trigger.reward.event;

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
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/triggers/reward-events")
public interface CampaignControllerTriggerRewardEventEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignControllerTriggerRewardEventResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        CampaignControllerTriggerRewardEventCreateRequest request)
        throws CampaignRestException, UserAuthorizationRestException, CampaignControllerRestException,
        CampaignControllerTriggerRewardEventValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        BuildCampaignControllerRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerRewardEventResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId,
        CampaignControllerTriggerRewardEventCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerRewardEventValidationRestException, CampaignControllerTriggerValidationRestException,
        CampaignComponentValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        BuildCampaignControllerRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerRewardEventResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException, BuildCampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerRewardEventResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;
}
