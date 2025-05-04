package com.extole.client.rest.impl.campaign.component.setting;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.CampaignComponentResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentRootValidationRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentDuplicateRestException;
import com.extole.client.rest.campaign.component.ComponentRestException;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingEndpoints;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingUpdateRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSocketAddComponentRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.TranslatableVariableExceptionMapper;
import com.extole.client.rest.impl.campaign.component.CampaignComponentProvider;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.Component;
import com.extole.model.entity.campaign.ComponentOwner;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;
import com.extole.model.entity.campaign.built.BuiltCampaignComponentAsset;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.entity.campaign.built.BuiltVariable;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.CampaignComponentDescriptionLengthException;
import com.extole.model.service.campaign.component.CampaignComponentDisplayNameLengthException;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.component.CampaignComponentIllegalCharacterInNameException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentNameLengthException;
import com.extole.model.service.campaign.component.CampaignComponentRootRenameException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.ComponentDuplicateBuilder;
import com.extole.model.service.campaign.component.ComponentDuplicationException;
import com.extole.model.service.campaign.component.ComponentInstallFailedException;
import com.extole.model.service.campaign.component.ComponentNotFoundException;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.campaign.component.ComponentSocketFilterTypeMismatchException;
import com.extole.model.service.campaign.component.ComponentSocketMissingRequiredParameterException;
import com.extole.model.service.campaign.component.ComponentSocketNotFoundException;
import com.extole.model.service.campaign.component.InvalidCampaignComponentInstalledIntoSocketException;
import com.extole.model.service.campaign.component.MissingSourceComponentTypeException;
import com.extole.model.service.campaign.component.MissingTargetComponentByAbsoluteNameException;
import com.extole.model.service.campaign.component.RootComponentDuplicationException;
import com.extole.model.service.campaign.component.UniqueComponentElementRequiredException;
import com.extole.model.service.campaign.component.anchor.AmbiguousFallbackComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.InvalidComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.MissingComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.MissingFallbackComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.UnrecognizedComponentAnchorsException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentMissingException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetContentSizeTooBigException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetDescriptionLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetFilenameLengthException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameInvalidException;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetNameLengthException;
import com.extole.model.service.campaign.component.asset.ComponentAsset;
import com.extole.model.service.campaign.component.asset.ComponentAssetNotFoundException;
import com.extole.model.service.campaign.component.asset.ComponentAssetService;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.setting.CampaignComponentSettingException;
import com.extole.model.service.campaign.setting.ComponentBuildSettingException;
import com.extole.model.service.campaign.setting.InvalidVariableTranslatableValueException;
import com.extole.model.service.campaign.setting.SettingBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameDuplicateException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingNameMissingException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.SettingValidationException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentTypeException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentTypeException;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.campaign.setting.VariableValueMissingException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveIncompatibleApiVersionException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;

@Provider
public class CampaignComponentSettingEndpointsImpl implements CampaignComponentSettingEndpoints {

    private final CampaignService campaignService;
    private final ComponentService componentService;
    private final ComponentAssetService componentAssetService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final CampaignComponentProvider campaignComponentProvider;
    private final CampaignComponentSettingProvider campaignComponentSettingProvider;
    private final SettingRequestMapperRepository settingRequestMapperRepository;
    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignComponentSettingEndpointsImpl(CampaignService campaignService,
        ComponentService componentService,
        ComponentAssetService componentAssetService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignComponentSettingRestMapper settingRestMapper,
        CampaignComponentProvider campaignComponentProvider,
        CampaignComponentSettingProvider campaignComponentSettingProvider,
        SettingRequestMapperRepository settingRequestMapperRepository,
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignService = campaignService;
        this.componentService = componentService;
        this.componentAssetService = componentAssetService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.settingRestMapper = settingRestMapper;
        this.campaignComponentProvider = campaignComponentProvider;
        this.campaignComponentSettingProvider = campaignComponentSettingProvider;
        this.settingRequestMapperRepository = settingRequestMapperRepository;
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignComponentSettingResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        CampaignComponentSettingRequest request)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignBuilder campaignBuilder =
                getCampaignBuilder(campaign.getId().getValue(), authorization, expectedCurrentVersion);
            CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
            CampaignComponentBuilder campaignComponentBuilder =
                campaignBuilder.updateComponent(campaignComponent);
            SettingBuilder settingBuilder;
            if (request.getType() != null) {
                settingBuilder = campaignComponentBuilder
                    .addSetting(SettingType.valueOf(request.getType().name()));
            } else {
                settingBuilder = campaignComponentBuilder.addSetting();
            }

            validateExplicitValuesForInheritedVariableShouldNotBePresent(request, Optional.empty());
            applyRequestToBuilder(request, settingBuilder);

