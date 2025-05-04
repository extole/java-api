package com.extole.client.rest.impl.campaign.component;

import static com.extole.model.entity.campaign.CampaignComponent.ROOT;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Maps;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentResponse;
import com.extole.client.rest.campaign.component.CampaignComponentCreateRequest;
import com.extole.client.rest.campaign.component.CampaignComponentEndpoints;
import com.extole.client.rest.campaign.component.CampaignComponentResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentRootValidationRestException;
import com.extole.client.rest.campaign.component.CampaignComponentUpdateRequest;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentDuplicateRequest;
import com.extole.client.rest.campaign.component.ComponentDuplicateRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.anchor.AnchorDetailsResponse;
import com.extole.client.rest.campaign.component.anchor.AnchorRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
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
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
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
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.Component;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.InvalidComponentReferenceSocketNameException;
import com.extole.model.entity.campaign.InvalidExternalComponentReferenceException;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignScheduleException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceDuplicateNameException;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionNotFoundException;
import com.extole.model.service.campaign.ComponentElementBuilder;
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
import com.extole.model.service.campaign.component.CampaignComponentNameMissingException;
import com.extole.model.service.campaign.component.CampaignComponentRootRenameException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.CircularComponentReferenceException;
import com.extole.model.service.campaign.component.ComponentDuplicateBuilder;
import com.extole.model.service.campaign.component.ComponentDuplicateBuilder.SettingUpdateClosure;
import com.extole.model.service.campaign.component.ComponentDuplicateBuilder.UnknownComponentSettingException;
import com.extole.model.service.campaign.component.ComponentDuplicationException;
import com.extole.model.service.campaign.component.ComponentInstallFailedException;
import com.extole.model.service.campaign.component.ComponentNotFoundException;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.campaign.component.ComponentSocketFilterTypeMismatchException;
import com.extole.model.service.campaign.component.ComponentSocketMissingRequiredParameterException;
import com.extole.model.service.campaign.component.ComponentSocketNotFoundException;
import com.extole.model.service.campaign.component.ExcessiveExternalComponentReferenceException;
import com.extole.model.service.campaign.component.InvalidCampaignComponentInstalledIntoSocketException;
import com.extole.model.service.campaign.component.MissingSourceComponentTypeException;
import com.extole.model.service.campaign.component.MissingTargetComponentByAbsoluteNameException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.component.RootComponentDuplicationException;
import com.extole.model.service.campaign.component.SelfComponentReferenceException;
import com.extole.model.service.campaign.component.UniqueComponentElementRequiredException;
import com.extole.model.service.campaign.component.anchor.AmbiguousComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.AmbiguousFallbackComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.InvalidComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.MissingComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.MissingFallbackComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.UnrecognizedComponentAnchorsException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
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
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.campaign.setting.VariableValueMissingException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveIncompatibleApiVersionException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;
import com.extole.model.service.event.stream.built.BuildEventStreamException;
import com.extole.model.service.prehandler.built.BuildPrehandlerException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.webhook.built.BuildWebhookException;

@Provider
public class CampaignComponentEndpointsImpl implements CampaignComponentEndpoints {

