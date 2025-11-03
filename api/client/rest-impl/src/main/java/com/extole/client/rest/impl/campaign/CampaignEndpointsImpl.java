package com.extole.client.rest.impl.campaign;

import static com.extole.client.rest.impl.campaign.upload.CampaignUploader.CAMPAIGN_JSON_FILENAME;
import static com.extole.client.rest.impl.campaign.upload.CampaignUploader.COMPONENTS_FOLDER_NAME;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignArchiveRestException;
import com.extole.client.rest.campaign.CampaignCreateRequest;
import com.extole.client.rest.campaign.CampaignDuplicateRequest;
import com.extole.client.rest.campaign.CampaignEndpoints;
import com.extole.client.rest.campaign.CampaignLaunchBurstRequest;
import com.extole.client.rest.campaign.CampaignLaunchRestException;
import com.extole.client.rest.campaign.CampaignLaunchTestRequest;
import com.extole.client.rest.campaign.CampaignListQueryParams;
import com.extole.client.rest.campaign.CampaignLockRequest;
import com.extole.client.rest.campaign.CampaignMakeLatestRequest;
import com.extole.client.rest.campaign.CampaignPublishRequest;
import com.extole.client.rest.campaign.CampaignResponse;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignScheduleRequest;
import com.extole.client.rest.campaign.CampaignScheduleStateRequest;
import com.extole.client.rest.campaign.CampaignScheduleValidationRestException;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.CampaignUnlockRequest;
import com.extole.client.rest.campaign.CampaignUpdateRequest;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.CampaignValidationRestException;
import com.extole.client.rest.campaign.CampaignVersionDescriptionResponse;
import com.extole.client.rest.campaign.GlobalCampaignRestException;
import com.extole.client.rest.campaign.StepResponse;
import com.extole.client.rest.campaign.StepsResponse;
import com.extole.client.rest.campaign.built.BuiltCampaignListQueryParams;
import com.extole.client.rest.campaign.built.BuiltCampaignResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetValidationRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentAssetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignConfiguration;
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
import com.extole.client.rest.impl.campaign.built.BuiltCampaignRestMapper;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.client.rest.impl.campaign.upload.CampaignUploader;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignLockType;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.campaign.CreativeArchiveVersionException;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.InvalidExternalComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.BuiltCampaignQueryBuilder;
import com.extole.model.service.campaign.BuiltCampaignService;
import com.extole.model.service.campaign.CampaignBackdatedStartDateException;
import com.extole.model.service.campaign.CampaignBackdatedStopDateException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignDateAfterStopDateException;
import com.extole.model.service.campaign.CampaignDateBeforeStartDateException;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalRenameException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignHasPendingChangesException;
import com.extole.model.service.campaign.CampaignHasScheduledSiblingException;
import com.extole.model.service.campaign.CampaignInvalidStateException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignMissingLockTypesException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignProgramTypeEmptyException;
import com.extole.model.service.campaign.CampaignProgramTypeInvalidException;
import com.extole.model.service.campaign.CampaignQueryBuilder;
import com.extole.model.service.campaign.CampaignRemoveStartDateException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceDescriptionLengthException;
import com.extole.model.service.campaign.CampaignServiceDuplicateNameException;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignServicePendingChangeException;
import com.extole.model.service.campaign.CampaignServiceTagInvalidException;
import com.extole.model.service.campaign.CampaignServiceTagsLengthException;
import com.extole.model.service.campaign.CampaignStartDateAfterStopDateException;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionDescription;
import com.extole.model.service.campaign.CampaignVersionNotFoundException;
import com.extole.model.service.campaign.CampaignVersionState;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.ComponentAbsoluteNameFinder;
import com.extole.model.service.campaign.component.OrphanExternalComponentReferenceException;
import com.extole.model.service.campaign.component.asset.ComponentAssetNotFoundException;
import com.extole.model.service.campaign.component.asset.ComponentAssetService;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.exception.CampaignControllerWithoutMatchingEventTriggerException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerMisconfigurationException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerNotFoundPageMisconfigurationException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.draft.DraftPreservingCampaignBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryNonMatchingTriggersException;
import com.extole.model.service.campaign.label.CampaignLabelBuildException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelIllegalCharacterInNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.label.CampaignLabelNameLengthException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.setting.ComponentBuildSettingException;
import com.extole.model.service.campaign.setting.InvalidVariableTranslatableValueException;
import com.extole.model.service.campaign.setting.SettingValidationException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentFacetException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentTypeException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetNameException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetValueException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentTypeException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.CreativeArchiveService;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeArchiveNotFoundException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;
import com.extole.model.service.event.stream.built.BuildEventStreamException;
import com.extole.model.service.prehandler.built.BuildPrehandlerException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.model.shared.campaign.built.BuiltCampaignCache;
import com.extole.person.service.StepName;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class CampaignEndpointsImpl implements CampaignEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(CampaignEndpointsImpl.class);
    private static final int DEFAULT_VERSIONS_LIST_LIMIT = 100;
    private static final int DEFAULT_VERSIONS_LIST_OFFSET = 0;

    private final CampaignService campaignService;
    private final ComponentAssetService componentAssetService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignRestMapper campaignRestMapper;
    private final BuiltCampaignRestMapper builtCampaignRestMapper;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final BuiltCampaignCache builtCampaignCache;
    private final BuiltCampaignService builtCampaignService;
    private final ObjectMapper mapper;
    private final CreativeArchiveService creativeArchiveService;
    private final CampaignUploader campaignUploader;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final ComponentAbsoluteNameFinder componentAbsoluteNameFinder;

    private static final int BYTES_PER_KB = 1024;

    private static final int BYTES_PER_MB = BYTES_PER_KB * 1024;
    private static final int STANDARD_DOWNLOAD_THRESHOLD = 256 * BYTES_PER_KB;
    private static final int VIP_DOWNLOAD_THRESHOLD = 50 * BYTES_PER_MB;
    private static final Map<Id<ClientHandle>, Integer> CLIENT_DOWNLOAD_THRESHOLD =
        Map.of(Id.valueOf("1842186254"), Integer.valueOf(VIP_DOWNLOAD_THRESHOLD));

    @Inject
    public CampaignEndpointsImpl(CampaignService campaignService,
        ComponentAssetService componentAssetService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignRestMapper campaignRestMapper,
        BuiltCampaignRestMapper builtCampaignRestMapper,
        CampaignComponentSettingRestMapper settingRestMapper,
        BuiltCampaignCache builtCampaignCache,
        BuiltCampaignService builtCampaignService,
        CreativeArchiveService creativeArchiveService,
        CampaignUploader campaignUploader,
        BackendAuthorizationProvider backendAuthorizationProvider,
        @Qualifier("clientApiObjectMapper") javax.inject.Provider<ObjectMapper> objectMapperProvider,
        ComponentAbsoluteNameFinder componentAbsoluteNameFinder) {
        this.campaignService = campaignService;
        this.componentAssetService = componentAssetService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.campaignRestMapper = campaignRestMapper;
        this.builtCampaignRestMapper = builtCampaignRestMapper;
        this.settingRestMapper = settingRestMapper;
        this.builtCampaignCache = builtCampaignCache;
        this.builtCampaignService = builtCampaignService;
        this.creativeArchiveService = creativeArchiveService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.campaignUploader = campaignUploader;
        this.mapper = objectMapperProvider.get()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        this.componentAbsoluteNameFinder = componentAbsoluteNameFinder;
    }

    @Override
    public CampaignResponse getCampaign(String accessToken, String campaignId, String version, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign =
            campaignProvider.getCampaignIncludeArchived(userAuthorization, Id.valueOf(campaignId), version);
        return campaignRestMapper.toCampaignResponse(campaign, timeZone);
    }

    @Override
    public List<CampaignResponse> getCampaigns(String accessToken, CampaignListQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignVersionState state =
            campaignProvider.getCampaignVersionState(queryParams.getVersion());
        CampaignQueryBuilder campaignQueryBuilder = campaignService.createCampaignQueryBuilder(authorization);
        campaignQueryBuilder.withCampaignVersionState(state);
        if (queryParams.getRewardSupplierId() != null) {
            campaignQueryBuilder.withRewardSupplierId(Id.valueOf(queryParams.getRewardSupplierId()));
        }
        if (queryParams.getTags() != null && !queryParams.getTags().isEmpty()) {
            campaignQueryBuilder.withTags(queryParams.getTags());
        }
        if (Boolean.TRUE.equals(queryParams.getIncludeArchived())) {
            campaignQueryBuilder.includeArchived();
        }

        return campaignQueryBuilder
            .list()
            .stream()
            .map(campaign -> campaignRestMapper.toCampaignResponse(campaign, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public BuiltCampaignResponse getBuiltCampaign(String accessToken, String campaignId, String version,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);

        return builtCampaignRestMapper.toBuiltCampaignResponse(campaign, timeZone);
    }

    @Override
    public List<BuiltCampaignResponse> getBuiltCampaigns(String accessToken, BuiltCampaignListQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignVersionState state = campaignProvider.getCampaignVersionState(queryParams.getVersion().orElse(null));
        BuiltCampaignQueryBuilder campaignQueryBuilder =
            builtCampaignService.createCampaignQueryBuilder(userAuthorization)
                .withCampaignVersionState(state);
        queryParams.getRewardSupplierId()
            .ifPresent(
                rewardSupplierId -> campaignQueryBuilder.withRewardSupplierId(Id.valueOf(rewardSupplierId.getValue())));
        queryParams.getProgramLabel().ifPresent(campaignQueryBuilder::withProgramLabel);

        return campaignQueryBuilder.list()
            .stream()
            .map(builtCampaign -> builtCampaignRestMapper.toBuiltCampaignResponse(builtCampaign, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public List<CampaignResponse> getDuplicatableCampaigns(String accessToken,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return campaignService.getDuplicatableCampaigns(authorization).stream()
                .map(campaign -> campaignRestMapper.toCampaignResponse(campaign, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<CampaignResponse> getTemplateCampaigns(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return campaignService.getTemplateCampaigns(authorization).stream()
                .map(campaign -> campaignRestMapper.toCampaignResponse(campaign, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<CampaignResponse> getDeletedCampaigns(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return campaignService.getDeletedCampaigns(userAuthorization).stream()
                .map(campaign -> campaignRestMapper.toCampaignResponse(campaign, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadCampaignBundle(String accessToken, String campaignId, String version, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException {
        UUID uuid = UUID.randomUUID();

        LOG.warn("Download campaign bundle request received for campaignId: {}, version: {}, uuid: {}", campaignId,
            version, uuid);

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        LOG.warn("Authorization loaded for campaignId: {}, version: {}, uuid: {}", campaignId, version, uuid);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        LOG.warn("Campaign loaded for campaignId: {}, version: {}, uuid: {}", campaignId, version, uuid);

        if (!authorization.getClientId().equals(campaign.getClientId())) {
            authorization = getClientBackendAuthorization(campaign.getClientId());
        }

        CampaignConfiguration campaignConfiguration =
            campaignRestMapper.toCampaignConfiguration(authorization, campaign, timeZone);
        Map<Id<CampaignComponent>, String> componentAbsoluteNames =
            componentAbsoluteNameFinder.findAbsoluteNameById(campaign.getComponents());

        LOG.warn("Campaign configuration done for campaignId: {}, version: {}, uuid: {}", campaignId, version, uuid);
        String campaignName = campaign.getName().toLowerCase().replaceAll("[^a-z0-9]", "-");

        int threshold = CLIENT_DOWNLOAD_THRESHOLD.getOrDefault(authorization.getClientId(),
            Integer.valueOf(STANDARD_DOWNLOAD_THRESHOLD)).intValue();

        try (FileBackedOutputStream fileOutputStream = new FileBackedOutputStream(threshold, true)) {
            try (ZipOutputStream zipOutput = new ZipOutputStream(fileOutputStream)) {
                zipOutput.putNextEntry(new ZipEntry(campaignName + "/" + CAMPAIGN_JSON_FILENAME));
                zipOutput.write(mapper.writeValueAsString(campaignConfiguration).getBytes());

                Authorization assetAuthorization =
                    backendAuthorizationProvider.getAuthorizationForBackend(campaign.getClientId());
                extractActionsCreativesToCampaignArchive(authorization, campaign, campaignName, zipOutput);
                LOG.warn("Extracted creatives for campaignId: {}, version: {}, uuid: {}", campaignId, version, uuid);

                for (CampaignComponentConfiguration component : campaignConfiguration.getComponents()) {
                    for (CampaignComponentAssetConfiguration asset : component.getAssets()) {
                        String assetName = asset.getName();

                        String contentPath;
                        if (component.getName().equalsIgnoreCase(CampaignComponent.ROOT)) {
                            contentPath = campaignName + "/assets/" + assetName;
                        } else {
                            String absoluteName = componentAbsoluteNames.get(component.getId().getValue());
                            contentPath = campaignName + absoluteName.replace("/", "/" + COMPONENTS_FOLDER_NAME + "/")
                                + "/assets/" + assetName;
                        }

                        zipOutput.putNextEntry(new ZipEntry(contentPath));
                        InputStream assetContent = componentAssetService
                            .get(assetAuthorization, Id.valueOf(asset.getId().getValue().getValue()),
                                campaign.getVersion())
                            .getContent()
                            .openBufferedStream();
                        ByteStreams.copy(assetContent, zipOutput);
                        LOG.warn("Extracted asset for campaignId: {}, version: {}, uuid: {}", campaignId, version,
                            uuid);
                    }
                }
                LOG.warn("Loaded components for campaignId: {}, version: {}, uuid: {}", campaignId, version, uuid);

                StreamingOutput stream = outputStream -> fileOutputStream.asByteSource().copyTo(outputStream);

                return Response.ok(stream).type("application/zip")
                    .header("Content-Disposition", "attachment; filename=" + campaignName + ".zip").build();
            }
        } catch (IOException | CreativeArchiveNotFoundException | CreativeArchiveVersionException
            | AuthorizationException | ComponentAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse uploadNewCampaignArchive(String accessToken, InputStream inputStream,
        FormDataContentDisposition contentDispositionHeader, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignArchiveRestException,
        CampaignValidationRestException, RewardRuleValidationRestException,
        CampaignControllerValidationRestException, CampaignControllerActionRestException,
        CampaignControllerTriggerRestException, TransitionRuleValidationRestException,
        QualityRuleValidationRestException, CampaignLabelValidationRestException, CreativeArchiveRestException,
        CampaignFlowStepValidationRestException, CampaignRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignComponentAssetValidationRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException, CampaignComponentRestException, GlobalCampaignRestException,
        ComponentTypeRestException, CampaignFrontendControllerValidationRestException, CampaignUpdateRestException {

        if (inputStream == null || contentDispositionHeader == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
        return campaignUploader.newUpload()
            .withInputStream(inputStream)
            .withContentDispositionHeader(contentDispositionHeader)
            .withTimeZone(timeZone)
            .withAccessToken(accessToken)
            .withObjectMapper(mapper)
            .upload();
    }

    @Override
    public CampaignResponse updateWithCampaignArchive(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        InputStream inputStream,
        FormDataContentDisposition contentDispositionHeader, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignArchiveRestException,
        CampaignValidationRestException, RewardRuleValidationRestException,
        CampaignControllerValidationRestException, CampaignControllerActionRestException,
        CampaignControllerTriggerRestException, TransitionRuleValidationRestException,
        QualityRuleValidationRestException, CampaignLabelValidationRestException, CreativeArchiveRestException,
        CampaignFlowStepValidationRestException, CampaignRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignComponentAssetValidationRestException, CampaignFlowStepMetricValidationRestException,
        CampaignFlowStepAppValidationRestException, CampaignComponentRestException, GlobalCampaignRestException,
        ComponentTypeRestException, CampaignFrontendControllerValidationRestException,
        CampaignUpdateRestException {

        if (inputStream == null || contentDispositionHeader == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), expectedCurrentVersion);
        return campaignUploader.newUpload()
            .withInputStream(inputStream)
            .withCampaign(campaign)
            .withContentDispositionHeader(contentDispositionHeader)
            .withTimeZone(timeZone)
            .withAccessToken(accessToken)
            .withObjectMapper(mapper)
            .upload();
    }

    @Override
    public List<CampaignVersionDescriptionResponse> getVersions(String accessToken, String campaignId, String limit,
        String offset, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, QueryLimitsRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<CampaignVersionDescription> campaignDescriptions = campaignService.getCampaignVersionDescriptions(
                userAuthorization, Id.valueOf(campaignId),
                Integer.valueOf(parseLimit(limit, DEFAULT_VERSIONS_LIST_LIMIT)),
                Integer.valueOf(parseOffset(offset, DEFAULT_VERSIONS_LIST_OFFSET)));
            return campaignDescriptions.stream()
                .map(version -> campaignRestMapper.toCampaignVersionDescription(version, timeZone))
                .collect(Collectors.toList());
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignVersionDescriptionResponse getVersion(String accessToken, String campaignId, String version,
        ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        Id<Campaign> id = Id.valueOf(campaignId);

        CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(Id.valueOf(campaignId), version);
        CampaignVersionDescription campaignDescription = getCampaignVersionDescription(userAuthorization,
            id, campaignVersion);

        return campaignRestMapper.toCampaignVersionDescription(campaignDescription, timeZone);
    }

    @Override
    public StepsResponse getSteps(String accessToken) throws UserAuthorizationRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);

        Set<String> steps = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
        StepName.defaultNames().forEach(name -> steps.add(name.getValue()));
        builtCampaignCache.getPublicCampaignsPublishedVersion(userAuthorization.getClientId())
            .forEach(campaign -> {
                campaign.getActionableSteps()
                    .forEach(controller -> {
                        steps.add(controller.getName());
                        steps.addAll(controller.getAliases());
                    });
            });
        return new StepsResponse(
            steps.stream().map(step -> new StepResponse(step.toLowerCase())).collect(Collectors.toList()));
    }

    @Override
    public CampaignResponse duplicate(String accessToken, String campaignId, Optional<CampaignDuplicateRequest> request,
        String version, ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException,
        CampaignValidationRestException, CampaignLabelValidationRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException, CampaignComponentRestException, SettingValidationRestException {

        CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(Id.valueOf(campaignId), version);
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.buildDuplicate(authorization, Id.valueOf(campaignId),
                campaignVersion);
            if (request.isPresent()) {
                if (!Strings.isNullOrEmpty(request.get().getProgramLabel())) {
                    campaignBuilder.withProgramLabel(request.get().getProgramLabel());
                }
                if (!Strings.isNullOrEmpty(request.get().getMessage())) {
                    campaignBuilder.withMessage(request.get().getMessage());
                }

                if (!Strings.isNullOrEmpty(request.get().getDescription())) {
                    campaignBuilder.withDescription(request.get().getDescription());
                }
            }
            Campaign campaign = campaignBuilder.save();
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (CampaignServiceDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", request.get().getDescription())
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignServiceNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getMinLength())
                .addParameter("max_length", e.getMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignServiceIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (CampaignLabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", request.get().getProgramLabel())
                .withCause(e)
                .build();
        } catch (CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLabelValidationRestException.class)
                .withErrorCode(CampaignLabelValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignServiceNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_IS_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (InvalidExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (SocketFilterMissingComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_COMPONENT_TYPE_MISSING)
                .withCause(e)
                .build();
        } catch (SocketFilterInvalidComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_INVALID_COMPONENT_TYPE)
                .addParameter("component_type", e.getComponentType())
                .withCause(e)
                .build();
        } catch (SocketFilterInvalidComponentFacetException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_INVALID_COMPONENT_FACET)
                .addParameter("facet_name", e.getComponentFacetName())
                .addParameter("facet_value", e.getComponentFacetValue())
                .withCause(e)
                .build();
        } catch (SocketFilterMissingComponentFacetNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_COMPONENT_FACET_NAME_MISSING)
                .withCause(e)
                .build();
        } catch (SocketFilterMissingComponentFacetValueException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_FILTER_COMPONENT_FACET_VALUE_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.COMPONENT_FACETS_NOT_FOUND)
                .addParameter("facets", e.getFacets())
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (AuthorizationException | CreativeArchiveJavascriptException | CreativeArchiveBuilderException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | StaleCampaignVersionException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException
            | StepDataBuildException | ConcurrentCampaignUpdateException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException | CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse lock(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        CampaignLockRequest lockRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Set<CampaignLockType> lockTypes = lockRequest.getLockTypes().stream()
                .map(lockType -> CampaignLockType.valueOf(lockType.name()))
                .collect(Collectors.toSet());
            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);

            Campaign campaign;
            if (expectedCampaignVersion.isPresent()) {
                campaign = campaignService.lock(authorization, Id.valueOf(campaignId), lockTypes,
                    expectedCampaignVersion.get());
            } else {
                campaign = campaignService.lock(authorization, Id.valueOf(campaignId), lockTypes);
            }

            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignMissingLockTypesException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.MISSING_LOCK_TYPES)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignHasPendingChangesException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_HAS_PENDING_CHANGES)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignResponse unlock(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        CampaignUnlockRequest unlockRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Set<CampaignLockType> locksToUnlock = unlockRequest.getLockTypes().stream()
                .map(lockType -> CampaignLockType.valueOf(lockType.name()))
                .collect(Collectors.toSet());
            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
            Campaign campaign;

            if (expectedCampaignVersion.isPresent()) {
                campaign = campaignService.unlock(authorization, Id.valueOf(campaignId), locksToUnlock,
                    expectedCampaignVersion.get());
            } else {
                campaign = campaignService.unlock(authorization, Id.valueOf(campaignId), locksToUnlock);
            }

            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignMissingLockTypesException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.UNLOCK_MISSING_LOCK_TYPES)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignResponse editCampaign(String accessToken, String campaignId, String expectedCurrentVersion,
        CampaignUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, GlobalCampaignRestException,
        CampaignComponentRestException, SettingValidationRestException, CampaignComponentValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignBuilder campaignBuilder = getCampaignBuilder(authorization, campaignId, expectedCurrentVersion);
        return update(updateRequest, campaignBuilder, timeZone);
    }

    @Override
    public CampaignResponse makeLatestDraft(String accessToken, String campaignId, String version, ZoneId timeZone,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException, CampaignComponentValidationRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Id<Campaign> idCampaign = Id.valueOf(campaignId);
        CampaignMakeLatestRequest requestData = getDefaultCampaignMakeLatestRequest(version,
            campaignMakeLatestDraftRequest);

        CampaignBuilder campaignBuilder = provideCampaignBuilderForMakeLatestVersion(authorization, idCampaign,
            requestData);
        Campaign campaign = applySaveForMakeLatestVersion(campaignBuilder);
        return campaignRestMapper.toCampaignResponse(campaign, timeZone);
    }

    @Override
    public CampaignResponse makeLatestPublished(String accessToken,
        String campaignId,
        String version,
        ZoneId timeZone,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException, CampaignComponentValidationRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignMakeLatestRequest requestData = getDefaultCampaignMakeLatestRequest(version,
            campaignMakeLatestDraftRequest);

        CampaignBuilder campaignBuilder = provideCampaignBuilderForMakeLatestVersion(authorization,
            Id.valueOf(campaignId), requestData);
        campaignBuilder.withPublished();
        Campaign campaign = applySaveForMakeLatestVersion(campaignBuilder);
        return campaignRestMapper.toCampaignResponse(campaign, timeZone);
    }

    @Override
    public CampaignResponse makeLatestPreserveState(String accessToken,
        String campaignId,
        String version,
        ZoneId timeZone,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException, CampaignComponentValidationRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Id<Campaign> idCampaign = Id.valueOf(campaignId);
        CampaignMakeLatestRequest requestData = getDefaultCampaignMakeLatestRequest(version,
            campaignMakeLatestDraftRequest);

        CampaignBuilder campaignBuilder = provideCampaignBuilderForMakeLatestVersion(authorization, idCampaign,
            requestData);

        CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(Id.valueOf(campaignId),
            requestData.getVersion().getValue());
        CampaignVersionDescription campaignVersionDescription = getCampaignVersionDescription(authorization,
            idCampaign, campaignVersion);
        if (campaignVersionDescription.getPublishedAt().isPresent()) {
            campaignBuilder.withPublished();
        }
        Campaign campaign = applySaveForMakeLatestVersion(campaignBuilder);
        return campaignRestMapper.toCampaignResponse(campaign, timeZone);
    }

    @Override
    public CampaignResponse create(String accessToken, CampaignCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignValidationRestException, BuildCampaignRestException,
        CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CampaignBuilder campaignBuilder = campaignService.create(authorization);

        return save(createRequest, campaignBuilder, timeZone);
    }

    @Override
    public CampaignResponse launchTest(String accessToken, String campaignId, String expectedCurrentVersion,
        Optional<CampaignLaunchTestRequest> request, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignLaunchRestException,
        CampaignControllerValidationRestException, BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException, GlobalCampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.launchTest(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            if (request.isPresent()) {
                request.get().getMessage()
                    .ifPresent(message -> message.ifPresent(value -> campaignBuilder.withMessage(value)));
                if (request.get().getStopDate().isPresent()) {
                    Optional<ZonedDateTime> stopDate = request.get().getStopDate().getValue();
                    if (stopDate.isPresent()) {
                        campaignBuilder.withStopDate(stopDate.get().toInstant());
                    }
                }
                if (request.get().getStartDate().isOmitted()) {
                    campaignBuilder.withStartDateNow();
                } else {
                    Optional<ZonedDateTime> startDate = request.get().getStartDate().getValue();
                    if (startDate.isPresent()) {
                        campaignBuilder.withStartDate(startDate.get().toInstant());
                    } else {
                        campaignBuilder.withStartDateNow();
                    }
                }
            } else {
                campaignBuilder.withStartDateNow();
            }

            Campaign campaign = campaignBuilder.save();
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignInvalidStateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLaunchRestException.class)
                .withErrorCode(CampaignLaunchRestException.INVALID_CAMPAIGN_STATE)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignControllerWithoutMatchingEventTriggerException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.CONTROLLER_MISSING_EVENT_TRIGGER)
                .addParameter("controller_id", e.getControllerId())
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignFrontendControllerMisconfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.CONTROLLER_MISCONFIGURATION)
                .addParameter("controller_id", e.getControllerId())
                .addParameter("campaign_id", campaignId)
                .addParameter("details", e.getMisconfigurationDetails())
                .withCause(e)
                .build();
        } catch (CampaignFrontendControllerNotFoundPageMisconfigurationException e) {
            throw FrontendControllerNotFoundPageRestExceptionMapper.getInstance().map(e, campaignId);
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (OrphanExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.ORPHAN_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("missing_campaign_component_id", e.getCampaignComponentId())
                .addParameter("reference_owner_campaign_id", e.getCampaignId())
                .addParameter("reference_owner_campaign_version", e.getCampaignVersion())
                .withCause(e)
                .build();
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
                .withCause(e)
                .build();
        } catch (CampaignScheduleException e) {
            throw handleScheduleExceptions(e, timeZone);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_STATE_CHANGE_EXCEPTION)
                .build();
        } catch (CreativeArchiveJavascriptException | CreativeArchiveBuilderException
            | CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServicePendingChangeException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException | StepDataBuildException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | AuthorizationException
            | ComponentTypeNotFoundException | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentException | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse launchBurst(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        Optional<CampaignLaunchBurstRequest> request,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, CampaignLaunchRestException,
        CampaignControllerValidationRestException, BuildCampaignRestException, CampaignComponentValidationRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<Integer> expectedVersion = Optional.empty();
            Optional<String> message = Optional.empty();
            Optional<Instant> startDate = Optional.empty();
            Optional<Instant> stopDate = Optional.empty();
            boolean startNow = false;
            if (request.isPresent()) {
                expectedVersion = campaignProvider.parseVersion(expectedCurrentVersion);
                if (request.get().getMessage().isPresent()) {
                    message = request.get().getMessage().getValue();
                }
                if (request.get().getStopDate().isPresent() && request.get().getStopDate().getValue().isPresent()) {
                    stopDate = request.get().getStopDate().getValue().map(date -> date.toInstant());
                }
                if (request.get().getStartDate().isOmitted()) {
                    startNow = true;
                } else {
                    if (request.get().getStartDate().getValue().isPresent()) {
                        startDate = request.get().getStartDate().getValue().map(date -> date.toInstant());
                    } else {
                        startNow = true;
                    }
                }
            } else {
                startNow = true;
            }
            Campaign launchedCampaign = campaignService.launchBurst(authorization, Id.valueOf(campaignId),
                expectedVersion, message, startDate, stopDate, startNow);
            return campaignRestMapper.toCampaignResponse(launchedCampaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignInvalidStateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignLaunchRestException.class)
                .withErrorCode(CampaignLaunchRestException.INVALID_CAMPAIGN_STATE)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignControllerWithoutMatchingEventTriggerException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.CONTROLLER_MISSING_EVENT_TRIGGER)
                .addParameter("controller_id", e.getControllerId())
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignFrontendControllerMisconfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.CONTROLLER_MISCONFIGURATION)
                .addParameter("controller_id", e.getControllerId())
                .addParameter("campaign_id", campaignId)
                .addParameter("details", e.getMisconfigurationDetails())
                .withCause(e)
                .build();
        } catch (CampaignFrontendControllerNotFoundPageMisconfigurationException e) {
            throw FrontendControllerNotFoundPageRestExceptionMapper.getInstance().map(e, campaignId);
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (OrphanExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.ORPHAN_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("missing_campaign_component_id", e.getCampaignComponentId())
                .addParameter("reference_owner_campaign_id", e.getCampaignId())
                .addParameter("reference_owner_campaign_version", e.getCampaignVersion())
                .withCause(e)
                .build();
        } catch (CampaignScheduleException e) {
            throw handleScheduleExceptions(e, timeZone);
            // TODO move the handling of these exceptions in the proper builder or service as part of ENG-19069
        } catch (CampaignServicePendingChangeException | CampaignServiceDescriptionLengthException
            | CampaignLabelIllegalCharacterInNameException | CampaignLabelNameLengthException
            | InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse publish(String accessToken, String campaignId, String expectedCurrentVersion,
        Optional<CampaignPublishRequest> request, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        CampaignJourneyEntryValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException, SettingValidationRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = campaignService.publishDraft(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            return doPublish(authorization, request, campaignId, campaignBuilder, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }
    }

    private CampaignResponse doPublish(Authorization authorization, Optional<CampaignPublishRequest> request,
        String campaignId, CampaignBuilder campaignBuilder, ZoneId timeZone)
        throws CampaignControllerValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        StaleCampaignVersionException, CampaignComponentValidationRestException,
        CampaignJourneyEntryValidationRestException, CampaignComponentRestException,
        CampaignFrontendControllerValidationRestException, SettingValidationRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {
        try {
            if (request.isPresent()) {
                if (!Strings.isNullOrEmpty(request.get().getMessage())) {
                    campaignBuilder.withMessage(request.get().getMessage());
                }
                if (BooleanUtils.isTrue(request.get().getLaunch())) {
                    campaignBuilder.withStartDateNow();
                }
            }
            Campaign campaign = campaignBuilder.save();
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignControllerWithoutMatchingEventTriggerException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerValidationRestException.class)
                .withErrorCode(CampaignControllerValidationRestException.CONTROLLER_MISSING_EVENT_TRIGGER)
                .addParameter("controller_id", e.getControllerId())
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignFrontendControllerNotFoundPageMisconfigurationException e) {
            throw FrontendControllerNotFoundPageRestExceptionMapper.getInstance().map(e, campaignId);
        } catch (CampaignFrontendControllerMisconfigurationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(CampaignFrontendControllerValidationRestException.CONTROLLER_MISCONFIGURATION)
                .addParameter("controller_id", e.getControllerId())
                .addParameter("campaign_id", campaignId)
                .addParameter("details", e.getMisconfigurationDetails())
                .withCause(e)
                .build();
        } catch (CampaignJourneyEntryNonMatchingTriggersException e) {
            throw RestExceptionBuilder.newBuilder(CampaignJourneyEntryValidationRestException.class)
                .withErrorCode(CampaignJourneyEntryValidationRestException.INVALID_TRIGGER_PHASE)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (OrphanExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.ORPHAN_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("missing_campaign_component_id", e.getCampaignComponentId())
                .addParameter("reference_owner_campaign_id", e.getCampaignId())
                .addParameter("reference_owner_campaign_version", e.getCampaignVersion())
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
                .withCause(e)
                .build();
        } catch (BuildEventStreamException e) {
            throw BuildEventStreamExceptionMapper.getInstance().map(e);
        } catch (BuildAudienceException e) {
            throw BuildAudienceExceptionMapper.getInstance().map(e);
        } catch (BuildClientKeyException e) {
            throw OAuthClientKeyBuildRestExceptionMapper.getInstance().map(e)
                .orElseThrow(() -> BuildClientKeyExceptionMapper.getInstance().map(e));
        } catch (BuildRewardSupplierException e) {
            throw BuildRewardSupplierExceptionMapper.getInstance().map(e);
        } catch (BuildPrehandlerException e) {
            throw BuildPrehandlerExceptionMapper.getInstance().map(e);
        } catch (BuildWebhookException e) {
            throw BuildWebhookExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CreativeArchiveJavascriptException | CampaignLabelMissingNameException
            | CampaignLabelDuplicateNameException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | CampaignServicePendingChangeException | TransitionRuleAlreadyExistsForActionType
            | CampaignFlowStepException | StepDataBuildException | CampaignScheduleException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | AuthorizationException | ComponentTypeNotFoundException | ReferencedExternalElementException
            | IncompatibleRewardRuleException | CampaignComponentException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse discard(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
            Campaign discardedCampaign;

            if (expectedCampaignVersion.isPresent()) {
                discardedCampaign = campaignService.discardDraft(authorization, Id.valueOf(campaignId),
                    expectedCampaignVersion.get());
            } else {
                discardedCampaign = campaignService.discardDraft(authorization, Id.valueOf(campaignId));
            }

            return campaignRestMapper.toCampaignResponse(discardedCampaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignResponse scheduleCampaign(String accessToken, String campaignId, String expectedCurrentVersion,
        CampaignScheduleRequest scheduleRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignUpdateRestException,
        BuildCampaignRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            DraftPreservingCampaignBuilder campaignBuilder =
                campaignService.editCampaignDraftAndPublished(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedCampaignVersion);

            scheduleRequest.getStartDate().ifPresent(startDate -> {
                if (startDate.isPresent()) {
                    campaignBuilder.withStartDate(startDate.get().toInstant());
                } else {
                    campaignBuilder.removeStartDate();
                }
            });
            scheduleRequest.getStopDate().ifPresent(stopDate -> {
                if (stopDate.isPresent()) {
                    campaignBuilder.withStopDate(stopDate.get().toInstant());
                } else {
                    campaignBuilder.removeStopDate();
                }
            });
            scheduleRequest.getPauseDate().ifPresent(pauseDate -> {
                if (pauseDate.isPresent()) {
                    campaignBuilder.withPauseDate(pauseDate.get().toInstant());
                } else {
                    campaignBuilder.removePauseDate();
                }
            });
            scheduleRequest.getEndDate().ifPresent(endDate -> {
                if (endDate.isPresent()) {
                    campaignBuilder.withEndDate(endDate.get().toInstant());
                } else {
                    campaignBuilder.removeEndDate();
                }
            });

            campaignBuilder.save();
            Campaign campaign = getMostRecentCampaignVersionPriorityPublished(authorization, Id.valueOf(campaignId));
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_STATE_CHANGE_EXCEPTION)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignScheduleException e) {
            throw handleScheduleExceptions(e, timeZone);
        } catch (CampaignServiceDescriptionLengthException | CampaignLabelIllegalCharacterInNameException
            | CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse pauseCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion, Optional<CampaignScheduleStateRequest> scheduleRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            DraftPreservingCampaignBuilder campaignBuilder =
                campaignService.editCampaignDraftAndPublished(authorization,
                    Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedCampaignVersion);
            if (scheduleRequest.isPresent()) {
                if (scheduleRequest.get().getScheduledDate().isOmitted()) {
                    campaignBuilder.withPauseDateNow();
                } else {
                    Optional<ZonedDateTime> pauseDate = scheduleRequest.get().getScheduledDate().getValue();
                    if (pauseDate.isPresent()) {
                        campaignBuilder.withPauseDate(pauseDate.get().toInstant());
                    } else {
                        campaignBuilder.removePauseDate();
                    }
                }
            } else {
                campaignBuilder.withPauseDateNow();
            }

            campaignBuilder.save();
            Campaign campaign = getMostRecentCampaignVersionPriorityPublished(authorization, Id.valueOf(campaignId));
            return campaignRestMapper.toCampaignResponse(
                campaign,
                timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_STATE_CHANGE_EXCEPTION)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignScheduleException e) {
            throw handleScheduleExceptions(e, timeZone);
        } catch (CampaignServiceDescriptionLengthException | CampaignLabelIllegalCharacterInNameException
            | CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse endCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        Optional<CampaignScheduleStateRequest> scheduleRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            DraftPreservingCampaignBuilder campaignBuilder =
                campaignService.editCampaignDraftAndPublished(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedCampaignVersion);
            if (scheduleRequest.isPresent()) {
                scheduleRequest.get().getScheduledDate().ifPresent(endDate -> {
                    if (endDate.isPresent()) {
                        campaignBuilder.withEndDate(endDate.get().toInstant());
                    } else {
                        campaignBuilder.removeEndDate();
                    }
                });
            } else {
                campaignBuilder.withEndDateNow();
            }
            campaignBuilder.save();
            Campaign campaign = getMostRecentCampaignVersionPriorityPublished(authorization, Id.valueOf(campaignId));
            return campaignRestMapper.toCampaignResponse(
                campaign,
                timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_STATE_CHANGE_EXCEPTION)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignScheduleException e) {
            throw handleScheduleExceptions(e, timeZone);
        } catch (CampaignServiceDescriptionLengthException | CampaignLabelIllegalCharacterInNameException
            | CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse stopCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        Optional<CampaignScheduleStateRequest> scheduleRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignScheduleValidationRestException, CampaignComponentRestException,
        GlobalCampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            DraftPreservingCampaignBuilder campaignBuilder =
                campaignService.editCampaignDraftAndPublished(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedCampaignVersion);
            if (scheduleRequest.isPresent()) {
                scheduleRequest.get().getScheduledDate().ifPresent(stopDate -> {
                    if (stopDate.isPresent()) {
                        campaignBuilder.withStopDate(stopDate.get().toInstant());
                    } else {
                        campaignBuilder.removeStopDate();
                    }
                });
            } else {
                campaignBuilder.withStopDateNow();
            }

            campaignBuilder.save();
            Campaign campaign = getMostRecentCampaignVersionPriorityPublished(authorization, Id.valueOf(campaignId));
            return campaignRestMapper.toCampaignResponse(
                campaign,
                timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_STATE_CHANGE_EXCEPTION)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignScheduleException e) {
            throw handleScheduleExceptions(e, timeZone);
        } catch (CampaignServiceDescriptionLengthException | CampaignLabelIllegalCharacterInNameException
            | CampaignLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse archiveCampaign(String accessToken, String campaignId, String expectedCurrentVersion,
        ZoneId timeZone) throws CampaignRestException, UserAuthorizationRestException,
        BuildCampaignRestException, CampaignComponentValidationRestException, CampaignUpdateRestException,
        GlobalCampaignRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
            Campaign campaign;

            if (expectedCampaignVersion.isPresent()) {
                campaign = campaignService.archive(authorization, Id.valueOf(campaignId),
                    expectedCampaignVersion.get());
            } else {
                campaign = campaignService.archive(authorization, Id.valueOf(campaignId));
            }

            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (ReferencedExternalElementException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXTERNAL_ELEMENT_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("element_type", e.getElementType().name())
                .addParameter("element_id", e.getElementId())
                .withCause(e)
                .build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (OrphanExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.ORPHAN_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("missing_campaign_component_id", e.getCampaignComponentId())
                .addParameter("reference_owner_campaign_id", e.getCampaignId())
                .addParameter("reference_owner_campaign_version", e.getCampaignVersion())
                .withCause(e)
                .build();
        } catch (CampaignGlobalArchiveException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_ARCHIVE)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse deleteCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        ZoneId timeZone) throws CampaignRestException, UserAuthorizationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CampaignComponentValidationRestException,
        CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
            Campaign campaign;
            if (expectedCampaignVersion.isPresent()) {
                campaign = campaignService.delete(authorization, Id.valueOf(campaignId), expectedCampaignVersion.get());
            } else {
                campaign = campaignService.delete(authorization, Id.valueOf(campaignId));
            }

            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignInvalidStateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_STATE)
                .addParameter("campaign_id", campaignId)
                .addParameter("expected_state", CampaignState.STOPPED.name())
                .addParameter("current_state", e.getCampaignState().name())
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (ReferencedExternalElementException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXTERNAL_ELEMENT_IS_REFERENCED)
                .addParameter("element_id", e.getElementId())
                .addParameter("element_type", e.getElementType())
                .addParameter("references", e.getReferences())
                .withCause(e).build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (OrphanExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.ORPHAN_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("missing_campaign_component_id", e.getCampaignComponentId())
                .addParameter("reference_owner_campaign_id", e.getCampaignId())
                .addParameter("reference_owner_campaign_version", e.getCampaignVersion())
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignResponse unArchiveCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        ZoneId timeZone) throws CampaignRestException, UserAuthorizationRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
            Campaign campaign;
            if (expectedCampaignVersion.isPresent()) {
                campaign = campaignService.unarchive(authorization, Id.valueOf(campaignId),
                    expectedCampaignVersion.get());
            } else {
                campaign = campaignService.unarchive(authorization, Id.valueOf(campaignId));
            }

            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        }
    }

    @Override
    public CampaignResponse unDeleteCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        ZoneId timeZone) throws CampaignRestException, UserAuthorizationRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
            Campaign campaign;
            if (expectedCampaignVersion.isPresent()) {
                campaign = campaignService.undelete(authorization, Id.valueOf(campaignId),
                    expectedCampaignVersion.get());
            } else {
                campaign = campaignService.undelete(authorization, Id.valueOf(campaignId));
            }

            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        }
    }

    @Override
    public CampaignResponse liveCampaign(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CampaignUpdateRestException, CampaignComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            DraftPreservingCampaignBuilder campaignBuilder =
                campaignService.editCampaignDraftAndPublished(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedCampaignVersion);

            campaignBuilder.withStartDateNow().save();
            Campaign campaign = getMostRecentCampaignVersionPriorityPublished(authorization, Id.valueOf(campaignId));
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignServiceDescriptionLengthException
            | CampaignLabelIllegalCharacterInNameException | CampaignLabelNameLengthException
            | CampaignScheduleException | CampaignGlobalStateChangeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private CampaignResponse save(CampaignCreateRequest createRequest,
        CampaignBuilder campaignBuilder, ZoneId timeZone)
        throws CampaignValidationRestException, BuildCampaignRestException, CampaignComponentRestException {
        try {
            if (StringUtils.isNotBlank(createRequest.getName())) {
                campaignBuilder.withName(createRequest.getName());
            }
            if (createRequest.getDescription().isPresent()
                && StringUtils.isNotBlank(createRequest.getDescription().get())) {
                campaignBuilder.withDescription(createRequest.getDescription().get());
            }
            if (createRequest.getProgramType().isPresent()) {
                campaignBuilder.withProgramType(createRequest.getProgramType().get());
            }
            if (!createRequest.getTags().isEmpty()) {
                campaignBuilder.withTags(createRequest.getTags());
            }
            if (createRequest.getVariantSelector().isPresent()) {
                campaignBuilder.withVariantSelector(createRequest.getVariantSelector().getValue());
            }
            if (createRequest.getVariants().isPresent()) {
                campaignBuilder.withVariants(createRequest.getVariants().getValue());
            }
            createRequest.getCampaignType().ifPresent(campaignType -> campaignBuilder
                .withCampaignType(Campaign.CampaignType.valueOf(campaignType.name())));
            Campaign campaign = campaignBuilder.save();
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (CampaignServiceDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", createRequest.getDescription().get())
                .withCause(e)
                .build();
        } catch (CampaignServiceNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getMinLength())
                .addParameter("max_length", e.getMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignServiceIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (CampaignServiceDuplicateNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_ALREADY_USED)
                .addParameter("name", createRequest.getName())
                .withCause(e)
                .build();
        } catch (CampaignServiceNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_IS_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignProgramTypeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.INVALID_PROGRAM_TYPE)
                .addParameter("program_type", createRequest.getProgramType().get())
                .withCause(e)
                .build();
        } catch (CampaignProgramTypeEmptyException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.PROGRAM_TYPE_EMPTY)
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (CampaignServiceTagInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .withCause(e)
                .build();
        } catch (CampaignServiceTagsLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.TAGS_TOO_LONG)
                .addParameter("tags_length", Integer.valueOf(e.getTagsLength()))
                .addParameter("max_tags_length", Integer.valueOf(e.getMaxTagsLength()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignGlobalRenameException | CreativeArchiveJavascriptException
            | CreativeArchiveBuilderException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignLabelBuildException | StaleCampaignVersionException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException
            | ConcurrentCampaignUpdateException | CampaignScheduleException | CampaignGlobalDeleteException
            | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | CampaignComponentFacetsNotFoundException
            | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private CampaignResponse update(CampaignUpdateRequest updateRequest,
        CampaignBuilder campaignBuilder, ZoneId timeZone)
        throws CampaignValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        GlobalCampaignRestException, CampaignComponentRestException, SettingValidationRestException,
        CampaignComponentValidationRestException {
        try {
            if (updateRequest.getName().isPresent() && StringUtils.isNotBlank(updateRequest.getName().get())) {
                campaignBuilder.withName(updateRequest.getName().get());
            }
            if (updateRequest.getDescription().isPresent()) {
                if (StringUtils.isBlank(updateRequest.getDescription().get())) {
                    campaignBuilder.removeDescription();
                } else {
                    campaignBuilder.withDescription(updateRequest.getDescription().get());
                }
            }
            if (updateRequest.getProgramType().isPresent()) {
                campaignBuilder.withProgramType(updateRequest.getProgramType().get());
            }
            if (updateRequest.getTags() != null) {
                if (updateRequest.getTags().isEmpty()) {
                    campaignBuilder.removeTags();
                } else {
                    campaignBuilder.withTags(updateRequest.getTags());
                }
            }
            if (updateRequest.getVariantSelector().isPresent()) {
                campaignBuilder.withVariantSelector(updateRequest.getVariantSelector().getValue());
            }
            if (updateRequest.getVariants().isPresent()) {
                campaignBuilder.withVariants(updateRequest.getVariants().getValue());
            }
            updateRequest.getCampaignType().ifPresent(campaignType -> campaignBuilder
                .withCampaignType(Campaign.CampaignType.valueOf(campaignType.name())));
            Campaign campaign = campaignBuilder.save();
            return campaignRestMapper.toCampaignResponse(campaign, timeZone);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignServiceDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", updateRequest.getDescription().get())
                .withCause(e)
                .build();
        } catch (CampaignServiceNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getMinLength())
                .addParameter("max_length", e.getMaxLength())
                .withCause(e)
                .build();
        } catch (CampaignServiceIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", updateRequest.getName())
                .withCause(e)
                .build();
        } catch (CampaignGlobalRenameException e) {
            throw RestExceptionBuilder.newBuilder(GlobalCampaignRestException.class)
                .withErrorCode(GlobalCampaignRestException.GLOBAL_RENAME)
                .withCause(e)
                .build();
        } catch (CampaignServiceDuplicateNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_ALREADY_USED)
                .addParameter("name", updateRequest.getName())
                .withCause(e)
                .build();
        } catch (CampaignServiceNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.NAME_IS_MISSING)
                .withCause(e)
                .build();
        } catch (CampaignProgramTypeInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.INVALID_PROGRAM_TYPE)
                .addParameter("program_type", updateRequest.getProgramType().get())
                .withCause(e)
                .build();
        } catch (CampaignProgramTypeEmptyException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.PROGRAM_TYPE_EMPTY)
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (CampaignServiceTagInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getTag())
                .withCause(e)
                .build();
        } catch (CampaignServiceTagsLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignValidationRestException.class)
                .withErrorCode(CampaignValidationRestException.TAGS_TOO_LONG)
                .addParameter("tags_length", Integer.valueOf(e.getTagsLength()))
                .addParameter("max_tags_length", Integer.valueOf(e.getMaxTagsLength()))
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CreativeArchiveJavascriptException | CreativeArchiveBuilderException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException | StepDataBuildException
            | CampaignScheduleException | CampaignGlobalDeleteException | CampaignGlobalArchiveException
            | CampaignGlobalStateChangeException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException | CampaignComponentException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Campaign getMostRecentCampaignVersionPriorityPublished(Authorization authorization,
        Id<Campaign> campaignId) throws CampaignNotFoundException {
        Campaign campaign;
        try {
            campaign = campaignService.getPublishedAnyStateCampaign(authorization, campaignId);
        } catch (CampaignNotFoundException e) {
            campaign = campaignService.getPublishedOrDraftAnyStateCampaign(authorization, campaignId);
        }
        return campaign;
    }

    private CampaignScheduleValidationRestException handleScheduleExceptions(CampaignScheduleException e,
        ZoneId timeZone) {
        if (e instanceof CampaignRemoveStartDateException) {
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.REMOVE_START_DATE)
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignBackdatedStartDateException) {
            CampaignBackdatedStartDateException exception = (CampaignBackdatedStartDateException) e;
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.BACKDATED_START_DATE)
                .addParameter("start_date", exception.getStartDate().atZone(timeZone))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignBackdatedStopDateException) {
            CampaignBackdatedStopDateException exception = (CampaignBackdatedStopDateException) e;
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.BACKDATED_STOP_DATE)
                .addParameter("stop_date", exception.getStopDate().atZone(timeZone))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignStartDateAfterStopDateException) {
            CampaignStartDateAfterStopDateException exception =
                (CampaignStartDateAfterStopDateException) e;
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.START_DATE_AFTER_STOP_DATE)
                .addParameter("start_date", exception.getStartDate().atZone(timeZone))
                .addParameter("stop_date", exception.getStopDate().atZone(timeZone))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignDateBeforeStartDateException) {
            CampaignDateBeforeStartDateException exception =
                (CampaignDateBeforeStartDateException) e;
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.DATE_BEFORE_START_DATE)
                .addParameter("start_date", exception.getStartDate().atZone(timeZone))
                .addParameter("date", exception.getDate().atZone(timeZone))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignDateAfterStopDateException) {
            CampaignDateAfterStopDateException exception =
                (CampaignDateAfterStopDateException) e;
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.DATE_AFTER_STOP_DATE)
                .addParameter("stop_date", exception.getStopDate().atZone(timeZone))
                .addParameter("date", exception.getDate().atZone(timeZone))
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignHasScheduledSiblingException) {
            CampaignHasScheduledSiblingException exception = (CampaignHasScheduledSiblingException) e;
            return RestExceptionBuilder.newBuilder(CampaignScheduleValidationRestException.class)
                .withErrorCode(CampaignScheduleValidationRestException.SIBLING_CAMPAIGN_SCHEDULED)
                .addParameter("sibling_campaign_id", exception.getSiblingId())
                .addParameter("sibling_campaign_name", exception.getSiblingName())
                .addParameter("sibling_campaign_start_date", exception.getSiblingStartDate().atZone(timeZone))
                .withCause(e)
                .build();
        }
        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(e)
            .build();
    }

    private CampaignComponentRestException mapComponentBuildVariableExceptionToRestException(
        ComponentBuildSettingException e) {

        Map<String, RestExceptionResponse> exceptionResponses = Maps.newHashMap();
        e.getSuppressedExceptions().forEach((variableName, buildException) -> {
            RestException restException =
                settingRestMapper.mapToBuildSettingRestException(buildException, e.getEntityId());
            exceptionResponses.put(variableName, new RestExceptionResponseBuilder(restException).build());
        });
        return RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
            .withErrorCode(CampaignComponentRestException.SETTINGS_BUILD_FAILED)
            .addParameter("errors", exceptionResponses)
            .withCause(e)
            .build();
    }

    private void extractActionsCreativesToCampaignArchive(Authorization authorization,
        Campaign campaign,
        String campaignName,
        ZipOutputStream zipOutput)
        throws IOException, CreativeArchiveNotFoundException, CreativeArchiveVersionException {

        CampaignControllerActionCreative[] creativeActions = campaign.getFrontendControllers()
            .stream()
            .flatMap(controller -> controller.getActions().stream())
            .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
            .map(value -> (CampaignControllerActionCreative) value)
            .toArray(CampaignControllerActionCreative[]::new);

        for (CampaignControllerActionCreative actionCreative : creativeActions) {
            Optional<CreativeArchiveId> creativeArchiveId = actionCreative.getCreativeArchiveId();
            if (creativeArchiveId.isPresent()) {
                zipOutput
                    .putNextEntry(new ZipEntry(createCreativeZipEntryName(campaignName, creativeArchiveId.get())));
                ByteStreams.copy(creativeArchiveService.getData(authorization, creativeArchiveId.get())
                    .openBufferedStream(), zipOutput);
            }
        }
    }

    private ClientAuthorization getClientBackendAuthorization(Id<ClientHandle> clientId)
        throws UserAuthorizationRestException {
        try {
            return backendAuthorizationProvider.getAuthorizationForBackend(clientId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    private String createCreativeZipEntryName(String campaignName, CreativeArchiveId creativeArchiveId) {
        return campaignName + "/creatives/" + creativeArchiveId.getId().getValue() + ".zip";
    }

    private CampaignVersionDescription getCampaignVersionDescription(Authorization userAuthorization,
        Id<Campaign> campaignId, CampaignVersion campaignVersion) throws CampaignRestException {
        try {
            return campaignService.getCampaignVersionDescription(userAuthorization, campaignId, campaignVersion);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", campaignVersion.getValue())
                .withCause(e)
                .build();
        }
    }

    private CampaignMakeLatestRequest getDefaultCampaignMakeLatestRequest(String version,
        Optional<CampaignMakeLatestRequest> campaignMakeLatestDraftRequest) {

        if (campaignMakeLatestDraftRequest.isEmpty()) {
            return CampaignMakeLatestRequest.builder()
                .withVersion(version)
                .build();
        }
        if (campaignMakeLatestDraftRequest.get().getVersion().isOmitted()) {
            return CampaignMakeLatestRequest.builder(campaignMakeLatestDraftRequest.get())
                .withVersion(version)
                .build();
        }
        return campaignMakeLatestDraftRequest.get();
    }

    private Campaign applySaveForMakeLatestVersion(CampaignBuilder campaignBuilder)
        throws CampaignUpdateRestException, CampaignComponentRestException, BuildCampaignRestException,
        CampaignComponentValidationRestException {

        try {
            return campaignBuilder.save();

        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            throw mapComponentBuildVariableExceptionToRestException(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException | CampaignGlobalDeleteException | CampaignGlobalArchiveException
            | CampaignGlobalStateChangeException | CampaignStartDateAfterStopDateException
            | CampaignDateBeforeStartDateException | CampaignDateAfterStopDateException
            | CreativeArchiveJavascriptException | CampaignLabelMissingNameException
            | CampaignLabelDuplicateNameException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | TransitionRuleAlreadyExistsForActionType
            | CampaignComponentException | CampaignFlowStepException | StepDataBuildException
            | CampaignHasScheduledSiblingException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private CampaignBuilder provideCampaignBuilderForMakeLatestVersion(ClientAuthorization authorization,
        Id<Campaign> campaignId, CampaignMakeLatestRequest campaignMakeLatestRequest)
        throws CampaignRestException {

        String version = campaignMakeLatestRequest.getVersion().getValue();
        CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(campaignId, version);

        try {
            CampaignBuilder campaignBuilder = campaignService.makeLatestByIdAndVersion(authorization, campaignId,
                campaignVersion);
            campaignMakeLatestRequest.getMessage()
                .ifPresent(optional -> optional.ifPresent(value -> campaignBuilder.withMessage(value)));

            return campaignBuilder;
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId.getValue())
                .withCause(e)
                .build();
        }
    }

    private CampaignBuilder getCampaignBuilder(ClientAuthorization authorization, String campaignId,
        String expectedCurrentVersion) throws CampaignRestException {
        try {
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
            return campaignBuilder;
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID).addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }
    }

}
