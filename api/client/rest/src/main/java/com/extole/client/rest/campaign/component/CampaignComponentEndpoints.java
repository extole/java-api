package com.extole.client.rest.campaign.component;

import java.time.ZoneId;
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
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;

import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentResponse;
import com.extole.client.rest.campaign.component.anchor.AnchorDetailsResponse;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Deprecated // TODO as soon as UI stops using it ENG-26408, it will be removed, see ENG-26409
@Path("/v2/campaigns/{campaign_id}{version:(/version/.+)?}/components")
public interface CampaignComponentEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<CampaignComponentResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{component_id}")
    CampaignComponentResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{component_id}/anchors")
    List<AnchorDetailsResponse> getAnchors(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    CampaignComponentResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignComponentCreateRequest request,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignComponentRestException,
        CampaignUpdateRestException, ComponentTypeRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{component_id}/duplicate")
    CampaignComponentResponse duplicate(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        ComponentDuplicateRequest componentDuplicateRequest,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentDuplicateRestException, CampaignRestException,
        CampaignComponentValidationRestException, CampaignComponentRootValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignComponentRestException,
        CampaignUpdateRestException, CreativeArchiveRestException, ComponentTypeRestException, SettingRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{component_id}")
    CampaignComponentResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        CampaignComponentUpdateRequest request,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentValidationRestException, CampaignComponentRootValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, OmissibleRestException, CampaignUpdateRestException,
        ComponentTypeRestException, BuildWebhookRestException, BuildPrehandlerRestException,
        BuildRewardSupplierRestException, BuildClientKeyRestException, BuildAudienceRestException,
        EventStreamValidationRestException, OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Operation(description = "This endpoint deletes component recursively. If \"root\" component is deleted then the"
        + " entire campaign is archived.")
    @Path("/{component_id}")
    CampaignComponentResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltCampaignComponentResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{component_id}/built")
    BuiltCampaignComponentResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        BuildCampaignRestException;

}
