package com.extole.client.rest.impl.campaign.controller.action.creative;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeCreateRequest;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeEndpoints;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeMissingRestException;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeResponse;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeUpdateRequest;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeValidationRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.FrontendControllerNotFoundPageRestExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.Classification;
import com.extole.model.entity.campaign.CreativeArchive;
import com.extole.model.entity.campaign.CreativeArchiveApiVersion;
import com.extole.model.entity.campaign.CreativeArchiveVersionException;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeInvalidCreativeArchiveException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerMisconfigurationException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerNotFoundPageMisconfigurationException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.label.CampaignLabelBuildException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.CreativeArchiveBuilder;
import com.extole.model.service.creative.CreativeArchiveService;
import com.extole.model.service.creative.exception.CreativeArchiveApiVersionMismatchException;
import com.extole.model.service.creative.exception.CreativeArchiveAssetContentSizeTooBigException;
import com.extole.model.service.creative.exception.CreativeArchiveAssetInvalidCharacterEncodingException;
import com.extole.model.service.creative.exception.CreativeArchiveAssetPathInvalidException;
import com.extole.model.service.creative.exception.CreativeArchiveAssetPathTooLongException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveDefaultLocaleDisabledException;
import com.extole.model.service.creative.exception.CreativeArchiveIncompatibleApiVersionException;
import com.extole.model.service.creative.exception.CreativeArchiveInvalidApiVersionException;
import com.extole.model.service.creative.exception.CreativeArchiveInvalidLocaleException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeArchiveMissingAttributesException;
import com.extole.model.service.creative.exception.CreativeArchiveMissingLocalizedRendererException;
import com.extole.model.service.creative.exception.CreativeArchiveMissingRendererException;
import com.extole.model.service.creative.exception.CreativeArchiveNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveSizeTooBigException;
import com.extole.model.service.creative.exception.CreativeArchiveUnknownClassificationException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignControllerActionCreativeEndpointsImpl implements CampaignControllerActionCreativeEndpoints {
    private static final String EXTOLE_DEBUG_HEADER = "X-Extole-Debug";
    private static final String EXTOLE_LOG_HEADER = "X-Extole-Log";

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignService campaignService;
    private final CampaignControllerActionCreativeResponseMapper responseMapper;
    private final CampaignProvider campaignProvider;
    private final CreativeArchiveService creativeArchiveService;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;
    private final int fileThreshold;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;

    @Autowired
    public CampaignControllerActionCreativeEndpointsImpl(
        @Value("${model.creativeArchive.fileThreshold:262144}") int fileThreshold,
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignService campaignService,
        CampaignControllerActionCreativeResponseMapper responseMapper,
        CampaignProvider campaignProvider,
        CreativeArchiveService creativeArchiveService,
        ComponentReferenceRequestMapper componentReferenceRequestMapper,
        @Context HttpServletRequest servletRequest,
        @Context HttpServletResponse servletResponse) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.campaignService = campaignService;
        this.responseMapper = responseMapper;
        this.campaignProvider = campaignProvider;
        this.creativeArchiveService = creativeArchiveService;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
        this.fileThreshold = fileThreshold;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }

    @Override
    public CampaignControllerActionCreativeResponse create(String accessToken,
        String campaignId,
        String controllerId,
        String expectedCurrentVersion,
        CampaignControllerActionCreativeCreateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException, BuildCampaignRestException,
        CampaignFrontendControllerValidationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            FrontendController frontendController = campaignStepProvider.getFrontendController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            CampaignControllerActionCreativeBuilder actionBuilder = campaignBuilder
                .updateFrontendController(frontendController)
                .addAction(CampaignControllerActionType.CREATIVE);

            request.getQuality().ifPresent(
                quality -> actionBuilder.withQuality(
                    com.extole.model.entity.campaign.CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(actionBuilder::withEnabled);
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });

            request.getClassification()
                .ifPresent(value -> actionBuilder.withClassification(Classification.valueOf(value.name())));
            request.getThemeVersion().ifPresent(value -> actionBuilder.withThemeVersion(value));
            request.getCreativeApiVersion().ifPresent(
                value -> actionBuilder.withCreativeArchiveApiVersion(CreativeArchiveApiVersion.fromVersion(value)));

            CampaignControllerActionCreative createdAction = actionBuilder.save();
            return responseMapper.toResponse(createdAction, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "action")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
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
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionCreativeResponse update(String accessToken,
        String campaignId,
        String controllerId,
        String actionId,
        String expectedCurrentVersion,
        CampaignControllerActionCreativeUpdateRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignComponentValidationRestException, CampaignUpdateRestException, BuildCampaignRestException,
        CampaignFrontendControllerValidationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            FrontendController frontendController = campaignStepProvider.getFrontendController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            CampaignControllerActionCreative action =
                campaignStepProvider.getCreativeControllerAction(campaign, controllerId, actionId);

            CampaignControllerActionCreativeBuilder actionBuilder = campaignBuilder
                .updateFrontendController(frontendController)
                .updateAction(action);

            request.getQuality().ifPresent(
                quality -> actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(quality.name())));
            request.getEnabled().ifPresent(actionBuilder::withEnabled);
            request.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(actionBuilder, componentIds);
            });
            request.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(actionBuilder, componentReferences);
            });

            request.getClassification()
                .ifPresent(value -> actionBuilder.withClassification(Classification.valueOf(value.name())));
            request.getCreativeApiVersion().ifPresent(value -> actionBuilder.withCreativeArchiveApiVersion(
                CreativeArchiveApiVersion.fromVersion(value)));
            request.getThemeVersion().ifPresent(value -> actionBuilder.withThemeVersion(value));

            return responseMapper.toResponse(actionBuilder.save(), ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "action")
                .addParameter("referencing_entity", actionId)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
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
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public CampaignControllerActionCreativeResponse delete(String accessToken,
        String campaignId,
        String controllerId,
        String actionId,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CampaignUpdateRestException, BuildCampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            FrontendController frontendController = campaignStepProvider.getFrontendController(campaign, controllerId);
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            CampaignControllerActionCreative action =
                campaignStepProvider.getCreativeControllerAction(campaign, controllerId, actionId);

            campaignBuilder.updateFrontendController(frontendController)
                .removeAction(action)
                .save();

            return responseMapper.toResponse(action, ZoneOffset.UTC);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignControllerTriggerBuildException
            | InvalidComponentReferenceException | StepDataBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionCreativeResponse get(String accessToken,
        String campaignId,
        String version,
        String controllerId,
        String actionId) throws UserAuthorizationRestException, CampaignControllerRestException, CampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionCreative action =
            campaignStepProvider.getCreativeControllerAction(campaign, controllerId, actionId);
        return responseMapper.toResponse(action, ZoneOffset.UTC);
    }

    @Override
    public Response downloadCreative(String accessToken, String campaignId, String version, String controllerId,
        String actionId, Optional<String> filename)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        CreativeArchiveRestException, CampaignControllerActionCreativeMissingRestException {

        String downloadFileName = actionId;
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerActionCreative action =
            campaignStepProvider.getCreativeControllerAction(campaign, controllerId, actionId);

        CreativeArchive creativeArchive = getCreativeArchive(authorization, action);

        try (
            ZipInputStream zipInputStream = new ZipInputStream(
                creativeArchiveService.getData(authorization, creativeArchive.getCreativeArchiveId())
                    .openBufferedStream());
            FileBackedOutputStream fileOutput = new FileBackedOutputStream(fileThreshold, true);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutput)) {
            ZipEntry originalEntry = zipInputStream.getNextEntry();
            while (originalEntry != null) {
                if (!originalEntry.isDirectory()) {
                    Path filePath = Paths.get(downloadFileName, originalEntry.getName());
                    zipOutputStream.putNextEntry(new ZipEntry(filePath.toString()));
                    ByteStreams.copy(zipInputStream, zipOutputStream);
                }
                originalEntry = zipInputStream.getNextEntry();
            }

            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException {
                    fileOutput.asByteSource().copyTo(outputStream);
                }
            };

            String contentDisposition = filename.map(value -> createZipAttachmentContentDisposition(value))
                .orElseGet(() -> {
                    return createZipAttachmentContentDisposition(actionId);
                });
            return Response.ok(stream)
                .type("application/zip")
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .build();
        } catch (IOException | CreativeArchiveNotFoundException | CreativeArchiveVersionException e) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.DOWNLOAD_ERROR)
                .addParameter("file", downloadFileName)
                .withCause(e).build();
        }

    }

    @Override
    public CampaignControllerActionCreativeResponse uploadCreative(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String controllerId,
        String actionId,
        FileInputStreamRequest fileRequest)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CreativeArchiveRestException, CampaignControllerRestException, CampaignUpdateRestException,
        CampaignControllerActionCreativeValidationRestException {

        validatePresenceOfRequestBody(fileRequest);

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign;
        CampaignBuilder campaignBuilder;
        FrontendController frontendController;
        CampaignControllerActionCreative action;

        try {
            campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            frontendController = campaignStepProvider.getFrontendController(campaign, controllerId);
            campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            action = campaignStepProvider.getCreativeControllerAction(campaign, controllerId, actionId);

            FrontendControllerBuilder frontendControllerBuilder =
                campaignBuilder.updateFrontendController(frontendController);
            CampaignControllerActionCreativeBuilder actionCreativeBuilder =
                frontendControllerBuilder.updateAction(action);
            CreativeArchiveBuilder creativeArchiveBuilder = actionCreativeBuilder.getCreativeArchive()
                .orElseGet(() -> actionCreativeBuilder.addCreativeArchive());
            CreativeArchiveBuilder.CreativeArchiveBuildResult savedResult = creativeArchiveBuilder
                .withData(getByteSourceFromInputStream(fileRequest.getInputStream()))
                .save();
            Campaign updatedCampaign = savedResult.getCampaign();
            return responseMapper.toResponse(
                campaignStepProvider.getCreativeControllerAction(updatedCampaign, controllerId, actionId),
                savedResult.getCreativeArchive().getLogMessages(),
                ZoneOffset.UTC);
        } catch (CreativeArchiveIncompatibleApiVersionException e) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_INCOMPATIBLE_API_VERSION)
                .addParameter("archive_id", e.getArchiveId())
                .addParameter("api_version", e.getApiVersion())
                .withCause(e).build();
        } catch (CreativeArchiveBuilderException e) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionCreativeValidationRestException.class)
                .withErrorCode(CampaignControllerActionCreativeValidationRestException.INVALID_ARCHIVE)
                .addParameter("archive_id", e.getArchiveId().getValue())
                .withCause(e)
                .build();
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.UPLOAD_ERROR)
                .addParameter("file", fileRequest.getAttributes().getFileName())
                .withCause(e).build();
        } catch (CreativeArchiveJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.JAVASCRIPT_ERROR)
                .addParameter("file", fileRequest.getAttributes().getFileName())
                .addParameter("creativeArchiveId",
                    e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .addParameter("output", e.getOutput())
                .withCause(e)
                .build();
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
        } catch (CampaignControllerActionCreativeInvalidCreativeArchiveException e) {
            throwApplicableCreativeRestExceptionsIfPossible(e.getCause(),
                fileRequest.getAttributes().getFileName());
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | StepDataBuildException | CampaignScheduleException
            | CampaignComponentTypeValidationException
            | AuthorizationException | ComponentTypeNotFoundException | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void throwApplicableCreativeRestExceptionsIfPossible(Throwable exception, String filename)
        throws CreativeArchiveRestException {
        if (exception instanceof CreativeArchiveApiVersionMismatchException) {
            CreativeArchiveApiVersionMismatchException e = (CreativeArchiveApiVersionMismatchException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_MISMATCH_API_VERSION)
                .addParameter("api_version", e.getCurrentApiVersion().getVersion())
                .addParameter("expected_api_version", e.getExpectedApiVersion().getVersion())
                .withCause(exception).build();
        }
        if (exception instanceof CreativeArchiveAssetInvalidCharacterEncodingException) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_ASSET_INVALID_CHARACTER_ENCODING)
                .addParameter("file_path",
                    ((CreativeArchiveAssetInvalidCharacterEncodingException) exception).getAssetPath())
                .withCause(exception).build();
        }
        if (exception instanceof CreativeArchiveAssetContentSizeTooBigException) {
            CreativeArchiveAssetContentSizeTooBigException e =
                (CreativeArchiveAssetContentSizeTooBigException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_ASSET_FILE_SIZE_TOO_BIG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_size", Long.valueOf(e.getAssetContentSize()))
                .addParameter("max_allowed_file_size", Long.valueOf(e.getMaxAllowedAssetContentSize()))
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveAssetPathInvalidException) {
            CreativeArchiveAssetPathInvalidException e = (CreativeArchiveAssetPathInvalidException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_ASSET_FILE_PATH_INVALID)
                .addParameter("file_path", e.getAssetPath())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveAssetPathTooLongException) {
            CreativeArchiveAssetPathTooLongException e = (CreativeArchiveAssetPathTooLongException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_ASSET_FILE_PATH_TOO_LONG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_path_length", Integer.valueOf(e.getAssetPathLength()))
                .addParameter("max_allowed_file_path_length", Integer.valueOf(e.getMaxAllowedAssetPathLength()))
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveUnknownClassificationException) {
            CreativeArchiveUnknownClassificationException e = (CreativeArchiveUnknownClassificationException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_UNKNOWN_CLASSIFICATION)
                .addParameter("file_paths", e.getRendererFilePaths())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveInvalidApiVersionException) {
            CreativeArchiveInvalidApiVersionException e = (CreativeArchiveInvalidApiVersionException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_INVALID_API_VERSION)
                .addParameter("archive_id", e.getArchiveId())
                .addParameter("api_version", e.getApiVersion())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveIncompatibleApiVersionException) {
            CreativeArchiveIncompatibleApiVersionException e =
                (CreativeArchiveIncompatibleApiVersionException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_INCOMPATIBLE_API_VERSION)
                .addParameter("archive_id", e.getArchiveId())
                .addParameter("api_version", e.getApiVersion())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveInvalidLocaleException) {
            CreativeArchiveInvalidLocaleException e = (CreativeArchiveInvalidLocaleException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_INVALID_LOCALE)
                .addParameter("locale", e.getLocale())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveDefaultLocaleDisabledException) {
            CreativeArchiveDefaultLocaleDisabledException e = (CreativeArchiveDefaultLocaleDisabledException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_DEFAULT_LOCALE_DISABLED)
                .addParameter("default_locale", e.getDefaultLocale())
                .addParameter("enabled_locales", e.getEnabledLocales())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveMissingRendererException) {
            CreativeArchiveMissingRendererException e = (CreativeArchiveMissingRendererException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_IS_MISSING_RENDERER)
                .addParameter("file_paths", e.getRendererFilePaths())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveMissingLocalizedRendererException) {
            CreativeArchiveMissingLocalizedRendererException e =
                (CreativeArchiveMissingLocalizedRendererException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_IS_MISSING_RENDERER)
                .addParameter("file_paths", e.getRendererFilesByLocale())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveMissingAttributesException) {
            CreativeArchiveMissingAttributesException e = (CreativeArchiveMissingAttributesException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_IS_MISSING_ATTRIBUTES)
                .addParameter("attributes", e.getMissingAttributes())
                .withCause(e).build();
        }
        if (exception instanceof CreativeArchiveJavascriptException) {
            CreativeArchiveJavascriptException e = (CreativeArchiveJavascriptException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.JAVASCRIPT_ERROR)
                .addParameter("creativeArchiveId",
                    e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .addParameter("output", e.getOutput())
                .addParameter("file", filename)
                .withCause(e)
                .build();
        }
        if (exception instanceof CreativeArchiveSizeTooBigException) {
            CreativeArchiveSizeTooBigException e = (CreativeArchiveSizeTooBigException) exception;
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e).build();
        }
        if (exception instanceof IOException) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.UPLOAD_ERROR)
                .addParameter("file", filename)
                .withCause(exception).build();
        }
    }

    private ByteSource getByteSourceFromInputStream(InputStream inputStream) throws IOException {
        try (FileBackedOutputStream outputStream = new FileBackedOutputStream(fileThreshold, true);
            InputStream closeableInputStream = inputStream) {
            ByteStreams.copy(closeableInputStream, outputStream);
            return outputStream.asByteSource();
        }
    }

    private void validatePresenceOfRequestBody(FileInputStreamRequest request) {
        if (request == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
    }

    private CreativeArchive getCreativeArchive(Authorization authorization,
        CampaignControllerActionCreative action)
        throws CampaignControllerActionCreativeMissingRestException {

        if (action.getCreativeArchiveId().isEmpty()) {
            throw RestExceptionBuilder.newBuilder(CampaignControllerActionCreativeMissingRestException.class)
                .withErrorCode(CampaignControllerActionCreativeMissingRestException.MISSING_ARCHIVE)
                .addParameter("action_id", action.getId())
                .build();
        }

        try {
            return creativeArchiveService.getCreativeArchive(authorization,
                action.getCreativeArchiveId().get());
        } catch (CreativeArchiveNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private CampaignBuilder getCampaignBuilder(String campaignId, Authorization authorization,
        String expectedCurrentVersion)
        throws CampaignRestException {
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e)
                .build();
        }
        return campaignBuilder;
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private String createZipAttachmentContentDisposition(String filename) {
        return String.format("attachment; filename = %s.zip", filename);
    }

    private void addExtoleLogResponseHeaders(List<String> output) {
        if (servletRequest.getHeader(EXTOLE_DEBUG_HEADER) != null) {
            output.forEach(value -> servletResponse.addHeader(EXTOLE_LOG_HEADER, value));
        }
    }

}
