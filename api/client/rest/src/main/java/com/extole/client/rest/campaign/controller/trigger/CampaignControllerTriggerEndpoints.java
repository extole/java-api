package com.extole.client.rest.campaign.controller.trigger;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/triggers")
public interface CampaignControllerTriggerEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    <RESPONSE extends CampaignControllerTriggerResponse> List<RESPONSE> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("controllerId") String controllerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/built")
    <RESPONSE extends BuiltCampaignControllerTriggerResponse> List<RESPONSE> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("controllerId") String controllerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}")
    <RESPONSE extends CampaignControllerTriggerResponse> RESPONSE get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{triggerId}/built")
    <RESPONSE extends BuiltCampaignControllerTriggerResponse> RESPONSE getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("controllerId") String controllerId,
        @PathParam("triggerId") String triggerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException;

}
