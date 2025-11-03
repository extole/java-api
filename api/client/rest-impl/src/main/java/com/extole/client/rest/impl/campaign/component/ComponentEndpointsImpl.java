package com.extole.client.rest.impl.campaign.component;

import static com.extole.model.entity.campaign.CampaignComponent.ROOT;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignControllerRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.BuiltComponentResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.CampaignComponentRootValidationRestException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentCreateRequest;
import com.extole.client.rest.campaign.component.ComponentDuplicateRequest;
import com.extole.client.rest.campaign.component.ComponentDuplicateRestException;
import com.extole.client.rest.campaign.component.ComponentEndpoints;
import com.extole.client.rest.campaign.component.ComponentListRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.ComponentRestException;
import com.extole.client.rest.campaign.component.ComponentUpdateRequest;
import com.extole.client.rest.campaign.component.ComponentUpgradeRestException;
import com.extole.client.rest.campaign.component.DuplicatableComponentListRequest;
import com.extole.client.rest.campaign.component.anchor.AnchorDetailsResponse;
import com.extole.client.rest.campaign.component.anchor.AnchorRequest;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableUpdateResponse;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableValues;
import com.extole.client.rest.campaign.component.setting.BatchVariableTypeBasedValueAdjuster;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.ComponentVariableTargetRestException;
import com.extole.client.rest.campaign.component.setting.ComponentVariablesDownloadRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.component.setting.VariableTagsFilter;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.event.stream.EventStreamValidationRestException;
import com.extole.client.rest.impl.campaign.BuildAudienceExceptionMapper;
import com.extole.client.rest.impl.campaign.BuildCampaignControllerRestExceptionMapper;
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
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentTranslatableVariableMapper;
import com.extole.client.rest.impl.campaign.component.setting.SettingRequestMapperRepository;
import com.extole.client.rest.impl.campaign.component.setting.variable.batch.BatchComponentVariableValuesResponseBinding;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.FileFormatRestException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.file.parser.content.FileContentParseFormat;
import com.extole.file.parser.content.FileContentParser;
import com.extole.file.parser.content.FileContentReadStrategy;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.CampaignState;
import com.extole.model.entity.campaign.Component;
import com.extole.model.entity.campaign.ComponentOwner;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.InvalidComponentReferenceSocketNameException;
import com.extole.model.entity.campaign.InvalidExternalComponentReferenceException;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Subcomponent;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignComponent;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.model.entity.campaign.built.BuiltComponent;
import com.extole.model.entity.campaign.built.BuiltFrontendController;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.entity.campaign.built.BuiltVariable;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.BuiltCampaignService;
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
import com.extole.model.service.campaign.CampaignServiceDuplicateNameException;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignStartDateAfterStopDateException;
import com.extole.model.service.campaign.CampaignVersion;
import com.extole.model.service.campaign.CampaignVersionNotFoundException;
import com.extole.model.service.campaign.CampaignVersionState;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.BatchComponentVariableUpdateBuilder;
import com.extole.model.service.campaign.component.BatchVariableUpdateBuilder;
import com.extole.model.service.campaign.component.BuiltComponentQueryBuilder;
import com.extole.model.service.campaign.component.BuiltComponentService;
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
import com.extole.model.service.campaign.component.ComponentDuplicateBuilder.UnknownComponentSettingException;
import com.extole.model.service.campaign.component.ComponentDuplicationException;
import com.extole.model.service.campaign.component.ComponentFacetFilterMismatchException;
import com.extole.model.service.campaign.component.ComponentInstallFailedException;
import com.extole.model.service.campaign.component.ComponentNotFoundException;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.campaign.component.ComponentSocketFilterTypeMismatchException;
import com.extole.model.service.campaign.component.ComponentSocketMissingRequiredParameterException;
import com.extole.model.service.campaign.component.ComponentSocketNotFoundException;
import com.extole.model.service.campaign.component.ComponentUpgradeBuilder;
import com.extole.model.service.campaign.component.ComponentUpgradeNoOriginException;
import com.extole.model.service.campaign.component.ComponentUpgradeRootComponentUpgradeNotAllowedException;
import com.extole.model.service.campaign.component.ComponentUpgradeWithSocketSettingsNotAllowedException;
import com.extole.model.service.campaign.component.ExcessiveExternalComponentReferenceException;
import com.extole.model.service.campaign.component.InvalidCampaignComponentInstalledIntoSocketException;
import com.extole.model.service.campaign.component.MissingSourceComponentTypeException;
import com.extole.model.service.campaign.component.MissingTargetComponentByAbsoluteNameException;
import com.extole.model.service.campaign.component.MultipleComponentsInstalledIntoSingleSocketException;
import com.extole.model.service.campaign.component.OrphanExternalComponentReferenceException;
import com.extole.model.service.campaign.component.RedundantComponentReferenceException;
import com.extole.model.service.campaign.component.RootComponentDuplicationException;
import com.extole.model.service.campaign.component.SelfComponentReferenceException;
import com.extole.model.service.campaign.component.SubcomponentService;
import com.extole.model.service.campaign.component.UniqueComponentElementRequiredException;
import com.extole.model.service.campaign.component.anchor.AmbiguousComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.AmbiguousFallbackComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.InvalidComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.MissingComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.MissingFallbackComponentAnchorException;
import com.extole.model.service.campaign.component.anchor.UnrecognizedComponentAnchorsException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.component.reference.ComponentQueryBuilder;
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerException;
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
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentFacetException;
import com.extole.model.service.campaign.setting.SocketFilterInvalidComponentTypeException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetNameException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentFacetValueException;
import com.extole.model.service.campaign.setting.SocketFilterMissingComponentTypeException;
import com.extole.model.service.campaign.setting.UnavailableReferencedComponentSettingValidationException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.campaign.setting.VariableValueMissingException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.component.sharing.subscription.UnknownSourceClientException;
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
public class ComponentEndpointsImpl implements ComponentEndpoints {
    private static final Predicate<Set<String>> DEFAULT_TAGS_PREDICATE = tag -> true;
    private static final String ATTACHMENT_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s";
    private static final String TRANSLATABLE_TAG = "translatable";
    private static final String COMMA = ",";
    private static final int MAX_COMPONENTS_FETCH_SIZE = 1000;