            return settingRestMapper.toSettingResponse(settingBuilder.save());
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
        } catch (SettingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
                .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                .withCause(e)
                .build();
        } catch (VariableValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VARIABLE_VALUE_MISSING)
                .addParameter("name", e.getName())
                .addParameter("details", e.getDescription())
                .withCause(e)
                .build();
        } catch (SettingInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.RESERVED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (SettingDisplayNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("display_name", e.getDisplayName())
                .addParameter("min_length", Integer.valueOf(e.getDisplayNameMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getDisplayNameMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingIllegalCharacterInDisplayNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DISPLAY_NAME_HAS_ILLEGAL_CHARACTER)
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
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
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (VariableValueKeyLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VALUE_KEY_LENGTH_OUT_OF_RANGE)
                .addParameter("value_key", e.getValueKey())
                .addParameter("min_length", Integer.valueOf(e.getValueKeyMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getValueKeyMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingTagLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.TAG_LENGTH_OUT_OF_RANGE)
                .addParameter("invalid_tag", e.getTag())
                .addParameter("max_length", Integer.valueOf(e.getTagMaxLength()))
                .addParameter("min_length", Integer.valueOf(e.getTagMinLength()))
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            Map<String, RestExceptionResponse> exceptionResponses = Maps.newHashMap();
            e.getSuppressedExceptions().forEach((settingName, buildException) -> {
                RestException restException =
                    settingRestMapper.mapToBuildSettingRestException(buildException, e.getEntityId());
                exceptionResponses.put(settingName, new RestExceptionResponseBuilder(restException).build());
            });
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.SETTINGS_BUILD_FAILED)
                .addParameter("errors", exceptionResponses)
                .withCause(e)
                .build();
        } catch (CampaignComponentSettingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", StringUtils.EMPTY)
                .addParameter("details", e.getMessage())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentSettingResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        String settingName,
        CampaignComponentSettingUpdateRequest request)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException, SettingRestException,
        ComponentRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignBuilder campaignBuilder =
                getCampaignBuilder(campaign.getId().getValue(), authorization, expectedCurrentVersion);
            CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
            CampaignComponentBuilder campaignComponentBuilder = campaignBuilder.updateComponent(campaignComponent);
            Setting setting = campaignComponentSettingProvider.getCampaignComponentSetting(campaign,
                componentId, settingName);
            SettingBuilder settingBuilder = campaignComponentBuilder.updateSetting(setting);
            if (request.getType() != null) {
                settingBuilder.withType(SettingType.valueOf(request.getType().name()));
            }

            validateExplicitValuesForInheritedVariableShouldNotBePresent(request, Optional.of(setting));

            applyRequestToBuilder(request, settingBuilder);

            if (variableSourceIsChangedFromLocalToInherited(request, setting)) {
                CampaignComponentVariableUpdateRequest variableRequest =
                    (CampaignComponentVariableUpdateRequest) request;
                Variable variable = (Variable) setting;

                BuiltCampaign builtCampaign = campaignProvider
                    .getBuiltCampaign(authorization, Id.valueOf(campaignId), String.valueOf(campaign.getVersion()));
                Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                    RuntimeEvaluatable<Object, Optional<Object>>>> values =
                        Maps.newLinkedHashMap(variableRequest.getValues().orElse(variable.getValues()));

                BuiltCampaignComponent builtComponent = lookupComponent(builtCampaign, Id.valueOf(componentId)).get();
                BuiltVariable builtVariable =
                    builtComponent.getSettings().stream().filter(value -> value.getName().equals(settingName))
                        .map(candidate -> (BuiltVariable) candidate)
                        .findFirst().get();
                Id<CampaignComponent> sourceComponentId = builtVariable.getSourceComponentId();
                Component component = extractFromCampaignOrElseLookupForExternalComponent(authorization,
                    campaign, sourceComponentId);
                boolean sourceRefersToOtherCampaign = !component.getCampaign().getId().equals(builtCampaign.getId());
                BuiltCampaign sourceBuiltCampaign = sourceRefersToOtherCampaign
                    ? campaignProvider.getBuiltCampaign(authorization, component.getCampaign().getId(),
                        "published")
                    : builtCampaign;

                List<PulledAssetFromSourceComponent> sourceAssets = pullAssetsFromSourceComponent(
                    sourceBuiltCampaign, sourceComponentId, builtCampaign, campaignComponent);

                values = modifyValuesConsideringSourceAssets(authorization, sourceAssets, builtVariable, values,
                    () -> campaignComponentBuilder.addAsset());

                ((VariableBuilder) settingBuilder).withValues(values);
            }

            return settingRestMapper.toSettingResponse(settingBuilder.save());
        } catch (StaleCampaignVersionException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.STALE_VERSION)
                .addParameter("actual_version", e.getActualCampaignVersion())
                .addParameter("expected_version", e.getExpectedCampaignVersion())
                .addParameter("campaign_id", e.getCampaignId())
                .withCause(e).build();
        } catch (VariableValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VARIABLE_VALUE_MISSING)
                .addParameter("name", e.getName())
                .addParameter("details", e.getDescription())
                .withCause(e)
                .build();
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (SettingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.RESERVED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (SettingDisplayNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("display_name", e.getDisplayName())
                .addParameter("min_length", Integer.valueOf(e.getDisplayNameMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getDisplayNameMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingIllegalCharacterInDisplayNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DISPLAY_NAME_HAS_ILLEGAL_CHARACTER)
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
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
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (VariableValueKeyLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VALUE_KEY_LENGTH_OUT_OF_RANGE)
                .addParameter("value_key", e.getValueKey())
                .addParameter("min_length", Integer.valueOf(e.getValueKeyMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getValueKeyMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingTagLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.TAG_LENGTH_OUT_OF_RANGE)
                .addParameter("invalid_tag", e.getTag())
                .addParameter("max_length", Integer.valueOf(e.getTagMaxLength()))
                .addParameter("min_length", Integer.valueOf(e.getTagMinLength()))
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            Map<String, RestExceptionResponse> exceptionResponses = Maps.newHashMap();
            e.getSuppressedExceptions().forEach((name, buildException) -> {
                RestException restException =
                    settingRestMapper.mapToBuildSettingRestException(buildException, e.getEntityId());
                exceptionResponses.put(name, new RestExceptionResponseBuilder(restException).build());
            });
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.SETTINGS_BUILD_FAILED)
                .addParameter("errors", exceptionResponses)
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentSettingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", StringUtils.EMPTY)
                .addParameter("details", e.getMessage())
                .withCause(e)
                .build();
        } catch (AuthorizationException | ComponentAssetNotFoundException | CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Component extractFromCampaignOrElseLookupForExternalComponent(Authorization authorization,
        Campaign campaign,
        Id<CampaignComponent> sourceComponentId) throws UserAuthorizationRestException, ComponentRestException {

        Optional<CampaignComponent> sourceComponentFromTheCurrentCampaign =
            campaign.getComponents().stream().filter(component -> component.getId().equals(sourceComponentId))
                .findFirst();

        if (sourceComponentFromTheCurrentCampaign.isPresent()) {
            return new ComponentImpl(campaign, sourceComponentFromTheCurrentCampaign.get());
        } else {
            return getComponent(authorization, sourceComponentId);
        }
    }

    private Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        modifyValuesConsideringSourceAssets(
            Authorization authorization,
            List<PulledAssetFromSourceComponent> sourceAssets,
            BuiltVariable currentBuiltVariable,
            Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>> initialValues,
            Supplier<CampaignComponentAssetBuilder> createAssetSupplier)
            throws AuthorizationException, ComponentAssetNotFoundException, CampaignComponentAssetNameInvalidException,
            CampaignComponentAssetNameLengthException, CampaignComponentAssetContentMissingException,
            CampaignComponentAssetContentSizeTooBigException, CampaignComponentAssetFilenameInvalidException,
            CampaignComponentAssetFilenameLengthException, CampaignComponentAssetDescriptionLengthException {

        Map<String, String> filteredProvidedValues =
            currentBuiltVariable.getSourcedValues().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Provided)
                .map(entry -> Pair.of(entry.getKey(), (Provided<Object, Optional<Object>>) entry.getValue()))
                .filter(value -> value.getRight().getValue().isPresent()
                    && value.getRight().getValue().get() instanceof String)
                .map(pair -> Pair.of(pair.getLeft(), pair.getRight().getValue().get()))
                .collect(
                    Collectors.toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight().toString()));

        Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> resultValues =
                Maps.newLinkedHashMap(initialValues);

        for (Map.Entry<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> entry : initialValues
                .entrySet()) {

            boolean isProvidedAndNotOriginUrl = filteredProvidedValues.containsKey(entry.getKey()) &&
                !filteredProvidedValues.get(entry.getKey()).contains("origin");
            if (isProvidedAndNotOriginUrl) {
                resultValues.put(entry.getKey(),
                    Provided.nestedOptionalOf(filteredProvidedValues.get(entry.getKey())));
            }
        }

        for (PulledAssetFromSourceComponent pulledAsset : sourceAssets) {
            ComponentAsset assetWithContent =
                componentAssetService.get(authorization, pulledAsset.getBuiltAsset().getId(),
                    pulledAsset.getCampaignVersion());

            BuiltCampaignComponentAsset builtCampaignComponentAsset = pulledAsset.getBuiltAsset();
            CampaignComponentAssetBuilder assetBuilder = createAssetSupplier.get()
                .withName(builtCampaignComponentAsset.getName())
                .withContent(assetWithContent.getContent())
                .withFilename(builtCampaignComponentAsset.getFilename())
                .withTags(builtCampaignComponentAsset.getTags());

            if (!pulledAsset.getOldName().equals(pulledAsset.getNewName())) {
                assetBuilder.withName(pulledAsset.getNewName());

                initialValues.entrySet().stream()
                    .filter(entry -> !(entry.getValue() instanceof Provided))
                    .forEach(entry -> {
                        BuildtimeEvaluatable<VariableBuildtimeContext,
                            RuntimeEvaluatable<Object, Optional<Object>>> value =
                                entry.getValue();
                        String serialized = ObjectMapperProvider.getConfiguredInstance()
                            .convertValue(value, String.class);
                        if (serialized.contains(builtCampaignComponentAsset.getName())) {
                            value = deserialize(
                                serialized.replaceAll(builtCampaignComponentAsset.getName(), pulledAsset.getNewName()),
                                new TypeReference<>() {});
                            resultValues.put(entry.getKey(), value);
                        }
                    });
            }

            if (builtCampaignComponentAsset.getDescription().isPresent()) {
                assetBuilder.withDescription(builtCampaignComponentAsset.getDescription().get());
            }
        }

        return Collections.unmodifiableMap(resultValues);
    }

    private List<PulledAssetFromSourceComponent> pullAssetsFromSourceComponent(
        BuiltCampaign sourceBuiltCampaign,
        Id<CampaignComponent> sourceComponentId,
        BuiltCampaign currentBuiltCampaign,
        CampaignComponent currentCampaignComponent) {
        BuiltCampaignComponent sourceBuiltComponent = sourceBuiltCampaign.getComponents().stream()
            .filter(value -> value.getId().equals(sourceComponentId)).findFirst().get();
        List<BuiltCampaignComponentAsset> variableSourceAssets = sourceBuiltComponent.getAssets();

        Set<String> existingAssetsNames = currentCampaignComponent.getAssets().stream().map(asset -> asset.getName())
            .collect(Collectors.toUnmodifiableSet());

        return variableSourceAssets.stream().map(builtAsset -> {

            return new PulledAssetFromSourceComponent() {
                private final String newName = incrementNameIfNeeded(builtAsset.getName(),
                    existingAssetsNames::contains);

                @Override
                public String getOldName() {
                    return builtAsset.getName();
                }

                @Override
                public String getNewName() {
                    return newName;
                }

                @Override
                public BuiltCampaignComponentAsset getBuiltAsset() {
                    return builtAsset;
                }

                @Override
                public Integer getCampaignVersion() {
                    return sourceBuiltCampaign.getVersion();
                }
            };
        }).collect(Collectors.toUnmodifiableList());
    }

    private String incrementNameIfNeeded(String name, Predicate<String> hasCollisionPredicate) {
        if (!hasCollisionPredicate.test(name)) {
            return name;
        }

        String incrementedName = name + "_copy";

        if (hasCollisionPredicate.test(incrementedName)) {
            int i;
            for (i = 1; hasCollisionPredicate.test(incrementedName + "_" + i);) {
                i++;
            }

            return incrementedName + "_" + i;
        }

        return incrementedName;
    }

    public static <T> T deserialize(String serialized, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = ObjectMapperProvider.getConfiguredInstance();
            return objectMapper.readValue(objectMapper.writeValueAsString(serialized), typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Component getComponent(Authorization authorization, Id<CampaignComponent> sourceComponentId)
        throws UserAuthorizationRestException, ComponentRestException {
        try {
            return componentService.get(authorization, sourceComponentId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", sourceComponentId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentSettingResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        String settingName)
        throws UserAuthorizationRestException, BuildCampaignRestException, SettingValidationRestException,
        CampaignUpdateRestException, CampaignRestException, CampaignComponentRestException, SettingRestException,
        CampaignComponentValidationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignBuilder campaignBuilder =
                getCampaignBuilder(campaign.getId().getValue(), authorization, expectedCurrentVersion);
            CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
            CampaignComponentBuilder campaignComponentBuilder =
                campaignBuilder.updateComponent(campaignComponent);
            Setting setting = campaignComponentSettingProvider.getCampaignComponentSetting(campaign,
                componentId, settingName);
            campaignComponentBuilder.removeSetting(setting).save();

            return settingRestMapper.toSettingResponse(setting);
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
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
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
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | CreativeArchiveIncompatibleApiVersionException | AuthorizationException | ComponentTypeNotFoundException
            | CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentSettingResponse get(String accessToken,
        String campaignId,
        String version,
        String componentId,
        String settingName)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        SettingRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        return settingRestMapper.toSettingResponse(campaignComponentSettingProvider
            .getCampaignComponentSetting(campaign, componentId, settingName));
    }

    @Override
    public List<CampaignComponentSettingResponse> list(String accessToken,
        String campaignId,
        String version,
        String componentId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignComponentRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        List<Setting> settings = campaignComponentSettingProvider.getCampaignComponentSettings(campaign, componentId);

        return settings.stream()
            .map(setting -> settingRestMapper.toSettingResponse(setting))
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public BuiltCampaignComponentSettingResponse getBuilt(String accessToken,
        String campaignId,
        String version,
        String componentId,
        String settingName)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        BuildCampaignRestException, SettingRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);

        return settingRestMapper.toBuiltSettingResponse(
            builtCampaign, componentId,
            campaignComponentSettingProvider.getBuiltCampaignComponentSetting(builtCampaign, componentId, settingName));
    }

    @Override
    public List<BuiltCampaignComponentSettingResponse> listBuilt(String accessToken,
        String campaignId,
        String version,
        String componentId) throws UserAuthorizationRestException, BuildCampaignRestException, CampaignRestException,
        CampaignComponentRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign builtCampaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        List<? extends BuiltSetting> builtSettings =
            campaignComponentSettingProvider.getBuiltCampaignComponentSettings(builtCampaign,
                componentId);

        return builtSettings.stream()
            .map(setting -> settingRestMapper.toBuiltSettingResponse(builtCampaign, componentId, setting))
            .collect(Collectors.toUnmodifiableList());
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignComponentResponse addComponent(String accessToken, String campaignId,
        String expectedCurrentVersion, String componentId, String settingName,
        CampaignComponentSocketAddComponentRequest request, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentDuplicateRestException, CampaignUpdateRestException,
        CampaignRestException, CampaignComponentValidationRestException, SettingValidationRestException,
        CampaignComponentRestException, ComponentTypeRestException, CreativeArchiveRestException,
        BuildCampaignRestException, CampaignComponentRootValidationRestException, ComponentRestException,
        SettingRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Component sourceComponent = getComponent(request.getSourceComponentId().getValue(), authorization);
            Component targetComponent = getComponent(componentId, authorization);

            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, Id.valueOf(campaignId));
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);

            ComponentDuplicateBuilder duplicateBuilder =
                campaignBuilder.createDuplicateComponentBuilder(sourceComponent);

            duplicateBuilder.withTargetComponent(targetComponent.getCampaignComponent().getId(), settingName);

            if (request.getComponentDisplayName().isPresent()) {
                duplicateBuilder.withDisplayName(request.getComponentDisplayName().getValue());
            }

            if (request.getSettings().isPresent()) {
                for (CampaignComponentSettingRequest setting : request.getSettings().getValue()
                    .stream()
                    .filter(settingRequest -> Objects.nonNull(settingRequest))
                    .collect(Collectors.toList())) {

                    if (setting.getName() == null) {
                        throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                            .withErrorCode(SettingValidationRestException.NAME_MISSING)
                            .build();
                    }

                    ComponentDuplicateBuilder.SettingUpdateClosure settingUpdateClosure =
                        (settingBuilder, oldSetting) -> {
                            if (setting.getDisplayName().isPresent()) {
                                settingBuilder.withDisplayName(setting.getDisplayName().getValue());
                            }
                            setting.getTags().ifPresent(tags -> {
                                settingBuilder.withTags(tags);
                            });
                            setting.getPriority().ifPresent(value -> settingBuilder.withPriority(value));

                            com.extole.client.rest.campaign.component.setting.SettingType settingType =
                                com.extole.client.rest.campaign.component.setting.SettingType
                                    .valueOf(oldSetting.getType().name());
                            settingRequestMapperRepository.getCreateRequestMapper(settingType).complete(setting,
                                settingBuilder);
                        };

                    duplicateBuilder.updateSetting(setting.getName(), settingUpdateClosure);
                }
            }

            Component duplicatedComponent = duplicateBuilder.duplicate();

            return campaignComponentRestMapper.toComponentResponse(duplicatedComponent.getCampaignComponent(),
                timeZone);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (MissingTargetComponentByAbsoluteNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.MISSING_TARGET_COMPONENT_BY_ABSOLUTE_NAME)
                .addParameter("absolute_name", e.getComponentAbsoluteName())
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.CAMPAIGN_NOT_FOUND)
                .addParameter("campaign_id", campaignId)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (RootComponentDuplicationException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ROOT_DUPLICATION_ATTEMPT)
                .build();
        } catch (InvalidCampaignComponentInstalledIntoSocketException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_INSTALLED_INTO_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("install_component_id", e.getInstallComponentId())
                .withCause(e)
                .build();
        } catch (ComponentDuplicationException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.COMPONENT_COLLISION)
                .addParameter("component_name", e.getComponentName())
                .build();
        } catch (UniqueComponentElementRequiredException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.UNIQUE_COMPONENT_ELEMENT_REQUIRED)
                .addParameter("element_type", e.getEntity())
                .addParameter("reference_value", e.getReferenceValue())
                .build();
        } catch (CampaignComponentNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.NAME_ALREADY_IN_USE)
                .addParameter("name", e.getComponentName())
                .withCause(e)
                .build();
        } catch (CampaignComponentNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getComponentName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignComponentDisplayNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("display_name", e.getComponentDisplayName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (CampaignComponentIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", e.getComponentName())
                .withCause(e)
                .build();
        } catch (CampaignComponentIllegalCharacterInDisplayNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.DISPLAY_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("display_name", e.getComponentDisplayName())
                .withCause(e)
                .build();
        } catch (CampaignComponentDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingDisplayNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("display_name", e.getDisplayName())
                .addParameter("min_length", Integer.valueOf(e.getDisplayNameMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getDisplayNameMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingIllegalCharacterInDisplayNameException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DISPLAY_NAME_HAS_ILLEGAL_CHARACTER)
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            Map<String, RestExceptionResponse> exceptionResponses = Maps.newHashMap();
            e.getSuppressedExceptions().forEach((name, buildException) -> {
                RestException restException =
                    settingRestMapper.mapToBuildSettingRestException(buildException, e.getEntityId());
                exceptionResponses.put(name, new RestExceptionResponseBuilder(restException).build());
            });
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.SETTINGS_BUILD_FAILED)
                .addParameter("errors", exceptionResponses)
                .withCause(e)
                .build();
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentSocketNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SOCKET_NOT_FOUND)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("campaign_id", e.getCampaignId())
                .build();
        } catch (ComponentDuplicateBuilder.UnknownComponentSettingException e) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SETTING_NOT_FOUND)
                .addParameter("setting_name", e.getName())
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        } catch (MissingSourceComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.MISSING_SOURCE_COMPONENT_TYPE)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("component_id", e.getComponentId())
                .addParameter("expected_component_type", e.getExpectedComponentType())
                .withCause(e)
                .build();
        } catch (InvalidComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_INVALID)
                .addParameter("source_element_id", e.getAnchorDetails().getSourceElementId())
                .addParameter("source_element_type", e.getAnchorDetails().getSourceElementType())
                .addParameter("target_element_id", e.getTargetElementId())
                .addParameter("expected_target_element_types", e.getExpectedParentTypes())
                .withCause(e)
                .build();
        } catch (MissingComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_MISSING)
                .addParameter("source_element_id", e.getAnchorDetails().getSourceElementId())
                .addParameter("source_element_type", e.getAnchorDetails().getSourceElementType())
                .withCause(e)
                .build();
        } catch (MissingFallbackComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_NO_DEFAULT_CANDIDATE)
                .addParameter("source_element_id", e.getAnchorDetails().getSourceElementId())
                .addParameter("source_element_type", e.getAnchorDetails().getSourceElementType())
                .withCause(e)
                .build();
        } catch (AmbiguousFallbackComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_MANY_DEFAULT_CANDIDATES)
                .addParameter("source_element_id", e.getAnchorDetails().getSourceElementId())
                .addParameter("source_element_type", e.getAnchorDetails().getSourceElementType())
                .addParameter("candidates", e.getFallbackAnchorCandidates())
                .withCause(e)
                .build();
        } catch (UnrecognizedComponentAnchorsException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_UNRECOGNIZED)
                .addParameter("source_element_ids", e.getUnrecognizedAnchorableElementIds())
                .withCause(e)
                .build();
        } catch (ComponentSocketFilterTypeMismatchException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.SOCKET_FILTER_TYPE_MISMATCH)
                .addParameter("source_component_types", e.getSourceComponentTypes())
                .addParameter("filter_component_type", e.getFilterComponentType())
                .withCause(e)
                .build();
        } catch (ComponentSocketMissingRequiredParameterException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SOCKET_MISSING_REQUIRED_PARAMETER)
                .addParameter("socket_parameter_name", e.getSocketParameterName())
                .addParameter("socket_parameter_type", e.getSocketParameterType())
                .addParameter("socket_name", e.getSocketName())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeRestException.class)
                .withErrorCode(ComponentTypeRestException.COMPONENT_TYPE_NOT_FOUND)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentInstallFailedException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.COMPONENT_INSTALL_FAILED)
                .addParameter("component_id", e.getComponentId())
                .addParameter("error_message", e.getErrorMessage())
                .withCause(e)
                .build();
        } catch (CreativeArchiveIncompatibleApiVersionException e) {
            throw RestExceptionBuilder.newBuilder(CreativeArchiveRestException.class)
                .withErrorCode(CreativeArchiveRestException.CREATIVE_HAS_INCOMPATIBLE_API_VERSION)
                .addParameter("archive_id", e.getArchiveId())
                .addParameter("api_version", e.getApiVersion())
                .withCause(e).build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (VariableValueKeyLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VALUE_KEY_LENGTH_OUT_OF_RANGE)
                .addParameter("value_key", e.getValueKey())
                .addParameter("min_length", Integer.valueOf(e.getValueKeyMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getValueKeyMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingTagLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.TAG_LENGTH_OUT_OF_RANGE)
                .addParameter("invalid_tag", e.getTag())
                .addParameter("max_length", Integer.valueOf(e.getTagMaxLength()))
                .addParameter("min_length", Integer.valueOf(e.getTagMinLength()))
                .withCause(e)
                .build();
        } catch (CampaignComponentRootRenameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentRootValidationRestException.class)
                .withErrorCode(CampaignComponentRootValidationRestException.ROOT_RENAME)
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (StaleCampaignVersionException | AuthorizationException | CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentResponse removeComponent(String accessToken, String campaignId,
        String expectedCurrentVersion,
        String componentId, String settingName, String targetComponentId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignRestException, CampaignComponentRestException, CampaignComponentValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);

            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));

            CampaignComponent campaignComponent =
                campaignComponentProvider.getCampaignComponent(targetComponentId, campaign);

            if (!(campaignComponent.getInstalledIntoSocket().isPresent()
                && campaignComponent.getInstalledIntoSocket().get().equals(settingName)
                && campaignComponent.getCampaignComponentReferences().stream()
                    .anyMatch(reference -> reference.getComponentId().equals(Id.valueOf(componentId))))) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                    .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                    .addParameter("campaign_id", campaign.getId())
                    .addParameter("campaign_component_id", targetComponentId)
                    .build();
            }

            campaignBuilder.removeComponentRecursively(campaignComponent);
            campaignBuilder.save();

            return campaignComponentRestMapper.toComponentResponse(campaignComponent, timeZone);
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
                .withCause(e)
                .build();
        } catch (ReferencedExternalElementException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXTERNAL_ELEMENT_IS_REFERENCED)
                .addParameter("references", e.getReferences())
                .addParameter("element_type", e.getElementType().name())
                .addParameter("element_id", e.getElementId())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        } catch (CampaignComponentNameDuplicateException
            | CampaignServiceNameMissingException | CampaignLabelMissingNameException
            | CampaignControllerTriggerBuildException | CreativeVariableUnsupportedException
            | CampaignServiceIllegalCharacterInNameException | CampaignServiceNameLengthException
            | CreativeArchiveBuilderException | CreativeArchiveJavascriptException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException
            | CampaignLabelDuplicateNameException | CampaignScheduleException | CampaignGlobalDeleteException
            | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | IncompatibleRewardRuleException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<CampaignComponentResponse> listInstalledComponents(String accessToken, String campaignId,
        String expectedCurrentVersion, String componentId, String settingName, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), expectedCurrentVersion);

        return campaign.getComponents()
            .stream()
            .filter(component -> component.getInstalledIntoSocket().isPresent()
                && component.getInstalledIntoSocket().get().equals(settingName))
            .filter(component -> component.getCampaignComponentReferences().stream()
                .anyMatch(reference -> reference.getComponentId().equals(Id.valueOf(componentId))))
            .map(component -> campaignComponentRestMapper.toComponentResponse(component, timeZone))
            .collect(Collectors.toList());
    }

    private Optional<BuiltCampaignComponent> lookupComponent(BuiltCampaign builtCampaign,
        Id<CampaignComponent> componentId) {

        return builtCampaign.getComponents().stream().filter(component -> component.getId().equals(componentId))
            .findFirst();
    }

    private void validateExplicitValuesForInheritedVariableShouldNotBePresent(
        CampaignComponentSettingUpdateRequest request,
        Optional<Setting> setting) throws SettingValidationRestException {
        if (request instanceof CampaignComponentVariableUpdateRequest) {
            CampaignComponentVariableUpdateRequest variableUpdateRequest =
                (CampaignComponentVariableUpdateRequest) request;
            boolean intentToBeInherited = variableUpdateRequest.getSource()
                .map(value -> Boolean
                    .valueOf(value == com.extole.client.rest.campaign.component.setting.VariableSource.INHERITED))
                .orElse(Boolean.valueOf(
                    setting.isPresent() && setting.get() instanceof Variable
                        && ((Variable) setting.get()).getSource() == VariableSource.INHERITED))
                .booleanValue();

            if (intentToBeInherited && variableUpdateRequest.getValues().isPresent()) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.EXPLICIT_VALUES_FOR_INHERITED_VARIABLE_NOT_ALLOWED)
                    .build();
            }
        }
    }

    private void validateExplicitValuesForInheritedVariableShouldNotBePresent(
        CampaignComponentSettingRequest request,
        Optional<Variable> variable) throws SettingValidationRestException {
        if (request instanceof CampaignComponentVariableRequest) {
            CampaignComponentVariableRequest variableUpdateRequest =
                (CampaignComponentVariableRequest) request;
            boolean intentToBeInherited = variableUpdateRequest.getSource()
                .map(value -> Boolean
                    .valueOf(value == com.extole.client.rest.campaign.component.setting.VariableSource.INHERITED))
                .orElse(Boolean.valueOf(
                    variable.isPresent() && variable.get().getSource() == VariableSource.INHERITED))
                .booleanValue();

            if (intentToBeInherited && variableUpdateRequest.getValues().isPresent()) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.EXPLICIT_VALUES_FOR_INHERITED_VARIABLE_NOT_ALLOWED)
                    .build();
            }
        }
    }

    private boolean variableSourceIsChangedFromLocalToInherited(CampaignComponentSettingUpdateRequest request,
        Setting setting) {
        if (request instanceof CampaignComponentVariableUpdateRequest && setting instanceof Variable) {
            CampaignComponentVariableUpdateRequest variableUpdateRequest =
                (CampaignComponentVariableUpdateRequest) request;
            Variable variable = (Variable) setting;
            if (variableUpdateRequest.getSource().isPresent()) {
                VariableSource source = VariableSource.valueOf(variableUpdateRequest.getSource().getValue().name());
                return source != variable.getSource() && source == VariableSource.LOCAL;
            }
        }

        return false;
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
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", campaignId)
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        }
        return campaignBuilder;
    }

    private void applyRequestToBuilder(CampaignComponentSettingRequest request,
        SettingBuilder settingBuilder)
        throws SettingNameLengthException, SettingInvalidNameException, VariableValueKeyLengthException,
        SettingTagLengthException, SettingDisplayNameLengthException, SettingIllegalCharacterInDisplayNameException,
        SettingValidationRestException {
        if (Objects.nonNull(request.getName())) {
            settingBuilder.withName(request.getName());
        }
        if (request.getDisplayName().isPresent()) {
            settingBuilder.withDisplayName(request.getDisplayName().getValue());
        }
        settingBuilder.withType(SettingType.valueOf(request.getType().name()));
        settingRequestMapperRepository.getCreateRequestMapper(request.getType())
            .complete(request, settingBuilder);
        request.getTags().ifPresent(tags -> {
            settingBuilder.withTags(tags);
        });
        request.getPriority().ifPresent(priority -> settingBuilder.withPriority(priority));
    }

    private void applyRequestToBuilder(CampaignComponentSettingUpdateRequest request, SettingBuilder settingBuilder)
        throws SettingNameLengthException, SettingInvalidNameException, VariableValueKeyLengthException,
        SettingTagLengthException, SettingDisplayNameLengthException, SettingIllegalCharacterInDisplayNameException {
        if (request.getName().isPresent()) {
            settingBuilder.withName(request.getName().getValue());
        }
        if (request.getDisplayName().isPresent()) {
            if (request.getDisplayName().getValue().isPresent()) {
                settingBuilder.withDisplayName(request.getDisplayName().getValue().get());
            } else {
                settingBuilder.cleanDisplayName();
            }
        }
        request.getTags().ifPresent(tags -> {
            settingBuilder.withTags(tags);
        });
        settingRequestMapperRepository.getUpdateRequestMapper(request.getType())
            .complete(request, settingBuilder);
        request.getPriority().ifPresent(value -> settingBuilder.withPriority(value));
    }

    private Component getComponent(String componentId, Authorization authorization)
        throws ComponentRestException, UserAuthorizationRestException {
        try {
            return componentService.get(authorization, Id.valueOf(componentId));
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private interface PulledAssetFromSourceComponent {
        String getOldName();

        String getNewName();

        BuiltCampaignComponentAsset getBuiltAsset();

        Integer getCampaignVersion();
    }

    private static final class ComponentImpl implements Component {
        private final Campaign campaign;
        private final CampaignComponent component;

        private ComponentImpl(Campaign campaign, CampaignComponent component) {
            this.campaign = campaign;
            this.component = component;
        }

        @Override
        public Campaign getCampaign() {
            return campaign;
        }

        @Override
        public CampaignComponent getCampaignComponent() {
            return component;
        }

        @Override
        public ComponentOwner getOwner() {
            return ComponentOwner.CLIENT;
        }
    }
}
