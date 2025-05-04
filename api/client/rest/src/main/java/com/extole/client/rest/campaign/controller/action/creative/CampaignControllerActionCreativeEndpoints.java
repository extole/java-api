package com.extole.client.rest.campaign.controller.action.creative;

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
import javax.ws.rs.core.Response;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.request.FileInputStreamRequest;

@Path("/v2/campaigns/{campaignId}{version:(/version/.+)?}/controllers/{controllerId}/actions/creatives")
public interface CampaignControllerActionCreativeEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    CampaignControllerActionCreativeResponse create(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("controllerId") String controllerId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignControllerActionCreativeCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException, BuildCampaignRestException,
        CampaignFrontendControllerValidationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionCreativeResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignControllerActionCreativeUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException, BuildCampaignRestException,
        CampaignFrontendControllerValidationRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionCreativeResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignUpdateRestException, BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actionId}")
    CampaignControllerActionCreativeResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("version") String version,
        @PathParam("controllerId") String controllerId, @PathParam("actionId") String actionId)
        throws UserAuthorizationRestException, CampaignControllerRestException, CampaignRestException;

    @GET
    @Path("/{actionId}.zip")
    Response downloadCreative(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId,
        @QueryParam("filename") Optional<String> filename)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CreativeArchiveRestException, CampaignControllerActionCreativeMissingRestException;

    @POST
    @Path("/{actionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    CampaignControllerActionCreativeResponse uploadCreative(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @PathParam("controllerId") String controllerId,
        @PathParam("actionId") String actionId,
        FileInputStreamRequest fileRequest)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CreativeArchiveRestException, CampaignControllerRestException, CampaignUpdateRestException,
        CampaignControllerActionCreativeValidationRestException;
}
