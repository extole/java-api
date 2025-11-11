package com.extole.client.rest.campaign.controller.trigger.group;

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
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerGroupResponse;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/triggers/groups")
public interface CampaignControllerTriggerGroupEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignControllerTriggerGroupResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        CampaignControllerTriggerGroupCreateRequest createRequest)
        throws CampaignRestException, UserAuthorizationRestException, CampaignControllerRestException,
        CampaignControllerTriggerGroupValidationRestException, BuildCampaignControllerRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerGroupResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId,
        CampaignControllerTriggerGroupUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerTriggerGroupValidationRestException, BuildCampaignControllerRestException,
        CampaignControllerTriggerValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerGroupResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, BuildCampaignControllerRestException, CampaignUpdateRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    CampaignControllerTriggerGroupResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}/built")
    BuiltCampaignControllerTriggerGroupResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

}
