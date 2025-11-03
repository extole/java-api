package com.extole.client.rest.campaign;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.built.BuiltCampaignListQueryParams;
import com.extole.client.rest.campaign.built.BuiltCampaignResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetValidationRestException;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.client.rest.campaign.controller.CampaignJourneyEntryValidationRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepValidationRestException;
import com.extole.client.rest.campaign.flow.step.app.CampaignFlowStepAppValidationRestException;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricValidationRestException;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleValidationRestException;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleValidationRestException;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleValidationRestException;
import com.extole.client.rest.campaign.label.CampaignLabelValidationRestException;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/campaigns")
@Tag(name = "/v2/campaigns", description = "Campaign")
public interface CampaignEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}")
    @Operation(summary = "Returns a campaign by id")
    CampaignResponse getCampaign(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with campaigns")
    List<CampaignResponse> getCampaigns(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam CampaignListQueryParams queryParams, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/built")
    BuiltCampaignResponse getBuiltCampaign(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with built campaigns")
    @Path("/built")
    List<BuiltCampaignResponse> getBuiltCampaigns(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam BuiltCampaignListQueryParams queryParams, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/duplicatable")
    List<CampaignResponse> getDuplicatableCampaigns(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/template")
    @Operation(summary = "Gets a list of templates")
    List<CampaignResponse> getTemplateCampaigns(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleted")
    List<CampaignResponse> getDeletedCampaigns(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{campaignId}{version:(/version/.+)?}.zip")
    Response downloadCampaignBundle(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/duplicate")
    @Operation(summary = "Duplicates a campaign")
    CampaignResponse duplicate(@UserAccessTokenParam String accessToken, @PathParam("campaignId") String campaignId,
        Optional<CampaignDuplicateRequest> duplicateRequest,
        @PathParam("version") String version, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignValidationRestException,
        CampaignLabelValidationRestException, CampaignControllerValidationRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignComponentRestException, SettingValidationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/lock")
    CampaignResponse lock(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignLockRequest lockRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/unlock")
    CampaignResponse unlock(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignUnlockRequest unlockRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a new campaign")
    CampaignResponse create(@UserAccessTokenParam String accessToken, CampaignCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignValidationRestException, BuildCampaignRestException,
        CampaignComponentRestException;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    CampaignResponse uploadNewCampaignArchive(@UserAccessTokenParam String accessToken,
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignValidationRestException, CampaignArchiveRestException,
        CampaignControllerValidationRestException, CampaignControllerActionRestException,
        CampaignControllerTriggerRestException, TransitionRuleValidationRestException,
        QualityRuleValidationRestException, RewardRuleValidationRestException, CampaignLabelValidationRestException,
        CreativeArchiveRestException, CampaignFlowStepValidationRestException, CampaignRestException,
        SettingValidationRestException, CampaignComponentAssetValidationRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException,
        CampaignFlowStepAppValidationRestException, SettingRestException,
        CampaignFlowStepMetricValidationRestException, CampaignComponentRestException, GlobalCampaignRestException,
        ComponentTypeRestException, CampaignFrontendControllerValidationRestException;

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}")
    CampaignResponse updateWithCampaignArchive(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @FormDataParam("file") InputStream fileInputStream,
        @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignValidationRestException, CampaignArchiveRestException,
        CampaignControllerValidationRestException, CampaignControllerActionRestException,
        CampaignControllerTriggerRestException, TransitionRuleValidationRestException,
        QualityRuleValidationRestException, RewardRuleValidationRestException, CampaignLabelValidationRestException,
        CreativeArchiveRestException, CampaignFlowStepValidationRestException, CampaignRestException,
        SettingValidationRestException, CampaignComponentAssetValidationRestException,
        BuildCampaignRestException, CampaignComponentValidationRestException, CampaignUpdateRestException,
        CampaignFlowStepAppValidationRestException, SettingRestException, CampaignFlowStepMetricValidationRestException,
        CampaignComponentRestException, GlobalCampaignRestException,
        ComponentTypeRestException, CampaignFrontendControllerValidationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}")
    @Operation(summary = "Edit an existing campaign")
    CampaignResponse editCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignUpdateRequest updateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, GlobalCampaignRestException,
        CampaignComponentRestException, SettingValidationRestException, CampaignComponentValidationRestException;

    // TODO after UI adjustment remove path param 'version' ENG-17676
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/make-latest-draft")
    CampaignResponse makeLatestDraft(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @TimeZoneParam ZoneId timeZone,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException, CampaignComponentValidationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/make-latest-published")
    CampaignResponse makeLatestPublished(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @TimeZoneParam ZoneId timeZone,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException, CampaignComponentValidationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/make-latest-preserve-state")
    CampaignResponse makeLatestPreserveState(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String version,
        @TimeZoneParam ZoneId timeZone,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException, CampaignComponentValidationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/launch-test")
    CampaignResponse launchTest(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        Optional<CampaignLaunchTestRequest> request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignLaunchRestException,
        CampaignControllerValidationRestException, BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException, GlobalCampaignRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/launch-burst")
    CampaignResponse launchBurst(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        Optional<CampaignLaunchBurstRequest> request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignLaunchRestException,
        CampaignControllerValidationRestException, BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/publish")
    @Operation(summary = "Publish an existing campaign")
    CampaignResponse publish(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        Optional<CampaignPublishRequest> request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        CampaignJourneyEntryValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException, SettingValidationRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/discard")
    CampaignResponse discard(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException;

    // TODO remove as part of ENG-19026
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/pause")
    CampaignResponse pauseCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        Optional<CampaignScheduleStateRequest> scheduleRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException;

    // TODO remove as part of ENG-19026
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/end")
    CampaignResponse endCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        Optional<CampaignScheduleStateRequest> scheduleRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException;

    // TODO remove as part of ENG-19026
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/stop")
    CampaignResponse stopCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        Optional<CampaignScheduleStateRequest> scheduleRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/archive")
    CampaignResponse archiveCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException, GlobalCampaignRestException,
        CampaignComponentRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}")
    CampaignResponse deleteCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentValidationRestException, CampaignComponentRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/unarchive")
    CampaignResponse unArchiveCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @TimeZoneParam ZoneId timeZone)
        throws CampaignRestException, UserAuthorizationRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/undelete")
    CampaignResponse unDeleteCampaign(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @TimeZoneParam ZoneId timeZone)
        throws CampaignRestException, UserAuthorizationRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException, CampaignComponentRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/live")
    CampaignResponse liveCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignEnableRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CampaignComponentRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}/versions")
    List<CampaignVersionDescriptionResponse> getVersions(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @Nullable @QueryParam("limit") String limit,
        @Nullable @QueryParam("offset") String offset, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, QueryLimitsRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}/versions/{campaignVersion}")
    CampaignVersionDescriptionResponse getVersion(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaignId") String campaignId, @PathParam("campaignVersion") String version,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/steps")
    StepsResponse getSteps(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{campaignId}{version:(/version/.+)?}/schedule")
    CampaignResponse scheduleCampaign(@UserAccessTokenParam String accessToken,
        @PathParam("campaignId") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        CampaignScheduleRequest scheduleRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignUpdateRestException, CampaignRestException,
        BuildCampaignRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException;

}
