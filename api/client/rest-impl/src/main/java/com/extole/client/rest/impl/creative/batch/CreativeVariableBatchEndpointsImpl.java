package com.extole.client.rest.impl.creative.batch;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.change.model.SimpleClient;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.creative.CreativeVariableResponse;
import com.extole.client.rest.creative.CreativeVariableRestException;
import com.extole.client.rest.creative.CreativeVariableScope;
import com.extole.client.rest.creative.batch.CreativeVariableBatchEndpoints;
import com.extole.client.rest.creative.batch.CreativeVariableBatchRestException;
import com.extole.client.rest.creative.batch.CreativeVariableUpdateRequest;
import com.extole.client.rest.creative.batch.ZoneCreativeVariableUpdateRequest;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.creative.ActionCreativeProvider;
import com.extole.client.rest.impl.creative.CreativeVariableRestMapper;
import com.extole.client.rest.impl.creative.batch.VariableUpdateRequest.Builder;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.FileFormatRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignControllerTriggerEventType;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CreativeArchive;
import com.extole.model.entity.campaign.CreativeArchiveId;
import com.extole.model.entity.campaign.CreativeArchiveVersionException;
import com.extole.model.entity.campaign.CreativeVariable;
import com.extole.model.entity.campaign.CreativeVariable.Scope;
import com.extole.model.entity.campaign.CreativeVariableServiceInvalidNameException;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionCreative;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerEvent;
import com.extole.model.entity.campaign.built.BuiltFrontendController;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignDateAfterStopDateException;
import com.extole.model.service.campaign.CampaignDateBeforeStartDateException;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignHasScheduledSiblingException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignStartDateAfterStopDateException;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionNotFoundException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeInvalidCreativeArchiveException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
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
import com.extole.model.service.creative.CreativeArchiveService;
import com.extole.model.service.creative.CreativeBatchVariableValuesFileParserFactory;
import com.extole.model.service.creative.CreativeVariableBuilder;
import com.extole.model.service.creative.CreativeVariableService;
import com.extole.model.service.creative.CreativeVariableZoneState;
import com.extole.model.service.creative.CreativeVariables;
import com.extole.model.service.creative.CreativeVariablesFormat;
import com.extole.model.service.creative.OriginHostService;
import com.extole.model.service.creative.UnsupportedFileFormatException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveInvalidLocaleException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeArchiveNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveSizeTooBigException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CreativeVariableBatchEndpointsImpl implements CreativeVariableBatchEndpoints {

    private static final String EXTOLE_DEBUG_HEADER = "X-Extole-Debug";
    private static final String EXTOLE_LOG_HEADER = "X-Extole-Log";
    private static final String BATCH_VARIABLES_VALUES_CONTENT_DISPOSITION_FORMATTER =
        "attachment; filename = batch-variables-values.%s";

    private final CampaignService campaignService;
    private final CreativeVariableService creativeVariableService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final ClientCoreAssetsVersionService clientCoreAssetsVersionService;
    private final OriginHostService originHostService;
    private final CreativeBatchVariableValuesFileParserFactory creativeBatchVariableValuesFileParserFactory;
    private final CreativeArchiveService creativeArchiveService;
    private final ActionCreativeProvider actionCreativeProvider;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;

    @Autowired
    public CreativeVariableBatchEndpointsImpl(
        CampaignService campaignService,
        CreativeVariableService creativeVariableService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        ClientCoreAssetsVersionService clientCoreAssetsVersionService,
        OriginHostService originHostService,
        CreativeBatchVariableValuesFileParserFactory creativeBatchVariableValuesFileParserFactory,
        CreativeArchiveService creativeArchiveService,
        ActionCreativeProvider actionCreativeProvider,
        @Context HttpServletRequest servletRequest,
        @Context HttpServletResponse servletResponse) {
        this.campaignService = campaignService;
        this.creativeVariableService = creativeVariableService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.clientCoreAssetsVersionService = clientCoreAssetsVersionService;
        this.originHostService = originHostService;
        this.creativeBatchVariableValuesFileParserFactory = creativeBatchVariableValuesFileParserFactory;
        this.creativeArchiveService = creativeArchiveService;
        this.actionCreativeProvider = actionCreativeProvider;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }

    @Override
    public Map<String, List<CreativeVariableResponse>> getVariables(
        String accessToken,
        String campaignIdAsString,
        String version,
        Optional<String> zoneState,
        String groupBy)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException,
        CreativeVariableBatchRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CreativeVariableZoneState creativeVariableZoneState = CreativeVariableZoneState.ANY;

        try {
            if (zoneState.isPresent()) {
                creativeVariableZoneState = parseZoneState(zoneState.get());
            }
            Id<Campaign> campaignId = Id.valueOf(campaignIdAsString);
            CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(campaignId, version);
            List<CreativeVariables> creativeVariables =
                creativeVariableService.getVariables(authorization, campaignId, campaignVersion,
                    creativeVariableZoneState);

            addCreativeVariableOutputToExtoleLogResponseHeaders(creativeVariables);

            SimpleClient.CoreAssetsVersion coreAssetsVersion = clientCoreAssetsVersionService
                .getLatestCoreAssetsVersion(authorization.getClientId()).getCoreAssetsVersion();

            Map<String, List<CreativeVariableResponse>> response = new HashMap<>();
            boolean groupByAction = StringUtils.equalsIgnoreCase(groupBy, "action");
            for (CreativeVariables item : creativeVariables) {
                String key = groupByAction
                    ? item.getActionCreativeId().getValue()
                    : item.getCreativeArchiveId().getValue();
                response.put(key,
                    toCreativeVariableResponse(authorization, item, coreAssetsVersion, campaignId,
                        campaignVersion.getValue()));
            }
            return response;
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignIdAsString)
                .withCause(e)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Map<String, List<CreativeVariableResponse>> updateVariables(
        String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        List<CreativeVariableUpdateRequest> request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CreativeVariableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));

            List<CreativeVariableUpdateRequest> updateRequestWithCreativeActionId =
                request.stream().filter(value -> value.getCreativeActionId().isPresent())
                    .collect(toUnmodifiableList());

            Map<Id<CreativeArchive>, List<VariableUpdateRequest>> batchedActionVariables =
                getCreativeVariablesToUpdateGroupedByCreativeAction(authorization, campaign,
                    updateRequestWithCreativeActionId)
                        .entrySet().stream()
                        .filter(entry -> entry.getKey().getCreativeArchiveId().isPresent())
                        .collect(Collectors.toUnmodifiableMap(entry -> entry.getKey()
                            .getCreativeArchiveId().get().getId(), Entry::getValue));

            Map<Id<CreativeArchive>, List<VariableUpdateRequest>> batchedVariables = ImmutableMap
                .<Id<CreativeArchive>, List<VariableUpdateRequest>>builder()
                .putAll(batchedActionVariables)
                .build();

            return updateVariables(authorization, campaign, batchedVariables, expectedCurrentVersion).entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                    entry -> entry.getKey().getValue(),
                    entry -> entry.getValue()));
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (CreativeArchiveInvalidLocaleException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.INVALID_LOCALE)
                .addParameter("locale", e.getLocale())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | CreativeVariableServiceInvalidNameException
            | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | StepDataBuildException
            | CampaignControllerTriggerBuildException | CampaignLabelBuildException
            | CampaignServiceNameMissingException | ClientCoreAssetsVersionNotFoundException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignScheduleException | CampaignFlowStepException
            | CampaignGlobalDeleteException | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException | CampaignComponentException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Map<String, List<CreativeVariableResponse>> updateZoneVariables(String accessToken, String campaignId,
        String expectedCurrentVersion, List<ZoneCreativeVariableUpdateRequest> request)
        throws CampaignRestException, UserAuthorizationRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CreativeVariableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return updateZoneVariables(campaignId, expectedCurrentVersion, authorization, request);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (CreativeArchiveInvalidLocaleException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.INVALID_LOCALE)
                .addParameter("locale", e.getLocale())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | CreativeVariableServiceInvalidNameException
            | CreativeArchiveBuilderException | StepDataBuildException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException | CampaignComponentNameDuplicateException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignScheduleException
            | CampaignFlowStepException | CampaignGlobalDeleteException | CampaignGlobalArchiveException
            | CampaignGlobalStateChangeException | CampaignComponentTypeValidationException | AuthorizationException
            | ComponentTypeNotFoundException | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentException | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    @BatchVariableValuesResponseBinding
    public Response getVariablesValues(
        String accessToken,
        String contentType,
        String campaignIdAsString,
        String version,
        String format,
        String type,
        String tags,
        Optional<String> zoneState)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        CreativeVariableZoneState creativeVariableZoneState = CreativeVariableZoneState.ANY;

        Id<Campaign> campaignId = Id.valueOf(campaignIdAsString);
        CampaignVersion campaignVersion = campaignProvider.getCampaignVersion(campaignId, version);

        try {
            if (zoneState.isPresent()) {
                creativeVariableZoneState = parseZoneState(zoneState.get());
            }

            List<CreativeVariables> creativeVariables = creativeVariableService.getVariables(authorization, campaignId,
                campaignVersion, creativeVariableZoneState);

            addCreativeVariableOutputToExtoleLogResponseHeaders(creativeVariables);

            SimpleClient.CoreAssetsVersion coreAssetsVersion = clientCoreAssetsVersionService
                .getLatestCoreAssetsVersion(authorization.getClientId()).getCoreAssetsVersion();
            Optional<CreativeVariableResponse.Type> creativeVariableType = parseToCreativeVariableType(type);
            List<BatchVariableValuesResponse> batchVariableValuesResponseList = new ArrayList<>();

            for (CreativeVariables variables : creativeVariables) {
                List<CreativeVariableResponse> creativeVariablesResponse =
                    toCreativeVariableResponse(authorization, variables,
                        coreAssetsVersion, campaignId, campaignVersion.getValue());

                creativeVariablesResponse.forEach(variable -> {
                    if (variableFilter(variable, variables.getZoneName(), creativeVariableType, tags)) {
                        BatchVariableValuesResponse.Builder batchVariableValuesResponseBuilder =
                            BatchVariableValuesResponse.builder();

                        batchVariableValuesResponseBuilder.withZone(variables.getZoneName());
                        batchVariableValuesResponseBuilder.withJourneyNames(variables.getJourneyNames().stream()
                            .map(journeyName -> journeyName.getValue())
                            .collect(Collectors.toUnmodifiableSet()));
                        batchVariableValuesResponseBuilder.withName(variable.getName());

                        LinkedHashMap<String, String> variableValues =
                            variable.getValues().entrySet().stream().sorted(comparingByKey())
                                .collect(toMap(Entry::getKey,
                                    Entry::getValue,
                                    (variableValue1, variableValue2) -> variableValue2, LinkedHashMap::new));

                        batchVariableValuesResponseBuilder.withValues(variableValues);
                        batchVariableValuesResponseList.add(batchVariableValuesResponseBuilder.build());
                    }
                });
            }

            CreativeVariablesFormat creativeVariablesFormat = getFormat(format, contentType);

            return Response.ok(batchVariableValuesResponseList)
                .type(creativeVariablesFormat.getMimeType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    String.format(BATCH_VARIABLES_VALUES_CONTENT_DISPOSITION_FORMATTER,
                        creativeVariablesFormat.getExtension()))
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignIdAsString)
                .withCause(e)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (ClientCoreAssetsVersionNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Map<String, List<CreativeVariableResponse>> updateVariableValues(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        FileInputStreamRequest request)
        throws UserAuthorizationRestException, CampaignRestException, CreativeVariableBatchRestException,
        BuildCampaignRestException, CampaignUpdateRestException, CreativeVariableRestException {
        if (request == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<Map<String, String>> variableMap = getVariablesMapFromFile(request);
            List<ZoneCreativeVariableUpdateRequest> requestResponse = toZoneVariableUpdateRequests(variableMap);
            return updateZoneVariables(campaignId, expectedCurrentVersion, authorization, requestResponse);
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e)
                .build();
        } catch (CreativeArchiveInvalidLocaleException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.INVALID_LOCALE)
                .addParameter("locale", e.getLocale())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e)
                .build();
        } catch (CreativeArchiveVersionException | CreativeVariableServiceInvalidNameException
            | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CampaignControllerTriggerBuildException
            | CampaignLabelBuildException | CampaignServiceNameMissingException
            | ClientCoreAssetsVersionNotFoundException | IOException | FileFormatRestException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | StepDataBuildException | CampaignScheduleException
            | CampaignFlowStepException | CampaignGlobalDeleteException | CampaignGlobalArchiveException
            | CampaignGlobalStateChangeException | CampaignComponentTypeValidationException | AuthorizationException
            | ComponentTypeNotFoundException | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CampaignComponentException | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Map<String, List<CreativeVariableResponse>> updateZoneVariables(String campaignId,
        String expectedCurrentVersion, Authorization authorization,
        List<ZoneCreativeVariableUpdateRequest> updateRequest)
        throws CampaignRestException, CreativeVariableBatchRestException, BuildCampaignRestException,
        CreativeArchiveVersionException, UserAuthorizationRestException, CreativeVariableServiceInvalidNameException,
        ClientCoreAssetsVersionNotFoundException, CampaignComponentException, CampaignLabelMissingNameException,
        CampaignLabelDuplicateNameException, CampaignStartDateAfterStopDateException,
        CampaignDateBeforeStartDateException, CampaignDateAfterStopDateException, CampaignHasScheduledSiblingException,
        ConcurrentCampaignUpdateException, CreativeArchiveBuilderException, CampaignServiceNameLengthException,
        CampaignServiceIllegalCharacterInNameException, CampaignControllerTriggerBuildException,
        CampaignServiceNameMissingException, CampaignComponentNameDuplicateException,
        InvalidComponentReferenceException, TransitionRuleAlreadyExistsForActionType, CampaignFlowStepException,
        StepDataBuildException, StaleCampaignVersionException, CampaignGlobalDeleteException,
        CampaignGlobalArchiveException, CampaignGlobalStateChangeException, CampaignComponentTypeValidationException,
        AuthorizationException, ComponentTypeNotFoundException, ReferencedExternalElementException,
        CreativeVariableRestException, IncompatibleRewardRuleException, CampaignComponentFacetsNotFoundException {
        Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
        List<ZoneCreativeVariableUpdateRequest> updateRequestWithCreativeActionId =
            updateRequest.stream().filter(value -> value.getCreativeActionId().isPresent())
                .collect(toUnmodifiableList());

        Map<BuiltCampaignControllerActionCreative, List<VariableUpdateRequest>> batchedVariablesByActions =
            getZoneVariablesToUpdateGroupedByCreativeAction(authorization, campaign,
                updateRequestWithCreativeActionId);
        Map<Id<CreativeArchive>, List<VariableUpdateRequest>> batchedVariables = new HashMap<>();
        batchedVariablesByActions.forEach((key, value) -> {
            batchedVariables.put(key.getCreativeArchiveId().get().getId(), value);
        });

        return updateVariables(authorization, campaign, batchedVariables, expectedCurrentVersion).entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(
                entry -> entry.getKey().getValue(),
                entry -> entry.getValue()));
    }

    private Map<Id<CreativeArchive>, List<CreativeVariableResponse>> updateVariables(Authorization authorization,
        Campaign campaign, Map<Id<CreativeArchive>, List<VariableUpdateRequest>> batchedVariables,
        String expectedCurrentVersion)
        throws CampaignRestException, CreativeVariableBatchRestException, BuildCampaignRestException,
        StepDataBuildException, StaleCampaignVersionException, CreativeArchiveVersionException,
        UserAuthorizationRestException, CreativeVariableServiceInvalidNameException,
        ClientCoreAssetsVersionNotFoundException, CampaignComponentException, CampaignLabelMissingNameException,
        CampaignLabelDuplicateNameException, CampaignStartDateAfterStopDateException,
        CampaignDateBeforeStartDateException, CampaignDateAfterStopDateException, CampaignHasScheduledSiblingException,
        ConcurrentCampaignUpdateException, CreativeArchiveBuilderException, CampaignServiceNameLengthException,
        CampaignServiceIllegalCharacterInNameException, CampaignControllerTriggerBuildException,
        CampaignServiceNameMissingException, CampaignComponentNameDuplicateException,
        InvalidComponentReferenceException, TransitionRuleAlreadyExistsForActionType, CampaignFlowStepException,
        CampaignGlobalDeleteException, CampaignGlobalArchiveException, CampaignGlobalStateChangeException,
        CampaignComponentTypeValidationException, AuthorizationException, ComponentTypeNotFoundException,
        ReferencedExternalElementException, CreativeVariableRestException, IncompatibleRewardRuleException,
        CampaignComponentFacetsNotFoundException {
        CampaignBuilder campaignBuilder = getCampaignBuilder(campaign, authorization, expectedCurrentVersion);

        for (Entry<Id<CreativeArchive>, List<VariableUpdateRequest>> variablesToUpdate : batchedVariables
            .entrySet()) {
            Id<CreativeArchive> creativeArchiveId = variablesToUpdate.getKey();
            CreativeArchiveBuilder archiveBuilder = getArchiveBuilder(campaign, campaignBuilder, creativeArchiveId);

            for (VariableUpdateRequest variableRequest : variablesToUpdate.getValue()) {
                CreativeVariableBuilder creativeVariableBuilder =
                    archiveBuilder.getCreativeVariable(campaign, variableRequest.getName());

                variableRequest.getValues().ifPresent(values -> creativeVariableBuilder.withValues(values));
                variableRequest.getScope().ifPresent(scope -> creativeVariableBuilder.withScope(
                    toScope(scope, campaign.getId().toString(), creativeArchiveId, variableRequest.getName())));
                variableRequest.getVisible().ifPresent(
                    visible -> creativeVariableBuilder.withVisible(visible.booleanValue()));
            }
        }

        try {
            Campaign updatedCampaign = campaignBuilder.save();
            HostAndPort originPublicHost = originHostService.getOriginPublicHost(authorization.getClientId());
            SimpleClient.CoreAssetsVersion coreAssetsVersion = clientCoreAssetsVersionService
                .getLatestCoreAssetsVersion(authorization.getClientId()).getCoreAssetsVersion();
            Map<Id<CreativeArchive>, List<CreativeVariableResponse>> creativeVariableResponse = new HashMap<>();

            for (Entry<Id<CreativeArchive>, List<VariableUpdateRequest>> variablesToUpdate : batchedVariables
                .entrySet()) {
                Id<CreativeArchive> creativeArchiveId = variablesToUpdate.getKey();
                CreativeArchive updatedArchive = getCreativeArchive(authorization, updatedCampaign, creativeArchiveId);

                List<CreativeVariable> updatedVariables =
                    getUpdatedVariablesList(updatedArchive, variablesToUpdate.getValue(), updatedCampaign);
                List<CreativeVariableResponse> variableResponses = new ArrayList<>();

                CampaignControllerActionCreative actionCreative =
                    actionCreativeProvider.getActionCreativeByCreativeId(campaign,
                        updatedArchive.getCreativeArchiveId().getId());

                for (CreativeVariable variable : updatedVariables) {
                    variableResponses.add(CreativeVariableRestMapper.toCreativeVariableResponse(originPublicHost,
                        authorization.getClientId(), coreAssetsVersion.getValue(), variable,
                        initBuildVersionProvider(authorization, campaign.getId(), campaign.getVersion()),
                        actionCreative.getId().getValue()));
                }
                creativeVariableResponse.put(variablesToUpdate.getKey(), variableResponses);
            }

            return creativeVariableResponse;
        } catch (CreativeArchiveSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.CREATIVE_ARCHIVE_SIZE_TOO_BIG)
                .addParameter("archive_size", Long.valueOf(e.getArchiveSize()))
                .addParameter("max_allowed_size", Long.valueOf(e.getMaxAllowedSize()))
                .withCause(e)
                .build();
        } catch (CreativeArchiveJavascriptException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.JAVASCRIPT_ERROR)
                .addParameter("output", e.getOutput())
                .addParameter("creative_id", e.getCreativeArchiveId().map(value -> value.getId().getValue()).orElse(""))
                .withCause(e)
                .build();
        } catch (CreativeVariableUnsupportedException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.CREATIVE_VARIABLE_UNSUPPORTED)
                .addParameter("creative_id", e.getCreativeId())
                .addParameter("client_id", e.getClientId())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionCreativeInvalidCreativeArchiveException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.CREATIVE_VARIABLE_UNSUPPORTED)
                .addParameter("creative_id", e.getActionId())
                .addParameter("client_id", authorization.getClientId())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    private void addCreativeVariableOutputToExtoleLogResponseHeaders(List<CreativeVariables> creativeVariables) {
        creativeVariables.stream()
            .findFirst()
            .ifPresent(variables -> addVariableOutputToExtoleLogResponseHeaders(variables.getVariables()));
    }

    private void addVariableOutputToExtoleLogResponseHeaders(List<CreativeVariable> creativeVariables) {
        creativeVariables.stream()
            .filter(variable -> !variable.getOutput().isEmpty())
            .findFirst()
            .ifPresent(variable -> addExtoleLogResponseHeaders(variable.getOutput()));
    }

    private void addExtoleLogResponseHeaders(List<String> output) {
        if (servletRequest.getHeader(EXTOLE_DEBUG_HEADER) != null) {
            output.forEach(value -> servletResponse.addHeader(EXTOLE_LOG_HEADER, value));
        }
    }

    private CampaignBuilder getCampaignBuilder(Campaign campaign, Authorization authorization,
        String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException {
        try {
            String campaignId = campaign.getId().getValue();
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

    private CreativeArchiveBuilder getArchiveBuilder(Campaign campaign, CampaignBuilder campaignBuilder,
        Id<CreativeArchive> creativeArchiveId)
        throws CreativeVariableBatchRestException {

        for (FrontendController frontendController : campaign.getFrontendControllers()) {
            for (CampaignControllerAction action : frontendController.getActions()) {
                if (action.getType() != CampaignControllerActionType.CREATIVE) {
                    continue;
                }
                CampaignControllerActionCreative actionCreative = (CampaignControllerActionCreative) action;

                Optional<CreativeArchiveId> candidate = actionCreative.getCreativeArchiveId();

                if (candidate.isPresent() && candidate.get().getId().equals(creativeArchiveId)) {
                    FrontendControllerBuilder frontendControllerBuilder =
                        campaignBuilder.updateFrontendController(frontendController);
                    CampaignControllerActionCreativeBuilder creativeActionBuilder =
                        frontendControllerBuilder.updateAction(actionCreative);
                    return creativeActionBuilder.getCreativeArchive().get();
                }
            }
        }

        throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
            .withErrorCode(CreativeVariableBatchRestException.INVALID_CREATIVE_ID)
            .addParameter("campaign_id", campaign.getId())
            .addParameter("creative_id", creativeArchiveId)
            .build();
    }

    private boolean archiveHasCreativeVariable(Campaign campaign, CreativeArchive archive, String variableName) {
        return archiveHasVariable(campaign, archive, variableName, Scope.CREATIVE);
    }

    private boolean archiveHasCampaignVariable(Campaign campaign, CreativeArchive archive, String variableName) {
        return archiveHasVariable(campaign, archive, variableName, Scope.CAMPAIGN);
    }

    private boolean archiveHasVariable(Campaign campaign, CreativeArchive archive, String variableName,
        Scope variableScope) {
        try {
            return creativeVariableService.getCreativeVariable(campaign, archive.getCreativeArchiveId(), variableName)
                .getScope() == variableScope;
        } catch (CreativeVariableServiceInvalidNameException e) {
            return false;
        }
    }

    private Map<BuiltCampaignControllerActionCreative, List<VariableUpdateRequest>>
        getCreativeVariablesToUpdateGroupedByCreativeAction(
            Authorization userAuthorization, Campaign campaign, List<CreativeVariableUpdateRequest> request)
            throws CreativeVariableBatchRestException, BuildCampaignRestException {
        validateVariablesCreativeArchiveId(request);
        validateVariablesVariableName(request);

        BuiltCampaign builtCampaign = campaignProvider.buildCampaign(campaign);
        List<VariableUpdateRequest> allVariablesNotPresentInArchive = new ArrayList<>();
        Map<BuiltCampaignControllerActionCreative, List<VariableUpdateRequest>> batchUpdateVariables = new HashMap<>();

        List<BuiltCampaignControllerActionCreative> allCreativeActions =
            builtCampaign.getFrontendControllers()
                .stream().flatMap(controller -> controller.getActions().stream()
                    .filter(value -> value.getType() == CampaignControllerActionType.CREATIVE)
                    .map(BuiltCampaignControllerActionCreative.class::cast))
                .collect(toUnmodifiableList());

        for (BuiltCampaignControllerActionCreative actionCreative : allCreativeActions) {
            if (actionCreative.getCreativeArchiveId().isEmpty()) {
                continue;
            }

            List<CreativeVariableUpdateRequest> variablesToUpdate = request.stream()
                .filter(updateVariableRequest -> Objects.equals(updateVariableRequest.getCreativeActionId().getValue(),
                    actionCreative.getId().getValue()))
                .collect(Collectors.toList());

            if (!variablesToUpdate.isEmpty()) {
                List<VariableUpdateRequest> variableUpdateRequests = variablesToUpdate.stream()
                    .map(value -> {
                        VariableUpdateRequest.Builder builder = VariableUpdateRequest.builder()
                            .withName(value.getName());
                        value.getScope().ifPresent(scope -> builder.withScope(scope));
                        value.getValues().ifPresent(values -> builder.withValues(values));
                        value.getVisible()
                            .ifPresent(visible -> builder.withVisible(visible.booleanValue()));

                        return builder.build();
                    }).collect(toUnmodifiableList());

                Optional<CreativeArchive> archive =
                    getCreativeArchive(userAuthorization, actionCreative.getCreativeArchiveId()
                        .map(id -> new CreativeArchiveId(id.getId(), id.getVersion())));

                if (archive.isPresent()) {
                    List<VariableUpdateRequest> variablesNotInArchive =
                        getVariablesNotPresentInArchive(archive.get(), variableUpdateRequests, campaign);
                    if (variablesNotInArchive.isEmpty()) {
                        batchUpdateVariables.put(actionCreative, variableUpdateRequests);
                    }

                    allVariablesNotPresentInArchive.addAll(variablesNotInArchive);
                }
            }
        }

        if (!allVariablesNotPresentInArchive.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.VARIABLES_NOT_FOUND)
                .addParameter("scope", Arrays.asList(Scope.CREATIVE, Scope.CAMPAIGN))
                .addParameter("variables_names",
                    allVariablesNotPresentInArchive.stream()
                        .map(value -> value.getName())
                        .collect(Collectors.joining(", ")))
                .build();
        }

        return batchUpdateVariables;
    }

    private Map<BuiltCampaignControllerActionCreative, List<VariableUpdateRequest>>
        getZoneVariablesToUpdateGroupedByCreativeAction(
            Authorization userAuthorization, Campaign campaign, List<ZoneCreativeVariableUpdateRequest> request)
            throws CreativeVariableBatchRestException, BuildCampaignRestException {
        validateVariablesZoneName(request);
        validateZoneVariablesVariableName(request);

        BuiltCampaign builtCampaign = campaignProvider.buildCampaign(campaign);
        List<VariableUpdateRequest> allVariablesNotPresentInArchive = new ArrayList<>();
        Map<BuiltCampaignControllerActionCreative, List<VariableUpdateRequest>> batchUpdateVariables = new HashMap<>();

        List<Triple<BuiltFrontendController, BuiltCampaignControllerActionCreative, String>> allCreativeActions =
            builtCampaign.getFrontendControllers()
                .stream().flatMap(controller -> controller.getActions().stream()
                    .filter(value -> value.getType() == CampaignControllerActionType.CREATIVE)
                    .map(BuiltCampaignControllerActionCreative.class::cast)
                    .map(action -> {
                        Optional<String> eventName = lookupForEventName(controller);
                        return Triple.of(controller, action, eventName);
                    }))
                .filter(triple -> triple.getRight().isPresent())
                .map(triple -> Triple.of(triple.getLeft(), triple.getMiddle(), triple.getRight().get()))
                .collect(toUnmodifiableList());
        Map<String, Integer> eventNamesOccurrences = new HashMap<>();
        allCreativeActions.forEach(pair -> {
            eventNamesOccurrences.compute(pair.getRight(), (key, value) -> {
                if (value == null) {
                    value = Integer.valueOf(1);
                } else {
                    value = Integer.valueOf(value.intValue() + 1);
                }
                return value;
            });
        });

        for (Triple<BuiltFrontendController, BuiltCampaignControllerActionCreative,
            String> triple : allCreativeActions) {
            BuiltFrontendController frontendController = triple.getLeft();
            BuiltCampaignControllerActionCreative actionCreative = triple.getMiddle();
            String eventName = triple.getRight();
            if (actionCreative.getCreativeArchiveId().isEmpty()) {
                continue;
            }

            boolean isSharedZoneName = eventNamesOccurrences.get(eventName).intValue() > 1;

            List<ZoneCreativeVariableUpdateRequest> variablesToUpdate = request.stream()
                .filter(updateVariableRequest -> updateVariableRequest.getZoneName().isPresent())
                .filter(updateVariableRequest -> eventName.equals(updateVariableRequest.getZoneName().getValue())
                    && actionCreative.getId().getValue().equals(updateVariableRequest.getCreativeActionId().getValue()))
                .filter(updateVariableRequest -> !isSharedZoneName
                    || (updateVariableRequest.getJourneyNames().isPresent()
                        && frontendController.getJourneyNames().stream()
                            .map(value -> value.getValue())
                            .collect(Collectors.toUnmodifiableSet())
                            .equals(updateVariableRequest.getJourneyNames().getValue())))
                .collect(toUnmodifiableList());

            if (variablesToUpdate.isEmpty()) {
                continue;
            }

            List<VariableUpdateRequest> variableUpdateRequests = variablesToUpdate.stream()
                .map(value -> {
                    Builder builder = VariableUpdateRequest.builder()
                        .withName(value.getName());

                    value.getScope().ifPresent(scope -> builder.withScope(scope));
                    value.getValues().ifPresent(values -> builder.withValues(values));
                    value.getVisible().ifPresent(visible -> builder.withVisible(visible.booleanValue()));

                    return builder.build();
                })
                .collect(toUnmodifiableList());

            Optional<CreativeArchive> archive =
                getCreativeArchive(userAuthorization, actionCreative.getCreativeArchiveId()
                    .map(value -> new CreativeArchiveId(value.getId(), value.getVersion())));

            if (archive.isPresent()) {
                List<VariableUpdateRequest> variablesNotInArchive =
                    getVariablesNotPresentInArchive(archive.get(), variableUpdateRequests, campaign);
                if (variablesNotInArchive.isEmpty()) {
                    batchUpdateVariables.put(actionCreative, variableUpdateRequests);
                }

                allVariablesNotPresentInArchive.addAll(variablesNotInArchive);
            }
        }

        if (!allVariablesNotPresentInArchive.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.VARIABLES_NOT_FOUND)
                .addParameter("scope", Arrays.asList(Scope.CREATIVE, Scope.CAMPAIGN))
                .addParameter("variables_names",
                    allVariablesNotPresentInArchive.stream()
                        .map(variableUpdateRequest -> variableUpdateRequest.getName())
                        .collect(Collectors.joining(", ")))
                .build();
        }

        return batchUpdateVariables;
    }

    private Optional<String> lookupForEventName(BuiltFrontendController builtFrontendController) {
        return builtFrontendController.getTriggers().stream()
            .filter(trigger -> trigger.getType() == CampaignControllerTriggerType.EVENT)
            .map(BuiltCampaignControllerTriggerEvent.class::cast)
            .filter(trigger -> trigger.getEventType() == CampaignControllerTriggerEventType.INPUT)
            .filter(trigger -> !trigger.getEventNames().isEmpty())
            .flatMap(trigger -> trigger.getEventNames().stream())
            .findFirst();
    }

    private void validateVariablesCreativeArchiveId(List<CreativeVariableUpdateRequest> request)
        throws CreativeVariableBatchRestException {
        List<CreativeVariableUpdateRequest> variablesWithoutCreativeArchiveId = request.stream()
            .filter(updateVariableRequest -> updateVariableRequest.getCreativeArchiveId().isOmitted())
            .collect(toUnmodifiableList());
        boolean hasAtLeastOneActionCreativeId = request.stream()
            .anyMatch(value -> value.getCreativeActionId().isPresent());

        if (!variablesWithoutCreativeArchiveId.isEmpty() && !hasAtLeastOneActionCreativeId) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.MISSING_CREATIVE_ARCHIVE_ID)
                .addParameter("variables_names",
                    variablesWithoutCreativeArchiveId.stream()
                        .map(value -> value.getName())
                        .distinct()
                        .collect(Collectors.joining(", ")))
                .build();
        }
    }

    private void validateVariablesVariableName(List<CreativeVariableUpdateRequest> request)
        throws CreativeVariableBatchRestException {
        List<CreativeVariableUpdateRequest> variablesWithoutVariableName = request.stream()
            .filter(updateVariableRequest -> Strings.isNullOrEmpty(updateVariableRequest.getName()))
            .collect(toUnmodifiableList());

        if (!variablesWithoutVariableName.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.MISSING_VARIABLE_NAME)
                .build();
        }
    }

    private void validateVariablesZoneName(List<ZoneCreativeVariableUpdateRequest> request)
        throws CreativeVariableBatchRestException {
        List<ZoneCreativeVariableUpdateRequest> variablesWithoutZoneName = request.stream()
            .filter(updateVariableRequest -> updateVariableRequest.getZoneName().isOmitted())
            .collect(toUnmodifiableList());

        if (!variablesWithoutZoneName.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.MISSING_ZONE_NAME)
                .addParameter("variables_names",
                    variablesWithoutZoneName.stream()
                        .map(value -> value.getName())
                        .distinct()
                        .collect(Collectors.joining(", ")))
                .build();
        }
    }

    private void validateZoneVariablesVariableName(List<ZoneCreativeVariableUpdateRequest> request)
        throws CreativeVariableBatchRestException {
        List<ZoneCreativeVariableUpdateRequest> variablesWithoutVariableName = request.stream()
            .filter(updateVariableRequest -> Strings.isNullOrEmpty(updateVariableRequest.getName()))
            .collect(toUnmodifiableList());

        if (!variablesWithoutVariableName.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.MISSING_VARIABLE_NAME)
                .build();
        }
    }

    private List<VariableUpdateRequest> getVariablesNotPresentInArchive(CreativeArchive archive,
        List<VariableUpdateRequest> variablesToUpdate, Campaign campaign) {
        List<VariableUpdateRequest> notPresentVariables = new ArrayList<>();

        for (VariableUpdateRequest variable : variablesToUpdate) {
            if (!archiveHasCreativeVariable(campaign, archive, variable.getName())
                && !archiveHasCampaignVariable(campaign, archive, variable.getName())) {
                notPresentVariables.add(variable);
            }
        }

        return notPresentVariables;
    }

    private List<CreativeVariable> getUpdatedVariablesList(CreativeArchive updatedArchive,
        List<VariableUpdateRequest> variablesToUpdate, Campaign campaign)
        throws CreativeVariableServiceInvalidNameException {
        List<CreativeVariable> updatedVariables = new ArrayList<>();
        List<CreativeVariable> creativeVariables =
            creativeVariableService.getCreativeVariables(campaign, updatedArchive.getCreativeArchiveId());

        for (VariableUpdateRequest updateRequest : variablesToUpdate) {
            String variableName = updateRequest.getName();
            CreativeVariable creativeVariable = creativeVariables.stream()
                .filter(variable -> variableName.equals(variable.getName())).findFirst()
                .orElseThrow(() -> new CreativeVariableServiceInvalidNameException(
                    "Failed to get variable with name: " + variableName));
            updatedVariables.add(creativeVariable);
        }

        return updatedVariables;
    }

    private Scope toScope(CreativeVariableScope scope, String campaignId, Id<CreativeArchive> creativeArchiveId,
        String variableName) {
        switch (scope) {
            case CAMPAIGN:
                return Scope.CAMPAIGN;
            case CREATIVE:
                return Scope.CREATIVE;
            default:
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class).withCause(
                    new RuntimeException("Unable to convert CreativeVariableScope " + scope.toString() +
                        " to Scope for campaignId " + campaignId + " creativeArchiveId " + creativeArchiveId +
                        " variableName " + variableName))
                    .build();
        }
    }

    private List<CreativeVariableResponse> toCreativeVariableResponse(Authorization authorization,
        CreativeVariables creativeVariables, SimpleClient.CoreAssetsVersion coreAssetsVersion,
        Id<Campaign> campaignId, Integer version) throws BuildCampaignRestException, CampaignRestException {
        return CreativeVariableRestMapper.toCreativeVariablesResponse(
            originHostService.getOriginPublicHost(authorization.getClientId()), authorization.getClientId(),
            coreAssetsVersion.getValue(), creativeVariables,
            initBuildVersionProvider(authorization, campaignId, version));
    }

    private CreativeVariablesFormat getFormat(String format, String contentType) {
        if (!Strings.isNullOrEmpty(format)) {
            String extension = format.substring(1);
            if (CreativeVariablesFormat.XLSX.getExtension().equalsIgnoreCase(extension)) {
                return CreativeVariablesFormat.XLSX;
            }
            if (CreativeVariablesFormat.CSV.getExtension().equalsIgnoreCase(extension)) {
                return CreativeVariablesFormat.CSV;
            }
        } else if (!Strings.isNullOrEmpty(contentType)) {
            return CreativeVariablesFormat.fromMimeType(contentType);
        }

        return CreativeVariablesFormat.JSON;
    }

    private boolean variableFilter(CreativeVariableResponse variable, String zoneName,
        Optional<CreativeVariableResponse.Type> creativeVariableType, String tags) {
        if (variable.getScope() == CreativeVariableScope.CAMPAIGN
            && !zoneName.trim().equalsIgnoreCase("campaign_image")) {
            return false;
        }

        if (creativeVariableType.isPresent() && !variable.getType().equals(creativeVariableType.get())) {
            return false;
        }

        if (!Strings.isNullOrEmpty(tags)) {
            if (variable.getTags().length == 0) {
                return false;
            }
            List<String> variableTags =
                Arrays.stream(variable.getTags()).map(String::toLowerCase)
                    .collect(Collectors.toList());
            List<String> queryParamTags =
                Arrays.stream(tags.split(",")).map(String::toLowerCase)
                    .collect(Collectors.toList());

            return !Collections.disjoint(variableTags, queryParamTags);
        }

        return true;
    }

    private String getFileExtension(Path path) {
        String file = path.getFileName().toString();
        return FilenameUtils.getExtension(file);
    }

    private Optional<CreativeVariableResponse.Type> parseToCreativeVariableType(String type)
        throws CreativeVariableBatchRestException {
        if (Strings.isNullOrEmpty(type)) {
            return Optional.empty();
        }
        try {
            return Optional.of(CreativeVariableResponse.Type.valueOf(type.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.INVALID_VARIABLES_TYPE)
                .addParameter("type", type)
                .withCause(e)
                .build();
        }
    }

    private CreativeArchive getCreativeArchive(Authorization authorization, Campaign campaign,
        Id<CreativeArchive> creativeArchiveIdValue) throws CreativeVariableBatchRestException {
        try {
            CreativeArchiveId creativeArchiveId = findCreativeArchiveId(campaign, creativeArchiveIdValue);
            return creativeArchiveService.getCreativeArchive(authorization, creativeArchiveId);
        } catch (CreativeArchiveNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Optional<CreativeArchive> getCreativeArchive(Authorization authorization,
        Optional<CreativeArchiveId> creativeArchiveId) {
        return creativeArchiveId
            .flatMap(id -> {
                try {
                    return Optional.of(creativeArchiveService.getCreativeArchive(authorization, id));
                } catch (CreativeArchiveNotFoundException e) {
                    return Optional.empty();
                }
            });
    }

    private CreativeVariableZoneState parseZoneState(String zoneState) throws CreativeVariableBatchRestException {
        try {
            if (StringUtils.isEmpty(zoneState)) {
                return CreativeVariableZoneState.ANY;
            }
            return CreativeVariableZoneState.valueOf(StringUtils.upperCase(zoneState));
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
                .withErrorCode(CreativeVariableBatchRestException.INVALID_ZONE_STATE)
                .addParameter("current_value", zoneState)
                .addParameter("allowed_values", CreativeVariableZoneState.values())
                .withCause(e)
                .build();
        }
    }

    private Function<CreativeArchiveId, Integer> initBuildVersionProvider(Authorization authorization,
        Id<Campaign> campaignId, Integer version) throws CampaignRestException, BuildCampaignRestException {
        Map<CreativeArchiveId, Optional<Integer>> buildVersions =
            campaignProvider.getBuiltCampaign(authorization, campaignId, version.toString())
                .getFrontendControllers()
                .stream()
                .flatMap(frontendController -> frontendController.getActions().stream())
                .filter(value -> value.getType() == CampaignControllerActionType.CREATIVE)
                .map(action -> (BuiltCampaignControllerActionCreative) action)
                .filter(action -> action.getCreativeArchiveId().isPresent())
                .collect(toUnmodifiableMap(
                    action -> new CreativeArchiveId(action.getCreativeArchiveId().get().getId(),
                        action.getCreativeArchiveId().get().getVersion()),
                    action -> action.getCreativeArchiveId().get().getBuildVersion()));

        return (id) -> buildVersions.getOrDefault(id, Optional.empty()).orElse(null);
    }

    private List<Map<String, String>> getVariablesMapFromFile(FileInputStreamRequest request)
        throws IOException, FileFormatRestException {
        String fileExtension = getFileExtension(Paths.get(request.getAttributes().getFileName()));
        try {
            return creativeBatchVariableValuesFileParserFactory
                .getParser(fileExtension)
                .parse(request.getInputStream());
        } catch (UnsupportedFileFormatException e) {
            throw RestExceptionBuilder.newBuilder(FileFormatRestException.class)
                .withErrorCode(FileFormatRestException.UNSUPPORTED_FILE_FORMAT)
                .addParameter("file_extension", fileExtension)
                .withCause(e)
                .build();
        }
    }

    private List<ZoneCreativeVariableUpdateRequest>
        toZoneVariableUpdateRequests(List<Map<String, String>> variablesMap) {
        List<ZoneCreativeVariableUpdateRequest> request = new ArrayList<>();

        variablesMap.forEach(variableMapValue -> {
            ZoneCreativeVariableUpdateRequest.Builder builder = ZoneCreativeVariableUpdateRequest.builder();
            Map<String, String> values = new HashMap<>();

            for (Map.Entry<String, String> entry : variableMapValue.entrySet()) {
                switch (entry.getKey()) {
                    case "zone":
                        builder.withZoneName(entry.getValue());
                        break;
                    case "name":
                        builder.withName(entry.getValue());
                        break;
                    case "journeyNames":
                        Set<String> journeyNames = Sets.newHashSet(entry.getValue().split(","));
                        builder.withJourneyNames(journeyNames);
                        break;
                    default:
                        values.put(entry.getKey(), entry.getValue());
                }
            }
            builder.withValues(values);
            request.add(builder.build());
        });

        return request;
    }

    private CreativeArchiveId findCreativeArchiveId(Campaign campaign, Id<CreativeArchive> creativeArchiveId)
        throws CreativeVariableBatchRestException {

        Optional<CreativeArchiveId> creativeArchiveIdOptional =
            findCreativeArchiveIdFromActions(campaign, creativeArchiveId);
        if (creativeArchiveIdOptional.isPresent()) {
            return creativeArchiveIdOptional.get();
        }

        throw RestExceptionBuilder.newBuilder(CreativeVariableBatchRestException.class)
            .withErrorCode(CreativeVariableBatchRestException.INVALID_CREATIVE_ID)
            .addParameter("creative_id", creativeArchiveId)
            .build();
    }

    private Optional<CreativeArchiveId> findCreativeArchiveIdFromActions(Campaign campaign,
        Id<CreativeArchive> creativeArchiveId) {
        for (FrontendController frontendController : campaign.getFrontendControllers()) {
            for (CampaignControllerAction action : frontendController.getActions()) {
                if (action.getType() != CampaignControllerActionType.CREATIVE) {
                    continue;
                }
                CampaignControllerActionCreative actionCreative = (CampaignControllerActionCreative) action;
                if (actionCreative.getCreativeArchiveId().isPresent() &&
                    actionCreative.getCreativeArchiveId().get().getId().equals(creativeArchiveId)) {
                    return actionCreative.getCreativeArchiveId();
                }
            }
        }

        return Optional.empty();
    }

}
