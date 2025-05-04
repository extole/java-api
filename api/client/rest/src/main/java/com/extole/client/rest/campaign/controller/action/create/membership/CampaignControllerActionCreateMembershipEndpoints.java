package com.extole.client.rest.campaign.controller.action.create.membership;

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
import com.extole.common.rest.omissible.OmissibleRestException;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/actions/create-memberships")
public interface CampaignControllerActionCreateMembershipEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignControllerActionCreateMembershipResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        CampaignControllerActionCreateMembershipCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionCreateMembershipValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionCreateMembershipResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("actionId") String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionCreateMembershipResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId,
        CampaignControllerActionCreateMembershipUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignControllerActionCreateMembershipValidationRestException, CampaignComponentValidationRestException,
        BuildCampaignRestException, OmissibleRestException, CampaignUpdateRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionCreateMembershipResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

}
