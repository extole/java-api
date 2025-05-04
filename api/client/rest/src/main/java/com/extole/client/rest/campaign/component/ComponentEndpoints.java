package com.extole.client.rest.campaign.component;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;

import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.BuiltComponentResponse;
import com.extole.client.rest.campaign.component.anchor.AnchorDetailsResponse;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableUpdateResponse;
import com.extole.client.rest.campaign.component.setting.ComponentVariableTargetRestException;
import com.extole.client.rest.campaign.component.setting.ComponentVariablesDownloadRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.ExtoleMediaType;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.FileFormatRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/components")
public interface ComponentEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<ComponentResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @BeanParam ComponentListRequest componentListRequest)
        throws UserAuthorizationRestException, ComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{component_id}")
    ComponentResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("component_id") String componentId,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{component_id}/anchors")
    List<AnchorDetailsResponse> getAnchors(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("component_id") String componentId, @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{component_id}{version:(/version/.+)?}/duplicate")
    ComponentResponse duplicate(@UserAccessTokenParam String accessToken,
        @PathParam("component_id") String componentId,
        @PathParam("version") String expectedCurrentVersion,
        ComponentDuplicateRequest componentDuplicateRequest,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentDuplicateRestException, ComponentRestException,
        CampaignRestException, CampaignComponentValidationRestException, CampaignComponentRootValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignComponentRestException, CreativeArchiveRestException, ComponentTypeRestException, SettingRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Operation(description = "This endpoint deletes component recursively. If \"root\" component is deleted then the"
        + " entire campaign is archived.")
    @Path("/{component_id}{version:(/version/.+)?}")
    ComponentResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("component_id") String componentId,
        @PathParam("version") String expectedCurrentVersion,
        @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/built")
    List<BuiltComponentResponse> listBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Nullable @BeanParam ComponentListRequest componentListRequest)
        throws UserAuthorizationRestException, ComponentRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{component_id}/built")
    BuiltComponentResponse getBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("component_id") String componentId, @Nullable @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException, BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{component_id}/variables")
    List<BatchComponentVariableUpdateResponse> batchGetComponentVariables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("component_id") String componentId) throws UserAuthorizationRestException, ComponentRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/variables")
    List<BatchComponentVariableUpdateResponse> batchUpdateComponentVariables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @DefaultValue("true") @QueryParam("ignore_unknown_variables") boolean ignoreUnknownVariables,
        List<BatchComponentVariableUpdateRequest> variablesFromRequest)
        throws UserAuthorizationRestException, SettingRestException, SettingValidationRestException,
        BuildCampaignRestException, ComponentRestException, ComponentVariableTargetRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

    @GET
    @Path("/{component_id}/variables.{format :(csv|json|xlsx)?}")
    @Produces({ExtoleMediaType.TEXT_CSV, ExtoleMediaType.APPLICATION_JSON, ExtoleMediaType.APPLICATION_EXCEL})
    Response batchGetComponentVariablesValues(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("component_id") String componentId,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) Optional<String> contentType,
        @PathParam("format") Optional<String> format,
        @QueryParam("filename") Optional<String> filename,
        @BeanParam ComponentVariablesDownloadRequest variablesDownloadRequest)
        throws UserAuthorizationRestException, ComponentRestException;

    @PUT
    @Path("/variables")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    List<BatchComponentVariableUpdateResponse> batchUpdateComponentVariablesValues(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("target_component_id") Optional<String> targetComponentId,
        @DefaultValue("true") @QueryParam("ignore_unknown_variables") boolean ignoreUnknownVariables,
        FileInputStreamRequest fileRequest)
        throws UserAuthorizationRestException, SettingValidationRestException, FileFormatRestException,
        SettingRestException, BuildCampaignRestException, ComponentRestException, ComponentVariableTargetRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

    @GET
    @Path("/{component_id}/translatable.{format :(csv|json|xlsx)?}")
    @Produces({ExtoleMediaType.TEXT_CSV, ExtoleMediaType.APPLICATION_JSON, ExtoleMediaType.APPLICATION_EXCEL})
    Response batchGetTranslatableComponentVariablesValues(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("component_id") String componentId,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) Optional<String> contentType,
        @PathParam("format") Optional<String> format,
        @QueryParam("filename") Optional<String> filename,
        @BeanParam ComponentVariablesDownloadRequest variablesDownloadRequest)
        throws UserAuthorizationRestException, ComponentRestException;

    @PUT
    @Path("/translatable")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    List<BatchComponentVariableUpdateResponse> batchUpdateTranslatableComponentVariablesValues(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("target_component_id") Optional<String> targetComponentId,
        @DefaultValue("true") @QueryParam("ignore_unknown_variables") boolean ignoreUnknownVariables,
        FileInputStreamRequest fileRequest)
        throws UserAuthorizationRestException, SettingValidationRestException, FileFormatRestException,
        SettingRestException, BuildCampaignRestException, ComponentRestException, ComponentVariableTargetRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException;

}
