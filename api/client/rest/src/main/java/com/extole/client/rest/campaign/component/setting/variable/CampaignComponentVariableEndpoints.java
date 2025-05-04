package com.extole.client.rest.campaign.component.setting.variable;

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

import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentRestException;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO remove after UI changes ENG-23427
@Path("/v2/campaigns/{campaign_id}{version:(/version/.+)?}/components/{component_id}/variables")
public interface CampaignComponentVariableEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    <TYPE extends CampaignComponentSettingResponse> TYPE create(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        CampaignComponentSettingRequest request)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{setting_name}")
    <TYPE extends CampaignComponentSettingResponse> TYPE update(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        @PathParam("setting_name") String settingName,
        CampaignComponentSettingUpdateRequest request)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException, SettingRestException,
        ComponentRestException, BuildWebhookRestException, BuildPrehandlerRestException,
        BuildRewardSupplierRestException, BuildClientKeyRestException, BuildAudienceRestException,
        EventStreamValidationRestException, OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{setting_name}")
    <TYPE extends CampaignComponentSettingResponse> TYPE delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("component_id") String componentId,
        @PathParam("setting_name") String settingName)
        throws UserAuthorizationRestException, BuildCampaignRestException, SettingValidationRestException,
        CampaignUpdateRestException, CampaignRestException, CampaignComponentRestException, SettingRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException, CampaignComponentValidationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{setting_name}")
    <TYPE extends CampaignComponentSettingResponse> TYPE get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @PathParam("setting_name") String settingName)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        SettingRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<? extends CampaignComponentSettingResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{setting_name}/built")
    BuiltCampaignComponentSettingResponse getBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId,
        @PathParam("setting_name") String settingName)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        BuildCampaignRestException, SettingRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltCampaignComponentSettingResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String version,
        @PathParam("component_id") String componentId)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignComponentRestException;

}