    private final CampaignService campaignService;
    private final ComponentService componentService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignComponentRestMapper campaignComponentRestMapper;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final CampaignComponentProvider campaignComponentProvider;
    private final SettingRequestMapperRepository settingRequestMapperRepository;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Inject
    public CampaignComponentEndpointsImpl(CampaignService campaignService,
        ComponentService componentService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignComponentRestMapper campaignComponentRestMapper,
        CampaignComponentSettingRestMapper settingRestMapper,
        CampaignComponentProvider campaignComponentProvider,
        SettingRequestMapperRepository settingRequestMapperRepository,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.campaignService = campaignService;
        this.componentService = componentService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.campaignComponentRestMapper = campaignComponentRestMapper;
        this.settingRestMapper = settingRestMapper;
        this.campaignComponentProvider = campaignComponentProvider;
        this.settingRequestMapperRepository = settingRequestMapperRepository;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<CampaignComponentResponse> list(String accessToken, String campaignId, String version,
        @Nullable ZoneId timeZone) throws UserAuthorizationRestException, CampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        return campaign.getComponents()
            .stream()
            .map(component -> campaignComponentRestMapper.toComponentResponse(component, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public CampaignComponentResponse get(String accessToken, String campaignId, String version, String componentId,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentRestException, CampaignRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
        return campaignComponentRestMapper.toComponentResponse(campaignComponent, timeZone);
    }

    @Override
    public List<AnchorDetailsResponse> getAnchors(String accessToken, String campaignId, String version,
        String componentId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);

        try {
            return componentService.getAnchors(authorization, campaign.getId(),
                new CampaignVersion(campaign.getVersion()), Id.valueOf(componentId)).stream()
                .map(anchorDetails -> campaignComponentRestMapper.toAnchorDetails(anchorDetails))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("campaign_component_id", componentId)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        }
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignComponentResponse create(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        CampaignComponentCreateRequest request,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException, CampaignRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignComponentRestException,
        CampaignUpdateRestException, ComponentTypeRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignBuilder campaignBuilder =
                getCampaignBuilder(campaign.getId().getValue(), authorization, expectedCurrentVersion);
            CampaignComponentBuilder campaignComponentBuilder = campaignBuilder.addComponent();
            applyRequestToBuilder(request, campaignComponentBuilder);
            Component created = campaignComponentBuilder.save();
            return campaignComponentRestMapper.toComponentResponse(created.getCampaignComponent(), timeZone);
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
        } catch (InvalidCampaignComponentInstalledIntoSocketException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_INSTALLED_INTO_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("install_component_id", e.getInstallComponentId())
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
        } catch (CampaignComponentNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.NAME_ALREADY_IN_USE)
                .addParameter("name", e.getComponentName())
                .withCause(e)
                .build();
        } catch (CampaignComponentNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                .withCause(e)
                .build();
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
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
        } catch (VariableValueKeyLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VALUE_KEY_LENGTH_OUT_OF_RANGE)
                .addParameter("value_key", e.getValueKey())
                .addParameter("min_length", e.getValueKeyMinLength())
                .addParameter("max_length", e.getValueKeyMaxLength())
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
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
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
        } catch (ExcessiveExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXCESSIVE_ROOT_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (SelfComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.SELF_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "component")
                .addParameter("referencing_entity", "undefined")
                .withCause(e)
                .build();
        } catch (CircularComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.CIRCULAR_COMPONENT_REFERENCE)
                .addParameter("cycles", e.getCycles())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .addParameter("socket_name", e.getSocketName())
                .withCause(e)
                .build();
        } catch (InvalidExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
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
        } catch (CampaignComponentException | CreativeArchiveIncompatibleApiVersionException
            | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public CampaignComponentResponse duplicate(String accessToken,
        String campaignId,
        String version,
        String componentId,
        ComponentDuplicateRequest componentDuplicateRequest,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentDuplicateRestException, CampaignRestException,
        CampaignComponentValidationRestException, CampaignComponentRootValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignComponentRestException,
        CampaignUpdateRestException, CreativeArchiveRestException, ComponentTypeRestException, SettingRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Component component = getSourceComponent(authorization, campaignId, version, componentId);
        try {
            if (componentDuplicateRequest.getTargetCampaignId().isOmitted()) {
                CampaignComponentBuilder rootCampaignComponentBuilder =
                    componentService.duplicateRootComponentCampaign(authorization, component);
                applyRequestToRootComponentBuilder(componentDuplicateRequest, rootCampaignComponentBuilder);
                Component duplicatedComponent = rootCampaignComponentBuilder.save();

                return campaignComponentRestMapper.toComponentResponse(duplicatedComponent.getCampaignComponent(),
                    timeZone);
            }

            Id<Campaign> targetCampaignId = Id.valueOf(componentDuplicateRequest.getTargetCampaignId().getValue());
            ComponentDuplicateBuilder duplicateBuilder = campaignService.editCampaign(authorization, targetCampaignId)
                .createDuplicateComponentBuilder(component);

            applyRequestToBuilder(componentDuplicateRequest, duplicateBuilder);
            Component duplicatedComponent = duplicateBuilder.duplicate();
            return campaignComponentRestMapper.toComponentResponse(duplicatedComponent.getCampaignComponent(),
                timeZone);
        } catch (ConcurrentCampaignUpdateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignUpdateRestException.class)
                .withErrorCode(CampaignUpdateRestException.CONCURRENT_UPDATE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("version", e.getVersion())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.CAMPAIGN_NOT_FOUND)
                .addParameter("campaign_id", componentDuplicateRequest.getTargetCampaignId())
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", componentDuplicateRequest.getTargetCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (MissingTargetComponentByAbsoluteNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.MISSING_TARGET_COMPONENT_BY_ABSOLUTE_NAME)
                .addParameter("absolute_name", e.getComponentAbsoluteName())
                .build();
        } catch (RootComponentDuplicationException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ROOT_DUPLICATION_ATTEMPT)
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
        } catch (InvalidCampaignComponentInstalledIntoSocketException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_INSTALLED_INTO_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("install_component_id", e.getInstallComponentId())
                .withCause(e)
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
        } catch (MissingSourceComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.MISSING_SOURCE_COMPONENT_TYPE)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("component_id", e.getComponentId())
                .addParameter("expected_component_type", e.getExpectedComponentType())
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
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
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
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
        } catch (AmbiguousComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_AMBIGUOUS)
                .addParameter("source_element_id", e.getSourceElementId())
                .withCause(e)
                .build();
        } catch (MissingAnchorSourceElementId e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_SOURCE_ELEMENT_ID_MISSING)
                .build();
        } catch (MissingAnchorTargetElementId e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_TARGET_ELEMENT_ID_MISSING)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .addParameter("socket_name", e.getSocketName())
                .withCause(e)
                .build();
        } catch (ComponentSocketNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SOCKET_NOT_FOUND)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("campaign_id", e.getCampaignId())
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
        } catch (UnknownComponentSettingException e) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SETTING_NOT_FOUND)
                .addParameter("setting_name", e.getName())
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (CampaignComponentException | StaleCampaignVersionException | CampaignServiceNameLengthException
            | CampaignServiceDuplicateNameException | CampaignServiceIllegalCharacterInNameException
            | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        CampaignComponentUpdateRequest request,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentRestException, CampaignComponentValidationRestException,
        CampaignComponentRootValidationRestException, CampaignRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException, ComponentTypeRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
            CampaignComponentBuilder campaignComponentBuilder =
                campaignBuilder.updateComponent(campaignComponent);
            applyRequestToBuilder(request, campaignComponentBuilder);
            Component updated = campaignComponentBuilder.save();
            return campaignComponentRestMapper.toComponentResponse(updated.getCampaignComponent(), timeZone);
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
        } catch (CampaignComponentRootRenameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentRootValidationRestException.class)
                .withErrorCode(CampaignComponentRootValidationRestException.ROOT_RENAME)
                .withCause(e)
                .build();
        } catch (InvalidCampaignComponentInstalledIntoSocketException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_INSTALLED_INTO_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("install_component_id", e.getInstallComponentId())
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
        } catch (CampaignComponentNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.NAME_ALREADY_IN_USE)
                .addParameter("name", e.getComponentName())
                .withCause(e)
                .build();
        } catch (VariableValueMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VARIABLE_VALUE_MISSING)
                .addParameter("name", e.getName())
                .addParameter("details", e.getDescription())
                .withCause(e)
                .build();
        } catch (SettingNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (SettingNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
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
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
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
        } catch (ExcessiveExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXCESSIVE_ROOT_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (SelfComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.SELF_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (RedundantComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.REDUNDANT_COMPONENT_REFERENCE)
                .addParameter("referenced_component_name", e.getReferencedComponentName())
                .addParameter("referencing_entity_type", "component")
                .addParameter("referencing_entity", componentId)
                .withCause(e)
                .build();
        } catch (CircularComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.CIRCULAR_COMPONENT_REFERENCE)
                .addParameter("cycles", e.getCycles())
                .withCause(e)
                .build();
        } catch (InvalidExternalComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_EXTERNAL_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceSocketNameException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE_SOCKET_NAME)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
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
        } catch (CampaignComponentException | CreativeArchiveIncompatibleApiVersionException
            | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignComponentResponse delete(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        @Nullable ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentRestException,
        CampaignRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignComponentValidationRestException, BuildWebhookRestException, BuildPrehandlerRestException,
        BuildRewardSupplierRestException, BuildClientKeyRestException, BuildAudienceRestException,
        EventStreamValidationRestException, OAuthClientKeyBuildRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            CampaignBuilder campaignBuilder = getCampaignBuilder(campaignId, authorization, expectedCurrentVersion);
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
            if (isRootComponent(campaignComponent)) {
                campaignService.archive(authorization, campaign.getId());
            } else {
                campaignBuilder.removeComponentRecursively(campaignComponent);
                campaignBuilder.save();
            }
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
                .withCause(e).build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (ReferencedExternalElementException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.EXTERNAL_ELEMENT_IS_REFERENCED)
                .addParameter("element_id", e.getElementId())
                .addParameter("element_type", e.getElementType())
                .addParameter("references", e.getReferences())
                .withCause(e).build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
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
        } catch (CampaignComponentNameDuplicateException | CampaignServiceNameMissingException
            | CampaignLabelMissingNameException
            | CampaignControllerTriggerBuildException | CreativeVariableUnsupportedException
            | CampaignServiceIllegalCharacterInNameException | CampaignServiceNameLengthException
            | CreativeArchiveBuilderException | CreativeArchiveJavascriptException
            | InvalidComponentReferenceException | TransitionRuleAlreadyExistsForActionType | CampaignComponentException
            | CampaignFlowStepException | StepDataBuildException
            | CampaignLabelDuplicateNameException | CampaignScheduleException | CampaignGlobalDeleteException
            | CampaignGlobalArchiveException | CampaignGlobalStateChangeException | IncompatibleRewardRuleException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

    }

    @Override
    public List<BuiltCampaignComponentResponse> listBuilt(String accessToken, String campaignId, String version,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        return campaign.getComponents()
            .stream()
            .map(component -> campaignComponentRestMapper.toBuiltComponentResponse(component, timeZone,
                campaign))
            .collect(Collectors.toList());
    }

    @Override
    public BuiltCampaignComponentResponse getBuilt(String accessToken, String campaignId, String version,
        String componentId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentRestException, CampaignRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignComponent campaignComponent =
            campaignComponentProvider.getBuiltCampaignComponent(componentId, campaign);
        return campaignComponentRestMapper.toBuiltComponentResponse(campaignComponent, timeZone,
            campaign);
    }

    private Component getSourceComponent(Authorization authorization, String campaignId, String version,
        String componentId)
        throws CampaignComponentRestException, UserAuthorizationRestException, CampaignRestException,
        ComponentDuplicateRestException {
        try {
            Campaign sourceCampaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
            return componentService.get(authorization, sourceCampaign.getId(),
                new CampaignVersion(sourceCampaign.getVersion()), Id.valueOf(componentId));
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.CAMPAIGN_COMPONENT_NOT_FOUND)
                .addParameter("campaign_component_id", componentId)
                .addParameter("campaign_id", campaignId)
                .withCause(e)
                .build();
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.CAMPAIGN_NOT_FOUND)
                .addParameter("campaign_id", Id.valueOf(campaignId))
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

    private void handleComponentIds(ComponentDuplicateBuilder componentDuplicateBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            componentDuplicateBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private void applyRequestToBuilder(CampaignComponentCreateRequest request,
        CampaignComponentBuilder campaignComponentBuilder)
        throws CampaignComponentIllegalCharacterInNameException, CampaignComponentNameLengthException,
        SettingNameLengthException, SettingInvalidNameException, VariableValueKeyLengthException,
        CampaignComponentDescriptionLengthException, CampaignComponentValidationRestException,
        SettingTagLengthException, CampaignComponentRootRenameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, CampaignComponentIllegalCharacterInDisplayNameException,
        CampaignComponentDisplayNameLengthException {
        if (request.getDisplayName() != null) {
            campaignComponentBuilder.withDisplayName(request.getDisplayName());
        }
        if (request.getName() != null) {
            campaignComponentBuilder.withName(request.getName());
        }
        if (request.getType() != null) {
            campaignComponentBuilder.withType(request.getType());
        }
        if (request.getTags() != null) {
            campaignComponentBuilder.withTags(request.getTags());
        }
        if (request.getSettings() != null) {
            for (CampaignComponentSettingRequest setting : request.getSettings().stream()
                .filter(settingRequest -> Objects.nonNull(settingRequest)).collect(Collectors.toList())) {
                populateSettingBuilder(campaignComponentBuilder, setting);

            }
        }
        if (request.getComponentVersion() != null) {
            campaignComponentBuilder.withComponentVersion(request.getComponentVersion());
        }
        if (request.getDescription() != null) {
            campaignComponentBuilder.withDescription(request.getDescription());
        }
        if (request.getInstalledIntoSocket() != null) {
            campaignComponentBuilder.withInstalledIntoSocket(request.getInstalledIntoSocket());
        }
        if (request.getInstall() != null) {
            campaignComponentBuilder.withInstall(request.getInstall());
        }
        request.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(campaignComponentBuilder, componentIds);
        });
        request.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(campaignComponentBuilder, componentReferences);
        });
    }

    @SuppressWarnings({"unchecked"})
    private void applyRequestToRootComponentBuilder(ComponentDuplicateRequest componentDuplicateRequest,
        CampaignComponentBuilder componentBuilder) throws CampaignComponentException {

        componentDuplicateRequest.getDescription().ifPresent(description -> {
            if (description.isPresent()) {
                componentBuilder.withDescription(description.get());
            } else {
                componentBuilder.clearDescription();
            }
        });
        componentDuplicateRequest.getTags().ifPresent(tags -> componentBuilder.withTags(tags));
        if (componentDuplicateRequest.getSettings().isPresent()) {
            for (CampaignComponentSettingRequest setting : componentDuplicateRequest.getSettings().getValue()
                .stream()
                .filter(settingRequest -> Objects.nonNull(settingRequest))
                .collect(Collectors.toList())) {
                SettingBuilder settingBuilder;
                if (setting.getType() != null) {
                    settingBuilder = componentBuilder
                        .addSetting(SettingType.valueOf(setting.getType().name()));
                    settingBuilder.withType(SettingType.valueOf(setting.getType()
                        .name()));
                    settingRequestMapperRepository.getCreateRequestMapper(setting.getType())
                        .complete(setting, settingBuilder);
                } else {
                    settingBuilder = componentBuilder.addSetting();
                }
                if (setting.getName() != null) {
                    settingBuilder.withName(setting.getName());
                }
                if (setting.getDisplayName().isPresent()) {
                    settingBuilder.withDisplayName(setting.getDisplayName().getValue());
                }
                setting.getTags().ifPresent(tags -> {
                    settingBuilder.withTags(tags);
                });
                setting.getPriority().ifPresent(value -> settingBuilder.withPriority(value));
            }
        }

        componentDuplicateRequest.getType().ifPresent(type -> {
            if (type.isPresent()) {
                componentBuilder.withType(type.get());
            } else {
                componentBuilder.clearType();
            }
        });
    }

    @SuppressWarnings({"unchecked"})
    private void applyRequestToBuilder(ComponentDuplicateRequest componentDuplicateRequest,
        ComponentDuplicateBuilder duplicateBuilder)
        throws CampaignComponentException, CampaignComponentValidationRestException, UnknownComponentSettingException,
        SettingValidationRestException, AuthorizationException, ComponentTypeNotFoundException,
        ComponentSocketNotFoundException, MissingTargetComponentByAbsoluteNameException {
        componentDuplicateRequest.getTargetComponentAbsoluteName().ifPresent(
            targetComponentAbsoluteName -> duplicateBuilder.withTargetComponent(targetComponentAbsoluteName));

        if (componentDuplicateRequest.getTargetSocketName().isPresent()) {
            duplicateBuilder.withTargetSocketName(componentDuplicateRequest.getTargetSocketName().getValue());
        }

        componentDuplicateRequest.getType().ifPresent(type -> {
            if (type.isPresent()) {
                duplicateBuilder.withType(type.get());
            } else {
                duplicateBuilder.clearType();
            }
        });

        componentDuplicateRequest.getComponentDisplayName().ifPresent(value -> duplicateBuilder.withDisplayName(value));
        componentDuplicateRequest.getComponentName().ifPresent(name -> duplicateBuilder.withName(name));
        componentDuplicateRequest.getDescription().ifPresent(description -> {
            if (description.isPresent()) {
                duplicateBuilder.withDescription(description.get());
            } else {
                duplicateBuilder.clearDescription();
            }
        });
        componentDuplicateRequest.getTags().ifPresent(tags -> duplicateBuilder.withTags(tags));
        if (componentDuplicateRequest.getSettings().isPresent()) {
            for (CampaignComponentSettingRequest setting : componentDuplicateRequest.getSettings().getValue()
                .stream()
                .filter(settingRequest -> Objects.nonNull(settingRequest))
                .collect(Collectors.toList())) {

                if (setting.getName() == null) {
                    throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                        .withErrorCode(SettingValidationRestException.NAME_MISSING)
                        .build();
                }

                SettingUpdateClosure settingUpdateClosure = (settingBuilder, oldSetting) -> {
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

        componentDuplicateRequest.getAnchors().ifPresent(anchors -> {
            for (AnchorRequest anchor : anchors
                .stream()
                .filter(anchor -> Objects.nonNull(anchor))
                .collect(Collectors.toList())) {
                if (anchor.getSourceElementId() == null) {
                    throw new MissingAnchorSourceElementId("Anchor source element id is required");
                }

                if (anchor.getTargetElementId() == null) {
                    throw new MissingAnchorTargetElementId("Anchor target element id is required");
                }
                duplicateBuilder.withAnchor(anchor.getSourceElementId(), anchor.getTargetElementId());
            }
        });
        componentDuplicateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(duplicateBuilder, componentIds);
        });
        componentDuplicateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(duplicateBuilder, componentReferences);
        });
    }

    private void applyRequestToBuilder(CampaignComponentUpdateRequest request,
        CampaignComponentBuilder campaignComponentBuilder)
        throws CampaignComponentException, CampaignComponentValidationRestException {
        request.getDisplayName().ifPresent(displayName -> {
            if (displayName.isPresent()) {
                campaignComponentBuilder.withDisplayName(displayName.get());
            } else {
                campaignComponentBuilder.clearDisplayName();
            }
        });
        request.getName().ifPresent(name -> {
            campaignComponentBuilder.withName(name);
        });
        request.getType().ifPresent(type -> {
            if (type.isPresent()) {
                campaignComponentBuilder.withType(type.get());
            } else {
                campaignComponentBuilder.clearType();
            }
        });
        request.getTags().ifPresent(tags -> {
            campaignComponentBuilder.withTags(tags);
        });
        request.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(campaignComponentBuilder, componentIds);
        });
        request.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(campaignComponentBuilder, componentReferences);
        });
        if (request.getSettings().isPresent()) {
            campaignComponentBuilder.clearSettings();
            for (CampaignComponentSettingRequest setting : request.getSettings().getValue().stream()
                .filter(settingRequest -> Objects.nonNull(settingRequest)).collect(Collectors.toList())) {
                populateSettingBuilder(campaignComponentBuilder, setting);
            }
        }
        request.getComponentVersion().ifPresent(componentVersion -> {
            campaignComponentBuilder.withComponentVersion(componentVersion);
        });
        request.getDescription().ifPresent(description -> {
            if (description.isPresent()) {
                campaignComponentBuilder.withDescription(description.get());
            } else {
                campaignComponentBuilder.clearDescription();
            }
        });
        request.getInstalledIntoSocket().ifPresent(installedIntoSocket -> {
            if (installedIntoSocket.isPresent()) {
                campaignComponentBuilder.withInstalledIntoSocket(installedIntoSocket.get());
            } else {
                campaignComponentBuilder.clearInstalledIntoSocket();
            }
        });
        request.getInstall().ifPresent(install -> {
            if (install.isPresent()) {
                campaignComponentBuilder.withInstall(install.get());
            } else {
                campaignComponentBuilder.clearInstall();
            }
        });
    }

    @SuppressWarnings({"unchecked"})
    private void populateSettingBuilder(CampaignComponentBuilder campaignComponentBuilder,
        CampaignComponentSettingRequest setting)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        SettingBuilder settingBuilder;
        if (setting.getType() != null) {
            settingBuilder = campaignComponentBuilder
                .addSetting(SettingType.valueOf(setting.getType().name()));
            settingRequestMapperRepository.getCreateRequestMapper(setting.getType())
                .complete(setting, settingBuilder);
        } else {
            settingBuilder = campaignComponentBuilder.addSetting();
        }
        if (setting.getName() != null) {
            settingBuilder.withName(setting.getName());
        }
        if (setting.getDisplayName().isPresent()) {
            settingBuilder.withDisplayName(setting.getDisplayName().getValue());
        }
        setting.getTags().ifPresent(tags -> {
            settingBuilder.withTags(tags);
        });
        setting.getPriority().ifPresent(value -> settingBuilder.withPriority(value));
    }

    private boolean isRootComponent(CampaignComponent component) {
        return component.getName().equalsIgnoreCase(ROOT);
    }

    private static class MissingAnchorSourceElementId extends CampaignComponentException {
        MissingAnchorSourceElementId(String message) {
            super(message);
        }
    }

    private static class MissingAnchorTargetElementId extends CampaignComponentException {
        MissingAnchorTargetElementId(String message) {
            super(message);
        }
    }

}