    private final ComponentService componentService;
    private final BuiltComponentService builtComponentService;
    private final CampaignService campaignService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignComponentRestMapper campaignComponentRestMapper;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final BuiltCampaignService builtCampaignService;
    private final CampaignComponentTranslatableVariableMapper campaignComponentTranslatableVariableMapper;
    private final SubcomponentService subcomponentService;
    private final SettingRequestMapperRepository settingRequestMapperRepository;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public ComponentEndpointsImpl(ComponentService componentService,
        BuiltComponentService builtComponentService,
        CampaignService campaignService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignComponentRestMapper campaignComponentRestMapper,
        CampaignComponentSettingRestMapper settingRestMapper,
        BuiltCampaignService builtCampaignService,
        CampaignComponentTranslatableVariableMapper campaignComponentTranslatableVariableMapper,
        SubcomponentService subcomponentService,
        SettingRequestMapperRepository settingRequestMapperRepository,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentService = componentService;
        this.builtComponentService = builtComponentService;
        this.campaignService = campaignService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.campaignComponentRestMapper = campaignComponentRestMapper;
        this.settingRestMapper = settingRestMapper;
        this.builtCampaignService = builtCampaignService;
        this.campaignComponentTranslatableVariableMapper = campaignComponentTranslatableVariableMapper;
        this.subcomponentService = subcomponentService;
        this.settingRequestMapperRepository = settingRequestMapperRepository;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<ComponentResponse> list(String accessToken, ComponentListRequest componentListRequest)
        throws UserAuthorizationRestException, ComponentRestException, QueryLimitsRestException {
        if (componentListRequest == null) {
            componentListRequest = ComponentListRequest.builder().build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        validateLimits(componentListRequest.getLimit(), componentListRequest.getOffset());
        ZoneId timeZone = componentListRequest.getTimeZone();
        List<Component> components = listComponents(authorization, componentListRequest);
        return components
            .stream()
            .map(component -> campaignComponentRestMapper.toComponentResponse(component, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public List<ComponentResponse> listDuplicatableComponents(String accessToken,
        DuplicatableComponentListRequest componentListRequest)
        throws UserAuthorizationRestException, ComponentRestException, QueryLimitsRestException {
        if (componentListRequest == null) {
            componentListRequest = DuplicatableComponentListRequest.builder().build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        validateLimits(componentListRequest.getLimit(), componentListRequest.getOffset());
        ZoneId timeZone = componentListRequest.getTimeZone();
        List<Component> components = listDuplicatableComponents(authorization, componentListRequest);
        return components
            .stream()
            .map(component -> campaignComponentRestMapper.toComponentResponse(component, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public ComponentResponse get(String accessToken, String componentId, String version, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException, CampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Component component = getComponent(authorization, componentId, version);
        return campaignComponentRestMapper.toComponentResponse(component, timeZone);
    }

    @Override
    public List<AnchorDetailsResponse> getAnchors(String accessToken, String componentId, String version,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException, CampaignRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<CampaignVersion> campaignVersion = parseCampaignVersion(version);
            if (campaignVersion.isPresent()) {
                return builtComponentService.getAnchors(authorization, Id.valueOf(componentId), campaignVersion.get())
                    .stream()
                    .map(anchorDetails -> campaignComponentRestMapper.toAnchorDetails(anchorDetails))
                    .collect(Collectors.toList());
            }
            return builtComponentService.getAnchors(authorization, Id.valueOf(componentId))
                .stream()
                .map(anchorDetails -> campaignComponentRestMapper.toAnchorDetails(anchorDetails))
                .collect(Collectors.toList());
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
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings("MethodLength")
    @Override
    public ComponentResponse create(String accessToken,
        ComponentCreateRequest request,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignComponentRestException,
        CampaignUpdateRestException, ComponentTypeRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {

        String campaignId = request.getCampaignId()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", null)
                .build());
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            CampaignBuilder campaignBuilder = getCampaignBuilder(Id.valueOf(campaignId), authorization);
            CampaignComponentBuilder campaignComponentBuilder = campaignBuilder.addComponent();
            applyCreateRequestToBuilder(request, campaignComponentBuilder);
            Component created = campaignComponentBuilder.save();
            return campaignComponentRestMapper.toComponentResponse(created, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.COMPONENT_FACETS_NOT_FOUND)
                .addParameter("facets", e.getFacets())
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
        } catch (UnavailableReferencedComponentSettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.NOT_ACCESSIBLE_COMPONENT_ID)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
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
                .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
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
        } catch (VariableValueKeyLengthException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.VALUE_KEY_LENGTH_OUT_OF_RANGE)
                .addParameter("value_key", e.getValueKey())
                .addParameter("min_length", Integer.valueOf(e.getValueKeyMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getValueKeyMaxLength()))
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
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
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
        } catch (CampaignComponentException | CreativeArchiveIncompatibleApiVersionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

    }

    @SuppressWarnings("MethodLength")
    @Override
    public ComponentResponse update(String accessToken,
        String componentId,
        ComponentUpdateRequest request,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignRestException, CampaignComponentRestException,
        CampaignComponentValidationRestException, CampaignComponentRootValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, OmissibleRestException, CampaignUpdateRestException,
        ComponentTypeRestException, BuildWebhookRestException, BuildPrehandlerRestException,
        BuildRewardSupplierRestException, BuildClientKeyRestException, BuildAudienceRestException,
        EventStreamValidationRestException, OAuthClientKeyBuildRestException, ComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Component component = componentService.get(authorization, Id.valueOf(componentId));
            CampaignBuilder campaignBuilder = getCampaignBuilder(component.getCampaign().getId(), authorization);
            CampaignComponentBuilder campaignComponentBuilder =
                campaignBuilder.updateComponent(component.getCampaignComponent());
            applyUpdateRequestToBuilder(request, campaignComponentBuilder);
            Component updated = campaignComponentBuilder.save();
            return campaignComponentRestMapper.toComponentResponse(updated, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        } catch (CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.COMPONENT_FACETS_NOT_FOUND)
                .addParameter("facets", e.getFacets())
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
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
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
        } catch (CampaignComponentException | CreativeArchiveIncompatibleApiVersionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    // CHECKSTYLE.OFF: MethodLength
    @Override
    public ComponentResponse duplicate(String accessToken,
        String componentId,
        String expectedCurrentVersion,
        ComponentDuplicateRequest componentDuplicateRequest,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentDuplicateRestException, ComponentRestException,
        CampaignRestException, CampaignComponentValidationRestException, CampaignComponentRootValidationRestException,
        SettingValidationRestException, BuildCampaignRestException, CampaignUpdateRestException,
        CampaignComponentRestException, CreativeArchiveRestException, ComponentTypeRestException, SettingRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException, BuildCampaignControllerRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Component component = getSourceComponent(authorization, componentId);
        try {
            if (componentDuplicateRequest.getTargetCampaignId().isOmitted()) {
                CampaignComponentBuilder rootCampaignComponentBuilder =
                    componentService.duplicateRootComponentCampaign(authorization, component);
                applyRequestToRootComponentBuilder(componentDuplicateRequest, rootCampaignComponentBuilder);
                Component duplicatedComponent = rootCampaignComponentBuilder.save();
                return campaignComponentRestMapper.toComponentResponse(duplicatedComponent, timeZone);
            }
            Id<Campaign> campaignId = Id.valueOf(componentDuplicateRequest.getTargetCampaignId().getValue());
            CampaignBuilder campaignBuilder = campaignService.editCampaign(authorization, campaignId);
            campaignProvider.parseVersion(expectedCurrentVersion)
                .ifPresent(campaignBuilder::withExpectedVersion);
            ComponentDuplicateBuilder duplicateBuilder = campaignBuilder.createDuplicateComponentBuilder(component);

            applyDuplicateRequestToBuilder(componentDuplicateRequest, duplicateBuilder);
            Component duplicatedComponent = duplicateBuilder.duplicate();
            return campaignComponentRestMapper.toComponentResponse(duplicatedComponent, timeZone);
        } catch (CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.COMPONENT_FACETS_NOT_FOUND)
                .addParameter("facets", e.getFacets())
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
        } catch (MissingTargetComponentByAbsoluteNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.MISSING_TARGET_COMPONENT_BY_ABSOLUTE_NAME)
                .addParameter("absolute_name", e.getComponentAbsoluteName())
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_NOT_FOUND)
                .addParameter("campaign_id", componentDuplicateRequest.getTargetCampaignId())
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", componentDuplicateRequest.getTargetCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
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
        } catch (ComponentSocketFilterTypeMismatchException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.SOCKET_FILTER_TYPE_MISMATCH)
                .addParameter("source_component_types", e.getSourceComponentTypes())
                .addParameter("filter_component_type", e.getFilterComponentType())
                .withCause(e)
                .build();
        } catch (ComponentFacetFilterMismatchException e) {
            List<Map<String, String>> sourceFacets = e.getSourceFacets().stream()
                .map(value -> Map.of("name", value.getName(), "value", value.getValue()))
                .toList();
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.SOCKET_FILTER_COMPONENT_FACET_MISMATCH)
                .addParameter("source_component_facets", sourceFacets)
                .addParameter("filter_component_facet_name", e.getFilterFacetName())
                .addParameter("filter_component_facet_value", e.getFilterFacetValue())
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
        } catch (MultipleComponentsInstalledIntoSingleSocketException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.MULTIPLE_COMPONENTS_INSTALLED_INTO_SINGLE_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("target_component_id", e.getTargetComponentId())
                .addParameter("installed_component_ids", e.getInstalledComponentIds())
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
        } catch (ComponentSocketNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SOCKET_NOT_FOUND)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("campaign_id", e.getCampaignId())
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
        } catch (SettingNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (InvalidComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_INVALID)
                .addParameter("source_element_id", e.getAnchorDetails().getSourceElementId())
                .addParameter("source_element_type", e.getAnchorDetails().getSourceElementType())
                .addParameter("target_element_id", e.getTargetElementId())
                .addParameter("expected_target_element_types", e.getExpectedParentTypes())
                .withCause(e).build();
        } catch (MissingComponentAnchorException e) {
            throw RestExceptionBuilder.newBuilder(ComponentDuplicateRestException.class)
                .withErrorCode(ComponentDuplicateRestException.ANCHOR_MISSING)
                .addParameter("source_element_id", e.getAnchorDetails().getSourceElementId())
                .addParameter("source_element_type", e.getAnchorDetails().getSourceElementType())
                .withCause(e).build();
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
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (ComponentBuildSettingException e) {
            Map<String, RestExceptionResponse> exceptionResponses = Maps.newHashMap();
            e.getSuppressedExceptions().forEach((variableName, buildException) -> {
                RestException restException =
                    settingRestMapper.mapToBuildSettingRestException(buildException, e.getEntityId());
                exceptionResponses.put(variableName, new RestExceptionResponseBuilder(restException).build());
            });
            throw RestExceptionBuilder.newBuilder(CampaignComponentRestException.class)
                .withErrorCode(CampaignComponentRestException.SETTINGS_BUILD_FAILED)
                .addParameter("errors", exceptionResponses)
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
        } catch (BuildCampaignControllerException e) {
            throw BuildCampaignControllerRestExceptionMapper.getInstance().map(e);
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
        } catch (CampaignComponentTypeValidationException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.TYPE_VALIDATION_FAILED)
                .addParameter("validation_result", e.getValidationResult())
                .addParameter("name", e.getName())
                .addParameter("component_name", e.getComponentName())
                .addParameter("component_id", e.getComponentId().toString())
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
        } catch (InvalidCampaignComponentInstalledIntoSocketException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_INSTALLED_INTO_SOCKET)
                .addParameter("socket_name", e.getSocketName())
                .addParameter("install_component_id", e.getInstallComponentId())
                .withCause(e)
                .build();
        } catch (CampaignComponentException | CampaignServiceNameLengthException | CampaignServiceDuplicateNameException
            | AuthorizationException | CampaignServiceIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentResponse upgrade(String accessToken, String componentId, @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException, ComponentUpgradeRestException,
        CampaignRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentUpgradeBuilder componentUpgradeBuilder =
                componentService.upgradeComponent(authorization, Id.valueOf(componentId));
            Component upgradedComponent = componentUpgradeBuilder.save();
            return campaignComponentRestMapper.toComponentResponse(upgradedComponent, timeZone);

        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        } catch (ComponentUpgradeNoOriginException e) {
            throw RestExceptionBuilder.newBuilder(ComponentUpgradeRestException.class)
                .withErrorCode(ComponentUpgradeRestException.NO_UPGRADES_AVAILABLE)
                .addParameter("component_id", componentId)
                .build();
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_NOT_FOUND)
                .addParameter("campaign_id", e.getCampaignId())
                .build();
        } catch (ComponentUpgradeWithSocketSettingsNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(ComponentUpgradeRestException.class)
                .withErrorCode(ComponentUpgradeRestException.COMPONENTS_WITH_SOCKET_SETTINGS_NOT_ALLOWED)
                .addParameter("component_id", componentId)
                .addParameter("socket_settings_names", e.getSocketSettingsNames())
                .build();
        } catch (ComponentUpgradeRootComponentUpgradeNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(ComponentUpgradeRestException.class)
                .withErrorCode(ComponentUpgradeRestException.ROOT_COMPONENT_UPGRADE_NOT_ALLOWED)
                .addParameter("component_id", componentId)
                .build();
        } catch (CampaignComponentException | ComponentDuplicationException
            | CampaignComponentTypeValidationException | MissingTargetComponentByAbsoluteNameException
            | CampaignLockedException | RootComponentDuplicationException | UniqueComponentElementRequiredException
            | ComponentTypeNotFoundException | InvalidComponentReferenceException | BuildCampaignException
            | CreativeArchiveIncompatibleApiVersionException | ComponentSocketNotFoundException
            | CampaignComponentNameDuplicateException | StaleCampaignVersionException
            | ConcurrentCampaignUpdateException | CampaignComponentFacetsNotFoundException
            | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class).withErrorCode(
                FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }

    }

    @Override
    public ComponentResponse delete(String accessToken,
        String componentId,
        String expectedCurrentVersion,
        @Nullable ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentRestException, CampaignRestException,
        BuildCampaignRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Component componentToBeRemoved = componentService.get(authorization, Id.valueOf(componentId));
            if (isRootComponent(componentToBeRemoved.getCampaignComponent())) {
                deleteCampaign(authorization, expectedCurrentVersion, componentToBeRemoved);
            } else {
                deleteComponent(authorization, expectedCurrentVersion, componentToBeRemoved);
            }

            return campaignComponentRestMapper.toComponentResponse(componentToBeRemoved, timeZone);
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
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        } catch (CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.CAMPAIGN_LOCKED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_locks", e.getCampaignLocks())
                .withCause(e).build();
        } catch (CampaignNotFoundException | AuthorizationException e) {
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
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (CreativeArchiveJavascriptException | CreativeArchiveBuilderException
            | CampaignServiceNameLengthException | CampaignServiceIllegalCharacterInNameException
            | CampaignControllerTriggerBuildException | CampaignLabelMissingNameException
            | CampaignLabelDuplicateNameException | CampaignServiceNameMissingException
            | CampaignComponentNameDuplicateException | InvalidComponentReferenceException
            | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException | StepDataBuildException
            | CampaignScheduleException | CampaignGlobalDeleteException | CampaignGlobalArchiveException
            | CampaignGlobalStateChangeException | CampaignComponentTypeValidationException
            | ComponentTypeNotFoundException | ReferencedExternalElementException | IncompatibleRewardRuleException
            | CreativeVariableUnsupportedException | CampaignComponentException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<BuiltComponentResponse> listBuilt(String accessToken,
        @Nullable ComponentListRequest componentListRequest)
        throws UserAuthorizationRestException, ComponentRestException, QueryLimitsRestException {
        if (componentListRequest == null) {
            componentListRequest = ComponentListRequest.builder().build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        validateLimits(componentListRequest.getLimit(), componentListRequest.getOffset());
        try {
            BuiltComponentQueryBuilder queryBuilder = builtComponentService.listBuilt(authorization);

            componentListRequest.getName().ifPresent(name -> queryBuilder.withName(name));
            componentListRequest.getCampaignIds()
                .map(campaignsIdsString -> transformCommaSeparatedStringToSet(campaignsIdsString))
                .map(strings -> strings.stream().map(Id::<Campaign>valueOf).collect(Collectors.toSet()))
                .ifPresent(campaignIds -> queryBuilder.withCampaignIds(campaignIds));
            componentListRequest.getHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tags -> queryBuilder.withMatchingAtLeastOneTag(tags));
            componentListRequest.getHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tags -> queryBuilder.withMatchingAllTags(tags));
            componentListRequest.getExcludeHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tags -> queryBuilder.excludeMatchingAtLeastOneTag(tags));
            componentListRequest.getExcludeHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tags -> queryBuilder.excludeMatchingAllTags(tags));
            componentListRequest.getHavingAnyTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(types -> queryBuilder.withMatchingAtLeastOneType(types));
            componentListRequest.getHavingAllTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(types -> queryBuilder.withMatchingAllTypes(types));

            Optional<String> owner = componentListRequest.getOwner();
            if (owner.isPresent()) {
                parseOwner(owner.get()).ifPresent(parsedOwner -> queryBuilder.withComponentOwner(parsedOwner));
            }

            Optional<String> state = componentListRequest.getState();
            if (state.isPresent() && StringUtils.isNotBlank(state.get())) {
                Set<CampaignState> parsedStates = new HashSet<>();
                for (String stateString : state.get().split(COMMA)) {
                    parsedStates.add(parseState(stateString));
                }
                queryBuilder.withStates(parsedStates);
            }

            queryBuilder.withCampaignVersionState(componentListRequest.getVersionState()
                .map(versionState -> CampaignVersionState.valueOf(versionState.name()))
                .orElse(CampaignVersionState.LATEST));

            Optional<String> targetComponentId = componentListRequest.getTargetComponentId();
            if (targetComponentId.isPresent() && StringUtils.isNotBlank(targetComponentId.get())) {
                Component component = getComponent(authorization, targetComponentId.get());
                queryBuilder.withTargetCompatibility(component);

                if (componentListRequest.getTargetSocketName().isPresent()) {
                    queryBuilder.withTargetSocketName(componentListRequest.getTargetSocketName().get());
                }
            }

            queryBuilder.withShowAll(componentListRequest.getShowAll());
            queryBuilder.withLimit(componentListRequest.getLimit());
            queryBuilder.withOffset(componentListRequest.getOffset());

            ZoneId timeZone = componentListRequest.getTimeZone();
            return queryBuilder.list().stream()
                .map(component -> campaignComponentRestMapper.toBuiltComponentResponse(component, timeZone))
                .collect(toUnmodifiableList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltComponentResponse getBuilt(String accessToken, String componentId, String version,
        @Nullable ZoneId timeZone) throws UserAuthorizationRestException, ComponentRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        return campaignComponentRestMapper
            .toBuiltComponentResponse(getBuiltComponent(authorization, componentId, version), timeZone);
    }

    @Override
    public List<BatchComponentVariableUpdateResponse> batchGetComponentVariables(String accessToken,
        String componentId) throws UserAuthorizationRestException, ComponentRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        return getComponentVariableResponses(authorization, componentId, DEFAULT_TAGS_PREDICATE, false, false, false);
    }

    @Override
    public List<BatchComponentVariableUpdateResponse> batchUpdateComponentVariables(
        String accessToken,
        boolean ignoreUnknownVariables,
        List<BatchComponentVariableUpdateRequest> variablesFromRequest)
        throws UserAuthorizationRestException, SettingRestException, SettingValidationRestException,
        BuildCampaignRestException, ComponentVariableTargetRestException, BuildWebhookRestException,
        BuildPrehandlerRestException, BuildRewardSupplierRestException, BuildClientKeyRestException,
        BuildAudienceRestException, EventStreamValidationRestException, OAuthClientKeyBuildRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        validateBatchComponentVariableUpdateRequest(variablesFromRequest);
        BatchComponentVariableUpdateBuilder batchUpdateBuilder =
            componentService.batchComponentVariableUpdate(authorization);
        List<Subcomponent> subcomponents = subcomponentService.getGlobalRootSubcomponents(authorization);
        Map<String, Subcomponent> subcomponentsByExternalAbsoluteName = subcomponents
            .stream()
            .flatMap(subcomponent -> subcomponent.getExternalAbsolutePaths().stream()
                .map(absoluteName -> Pair.of(absoluteName, subcomponent)))
            .collect(toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
        Map<String, Map<String, Variable>> variableAbsoluteNames =
            groupVariablesByExternalAbsoluteNameAndVariableName(subcomponentsByExternalAbsoluteName);

        try {
            List<BatchComponentVariableUpdateRequest> unexpectedVariables = new LinkedList<>();
            for (BatchComponentVariableUpdateRequest variableFromRequest : variablesFromRequest) {
                Map<String, Variable> variablesByName = variableAbsoluteNames
                    .get(variableFromRequest.getComponentAbsolutePath());
                if (variablesByName == null) {
                    unexpectedVariables.add(variableFromRequest);
                    continue;
                }

                Variable currentVariable = variablesByName.get(variableFromRequest.getName());
                if (currentVariable == null) {
                    unexpectedVariables.add(variableFromRequest);
                    continue;
                }

                BatchVariableUpdateBuilder variableBuilder = batchUpdateBuilder.withVariable(
                    subcomponentsByExternalAbsoluteName
                        .get(variableFromRequest.getComponentAbsolutePath())
                        .getCampaignComponent().getId(),
                    currentVariable.getName());

                variableBuilder.withValues(variableFromRequest.getValues());
                variableBuilder
                    .withSettingType(SettingType.valueOf(variableFromRequest.getSettingType().name()));
                variableFromRequest.getDisplayName().ifPresent(value -> variableBuilder.withDisplayName(value));
                variableBuilder.withVariableSource(currentVariable.getSource());
                if (!currentVariable.getValues().equals(variableFromRequest.getValues())) {
                    variableBuilder.withVariableSource(VariableSource.LOCAL);
                } else {
                    variableBuilder.withVariableSource(currentVariable.getSource());
                }
            }

            validateUnexpectedVariables(unexpectedVariables, ignoreUnknownVariables, StringUtils.EMPTY);

            Set<String> requestAbsolutePaths =
                variablesFromRequest.stream().map(variable -> variable.getComponentAbsolutePath())
                    .collect(toUnmodifiableSet());
            BatchComponentVariableUpdateBuilder.BatchVariableUpdate updateResult = batchUpdateBuilder.save();
            return mapBatchVariableUpdateToResponseList(requestAbsolutePaths,
                subcomponents, updateResult,
                settingRestMapper::mapVariableToBatchVariableUpdateResponse);
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
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (CampaignNotFoundException | CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
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
        }
    }

    @Override
    @BatchComponentVariableValuesResponseBinding
    public Response batchGetComponentVariablesValues(String accessToken,
        String componentId,
        Optional<String> contentType,
        Optional<String> format,
        Optional<String> filename,
        ComponentVariablesDownloadRequest variablesDownloadRequest)
        throws UserAuthorizationRestException, ComponentRestException {

        if (variablesDownloadRequest == null) {
            variablesDownloadRequest = ComponentVariablesDownloadRequest.builder().build();
        }
        Predicate<Set<String>> tagsFilter = constructTagsFilter(variablesDownloadRequest);
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        FileContentParseFormat fileContentParseFormat =
            getFormat(format, contentType).orElse(FileContentParseFormat.CSV);

        List<BatchComponentVariableValues> batchValues = toSortedBatchValues(
            getComponentVariableResponses(authorization, componentId, tagsFilter,
                variablesDownloadRequest.getExcludeDisabledCreatives(), variablesDownloadRequest.getEnabledVariants(),
                variablesDownloadRequest.getExcludeInheriting()));

        String contentDisposition = filename.map(value -> createAttachmentContentDisposition(value))
            .orElseGet(() -> createAttachmentContentDisposition(componentId, fileContentParseFormat.getExtension()));

        return Response.ok(batchValues)
            .type(fileContentParseFormat.getMimeType())
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .build();
    }

    @Override
    public List<BatchComponentVariableUpdateResponse> batchUpdateComponentVariablesValues(
        String accessToken,
        Optional<String> targetComponentId,
        boolean ignoreUnknownVariables,
        FileInputStreamRequest fileRequest)
        throws UserAuthorizationRestException, SettingValidationRestException, FileFormatRestException,
        SettingRestException, BuildCampaignRestException, ComponentRestException, ComponentVariableTargetRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException {

        validatePresenceOfRequestBody(fileRequest);
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        List<BatchComponentVariableValues> variablesFromRequest = extractVariablesFromFileRequest(fileRequest);
        validateBatchComponentVariableValues(variablesFromRequest);

        BatchComponentVariableUpdateBuilder batchUpdateBuilder =
            componentService.batchComponentVariableUpdate(authorization);
        List<Subcomponent> subcomponents = subcomponentService.getGlobalRootSubcomponents(authorization);

        try {
            if (targetComponentId.isPresent()) {
                subcomponents = subcomponentService.getSubtree(subcomponents, Id.valueOf(targetComponentId.get()));
            }
            Map<String, Subcomponent> subcomponentsByExternalAbsoluteName = subcomponents
                .stream()
                .flatMap(subcomponent -> subcomponent.getExternalAbsolutePaths().stream()
                    .map(absoluteName -> Pair.of(absoluteName, subcomponent)))
                .collect(toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
            Map<String, Map<String, Variable>> variableAbsoluteNames =
                groupVariablesByExternalAbsoluteNameAndVariableName(subcomponentsByExternalAbsoluteName);

            List<BatchComponentVariableValues> unexpectedVariables = new LinkedList<>();
            for (BatchComponentVariableValues variableFromRequest : variablesFromRequest) {
                Map<String, Variable> variablesByName = variableAbsoluteNames
                    .get(variableFromRequest.getComponentAbsolutePath());
                if (variablesByName == null) {
                    unexpectedVariables.add(variableFromRequest);
                    continue;
                }

                Variable currentVariable = variablesByName.get(variableFromRequest.getName());
                if (currentVariable == null) {
                    unexpectedVariables.add(variableFromRequest);
                    continue;
                }

                BatchVariableUpdateBuilder variableBuilder = batchUpdateBuilder.withVariable(
                    subcomponentsByExternalAbsoluteName
                        .get(variableFromRequest.getComponentAbsolutePath())
                        .getCampaignComponent().getId(),
                    currentVariable.getName());

                variableBuilder.withValues(variableFromRequest.getValues());
                variableBuilder
                    .withSettingType(SettingType.valueOf(variableFromRequest.getSettingType().name()));
                variableFromRequest.getDisplayName().ifPresent(value -> variableBuilder.withDisplayName(value));
                variableBuilder.withVariableSource(currentVariable.getSource());
                if (!currentVariable.getValues().equals(variableFromRequest.getValues())) {
                    variableBuilder.withVariableSource(VariableSource.LOCAL);
                } else {
                    variableBuilder.withVariableSource(currentVariable.getSource());
                }
            }

            validateUnexpectedVariables(unexpectedVariables, ignoreUnknownVariables,
                targetComponentId.orElse(StringUtils.EMPTY));

            Set<String> requestAbsolutePaths =
                variablesFromRequest.stream().map(variable -> variable.getComponentAbsolutePath())
                    .collect(toUnmodifiableSet());
            BatchComponentVariableUpdateBuilder.BatchVariableUpdate updateResult = batchUpdateBuilder.save();
            return mapBatchVariableUpdateToResponseList(requestAbsolutePaths,
                subcomponents, updateResult,
                settingRestMapper::mapVariableToBatchVariableUpdateResponse);
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
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (CampaignNotFoundException | CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
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
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", e.getComponentId())
                .withCause(e)
                .build();
        }
    }

    @Override
    @BatchComponentVariableValuesResponseBinding
    public Response batchGetTranslatableComponentVariablesValues(String accessToken,
        String componentId,
        Optional<String> contentType,
        Optional<String> format,
        Optional<String> filename,
        ComponentVariablesDownloadRequest variablesDownloadRequest)
        throws UserAuthorizationRestException, ComponentRestException {

        if (variablesDownloadRequest == null) {
            variablesDownloadRequest = ComponentVariablesDownloadRequest.builder().build();
        }
        Predicate<Set<String>> tagsFilter = constructTagsFilter(variablesDownloadRequest);
        tagsFilter = tagsFilter.and(tags -> tags.contains(TRANSLATABLE_TAG));
        Authorization authorization = authorizationProvider.getUserAuthorization(accessToken);
        FileContentParseFormat fileContentParseFormat =
            getFormat(format, contentType).orElse(FileContentParseFormat.CSV);

        List<BatchComponentVariableValues> batchValues = toSortedBatchValues(
            getComponentVariableResponses(authorization, componentId, tagsFilter,
                variablesDownloadRequest.getExcludeDisabledCreatives(), variablesDownloadRequest.getEnabledVariants(),
                variablesDownloadRequest.getExcludeInheriting()))
                    .stream()
                    .map(campaignComponentTranslatableVariableMapper::mapToTranslatableVariableValue)
                    .collect(toUnmodifiableList());

        String contentDisposition = filename.map(value -> createAttachmentContentDisposition(value))
            .orElseGet(() -> createAttachmentContentDisposition(componentId, fileContentParseFormat.getExtension()));

        return Response.ok(batchValues)
            .type(fileContentParseFormat.getMimeType())
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .build();
    }

    @Override
    public List<BatchComponentVariableUpdateResponse> batchUpdateTranslatableComponentVariablesValues(
        String accessToken,
        Optional<String> targetComponentId,
        boolean ignoreUnknownVariables,
        FileInputStreamRequest fileRequest)
        throws UserAuthorizationRestException, SettingValidationRestException, FileFormatRestException,
        SettingRestException, BuildCampaignRestException, ComponentVariableTargetRestException, ComponentRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException {

        validatePresenceOfRequestBody(fileRequest);
        Authorization authorization = authorizationProvider.getUserAuthorization(accessToken);

        FileContentParseFormat fileContentParseFormat = fileFormatByName(fileRequest.getAttributes().getFileName());
        Consumer<ObjectNode> valueModifier = BatchVariableTypeBasedValueAdjuster.getInstance();

        List<BatchComponentVariableValues> variablesFromRequest =
            extractTranslatableVariablesFromFileRequest(fileRequest,
                FileContentParser.getReadStrategy(fileContentParseFormat), valueModifier);
        validateBatchComponentVariableValues(variablesFromRequest);

        BatchComponentVariableUpdateBuilder batchUpdateBuilder =
            componentService.batchComponentVariableUpdate(authorization);
        List<Subcomponent> subcomponents = subcomponentService.getGlobalRootSubcomponents(authorization);

        try {
            if (targetComponentId.isPresent()) {
                subcomponents = subcomponentService.getSubtree(subcomponents, Id.valueOf(targetComponentId.get()));
            }

            Map<String, Subcomponent> subcomponentsByExternalAbsoluteName = subcomponents
                .stream()
                .flatMap(subcomponent -> subcomponent.getExternalAbsolutePaths().stream()
                    .map(absoluteName -> Pair.of(absoluteName, subcomponent)))
                .collect(toUnmodifiableMap(pair -> pair.getLeft(), pair -> pair.getRight()));
            Map<String, Map<String, Variable>> variableAbsoluteNames =
                groupVariablesByExternalAbsoluteNameAndVariableName(subcomponentsByExternalAbsoluteName);

            List<BatchComponentVariableValues> matchedNonTranslatableVariables = new LinkedList<>();
            List<BatchComponentVariableValues> unexpectedVariables = new LinkedList<>();
            for (BatchComponentVariableValues variableFromRequest : variablesFromRequest) {
                Map<String, Variable> variablesByName = variableAbsoluteNames
                    .get(variableFromRequest.getComponentAbsolutePath());
                if (variablesByName == null) {
                    unexpectedVariables.add(variableFromRequest);
                    continue;
                }

                Variable currentVariable = variablesByName.get(variableFromRequest.getName());
                if (currentVariable == null) {
                    unexpectedVariables.add(variableFromRequest);
                    continue;
                }

                if (!currentVariable.getTags().contains(TRANSLATABLE_TAG)) {
                    matchedNonTranslatableVariables.add(variableFromRequest);
                    continue;
                }

                BatchVariableUpdateBuilder variableBuilder = batchUpdateBuilder.withVariable(
                    subcomponentsByExternalAbsoluteName
                        .get(variableFromRequest.getComponentAbsolutePath())
                        .getCampaignComponent().getId(),
                    currentVariable.getName());

                Map<String,
                    BuildtimeEvaluatable<VariableBuildtimeContext,
                        RuntimeEvaluatable<Object, Optional<Object>>>> requestValues =
                            campaignComponentTranslatableVariableMapper.convertToHandlebarsIfNeeded(
                                variableFromRequest.getValues(),
                                currentVariable);
                variableBuilder.withValues(requestValues);
                variableBuilder
                    .withSettingType(SettingType.valueOf(variableFromRequest.getSettingType().name()));
                variableFromRequest.getDisplayName().ifPresent(value -> variableBuilder.withDisplayName(value));
                variableBuilder.withVariableSource(currentVariable.getSource());
                if (!currentVariable.getValues().equals(requestValues)) {
                    variableBuilder.withVariableSource(VariableSource.LOCAL);
                } else {
                    variableBuilder.withVariableSource(currentVariable.getSource());
                }
            }

            validateUnexpectedVariables(unexpectedVariables, ignoreUnknownVariables,
                targetComponentId.orElse(StringUtils.EMPTY));

            if (!matchedNonTranslatableVariables.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                    .withErrorCode(SettingValidationRestException.NON_TRANSLATABLE_VARIABLE_CAN_NOT_BE_UPDATED)
                    .addParameter("forbidden_variables", matchedNonTranslatableVariables)
                    .build();
            }

            Set<String> requestAbsolutePaths =
                variablesFromRequest.stream().map(variable -> variable.getComponentAbsolutePath())
                    .collect(toUnmodifiableSet());
            BatchComponentVariableUpdateBuilder.BatchVariableUpdate updateResult = batchUpdateBuilder.save();
            return mapBatchVariableUpdateToResponseList(requestAbsolutePaths,
                subcomponents, updateResult,
                settingRestMapper::mapVariableToBatchVariableUpdateResponse);
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
        } catch (CampaignNotFoundException | CampaignLockedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", e.getComponentId())
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
        } catch (InvalidVariableTranslatableValueException e) {
            throw TranslatableVariableExceptionMapper.getInstance().map(e);
        } catch (BuildPrehandlerException e) {
            throw BuildPrehandlerExceptionMapper.getInstance().map(e);
        } catch (BuildWebhookException e) {
            throw BuildWebhookExceptionMapper.getInstance().map(e);
        } catch (BuildCampaignException e) {
            throw BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }

    @Override
    public List<BuiltComponentResponse> listBuiltDuplicatableComponents(String accessToken,
        DuplicatableComponentListRequest componentListRequest)
        throws UserAuthorizationRestException, ComponentRestException, QueryLimitsRestException {
        if (componentListRequest == null) {
            componentListRequest = DuplicatableComponentListRequest.builder().build();
        }
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        validateLimits(componentListRequest.getLimit(), componentListRequest.getOffset());
        ZoneId timeZone = componentListRequest.getTimeZone();
        return listBuiltDuplicatableComponents(authorization, componentListRequest)
            .stream()
            .map(component -> campaignComponentRestMapper.toBuiltComponentResponse(component, timeZone))
            .collect(Collectors.toList());
    }

    private void validateBatchComponentVariableValues(List<BatchComponentVariableValues> variablesFromRequest)
        throws SettingRestException {

        List<String> variableNamesWithMissedTypes = Lists.newLinkedList();
        for (BatchComponentVariableValues variable : variablesFromRequest) {
            if (variable.getSettingType() == null) {
                variableNamesWithMissedTypes.add(variable.getName());
            }
        }

        if (!variableNamesWithMissedTypes.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.VARIABLE_BATCH_UPDATE_MISSING_TYPE)
                .addParameter("variables", variableNamesWithMissedTypes)
                .build();
        }

        Set<Pair<String, String>> variableNames = new HashSet<>();
        for (BatchComponentVariableValues variable : variablesFromRequest) {
            if (!variableNames.add(Pair.of(variable.getComponentAbsolutePath(), variable.getName()))) {
                throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                    .withErrorCode(SettingRestException.VARIABLE_BATCH_UPDATE_HAS_DUPLICATED_VARIABLE_NAME)
                    .addParameter("absolute_name", variable.getComponentAbsolutePath())
                    .addParameter("variable_name", variable.getName())
                    .build();
            }
        }
    }

    private void validateBatchComponentVariableUpdateRequest(
        List<BatchComponentVariableUpdateRequest> variablesFromRequest)
        throws SettingRestException {

        List<String> variableNamesWithMissedTypes = Lists.newLinkedList();
        for (BatchComponentVariableUpdateRequest variable : variablesFromRequest) {
            if (variable.getSettingType() == null) {
                variableNamesWithMissedTypes.add(variable.getName());
            }
        }
        if (!variableNamesWithMissedTypes.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.VARIABLE_BATCH_UPDATE_MISSING_TYPE)
                .addParameter("variables", variableNamesWithMissedTypes)
                .build();
        }

        Set<Pair<String, String>> variableNames = new HashSet<>();
        for (BatchComponentVariableUpdateRequest variable : variablesFromRequest) {
            if (!variableNames.add(Pair.of(variable.getComponentAbsolutePath(), variable.getName()))) {
                throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                    .withErrorCode(SettingRestException.VARIABLE_BATCH_UPDATE_HAS_DUPLICATED_VARIABLE_NAME)
                    .addParameter("absolute_name", variable.getComponentAbsolutePath())
                    .addParameter("variable_name", variable.getName())
                    .build();
            }
        }
    }

    private <R> List<R> mapBatchVariableUpdateToResponseList(
        Set<String> externalPathsFromRequest,
        List<Subcomponent> subcomponents,
        BatchComponentVariableUpdateBuilder.BatchVariableUpdate variableUpdate,
        BiFunction<String, Variable, R> variableMapper) {

        Map<Id<CampaignComponent>, Subcomponent> subcomponentsByComponentId = groupSubcomponentsByComponentId(
            subcomponents);

        return variableUpdate.getChangedVariablesByComponent().entrySet().stream().flatMap(entry -> entry.getValue()
            .stream().map(variable -> {
                Subcomponent subcomponent =
                    subcomponentsByComponentId.get(entry.getKey().getCampaignComponent().getId());
                String externalAbsolutePath =
                    subcomponent.getExternalAbsolutePaths().stream().findFirst().orElseThrow();
                externalAbsolutePath = subcomponent.getExternalAbsolutePaths().stream()
                    .filter(absolutePath -> externalPathsFromRequest.contains(absolutePath))
                    .findFirst().orElse(externalAbsolutePath);
                return variableMapper.apply(externalAbsolutePath, variable);
            })).collect(toUnmodifiableList());
    }

    private Map<Id<CampaignComponent>, Subcomponent> groupSubcomponentsByComponentId(List<Subcomponent> subcomponents) {
        return subcomponents.stream()
            .collect(Collectors.toUnmodifiableMap(subcomponent -> subcomponent.getCampaignComponent().getId(),
                Function.identity()));
    }

    private String createAttachmentContentDisposition(String componentId, String fileExtension) {
        String filename = String.format("%s-variables.%s", componentId, fileExtension);
        return createAttachmentContentDisposition(filename);
    }

    private String createAttachmentContentDisposition(String filename) {
        return String.format(ATTACHMENT_CONTENT_DISPOSITION_FORMATTER, filename);
    }

    private List<BatchComponentVariableValues> toSortedBatchValues(
        List<BatchComponentVariableUpdateResponse> variableUpdateResponses) {

        return variableUpdateResponses.stream()
            .map(value -> new BatchComponentVariableValues(value.getComponentAbsolutePath(), value.getName(),
                value.getDisplayName(), value.getSettingType(), value.getValues()))
            .sorted(Comparator.comparing((BatchComponentVariableValues value) -> value.getComponentAbsolutePath())
                .thenComparing(value -> value.getName()))
            .collect(toUnmodifiableList());
    }

    private List<BatchComponentVariableUpdateResponse> getComponentVariableResponses(
        Authorization authorization, String componentId, Predicate<Set<String>> tagsFilter,
        boolean excludeDisabledCreatives, boolean enabledVariants, boolean excludeInheriting)
        throws ComponentRestException {

        List<Subcomponent> allSubcomponents = subcomponentService.getGlobalRootSubcomponents(authorization);
        List<Subcomponent> subcomponents;
        try {
            subcomponents = subcomponentService.getSubtree(allSubcomponents, Id.valueOf(componentId));
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        }
        Predicate<String> variableValueFilter = initializeVariableValueFilter(enabledVariants, subcomponents);
        List<BatchComponentVariableUpdateResponse> variables = new ArrayList<>();
        Map<Id<Campaign>, BuiltCampaign> cachedCampaigns = new HashMap<>();
        for (Subcomponent subcomponent : subcomponents) {
            Id<CampaignComponent> candidateComponentId = subcomponent.getCampaignComponent().getId();
            if (excludeDisabledCreatives) {
                BuiltCampaign builtCampaign = cachedCampaigns.computeIfAbsent(subcomponent.getCampaign().getId(),
                    (key) -> getBuiltCampaign(subcomponent.getCampaign()));
                if (checkCreativesAreDisabled(builtCampaign, candidateComponentId)) {
                    continue;
                }
            }
            for (Setting setting : getVariables(subcomponent.getCampaignComponent().getSettings())) {
                if (!tagsFilter.test(setting.getTags())) {
                    continue;
                }
                Variable variable = (Variable) setting;
                if (excludeInheriting && variable.getSource() != VariableSource.LOCAL) {
                    BuiltCampaign builtCampaign =
                        cachedCampaigns.computeIfAbsent(subcomponent.getCampaign().getId(),
                            (key) -> getBuiltCampaign(subcomponent.getCampaign()));
                    BuiltCampaignComponent builtComponent = builtCampaign.getComponents().stream()
                        .filter(component -> component.getId().equals(candidateComponentId)).findFirst().get();
                    BuiltVariable builtVariable = getBuiltVariables(builtComponent.getSettings()).stream()
                        .filter(
                            builtVariableCandidate -> builtVariableCandidate.getName().equals(setting.getName()))
                        .findFirst().get();
                    if (!builtVariable.getSourceComponentId().equals(candidateComponentId)) {
                        continue;
                    }
                }
                String externalAbsolutePath =
                    subcomponent.getExternalAbsolutePaths().stream().findFirst().orElseThrow();
                BatchComponentVariableUpdateResponse response = settingRestMapper
                    .mapVariableToBatchVariableUpdateResponse(externalAbsolutePath, variable);

                Map<String,
                    BuildtimeEvaluatable<VariableBuildtimeContext,
                        RuntimeEvaluatable<Object, Optional<Object>>>> newValues =
                            variable.getValues().entrySet().stream()
                                .filter(entry -> variableValueFilter.test(entry.getKey()))
                                .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
                variables.add(BatchComponentVariableUpdateResponse.builder(response)
                    .withValues(newValues)
                    .build());
            }
        }
        return variables;
    }

    private boolean checkCreativesAreDisabled(BuiltCampaign builtCampaign, Id<CampaignComponent> candidateComponentId) {
        List<BuiltFrontendController> frontendControllers = builtCampaign.getFrontendControllers();

        return frontendControllers.stream().anyMatch(controller -> {

            if (controller.getActions().stream()
                .anyMatch(action -> action.getComponentReferences().stream()
                    .anyMatch(
                        reference -> reference.getComponentId().equals(candidateComponentId)))) {

                boolean allCreativeActionsAreDisabled = controller.getActions()
                    .stream()
                    .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
                    .noneMatch(value -> value.getEnabled().booleanValue());

                return !atLeastOneInputEventTriggerIsEnabled(controller)
                    || allCreativeActionsAreDisabled
                    || !controller.isEnabled();
            }
            return false;
        });
    }

    private boolean atLeastOneInputEventTriggerIsEnabled(BuiltFrontendController controller) {
        for (BuiltCampaignControllerTrigger trigger : controller.getTriggers()) {
            if (trigger.getType() == CampaignControllerTriggerType.EVENT
                && trigger.getEnabled().booleanValue()) {
                return true;
            }
        }
        return false;
    }

    private Predicate<String> initializeVariableValueFilter(boolean enabledVariants, List<Subcomponent> subcomponents) {
        if (enabledVariants) {
            Map<Id<Campaign>, List<String>> variantsByCampaignId = new HashMap<>();
            subcomponents.forEach(subcomponent -> {
                variantsByCampaignId.computeIfAbsent(subcomponent.getCampaign().getId(), (key) -> {
                    BuiltCampaign builtCampaign = getBuiltCampaign(subcomponent.getCampaign());
                    return builtCampaign.getVariants();
                });
            });
            Set<String> enabledVariantsRecords = variantsByCampaignId.values().stream()
                .flatMap(variantsList -> variantsList.stream())
                .collect(Collectors.toUnmodifiableSet());
            return value -> enabledVariantsRecords.contains(value);
        }
        return value -> true;
    }

    private BuiltCampaign getBuiltCampaign(Campaign campaign) {
        try {
            return builtCampaignService.buildCampaign(campaign);
        } catch (BuildCampaignException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<BatchComponentVariableValues> extractVariablesFromFileRequest(FileInputStreamRequest request)
        throws FileFormatRestException {
        FileContentParseFormat fileContentParseFormat = fileFormatByName(request.getAttributes().getFileName());
        FileContentReadStrategy readStrategy = FileContentParser.getReadStrategy(fileContentParseFormat);
        try {
            return readStrategy.readFileContent(request.getInputStream(),
                new TypeReference<>() {}, BatchVariableTypeBasedValueAdjuster.getInstance());
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(FileFormatRestException.class)
                .withErrorCode(FileFormatRestException.INVALID_FILE_CONTENT)
                .build();
        }
    }

    private List<BatchComponentVariableValues>
        extractTranslatableVariablesFromFileRequest(FileInputStreamRequest request,
            FileContentReadStrategy readStrategy,
            Consumer<ObjectNode> valueModifier)
            throws FileFormatRestException {

        try {
            return readStrategy.readFileContent(request.getInputStream(),
                new TypeReference<>() {}, valueModifier);
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(FileFormatRestException.class)
                .withErrorCode(FileFormatRestException.INVALID_FILE_CONTENT)
                .build();
        }
    }

    private FileContentParseFormat fileFormatByName(String fileName) throws FileFormatRestException {
        String extensionFromName = FilenameUtils.getExtension(fileName);
        return FileContentParseFormat.fromExtension(extensionFromName)
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(FileFormatRestException.class)
                .withErrorCode(FileFormatRestException.UNSUPPORTED_FILE_FORMAT)
                .addParameter("file_extension", extensionFromName)
                .build());
    }

    private void validatePresenceOfRequestBody(FileInputStreamRequest request) {
        if (request == null) {
            throw RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                .build();
        }
    }

    private Optional<FileContentParseFormat> getFormat(Optional<String> format,
        Optional<String> contentType) {
        if (format.isPresent()) {
            return FileContentParseFormat.fromExtension(format.get());
        }
        if (contentType.isPresent()) {
            return FileContentParseFormat.fromMimeType(contentType.get());
        }
        return Optional.empty();
    }

    private List<Component> listComponents(ClientAuthorization authorization, ComponentListRequest componentListRequest)
        throws ComponentRestException, UserAuthorizationRestException {
        try {
            ComponentQueryBuilder queryBuilder = componentService.createQueryBuilder(authorization);

            Optional<String> owner = componentListRequest.getOwner();
            if (owner.isPresent()) {
                parseOwner(owner.get()).ifPresent(parsedOwner -> queryBuilder.withComponentOwner(parsedOwner));
            }

            Optional<String> state = componentListRequest.getState();
            if (state.isPresent() && StringUtils.isNotBlank(state.get())) {
                Set<CampaignState> parsedStates = new HashSet<>();
                for (String stateString : state.get().split(COMMA)) {
                    parsedStates.add(parseState(stateString));
                }
                queryBuilder.withStates(parsedStates);
            }

            queryBuilder.withCampaignVersionState(componentListRequest.getVersionState()
                .map(versionState -> CampaignVersionState.valueOf(versionState.name()))
                .orElse(CampaignVersionState.LATEST));

            Optional<String> targetComponentId = componentListRequest.getTargetComponentId();
            if (targetComponentId.isPresent() && StringUtils.isNotBlank(targetComponentId.get())) {
                Component component = getComponent(authorization, targetComponentId.get());
                queryBuilder.withTargetCompatibility(component);

                if (componentListRequest.getTargetSocketName().isPresent()) {
                    queryBuilder.withTargetSocketName(componentListRequest.getTargetSocketName().get());
                }
            }

            componentListRequest.getName().ifPresent(name -> queryBuilder.withName(name));
            componentListRequest.getCampaignIds()
                .map(camapignIdsString -> transformCommaSeparatedStringToSet(camapignIdsString))
                .map(strings -> strings.stream().map(Id::<Campaign>valueOf).collect(Collectors.toSet()))
                .ifPresent(campaignIdsSet -> queryBuilder.withCampaignIds(campaignIdsSet));
            componentListRequest.getHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.withMatchingAtLeastOneTag(tagsSet));
            componentListRequest.getHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.withMatchingAllTags(tagsSet));
            componentListRequest.getExcludeHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.excludeMatchingAtLeastOneTag(tagsSet));
            componentListRequest.getExcludeHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.excludeMatchingAllTags(tagsSet));
            componentListRequest.getHavingAnyTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(typesSet -> queryBuilder.withMatchingAtLeastOneType(typesSet));
            componentListRequest.getHavingAllTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(typesSet -> queryBuilder.withMatchingAllTypes(typesSet));

            queryBuilder.withShowAll(componentListRequest.getShowAll());
            queryBuilder.withLimit(componentListRequest.getLimit());
            queryBuilder.withOffset(componentListRequest.getOffset());
            return queryBuilder.list();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private List<Component> listDuplicatableComponents(ClientAuthorization authorization,
        DuplicatableComponentListRequest componentListRequest)
        throws ComponentRestException, UserAuthorizationRestException {
        try {
            ComponentQueryBuilder queryBuilder = componentService.createQueryBuilder(authorization);
            queryBuilder.withCampaignVersionState(componentListRequest.getVersionState()
                .map(versionState -> CampaignVersionState.valueOf(versionState.name()))
                .orElse(CampaignVersionState.PUBLISHED));
            componentListRequest.getSourceClientIds()
                .map(sourceClientIds -> transformCommaSeparatedStringToSet(sourceClientIds))
                .map(stringIds -> stringIds
                    .stream()
                    .map(Id::<ClientHandle>valueOf)
                    .collect(Collectors.toSet()))
                .ifPresent(sourceClientIds -> queryBuilder.withSourceClientIds(sourceClientIds));

            Optional<String> state = componentListRequest.getState();
            if (state.isPresent() && StringUtils.isNotBlank(state.get())) {
                Set<CampaignState> parsedStates = new HashSet<>();
                for (String stateString : state.get().split(COMMA)) {
                    parsedStates.add(parseState(stateString));
                }
                queryBuilder.withStates(parsedStates);
            }

            Optional<String> targetComponentId = componentListRequest.getTargetComponentId();
            if (targetComponentId.isPresent() && StringUtils.isNotBlank(targetComponentId.get())) {
                Component component = getComponent(authorization, targetComponentId.get());
                queryBuilder.withTargetCompatibility(component);

                if (componentListRequest.getTargetSocketName().isPresent()) {
                    queryBuilder.withTargetSocketName(componentListRequest.getTargetSocketName().get());
                }
            }

            componentListRequest.getName().ifPresent(name -> queryBuilder.withName(name));
            componentListRequest.getCampaignIds()
                .map(camapignIdsString -> transformCommaSeparatedStringToSet(camapignIdsString))
                .map(strings -> strings.stream().map(Id::<Campaign>valueOf).collect(Collectors.toSet()))
                .ifPresent(campaignIdsSet -> queryBuilder.withCampaignIds(campaignIdsSet));
            componentListRequest.getHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.withMatchingAtLeastOneTag(tagsSet));
            componentListRequest.getHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.withMatchingAllTags(tagsSet));
            componentListRequest.getExcludeHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.excludeMatchingAtLeastOneTag(tagsSet));
            componentListRequest.getExcludeHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.excludeMatchingAllTags(tagsSet));
            componentListRequest.getHavingAnyTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(typesSet -> queryBuilder.withMatchingAtLeastOneType(typesSet));
            componentListRequest.getHavingAllTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(typesSet -> queryBuilder.withMatchingAllTypes(typesSet));

            queryBuilder.withShowAll(componentListRequest.getShowAll());
            queryBuilder.withLimit(componentListRequest.getLimit());
            queryBuilder.withOffset(componentListRequest.getOffset());
            return queryBuilder.listDuplicatable();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UnknownSourceClientException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.UNKNOWN_SOURCE_CLIENT_IDS)
                .withCause(e)
                .addParameter("known_source_client_ids", e.getKnownSourceClientIds())
                .addParameter("unknown_source_client_ids", e.getUnknownSourceClientIds())
                .build();
        }
    }

    private List<BuiltComponent> listBuiltDuplicatableComponents(ClientAuthorization authorization,
        DuplicatableComponentListRequest componentListRequest)
        throws ComponentRestException, UserAuthorizationRestException {
        try {
            BuiltComponentQueryBuilder queryBuilder = builtComponentService.listBuilt(authorization);
            queryBuilder.withCampaignVersionState(componentListRequest.getVersionState()
                .map(versionState -> CampaignVersionState.valueOf(versionState.name()))
                .orElse(CampaignVersionState.PUBLISHED));

            componentListRequest.getSourceClientIds()
                .map(sourceClientIds -> transformCommaSeparatedStringToSet(sourceClientIds))
                .map(stringIds -> stringIds
                    .stream()
                    .map(Id::<ClientHandle>valueOf)
                    .collect(Collectors.toSet()))
                .ifPresent(sourceClientIds -> queryBuilder.withSourceClientIds(sourceClientIds));

            Optional<String> state = componentListRequest.getState();
            if (state.isPresent() && StringUtils.isNotBlank(state.get())) {
                Set<CampaignState> parsedStates = new HashSet<>();
                for (String stateString : state.get().split(COMMA)) {
                    parsedStates.add(parseState(stateString));
                }
                queryBuilder.withStates(parsedStates);
            }

            Optional<String> targetComponentId = componentListRequest.getTargetComponentId();
            if (targetComponentId.isPresent() && StringUtils.isNotBlank(targetComponentId.get())) {
                Component component = getComponent(authorization, targetComponentId.get());
                queryBuilder.withTargetCompatibility(component);

                if (componentListRequest.getTargetSocketName().isPresent()) {
                    queryBuilder.withTargetSocketName(componentListRequest.getTargetSocketName().get());
                }
            }

            componentListRequest.getName().ifPresent(name -> queryBuilder.withName(name));
            componentListRequest.getCampaignIds()
                .map(camapignIdsString -> transformCommaSeparatedStringToSet(camapignIdsString))
                .map(strings -> strings.stream().map(Id::<Campaign>valueOf).collect(Collectors.toSet()))
                .ifPresent(campaignIdsSet -> queryBuilder.withCampaignIds(campaignIdsSet));
            componentListRequest.getHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.withMatchingAtLeastOneTag(tagsSet));
            componentListRequest.getHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.withMatchingAllTags(tagsSet));
            componentListRequest.getExcludeHavingAnyTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.excludeMatchingAtLeastOneTag(tagsSet));
            componentListRequest.getExcludeHavingAllTags()
                .map(tagsString -> transformCommaSeparatedStringToSet(tagsString))
                .ifPresent(tagsSet -> queryBuilder.excludeMatchingAllTags(tagsSet));
            componentListRequest.getHavingAnyTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(typesSet -> queryBuilder.withMatchingAtLeastOneType(typesSet));
            componentListRequest.getHavingAllTypes()
                .map(typesString -> transformCommaSeparatedStringToSet(typesString))
                .ifPresent(typesSet -> queryBuilder.withMatchingAllTypes(typesSet));

            queryBuilder.withShowAll(componentListRequest.getShowAll());
            queryBuilder.withLimit(componentListRequest.getLimit());
            queryBuilder.withOffset(componentListRequest.getOffset());
            return queryBuilder.listDuplicatable();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (UnknownSourceClientException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.UNKNOWN_SOURCE_CLIENT_IDS)
                .withCause(e)
                .addParameter("known_source_client_ids", e.getKnownSourceClientIds())
                .addParameter("unknown_source_client_ids", e.getUnknownSourceClientIds())
                .build();
        }
    }

    private Component getComponent(ClientAuthorization authorization, String componentId)
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

    private Component getComponent(ClientAuthorization authorization, String componentId, String version)
        throws ComponentRestException, UserAuthorizationRestException, CampaignRestException {
        try {
            Optional<CampaignVersion> campaignVersion = parseCampaignVersion(version);
            if (campaignVersion.isPresent()) {
                return componentService.get(authorization, Id.valueOf(componentId), campaignVersion.get());
            }
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
        } catch (CampaignVersionNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignRestException.class)
                .withErrorCode(CampaignRestException.INVALID_CAMPAIGN_VERSION)
                .addParameter("version", version)
                .withCause(e)
                .build();
        }
    }

    private BuiltComponent getBuiltComponent(ClientAuthorization authorization, String componentId, String version)
        throws UserAuthorizationRestException, ComponentRestException {
        try {
            Optional<CampaignVersion> campaignVersion = parseCampaignVersion(version);
            if (campaignVersion.isPresent()) {
                return builtComponentService.get(authorization, Id.valueOf(componentId), campaignVersion.get());
            }
            return builtComponentService.get(authorization, Id.valueOf(componentId));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ComponentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.COMPONENT_NOT_FOUND)
                .addParameter("component_id", componentId)
                .withCause(e)
                .build();
        }
    }

    private Optional<ComponentOwner> parseOwner(String owner) throws ComponentRestException {
        if (StringUtils.isBlank(owner)) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(ComponentOwner.valueOf(owner.toUpperCase()));
            } catch (IllegalArgumentException | NullPointerException e) {
                throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                    .withErrorCode(ComponentRestException.UNSUPPORTED_OWNER)
                    .addParameter("owner", owner)
                    .addParameter("supported_values", ComponentOwner.values())
                    .withCause(e)
                    .build();
            }
        }
    }

    private CampaignState parseState(String state) throws ComponentRestException {
        try {
            return CampaignState.valueOf(state);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw RestExceptionBuilder.newBuilder(ComponentRestException.class)
                .withErrorCode(ComponentRestException.UNSUPPORTED_STATE)
                .addParameter("state", state)
                .addParameter("supported_values", CampaignState.values())
                .withCause(e)
                .build();
        }
    }

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
                .filter(variableRequest -> Objects.nonNull(variableRequest))
                .collect(Collectors.toList())) {
                SettingBuilder settingBuilder =
                    componentBuilder.addSetting(SettingType.valueOf(setting.getType().name()));
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

                settingBuilder.withType(SettingType.valueOf(setting.getType().name()));
                settingRequestMapperRepository.getCreateRequestMapper(setting.getType())
                    .complete(setting, settingBuilder);
            }
        }

        componentDuplicateRequest.getType().ifPresent(type -> {
            if (type.isPresent()) {
                componentBuilder.withTypes(List.of(type.get()));
            } else {
                componentBuilder.withTypes(List.of());
            }
        });

        componentDuplicateRequest.getTypes().ifPresent(types -> {
            componentBuilder.withTypes(types);
        });
    }

    private void applyCreateRequestToBuilder(ComponentCreateRequest request,
        CampaignComponentBuilder campaignComponentBuilder)
        throws CampaignComponentIllegalCharacterInNameException, CampaignComponentNameLengthException,
        SettingNameLengthException, SettingInvalidNameException, VariableValueKeyLengthException,
        CampaignComponentDescriptionLengthException, CampaignComponentValidationRestException,
        SettingTagLengthException, CampaignComponentRootRenameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, CampaignComponentIllegalCharacterInDisplayNameException,
        CampaignComponentDisplayNameLengthException {
        if (request.getDisplayName().isPresent()) {
            campaignComponentBuilder.withDisplayName(request.getDisplayName().getValue());
        }
        if (request.getName() != null) {
            campaignComponentBuilder.withName(request.getName());
        }
        if (request.getTypes().isPresent()) {
            campaignComponentBuilder.withTypes(request.getTypes().getValue());
        }
        if (request.getTags().isPresent()) {
            campaignComponentBuilder.withTags(request.getTags().getValue());
        }
        if (request.getSettings().isPresent()) {
            for (CampaignComponentSettingRequest setting : request.getSettings().getValue().stream()
                .filter(settingRequest -> Objects.nonNull(settingRequest)).toList()) {
                populateSettingBuilder(campaignComponentBuilder, setting);
            }
        }

        if (request.getUploadVersion().isPresent()) {
            campaignComponentBuilder.withUploadVersion(request.getUploadVersion().getValue());
        }
        if (request.getDescription().isPresent()) {
            campaignComponentBuilder.withDescription(request.getDescription().getValue());
        }
        if (request.getInstalledIntoSocket().isPresent()) {
            campaignComponentBuilder.withInstalledIntoSocket(request.getInstalledIntoSocket().getValue());
        }
        if (request.getInstall().isPresent()) {
            campaignComponentBuilder.withInstall(request.getInstall().getValue());
        }
        request.getFacets().ifPresent(facets -> facets
            .stream().filter(Objects::nonNull)
            .forEach(facet -> campaignComponentBuilder.addFacet()
                .withName(facet.getName())
                .withValue(facet.getValue())));
        request.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(campaignComponentBuilder, componentIds);
        });
        request.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(campaignComponentBuilder, componentReferences);
        });
    }

    private void applyUpdateRequestToBuilder(ComponentUpdateRequest request,
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
        request.getTypes().ifPresent(types -> {
            campaignComponentBuilder.withTypes(types);
        });
        request.getTags().ifPresent(tags -> {
            campaignComponentBuilder.withTags(tags);
        });
        request.getFacets().ifPresent(facets -> {
            campaignComponentBuilder.clearFacets();
            facets.forEach(facet -> campaignComponentBuilder.addFacet()
                .withName(facet.getName())
                .withValue(facet.getValue()));
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
                .filter(settingRequest -> Objects.nonNull(settingRequest)).toList()) {
                populateSettingBuilder(campaignComponentBuilder, setting);
            }
        }

        request.getUploadVersion().ifPresent(uploadVersion -> {
            if (uploadVersion.isPresent()) {
                campaignComponentBuilder.withUploadVersion(uploadVersion.get());
            } else {
                campaignComponentBuilder.clearUploadVersion();
            }
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

    private void applyDuplicateRequestToBuilder(ComponentDuplicateRequest componentDuplicateRequest,
        ComponentDuplicateBuilder duplicateBuilder)
        throws CampaignComponentException, CampaignComponentValidationRestException, UnknownComponentSettingException,
        SettingValidationRestException, AuthorizationException, ComponentTypeNotFoundException,
        ComponentSocketNotFoundException, MissingTargetComponentByAbsoluteNameException {
        componentDuplicateRequest.getTargetComponentAbsoluteName().ifPresent(
            targetComponentAbsoluteName -> duplicateBuilder.withTargetComponent(targetComponentAbsoluteName));

        componentDuplicateRequest.getType().ifPresent(type -> {
            if (type.isPresent()) {
                duplicateBuilder.withTypes(List.of(type.get()));
            } else {
                duplicateBuilder.clearType();
            }
        });

        componentDuplicateRequest.getTypes().ifPresent(types -> {
            duplicateBuilder.withTypes(List.copyOf(types));
        });

        if (componentDuplicateRequest.getTargetSocketName().isPresent()) {
            duplicateBuilder.withTargetSocketName(componentDuplicateRequest.getTargetSocketName().getValue());
        }

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
                .filter(variableRequest -> Objects.nonNull(variableRequest))
                .collect(Collectors.toList())) {

                if (setting.getName() == null) {
                    throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                        .withErrorCode(SettingValidationRestException.NAME_MISSING)
                        .build();
                }

                ComponentDuplicateBuilder.SettingUpdateClosure settingUpdateClosure = (settingBuilder, oldSetting) -> {
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
            handleComponentIdsOnDuplicate(duplicateBuilder, componentIds);
        });
        componentDuplicateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(duplicateBuilder, componentReferences);
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

    private void handleComponentIdsOnDuplicate(ComponentDuplicateBuilder componentDuplicateBuilder,
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

    private void handleComponentIds(CampaignComponentBuilder campaignComponentBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            campaignComponentBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private Map<String, Map<String, Variable>>
        groupVariablesByExternalAbsoluteNameAndVariableName(
            Map<String, Subcomponent> subcomponentsByExternalAbsoluteName) {
        Map<String, Map<String, Variable>> result = new HashMap<>();
        for (Map.Entry<String, Subcomponent> entry : subcomponentsByExternalAbsoluteName.entrySet()) {
            for (String externalAbsolutePath : entry.getValue().getExternalAbsolutePaths()) {
                Map<String, Variable> innerMap = result.compute(externalAbsolutePath, (key, value) -> {
                    if (value == null) {
                        value = new HashMap<>();
                    }
                    return value;
                });
                for (Variable variable : getVariables(entry.getValue().getCampaignComponent().getSettings())) {
                    innerMap.put(variable.getName(), variable);
                }
            }
        }
        return result;
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

    private CampaignBuilder getCampaignBuilder(Id<Campaign> campaignId, Authorization authorization)
        throws CampaignRestException {
        CampaignBuilder campaignBuilder;
        try {
            campaignBuilder = campaignService.editCampaign(authorization, campaignId);
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

    private Component getSourceComponent(ClientAuthorization authorization, String componentId)
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

    private Predicate<Set<String>> constructTagsFilter(ComponentVariablesDownloadRequest variablesDownloadRequest) {
        VariableTagsFilter.Builder filterBuilder = VariableTagsFilter.builder();
        String havingAnyTagsString = variablesDownloadRequest.getHavingAnyTags();
        if (StringUtils.isNotBlank(havingAnyTagsString)) {
            filterBuilder.withHavingAnyTags(transformCommaSeparatedStringToSet(havingAnyTagsString));
        }

        String havingAllTagsString = variablesDownloadRequest.getHavingAllTags();
        if (StringUtils.isNotBlank(havingAllTagsString)) {
            filterBuilder.withHavingAllTags(transformCommaSeparatedStringToSet(havingAllTagsString));
        }

        String excludeHavingAnyTagsString = variablesDownloadRequest.getExcludeHavingAnyTags();
        if (StringUtils.isNotBlank(excludeHavingAnyTagsString)) {
            filterBuilder.withExcludeHavingAnyTags(transformCommaSeparatedStringToSet(excludeHavingAnyTagsString));
        }

        String excludeHavingAllTagsString = variablesDownloadRequest.getExcludeHavingAllTags();
        if (StringUtils.isNotBlank(excludeHavingAllTagsString)) {
            filterBuilder.withExcludeHavingAllTags(transformCommaSeparatedStringToSet(excludeHavingAllTagsString));
        }
        return filterBuilder.buildFilter();
    }

    private Set<String> transformCommaSeparatedStringToSet(String commaSeparatedString) {
        return Arrays.stream(commaSeparatedString.split(COMMA))
            .collect(toUnmodifiableSet());
    }

    private void deleteCampaign(ClientAuthorization authorization, String expectedCurrentVersion, Component component)
        throws CampaignLockedException, OrphanExternalComponentReferenceException, BuildCampaignEvaluatableException,
        CampaignNotFoundException, ReferencedExternalElementException, CampaignGlobalArchiveException,
        StaleCampaignVersionException, ConcurrentCampaignUpdateException, CampaignRestException {

        Optional<Integer> expectedCampaignVersion = campaignProvider.parseVersion(expectedCurrentVersion);
        Id<Campaign> campaignId = component.getCampaign().getId();

        if (expectedCampaignVersion.isPresent()) {
            campaignService.archive(authorization, campaignId, expectedCampaignVersion.get());
        } else {
            campaignService.archive(authorization, campaignId);
        }
    }

    private void deleteComponent(Authorization authorization, String expectedCurrentVersion, Component component)
        throws CampaignLockedException, CampaignNotFoundException, CampaignRestException,
        TransitionRuleAlreadyExistsForActionType, CampaignDateBeforeStartDateException,
        CampaignHasScheduledSiblingException, CampaignGlobalStateChangeException, CampaignServiceNameMissingException,
        CampaignStartDateAfterStopDateException, CampaignGlobalDeleteException, StepDataBuildException,
        BuildCampaignException, CampaignComponentException, InvalidComponentReferenceException,
        CampaignDateAfterStopDateException, CreativeArchiveBuilderException, CampaignLabelDuplicateNameException,
        CampaignServiceNameLengthException, StaleCampaignVersionException, ConcurrentCampaignUpdateException,
        CampaignLabelMissingNameException, CampaignControllerTriggerBuildException, CampaignFlowStepException,
        ReferencedExternalElementException, CreativeArchiveJavascriptException,
        CampaignServiceIllegalCharacterInNameException, CampaignGlobalArchiveException,
        CampaignComponentNameDuplicateException, CampaignComponentTypeValidationException, AuthorizationException,
        ComponentTypeNotFoundException, IncompatibleRewardRuleException, CreativeVariableUnsupportedException,
        CampaignComponentFacetsNotFoundException {
        CampaignBuilder campaignBuilder =
            campaignService.editCampaign(authorization, component.getCampaign().getId());
        campaignProvider.parseVersion(expectedCurrentVersion)
            .ifPresent(campaignBuilder::withExpectedVersion);
        CampaignComponent campaignComponent = component.getCampaignComponent();
        campaignBuilder.removeComponentRecursively(campaignComponent).save();
    }

    private static void validateUnexpectedVariables(List<?> unexpectedVariables, boolean ignoreUnknownVariables,
        String targetComponentId)
        throws ComponentVariableTargetRestException {

        if (ignoreUnknownVariables) {
            return;
        }
        if (!unexpectedVariables.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(ComponentVariableTargetRestException.class)
                .withErrorCode(ComponentVariableTargetRestException.UNRELATED_TARGET_COMPONENT_VARIABLES)
                .addParameter("target_component_id", targetComponentId)
                .addParameter("unrelated_variables", unexpectedVariables)
                .build();
        }
    }

    private boolean isRootComponent(CampaignComponent component) {
        return component.getName().equalsIgnoreCase(ROOT);
    }

    private List<Variable> getVariables(List<Setting> settings) {
        return settings.stream()
            .filter(setting -> setting instanceof Variable)
            .map(setting -> (Variable) setting)
            .collect(Collectors.toList());
    }

    private List<BuiltVariable> getBuiltVariables(List<? extends BuiltSetting> settings) {
        return settings.stream()
            .filter(setting -> setting instanceof BuiltVariable)
            .map(setting -> (BuiltVariable) setting)
            .collect(Collectors.toList());
    }

    private Optional<CampaignVersion> parseCampaignVersion(String version) {
        String sanitizedVersion = sanitize(version);
        try {
            return Optional.of(new CampaignVersion(Integer.valueOf(sanitizedVersion)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String sanitize(String version) {
        String[] versionArray = version.split("/");
        return versionArray[versionArray.length - 1];
    }

    private void validateLimits(int limit, int offset) throws QueryLimitsRestException {
        if (limit < 0) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_LIMIT)
                .addParameter("limit", limit)
                .build();
        }
        if (offset < 0) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_OFFSET)
                .addParameter("offset", offset)
                .build();
        }
        if (limit - offset > MAX_COMPONENTS_FETCH_SIZE) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.MAX_FETCH_SIZE_1000)
                .addParameter("limit", limit)
                .addParameter("offset", offset)
                .build();
        }
    }
}
