package com.extole.client.rest.impl.creative;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.change.model.SimpleClient;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.creative.CreativeVariableEndpoints;
import com.extole.client.rest.creative.CreativeVariableRequest;
import com.extole.client.rest.creative.CreativeVariableResponse;
import com.extole.client.rest.creative.CreativeVariableRestException;
import com.extole.client.rest.creative.CreativeVariableScope;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CreativeArchive;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.campaign.CreativeArchiveVersionException;
import com.extole.model.entity.campaign.CreativeVariable;
import com.extole.model.entity.campaign.CreativeVariable.Scope;
import com.extole.model.entity.campaign.CreativeVariableServiceInvalidNameException;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.FrontendControllerCreativeVariable;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionCreative;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionNotFoundException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeInvalidCreativeArchiveException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.label.CampaignLabelBuildException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.client.core.ClientCoreAssetsVersionNotFoundException;
import com.extole.model.service.client.core.ClientCoreAssetsVersionService;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.CreativeArchiveBuilder;
import com.extole.model.service.creative.CreativeArchiveBuilder.CreativeArchiveBuildResult;
import com.extole.model.service.creative.CreativeArchiveService;
import com.extole.model.service.creative.CreativeVariableBuilder;
import com.extole.model.service.creative.CreativeVariableService;
import com.extole.model.service.creative.CreativeVariableZoneState;
import com.extole.model.service.creative.OriginHostService;
import com.extole.model.service.creative.exception.CreativeArchiveAssetContentSizeTooBigException;
import com.extole.model.service.creative.exception.CreativeArchiveAssetPathTooLongException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveDefaultLocaleDisabledException;
import com.extole.model.service.creative.exception.CreativeArchiveInvalidLocaleException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeArchiveNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveSizeTooBigException;
import com.extole.model.service.creative.exception.CreativeVariableImageUnsupportedException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CreativeVariableEndpointsImpl
    implements CreativeVariableEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(CreativeVariableEndpointsImpl.class);

    private static final String EXTOLE_DEBUG_HEADER = "X-Extole-Debug";
    private static final String EXTOLE_LOG_HEADER = "X-Extole-Log";

    private final int fileThreshold;
    private final CampaignService campaignService;
    private final CreativeVariableService creativeVariableService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final ClientCoreAssetsVersionService clientCoreAssetsVersionService;
    private final OriginHostService originHostService;
    private final CreativeArchiveService creativeArchiveService;
    private final ActionCreativeProvider actionCreativeProvider;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;

    @Autowired
    public CreativeVariableEndpointsImpl(
        @Value("${model.creativeArchive.fileThreshold:262144}") int fileThreshold,
        CampaignService campaignService,
        CreativeVariableService creativeVariableService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        ClientCoreAssetsVersionService clientCoreAssetsVersionService,
        OriginHostService originHostService,
        CreativeArchiveService creativeArchiveService,
        ActionCreativeProvider actionCreativeProvider,
        @Context HttpServletRequest servletRequest,
        @Context HttpServletResponse servletResponse) {
        this.fileThreshold = fileThreshold;
        this.campaignService = campaignService;
        this.creativeVariableService = creativeVariableService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.clientCoreAssetsVersionService = clientCoreAssetsVersionService;
        this.originHostService = originHostService;
        this.creativeArchiveService = creativeArchiveService;
        this.actionCreativeProvider = actionCreativeProvider;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }

    @Override
    public List<CreativeVariableResponse> getCampaignVariables(String accessToken,
        String campaignId,
        String version,
        Optional<String> zoneState)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CreativeVariableZoneState creativeVariableZoneState = CreativeVariableZoneState.ANY;

        try {
            if (zoneState.isPresent()) {
                creativeVariableZoneState = parseZoneState(zoneState.get());
            }

            CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(Id.valueOf(campaignId), version);
            List<FrontendControllerCreativeVariable> frontendControllerCreativeVariables =
                creativeVariableService.getCampaignVariables(authorization, Id.valueOf(campaignId), campaignVersion,
                    creativeVariableZoneState);
            List<CreativeVariable> creativeVariables = frontendControllerCreativeVariables.stream()
                .map(value -> value.getCreativeVariable()).collect(Collectors.toUnmodifiableList());

            addVariableOutputToExtoleLogResponseHeaders(creativeVariables);

            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            initBuildVersionProvider(authorization, campaignId, campaignVersion.getValue());

            List<CreativeVariableResponse> result = new ArrayList<>(frontendControllerCreativeVariables.size());
            for (FrontendControllerCreativeVariable variable : frontendControllerCreativeVariables) {
                CreativeVariableResponse response = CreativeVariableRestMapper.toCreativeVariableResponse(
                    originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                    coreAssetsVersion.getValue(),
                    variable.getCreativeVariable(),
                    initBuildVersionProvider(authorization, campaignId, campaignVersion.getValue()),
                    variable.getActionCreativeId().getValue());
                result.add(response);
            }
            return result;
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CreativeVariableResponse getCampaignVariable(String accessToken,
        String campaignId,
        String version,
        String variableName)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(Id.valueOf(campaignId), version);

        try {
            FrontendControllerCreativeVariable frontendControllerCreativeVariable =
                creativeVariableService.getCampaignVariable(authorization, Id.valueOf(campaignId), campaignVersion,
                    variableName);
            CreativeVariable creativeVariable = frontendControllerCreativeVariable.getCreativeVariable();

            addExtoleLogResponseHeaders(creativeVariable.getOutput());

            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            return CreativeVariableRestMapper.toCreativeVariableResponse(
                originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                coreAssetsVersion.getValue(), creativeVariable,
                initBuildVersionProvider(authorization, campaignId, campaignVersion.getValue()),
                frontendControllerCreativeVariable.getActionCreativeId().getValue());
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        } catch (CreativeVariableServiceInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(e)
                .build();
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CreativeVariableResponse editCampaignVariable(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String variableName,
        CreativeVariableRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        CampaignUpdateRestException, BuildCampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign =
                campaignService.getPublishedOrDraftAnyStateCampaign(authorization, Id.valueOf(campaignId));

            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            return internallyEditCampaignVariable(authorization, campaign, request, variableName,
                expectedCurrentVersion, coreAssetsVersion);
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
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CreativeArchiveInvalidLocaleException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_LOCALE)
                .addParameter("locale", e.getLocale())
                .withCause(e)
                .build();
        } catch (CreativeArchiveDefaultLocaleDisabledException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.DEFAULT_LOCALE_IS_DISABLED)
                .addParameter("default_locale", e.getDefaultLocale())
                .addParameter("enabled_locales", e.getEnabledLocales())
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | CreativeVariableServiceInvalidNameException
            | CreativeArchiveBuilderException | CreativeArchiveRestException
            | CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CampaignControllerTriggerBuildException | CampaignLabelBuildException
            | CampaignServiceNameMissingException | ClientCoreAssetsVersionNotFoundException
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

    @Override
    public CreativeVariableResponse replaceCampaignLocaleImagePost(String accessToken,
        String campaignId,
        String variableName,
        String locale,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        return replaceCampaignLocaleImagePut(accessToken, campaignId, expectedCurrentVersion, variableName, locale,
            inputStream, contentDisposition);
    }

    @Override
    public CreativeVariableResponse replaceCampaignLocaleImagePut(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String variableName,
        String locale,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        if (inputStream == null || contentDisposition == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            return editCampaignImageVariable(campaignId, variableName, inputStream, contentDisposition, authorization,
                coreAssetsVersion, locale, expectedCurrentVersion);
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
        } catch (CreativeVariableImageUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_TYPE_IMAGE)
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_SIZE_TOO_BIG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_size", Long.valueOf(e.getAssetContentSize()))
                .addParameter("max_allowed_file_size", Long.valueOf(e.getMaxAllowedAssetContentSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetPathTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_PATH_TOO_LONG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_path_length", Integer.valueOf(e.getAssetPathLength()))
                .addParameter("max_allowed_file_path_length", Integer.valueOf(e.getMaxAllowedAssetPathLength()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof CampaignControllerActionCreativeInvalidCreativeArchiveException) {
                mapAndRethrowIfNeeded((CampaignControllerActionCreativeInvalidCreativeArchiveException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance()
                .map(e);
        } catch (CreativeArchiveVersionException | CreativeVariableServiceInvalidNameException
            | CreativeArchiveJavascriptException | IOException | CreativeArchiveBuilderException
            | CreativeArchiveRestException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType
            | StepDataBuildException | CampaignScheduleException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CreativeVariableResponse replaceCampaignImagePost(String accessToken,
        String campaignId,
        String variableName,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        return replaceCampaignImagePut(accessToken, campaignId, expectedCurrentVersion, variableName, inputStream,
            contentDisposition);
    }

    @Override
    public CreativeVariableResponse replaceCampaignImagePut(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String variableName,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        if (inputStream == null || contentDisposition == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CreativeVariableResponse creativeVariableResponse;

        try {
            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            creativeVariableResponse =
                editCampaignImageVariable(campaignId, variableName, inputStream, contentDisposition, authorization,
                    coreAssetsVersion, null, expectedCurrentVersion);
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
        } catch (CreativeVariableImageUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_TYPE_IMAGE)
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_SIZE_TOO_BIG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_size", Long.valueOf(e.getAssetContentSize()))
                .addParameter("max_allowed_file_size", Long.valueOf(e.getMaxAllowedAssetContentSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetPathTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_PATH_TOO_LONG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_path_length", Integer.valueOf(e.getAssetPathLength()))
                .addParameter("max_allowed_file_path_length", Integer.valueOf(e.getMaxAllowedAssetPathLength()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof CampaignControllerActionCreativeInvalidCreativeArchiveException) {
                mapAndRethrowIfNeeded((CampaignControllerActionCreativeInvalidCreativeArchiveException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance()
                .map(e);
        } catch (CreativeArchiveVersionException | CreativeVariableServiceInvalidNameException
            | CreativeArchiveJavascriptException | IOException | CreativeArchiveBuilderException
            | CreativeArchiveRestException
            | CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CreativeVariableUnsupportedException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType
            | StepDataBuildException | CampaignScheduleException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

        if (creativeVariableResponse == null) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(new CreativeVariableServiceInvalidNameException("Campaign variable does not exist"))
                .build();
        }
        return creativeVariableResponse;
    }

    @Override
    public List<CreativeVariableResponse> getCreativeVariables(String accessToken,
        String campaignId,
        String version,
        String frontendControllerActionId)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CreativeArchive creativeArchive =
            getCreativeArchive(authorization, campaign, Id.valueOf(frontendControllerActionId));

        try {
            List<CreativeVariable> creativeVariables =
                creativeVariableService.getCreativeVariables(campaign, creativeArchive.getCreativeArchiveId());

            addVariableOutputToExtoleLogResponseHeaders(creativeVariables);

            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            CampaignControllerActionCreative actionCreative =
                actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                    creativeArchive.getCreativeArchiveId().getId());

            List<CreativeVariableResponse> result = new ArrayList<>(creativeVariables.size());
            for (CreativeVariable variable : creativeVariables) {
                CreativeVariableResponse response = CreativeVariableRestMapper.toCreativeVariableResponse(
                    originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                    coreAssetsVersion.getValue(), variable,
                    initBuildVersionProvider(authorization, campaignId, campaign.getVersion()),
                    actionCreative.getId().getValue());
                result.add(response);
            }
            return result;
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CreativeVariableResponse getCreativeVariable(String accessToken,
        String campaignId,
        String version,
        String frontendControllerActionId,
        String variableName)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CreativeArchive creativeArchive =
            getCreativeArchive(authorization, campaign, Id.valueOf(frontendControllerActionId));

        try {
            CreativeVariable creativeVariable =
                creativeVariableService.getCreativeVariable(campaign, creativeArchive.getCreativeArchiveId(),
                    variableName);

            addExtoleLogResponseHeaders(creativeVariable.getOutput());

            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            CampaignControllerActionCreative actionCreative =
                actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                    creativeArchive.getCreativeArchiveId().getId());
            return CreativeVariableRestMapper.toCreativeVariableResponse(
                originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                coreAssetsVersion.getValue(), creativeVariable,
                initBuildVersionProvider(authorization, campaignId, campaign.getVersion()),
                actionCreative.getId().getValue());
        } catch (CreativeVariableServiceInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(e)
                .build();
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private CreativeArchiveId findCreativeArchiveId(Campaign campaign,
        Id<CreativeArchive> creativeArchiveIdCandidate) throws CreativeVariableRestException {

        Optional<CreativeArchiveId> creativeArchiveId =
            campaign.getFrontendControllers().stream()
                .flatMap(controller -> controller.getActions().stream())
                .filter(action -> action.getId().equals(creativeArchiveIdCandidate)
                    && action.getType().equals(CampaignControllerActionType.CREATIVE)
                    && ((CampaignControllerActionCreative) action).getCreativeArchiveId().isPresent())
                .map(action -> ((CampaignControllerActionCreative) action).getCreativeArchiveId().get())
                .findFirst();

        return creativeArchiveId.orElseThrow(() -> RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
            .withErrorCode(CreativeVariableRestException.INVALID_CREATIVE_ID)
            .addParameter("creative_id", creativeArchiveIdCandidate)
            .build());
    }

    @Override
    public CreativeVariableResponse editCreativeVariable(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String frontendControllerActionId,
        String variableName,
        CreativeVariableRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableRestException,
        BuildCampaignRestException, CampaignUpdateRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        LOG.debug("editing creative variable: " + campaignId + " " + frontendControllerActionId + " " + variableName +
            " with request: " + request);
        try {
            request.getValues()
                .ifPresent(values -> validateRequestVariables(variableName, values));

            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            frontendControllerActionId =
                findCreativeArchiveId(campaign, Id.valueOf(frontendControllerActionId)).getId().getValue();
            CreativeArchiveBuilder archiveBuilder =
                getArchiveBuilder(campaign, Id.valueOf(frontendControllerActionId), authorization,
                    expectedCurrentVersion);
            LOG.debug("archiveBuilder " + archiveBuilder);
            CreativeVariableBuilder creativeVariableBuilder =
                archiveBuilder.getCreativeVariable(campaign, variableName);
            LOG.debug("creativeVariableBuilder " + creativeVariableBuilder);

            LOG.debug("scope: " + request.getScope());
            if (request.getScope() != null) {
                LOG.debug("scope is not null, adding scope to builder");
                String creativeArchiveIdForScope = frontendControllerActionId;
                request.getScope()
                    .ifPresent(scope -> creativeVariableBuilder.withScope(
                        toScope(scope, campaignId, creativeArchiveIdForScope, variableName)));
            }

            request.getValues()
                .ifPresent(values -> creativeVariableBuilder.withValues(values));
            request.getVisible()
                .ifPresent(value -> creativeVariableBuilder.withVisible(value.booleanValue()));

            LOG.debug("saving builder");

            CreativeArchiveBuildResult builderSaveResult = archiveBuilder.save();
            CreativeArchive updatedArchive = builderSaveResult.getCreativeArchive();

            campaign = builderSaveResult.getCampaign();

            CreativeVariable creativeVariable =
                creativeVariableService.getCreativeVariable(campaign, updatedArchive.getCreativeArchiveId(),
                    variableName);

            addExtoleLogResponseHeaders(creativeVariable.getOutput());
            addArchiveOutputToExtoleLogResponseHeaders(updatedArchive);

            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            CampaignControllerActionCreative actionCreative =
                actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                    updatedArchive.getCreativeArchiveId().getId());
            return CreativeVariableRestMapper.toCreativeVariableResponse(
                originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                coreAssetsVersion.getValue(), creativeVariable,
                initBuildVersionProvider(authorization, campaignId, campaign.getVersion()),
                actionCreative.getId().getValue());
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
        } catch (CreativeVariableServiceInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(e)
                .build();
        } catch (CreativeArchiveJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.JAVASCRIPT_ERROR)
                .addParameter("creative_id", e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .addParameter("output", e.getOutput())
                .withCause(e)
                .build();
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | CreativeArchiveBuilderException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_CREATIVE_ID)
                .addParameter("creative_id", frontendControllerActionId)
                .withCause(e)
                .build();
        } catch (CreativeVariableUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_UNSUPPORTED)
                .addParameter("creative_id", frontendControllerActionId)
                .addParameter("client_id", authorization.getClientId()
                    .getValue())
                .withCause(e)
                .build();
        } catch (CreativeVariableInvalidValueFormatException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_VALUE_FORMAT)
                .addParameter("variable_name", e.getVariableName())
                .addParameter("variable_value", e.getVariableValue())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance()
                .map(e);
        } catch (CreativeArchiveRestException
            | CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CampaignControllerTriggerBuildException | CampaignLabelBuildException
            | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType
            | StepDataBuildException
            | CampaignScheduleException | CampaignComponentTypeValidationException
            | AuthorizationException | ComponentTypeNotFoundException | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void validateRequestVariables(String variableName,
        Map<String, String> values) throws CreativeVariableInvalidValueFormatException {
        if (variableName.equals(CreativeVariableNames.DELAY_PERIOD.getName())) {
            for (String value : values.values()) {
                try {
                    Arrays.stream(value.split(","))
                        .filter(valuePart -> !valuePart.equals(" "))
                        .map(String::trim)
                        .mapToDouble(Double::parseDouble)
                        .toArray();
                } catch (NumberFormatException e) {
                    throw new CreativeVariableInvalidValueFormatException(CreativeVariableNames.DELAY_PERIOD.getName(),
                        value, e);
                }
            }
        }
    }

    @Override
    public CreativeVariableResponse replaceImagePost(String accessToken,
        String campaignId,
        String frontendControllerActionId,
        String variableName,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        return replaceImagePut(accessToken, campaignId, expectedCurrentVersion, frontendControllerActionId,
            variableName,
            inputStream, contentDisposition);
    }

    @Override
    public CreativeVariableResponse replaceImagePut(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String frontendControllerActionId,
        String variableName,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        if (inputStream == null || contentDisposition == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();

            return editImageVariable(campaignId, frontendControllerActionId, variableName, inputStream,
                contentDisposition,
                authorization, coreAssetsVersion, expectedCurrentVersion);
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
        } catch (CreativeVariableServiceInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(e)
                .build();
        } catch (CreativeVariableImageUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_TYPE_IMAGE)
                .withCause(e)
                .build();
        } catch (CreativeVariableUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_UNSUPPORTED)
                .addParameter("creative_id", frontendControllerActionId)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e)
                .build();
        } catch (CreativeArchiveJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.JAVASCRIPT_ERROR)
                .addParameter("creative_id", e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .addParameter("output", e.getOutput())
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | IOException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_CREATIVE_ID)
                .addParameter("creative_id", frontendControllerActionId)
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_SIZE_TOO_BIG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_size", Long.valueOf(e.getAssetContentSize()))
                .addParameter("max_allowed_file_size", Long.valueOf(e.getMaxAllowedAssetContentSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetPathTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_PATH_TOO_LONG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_path_length", Integer.valueOf(e.getAssetPathLength()))
                .addParameter("max_allowed_file_path_length", Integer.valueOf(e.getMaxAllowedAssetPathLength()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof CampaignControllerActionCreativeInvalidCreativeArchiveException) {
                mapAndRethrowIfNeeded((CampaignControllerActionCreativeInvalidCreativeArchiveException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance()
                .map(e);
        } catch (CreativeArchiveBuilderException
            | CreativeArchiveRestException
            | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType
            | StepDataBuildException | CampaignScheduleException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CreativeVariableResponse replaceLocaleImagePost(String accessToken,
        String campaignId,
        String frontendControllerActionId,
        String variableName,
        String locale,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        return replaceLocaleImagePut(accessToken, campaignId, expectedCurrentVersion, frontendControllerActionId,
            variableName,
            locale, inputStream, contentDisposition);
    }

    @Override
    public CreativeVariableResponse replaceLocaleImagePut(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String frontendControllerActionId,
        String variableName,
        String locale,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition)
        throws UserAuthorizationRestException, CreativeVariableRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException {
        if (inputStream == null || contentDisposition == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SimpleClient.CoreAssetsVersion coreAssetsVersion =
                clientCoreAssetsVersionService.getLatestCoreAssetsVersion(authorization.getClientId())
                    .getCoreAssetsVersion();
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));

            frontendControllerActionId =
                findCreativeArchiveId(campaign, Id.valueOf(frontendControllerActionId)).getId().getValue();
            CreativeArchiveBuilder archiveBuilder =
                getArchiveBuilder(campaign, Id.valueOf(frontendControllerActionId), authorization,
                    expectedCurrentVersion);
            ByteSource byteSource = getByteSourceFromInputStream(inputStream);
            Path filePath = cleanImagePath(Paths.get(contentDisposition.getFileName()));

            archiveBuilder.getCreativeVariable(campaign, variableName)
                .replaceLocaleImage(locale, filePath, byteSource)
                .withScope(Scope.CREATIVE);
            CreativeArchiveBuildResult builderSaveResult = archiveBuilder.save();
            CreativeArchive updatedArchive = builderSaveResult.getCreativeArchive();
            campaign = builderSaveResult.getCampaign();
            CampaignControllerActionCreative actionCreative =
                actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                    updatedArchive.getCreativeArchiveId().getId());
            return CreativeVariableRestMapper.toCreativeVariableResponse(
                originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                coreAssetsVersion.getValue(),
                creativeVariableService.getCreativeVariable(campaign, updatedArchive.getCreativeArchiveId(),
                    variableName),
                initBuildVersionProvider(authorization, campaignId, campaign.getVersion()),
                actionCreative.getId().getValue());
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
        } catch (CreativeVariableServiceInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(e)
                .build();
        } catch (CreativeVariableImageUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_TYPE_IMAGE)
                .withCause(e)
                .build();
        } catch (CreativeVariableUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_UNSUPPORTED)
                .addParameter("creative_id", frontendControllerActionId)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e)
                .build();
        } catch (CreativeArchiveJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.JAVASCRIPT_ERROR)
                .addParameter("creative_id", e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .addParameter("output", e.getOutput())
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | IOException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_CREATIVE_ID)
                .addParameter("creative_id", frontendControllerActionId)
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_SIZE_TOO_BIG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_size", Long.valueOf(e.getAssetContentSize()))
                .addParameter("max_allowed_file_size", Long.valueOf(e.getMaxAllowedAssetContentSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveAssetPathTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_PATH_TOO_LONG)
                .addParameter("file_path", e.getAssetPath())
                .addParameter("file_path_length", Integer.valueOf(e.getAssetPathLength()))
                .addParameter("max_allowed_file_path_length", Integer.valueOf(e.getMaxAllowedAssetPathLength()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            if (e instanceof CampaignControllerActionCreativeInvalidCreativeArchiveException) {
                mapAndRethrowIfNeeded((CampaignControllerActionCreativeInvalidCreativeArchiveException) e);
            }
            throw BuildCampaignRestExceptionMapper.getInstance()
                .map(e);
        } catch (CreativeArchiveBuilderException | CreativeArchiveRestException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType
            | StepDataBuildException | CampaignScheduleException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private void addVariableOutputToExtoleLogResponseHeaders(List<CreativeVariable> creativeVariables) {
        creativeVariables.stream()
            .filter(variable -> !variable.getOutput()
                .isEmpty())
            .findFirst()
            .ifPresent(variable -> addExtoleLogResponseHeaders(variable.getOutput()));
    }

    private void addArchiveOutputToExtoleLogResponseHeaders(CreativeArchive updatedArchive) {
        updatedArchive.getLogMessages()
            .entrySet()
            .stream()
            .findFirst()
            .ifPresent(value -> addExtoleLogResponseHeaders(value.getValue()));
    }

    private void addExtoleLogResponseHeaders(List<String> output) {
        if (servletRequest.getHeader(EXTOLE_DEBUG_HEADER) != null) {
            output.forEach(value -> servletResponse.addHeader(EXTOLE_LOG_HEADER, value));
        }
    }

    private CreativeVariableResponse editImageVariable(String campaignId,
        String creativeArchiveId,
        String variableName,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition,
        Authorization authorization,
        SimpleClient.CoreAssetsVersion coreAssetsVersion,
        String expectedCurrentVersion)
        throws ConcurrentCampaignUpdateException, UserAuthorizationRestException, CreativeArchiveRestException,
        CreativeArchiveVersionException,
        CreativeVariableImageUnsupportedException, CreativeVariableServiceInvalidNameException,
        CreativeArchiveJavascriptException,
        CreativeArchiveBuilderException, CampaignServiceNameLengthException,
        CampaignServiceIllegalCharacterInNameException, CreativeVariableUnsupportedException,
        CampaignControllerTriggerBuildException, BuildCampaignException, CampaignLabelMissingNameException,
        CampaignLabelDuplicateNameException, CampaignServiceNameMissingException, IOException, CampaignRestException,
        CampaignComponentNameDuplicateException, InvalidComponentReferenceException,
        TransitionRuleAlreadyExistsForActionType, IncompatibleRewardRuleException,
        StepDataBuildException, StaleCampaignVersionException, CampaignScheduleException, BuildCampaignRestException,
        CampaignComponentTypeValidationException,
        AuthorizationException, ComponentTypeNotFoundException, CreativeVariableRestException {
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        creativeArchiveId = findCreativeArchiveId(campaign, Id.valueOf(creativeArchiveId)).getId().getValue();
        CreativeArchiveBuilder archiveBuilder =
            getArchiveBuilder(campaign, Id.valueOf(creativeArchiveId), authorization, expectedCurrentVersion);
        ByteSource byteSource = getByteSourceFromInputStream(inputStream);
        Path filePath = cleanImagePath(Paths.get(contentDisposition.getFileName()));

        archiveBuilder.getCreativeVariable(campaign, variableName)
            .replaceImage(filePath, byteSource)
            .withScope(Scope.CREATIVE);
        CreativeArchiveBuildResult builderSaveResult = archiveBuilder.save();
        CreativeArchive updatedArchive = builderSaveResult.getCreativeArchive();
        campaign = builderSaveResult.getCampaign();
        CampaignControllerActionCreative actionCreative =
            actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                updatedArchive.getCreativeArchiveId().getId());
        return CreativeVariableRestMapper.toCreativeVariableResponse(
            originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
            coreAssetsVersion.getValue(),
            creativeVariableService.getCreativeVariable(campaign, updatedArchive.getCreativeArchiveId(), variableName),
            initBuildVersionProvider(authorization, campaignId, campaign.getVersion()),
            actionCreative.getId().getValue());
    }

    private CreativeVariableResponse editCampaignImageVariable(String campaignId,
        String variableName,
        InputStream inputStream,
        FormDataContentDisposition contentDisposition,
        Authorization authorization,
        SimpleClient.CoreAssetsVersion coreAssetsVersion,
        @Nullable String locale,
        String expectedCurrentVersion)
        throws ConcurrentCampaignUpdateException, UserAuthorizationRestException, CreativeArchiveVersionException,
        CreativeArchiveRestException,
        CreativeVariableImageUnsupportedException, CreativeVariableServiceInvalidNameException,
        CreativeArchiveJavascriptException,
        CreativeArchiveBuilderException, CampaignServiceNameLengthException,
        CampaignServiceIllegalCharacterInNameException, CreativeVariableUnsupportedException,
        CampaignControllerTriggerBuildException, BuildCampaignException, CampaignLabelMissingNameException,
        CampaignLabelDuplicateNameException, IncompatibleRewardRuleException, CampaignServiceNameMissingException,
        IOException, CreativeVariableRestException, CampaignRestException,
        CampaignComponentNameDuplicateException, InvalidComponentReferenceException,
        TransitionRuleAlreadyExistsForActionType, BuildCampaignEvaluatableException,
        StepDataBuildException, StaleCampaignVersionException, CampaignScheduleException, BuildCampaignRestException,
        CampaignComponentTypeValidationException,
        AuthorizationException, ComponentTypeNotFoundException {

        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        CreativeArchiveBuilder archiveBuilder = null;
        ByteSource byteSource = getByteSourceFromInputStream(inputStream);
        Path filePath = cleanImagePath(Paths.get(contentDisposition.getFileName()));

        CampaignControllerActionCreative[] creativeActions = campaign.getFrontendControllers()
            .stream()
            .flatMap(controller -> controller.getActions().stream())
            .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
            .map(value -> (CampaignControllerActionCreative) value)
            .filter(value -> value.getCreativeArchiveId().isPresent())
            .toArray(CampaignControllerActionCreative[]::new);

        for (CampaignControllerActionCreative actionCreative : creativeActions) {
            if (!archiveHasCampaignVariable(campaign, actionCreative.getCreativeArchiveId().get(), variableName)) {
                continue;
            }
            archiveBuilder = getArchiveBuilder(campaign, actionCreative
                .getCreativeArchiveId().get().getId(), authorization, expectedCurrentVersion);
            if (locale != null) {
                archiveBuilder.getCreativeVariable(campaign, variableName)
                    .replaceLocaleImage(locale, filePath, byteSource);
            } else {
                archiveBuilder.getCreativeVariable(campaign, variableName)
                    .replaceImage(filePath, byteSource);
            }
        }

        if (archiveBuilder != null) {
            CreativeArchiveBuildResult builderSaveResult = archiveBuilder.save();
            CreativeArchive updatedArchive = builderSaveResult.getCreativeArchive();
            campaign = builderSaveResult.getCampaign();
            CampaignControllerActionCreative actionCreative =
                actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                    updatedArchive.getCreativeArchiveId().getId());
            return CreativeVariableRestMapper.toCreativeVariableResponse(
                originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                coreAssetsVersion.getValue(),
                creativeVariableService.getCreativeVariable(campaign, updatedArchive.getCreativeArchiveId(),
                    variableName),
                initBuildVersionProvider(authorization, campaignId, campaign.getVersion()),
                actionCreative.getId().getValue());
        } else {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
                .addParameter("variable_name", variableName)
                .withCause(new CreativeVariableServiceInvalidNameException("Campaign variable does not exist"))
                .build();
        }
    }

    private ByteSource getByteSourceFromInputStream(InputStream inputStream) throws IOException {
        try (FileBackedOutputStream outputStream = new FileBackedOutputStream(fileThreshold, true);
            InputStream closeableInputStream = inputStream) {
            ByteStreams.copy(closeableInputStream, outputStream);
            return outputStream.asByteSource();
        }
    }

    private Path cleanImagePath(Path imagePath) {
        String imageFile = imagePath.getFileName()
            .toString();
        String cleanedImageBaseName = FilenameUtils.getBaseName(imageFile)
            .replaceAll("[^a-zA-Z0-9-]", "_");
        String imageExtension = FilenameUtils.getExtension(imageFile);
        String uniqueID = String.valueOf(Instant.now()
            .toEpochMilli());
        String cleanedUniqueImageFileName = new StringBuilder().append(cleanedImageBaseName)
            .append("_")
            .append(uniqueID)
            .append(".")
            .append(imageExtension)
            .toString();
        return Paths.get(cleanedUniqueImageFileName);
    }

    private CreativeArchiveBuilder getArchiveBuilder(Campaign campaign,
        Id<CreativeArchive> creativeArchiveId, Authorization userAuthorization, String expectedCurrentVersion)
        throws UserAuthorizationRestException, CreativeArchiveRestException, CampaignRestException {
        CampaignBuilder campaignBuilder = getCampaignBuilder(campaign, userAuthorization, expectedCurrentVersion);
        return getArchiveBuilder(campaign, campaignBuilder, creativeArchiveId);
    }

    private CampaignBuilder getCampaignBuilder(Campaign campaign,
        Authorization authorization,
        String expectedCurrentVersion) throws UserAuthorizationRestException, CampaignRestException {
        try {
            String campaignId = campaign.getId()
                .getValue();
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            return campaignBuilder;
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
    }

    private CreativeArchiveBuilder getArchiveBuilder(Campaign campaign,
        CampaignBuilder campaignBuilder,
        Id<CreativeArchive> creativeArchiveId)
        throws CreativeArchiveRestException {

        List<Pair<FrontendController, CampaignControllerActionCreative>> controllersWithActions =
            campaign.getFrontendControllers()
                .stream()
                .flatMap(controller -> controller.getActions().stream()
                    .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
                    .map(value -> (CampaignControllerActionCreative) value)
                    .filter(value -> value.getCreativeArchiveId().isPresent())
                    .filter(value -> value.getCreativeArchiveId().get().getId().equals(creativeArchiveId))
                    .map(action -> Pair.of(controller, action)))
                .collect(Collectors.toUnmodifiableList());

        for (Pair<FrontendController, CampaignControllerActionCreative> pair : controllersWithActions) {
            FrontendControllerBuilder controllerBuilder = campaignBuilder.updateFrontendController(pair.getLeft());
            CampaignControllerActionCreativeBuilder actionBuilder = controllerBuilder.updateAction(pair.getRight());
            return actionBuilder.getCreativeArchive().get();
        }

        throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
            .withErrorCode(CreativeArchiveRestException.INVALID_ARCHIVE_ID)
            .addParameter("campaign_id", campaign.getId())
            .addParameter("creative_archive_id", creativeArchiveId)
            .build();
    }

    private boolean archiveHasCampaignVariable(Campaign campaign,
        CreativeArchiveId creativeArchiveId,
        String variableName) {
        return archiveHasVariable(campaign, creativeArchiveId, variableName, Scope.CAMPAIGN);
    }

    private boolean archiveHasVariable(Campaign campaign,
        CreativeArchiveId creativeArchiveId,
        String variableName,
        Scope variableScope) {
        try {
            return creativeVariableService.getCreativeVariable(campaign, creativeArchiveId, variableName)
                .getScope() == variableScope;
        } catch (CreativeVariableServiceInvalidNameException e) {
            return false;
        }
    }

    private CreativeArchiveBuildResult saveArchive(Authorization userAuthorization,
        CreativeArchiveId creativeArchiveId,
        CreativeArchiveBuilder archiveBuilder)
        throws ConcurrentCampaignUpdateException, CreativeArchiveBuilderException, CampaignServiceNameLengthException,
        CampaignServiceIllegalCharacterInNameException, CampaignControllerTriggerBuildException,
        CampaignLabelMissingNameException, CampaignLabelDuplicateNameException, StaleCampaignVersionException,
        CampaignServiceNameMissingException, CreativeVariableRestException, CampaignComponentNameDuplicateException,
        InvalidComponentReferenceException,
        TransitionRuleAlreadyExistsForActionType, StepDataBuildException,
        CampaignScheduleException, BuildCampaignRestException, CampaignComponentTypeValidationException,
        AuthorizationException, ComponentTypeNotFoundException, IncompatibleRewardRuleException {
        try {
            return archiveBuilder.save();
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.JAVASCRIPT_ERROR)
                .addParameter("creative_id", e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .addParameter("output", e.getOutput())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance()
                .map(e);
        } catch (CreativeVariableUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_UNSUPPORTED)
                .addParameter("creative_id", creativeArchiveId
                    .getId()
                    .getValue())
                .addParameter("client_id", userAuthorization.getClientId()
                    .getValue())
                .withCause(e)
                .build();
        }
    }

    private Scope toScope(CreativeVariableScope scope,
        String campaignId,
        String creativeArchiveId,
        String variableName) {
        switch (scope) {
            case CAMPAIGN:
                return Scope.CAMPAIGN;
            case CREATIVE:
                return Scope.CREATIVE;
            default:
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withCause(new RuntimeException(
                        "Unable to convert CreativeVariableScope " + scope.toString() + " to Scope for campaignId " +
                            campaignId + " creativeArchiveId " + creativeArchiveId + " variableName " + variableName))
                    .build();
        }
    }

    private CreativeArchive getCreativeArchive(Authorization authorization,
        Campaign campaign,
        Id<CreativeArchive> creativeArchiveIdNoVersion) throws CreativeVariableRestException {
        try {
            CreativeArchiveId creativeArchiveId = findCreativeArchiveId(campaign, creativeArchiveIdNoVersion);
            return creativeArchiveService.getCreativeArchive(authorization, creativeArchiveId);
        } catch (CreativeArchiveNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private CreativeVariableResponse internallyEditCampaignVariable(Authorization authorization,
        Campaign campaign,
        CreativeVariableRequest request, String variableName, String expectedCurrentVersion,
        SimpleClient.CoreAssetsVersion coreAssetsVersion)
        throws UserAuthorizationRestException,
        CreativeArchiveRestException, CampaignRestException,
        BuildCampaignRestException, TransitionRuleAlreadyExistsForActionType, CampaignComponentTypeValidationException,
        AuthorizationException, CreativeVariableRestException, CampaignScheduleException,
        CampaignServiceNameMissingException, StepDataBuildException,
        InvalidComponentReferenceException, CreativeArchiveBuilderException, CampaignLabelDuplicateNameException,
        CampaignServiceNameLengthException, StaleCampaignVersionException, ConcurrentCampaignUpdateException,
        CampaignLabelMissingNameException, CampaignControllerTriggerBuildException, ComponentTypeNotFoundException,
        CampaignServiceIllegalCharacterInNameException, CampaignComponentNameDuplicateException,
        CreativeVariableServiceInvalidNameException, IncompatibleRewardRuleException,
        CreativeArchiveVersionException {

        CreativeVariableResponse creativeVariableResponse = null;
        CampaignControllerActionCreative[] creativeActions = campaign.getFrontendControllers()
            .stream()
            .flatMap(controller -> controller.getActions().stream())
            .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
            .map(value -> (CampaignControllerActionCreative) value)
            .filter(value -> value.getCreativeArchiveId().isPresent())
            .toArray(CampaignControllerActionCreative[]::new);

        for (CampaignControllerActionCreative actionCreative : creativeActions) {
            if (!archiveHasCampaignVariable(campaign, actionCreative.getCreativeArchiveId().get(), variableName)) {
                continue;
            }
            CreativeArchiveBuilder archiveBuilder = getArchiveBuilder(campaign, actionCreative
                .getCreativeArchiveId().get().getId(), authorization, expectedCurrentVersion);
            CreativeVariableBuilder creativeVariableBuilder =
                archiveBuilder.getCreativeVariable(campaign, variableName);

            request.getValues()
                .ifPresent(values -> creativeVariableBuilder.withValues(values));
            request.getVisible()
                .ifPresent(visible -> creativeVariableBuilder.withVisible(visible.booleanValue()));

            CreativeArchiveBuildResult builderSaveResult =
                saveArchive(authorization, actionCreative.getCreativeArchiveId().get(), archiveBuilder);
            CreativeArchive updatedArchive = builderSaveResult.getCreativeArchive();
            campaign = builderSaveResult.getCampaign();

            if (creativeVariableResponse == null) {
                CreativeVariable updatedVariable =
                    creativeVariableService.getCreativeVariable(campaign, updatedArchive.getCreativeArchiveId(),
                        variableName);

                addExtoleLogResponseHeaders(updatedVariable.getOutput());
                addArchiveOutputToExtoleLogResponseHeaders(updatedArchive);

                creativeVariableResponse = CreativeVariableRestMapper.toCreativeVariableResponse(
                    originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
                    coreAssetsVersion.getValue(), updatedVariable,
                    initBuildVersionProvider(authorization, campaign.getId().getValue(), campaign.getVersion()),
                    actionCreative.getId().getValue());
            }
        }
        if (creativeVariableResponse != null) {
            return creativeVariableResponse;
        }

        throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
            .withErrorCode(CreativeVariableRestException.INVALID_VARIABLE_NAME)
            .addParameter("variable_name", variableName)
            .withCause(new CreativeVariableServiceInvalidNameException("Campaign variable does not exist"))
            .build();
    }

    private CreativeVariableZoneState parseZoneState(String zoneState) throws CreativeVariableRestException {
        try {
            if (StringUtils.isEmpty(zoneState)) {
                return CreativeVariableZoneState.ANY;
            }
            return CreativeVariableZoneState.valueOf(StringUtils.upperCase(zoneState));
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_ZONE_STATE)
                .addParameter("current_value", zoneState)
                .addParameter("allowed_values", CreativeVariableZoneState.values())
                .withCause(e)
                .build();
        }
    }

    private Function<CreativeArchiveId, Integer> initBuildVersionProvider(Authorization authorization,
        String campaignId,
        Integer version) throws CampaignRestException, BuildCampaignRestException {
        com.extole.model.entity.campaign.built.BuiltCampaign builtCampaign = campaignProvider
            .getBuiltCampaign(authorization, Id.valueOf(campaignId), version.toString());
        Map<CreativeArchiveId, Optional<Integer>> buildVersions = new HashMap<>();
        builtCampaign.getFrontendControllers()
            .stream()
            .flatMap(controller -> controller.getActions().stream())
            .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
            .map(value -> (BuiltCampaignControllerActionCreative) value)
            .filter(value -> value.getCreativeArchiveId().isPresent())
            .forEach(action -> {
                CreativeArchiveId creativeArchiveId = new CreativeArchiveId(action.getCreativeArchiveId().get().getId(),
                    action.getCreativeArchiveId().get().getVersion());
                action.getCreativeArchiveId().get().getBuildVersion().ifPresent(builtVersion -> {
                    buildVersions.computeIfAbsent(creativeArchiveId, (key) -> {
                        return Optional.of(builtVersion);
                    });
                });
            });

        return (id) -> buildVersions.getOrDefault(id, Optional.empty())
            .orElse(null);
    }

    private void mapAndRethrowIfNeeded(
        CampaignControllerActionCreativeInvalidCreativeArchiveException e) throws CreativeVariableRestException {
        if (e.getCause() instanceof CreativeArchiveAssetContentSizeTooBigException) {
            CreativeArchiveAssetContentSizeTooBigException ex =
                (CreativeArchiveAssetContentSizeTooBigException) e.getCause();
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_SIZE_TOO_BIG)
                .addParameter("file_path", ex.getAssetPath())
                .addParameter("file_size", Long.valueOf(ex.getAssetContentSize()))
                .addParameter("max_allowed_file_size", Long.valueOf(ex.getMaxAllowedAssetContentSize()))
                .withCause(ex)
                .build();
        }
        if (e.getCause() instanceof CreativeArchiveSizeTooBigException) {
            CreativeArchiveSizeTooBigException ex = (CreativeArchiveSizeTooBigException) e.getCause();
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(ex.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(ex.getMaxAllowedSize()))
                .withCause(e)
                .build();
        }
        if (e.getCause() instanceof CreativeArchiveAssetPathTooLongException) {
            CreativeArchiveAssetPathTooLongException ex = (CreativeArchiveAssetPathTooLongException) e.getCause();
            throw RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.CREATIVE_VARIABLE_IMAGE_FILE_PATH_TOO_LONG)
                .addParameter("file_path", ex.getAssetPath())
                .addParameter("file_path_length", Integer.valueOf(ex.getAssetPathLength()))
                .addParameter("max_allowed_file_path_length", Integer.valueOf(ex.getMaxAllowedAssetPathLength()))
                .withCause(e)
                .build();
        }
    }

    private static class CreativeVariableInvalidValueFormatException
        extends Exception {
        private final String variableName;
        private final String variableValue;

        CreativeVariableInvalidValueFormatException(String variableName,
            String variableValue,
            Throwable cause) {
            super("Variable: " + variableName + " has an invalid value format: " + variableValue, cause);
            this.variableName = variableName;
            this.variableValue = variableValue;
        }

        public String getVariableName() {
            return this.variableName;
        }

        public String getVariableValue() {
            return this.variableValue;
        }
    }

}
