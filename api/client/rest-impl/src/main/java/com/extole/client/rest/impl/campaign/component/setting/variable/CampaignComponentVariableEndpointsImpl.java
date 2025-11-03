package com.extole.client.rest.impl.campaign.component.setting.variable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
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
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.component.setting.variable.CampaignComponentVariableEndpoints;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.impl.campaign.BuildAudienceExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildClientKeyExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildEventStreamExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildPrehandlerExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildRewardSupplierExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildWebhookExceptionMapper;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.OAuthClientKeyBuildRestExceptionMapper;
import com.extole.client.rest.impl.campaign.TranslatableVariableExceptionMapper;
import com.extole.client.rest.impl.campaign.component.CampaignComponentProvider;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingProvider;
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.client.rest.impl.campaign.component.setting.SettingAssetsPullBuilder;
import com.extole.client.rest.impl.campaign.component.setting.SettingRequestMapperRepository;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
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
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentFacetException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentTypeException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetNameException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetValueException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentTypeException;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.campaign.setting.VariableValueMissingException;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveIncompatibleApiVersionException;
import com.extole.model.service.event.stream.built.BuildEventStreamException;
import com.extole.model.service.prehandler.built.BuildPrehandlerException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.webhook.built.BuildWebhookException;
import com.extole.spring.ServiceLocator;

@Provider
public class CampaignComponentVariableEndpointsImpl implements CampaignComponentVariableEndpoints {

    private final CampaignService campaignService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final CampaignComponentProvider campaignComponentProvider;
    private final CampaignComponentSettingProvider campaignComponentSettingProvider;
    private final SettingRequestMapperRepository settingRequestMapperRepository;
    private final ServiceLocator serviceLocator;

    @Autowired
    public CampaignComponentVariableEndpointsImpl(CampaignService campaignService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignComponentSettingRestMapper settingRestMapper,
        CampaignComponentProvider campaignComponentProvider,
        CampaignComponentSettingProvider campaignComponentSettingProvider,
        SettingRequestMapperRepository settingRequestMapperRepository,
        ServiceLocator serviceLocator) {
        this.campaignService = campaignService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.settingRestMapper = settingRestMapper;
        this.campaignComponentProvider = campaignComponentProvider;
        this.campaignComponentSettingProvider = campaignComponentSettingProvider;
        this.settingRequestMapperRepository = settingRequestMapperRepository;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public CampaignComponentSettingResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        CampaignComponentSettingRequest request)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {

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
                settingRequestMapperRepository.getCreateRequestMapper(request.getType())
                    .complete(request, settingBuilder);
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
        } catch (CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentSettingResponse update(String accessToken, String campaignId, String expectedCurrentVersion,
        String componentId, String settingName, CampaignComponentSettingUpdateRequest request)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException, SettingRestException,
        ComponentRestException, BuildWebhookRestException, BuildPrehandlerRestException,
        BuildRewardSupplierRestException, BuildClientKeyRestException, BuildAudienceRestException,
        EventStreamValidationRestException, OAuthClientKeyBuildRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
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
                settingRequestMapperRepository.getUpdateRequestMapper(request.getType())
                    .complete(request, settingBuilder);
                settingBuilder.withType(SettingType.valueOf(request.getType().name()));
            }

            validateExplicitValuesForInheritedVariableShouldNotBePresent(request, Optional.of(setting));
            applyRequestToBuilder(request, settingBuilder);

            if (variableSourceIsChangedFromLocalToInherited(request, setting)) {
                CampaignComponentVariableUpdateRequest variableRequest =
                    (CampaignComponentVariableUpdateRequest) request;
                Variable variable = (Variable) setting;
                Map<String,
                    BuildtimeEvaluatable<VariableBuildtimeContext,
                        RuntimeEvaluatable<Object, Optional<Object>>>> values =
                            Maps.newLinkedHashMap(variableRequest.getValues().orElse(variable.getValues()));

                serviceLocator.create(SettingAssetsPullBuilder.class)
                    .initialize(authorization, campaign, campaignComponent, campaignComponentBuilder)
                    .buildForVariable(variable, (VariableBuilder) settingBuilder, values)
                    .performOperation();
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
        } catch (CampaignComponentSettingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", StringUtils.EMPTY).addParameter("details", e.getMessage())
                .withCause(e).build();
        } catch (CampaignComponentException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
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
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException, CampaignComponentValidationRestException {

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
        } catch (CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | CreativeArchiveIncompatibleApiVersionException | AuthorizationException | ComponentTypeNotFoundException
            | CampaignComponentException | CampaignComponentFacetsNotFoundException e) {
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

        return settingRestMapper.toBuiltSettingResponse(builtCampaign, componentId, campaignComponentSettingProvider
            .getBuiltCampaignComponentSetting(builtCampaign, componentId, settingName));
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
        SettingTagLengthException, SettingDisplayNameLengthException, SettingIllegalCharacterInDisplayNameException {
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
}
