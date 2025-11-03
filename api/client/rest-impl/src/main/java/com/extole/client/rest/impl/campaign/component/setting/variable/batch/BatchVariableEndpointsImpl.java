package com.extole.client.rest.impl.campaign.component.setting.variable.batch;

import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.audience.BuildAudienceRestException;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.BuildWebhookRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentVariableResponse;
import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.component.setting.variable.batch.BatchVariableEndpoints;
import com.extole.client.rest.campaign.component.setting.variable.batch.BatchVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.variable.batch.BatchVariableUpdateResponse;
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
import com.extole.client.rest.impl.campaign.component.setting.CampaignComponentSettingRestMapper;
import com.extole.client.rest.impl.campaign.component.setting.SettingRequestMapperRepository;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.common.lang.KeyCaseInsensitiveMap;
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
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.entity.campaign.built.BuiltVariable;
import com.extole.model.service.ReferencedExternalElementException;
import com.extole.model.service.audience.built.BuildAudienceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.CampaignBuilder.CampaignSave;
import com.extole.model.service.campaign.CampaignDateAfterStopDateException;
import com.extole.model.service.campaign.CampaignDateBeforeStartDateException;
import com.extole.model.service.campaign.CampaignGlobalArchiveException;
import com.extole.model.service.campaign.CampaignGlobalDeleteException;
import com.extole.model.service.campaign.CampaignGlobalStateChangeException;
import com.extole.model.service.campaign.CampaignHasScheduledSiblingException;
import com.extole.model.service.campaign.CampaignLockedException;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;
import com.extole.model.service.campaign.CampaignServiceIllegalCharacterInNameException;
import com.extole.model.service.campaign.CampaignServiceNameLengthException;
import com.extole.model.service.campaign.CampaignServiceNameMissingException;
import com.extole.model.service.campaign.CampaignStartDateAfterStopDateException;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.CampaignComponentException;
import com.extole.model.service.campaign.component.CampaignComponentNameDuplicateException;
import com.extole.model.service.campaign.component.CampaignComponentTypeValidationException;
import com.extole.model.service.campaign.component.facet.CampaignComponentFacetsNotFoundException;
import com.extole.model.service.campaign.controller.trigger.CampaignControllerTriggerBuildException;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepException;
import com.extole.model.service.campaign.label.CampaignLabelDuplicateNameException;
import com.extole.model.service.campaign.label.CampaignLabelMissingNameException;
import com.extole.model.service.campaign.reward.rule.IncompatibleRewardRuleException;
import com.extole.model.service.campaign.setting.ComponentBuildSettingException;
import com.extole.model.service.campaign.setting.InvalidVariableTranslatableValueException;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameDuplicateException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingNameMissingException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.SettingValidationException;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;
import com.extole.model.service.campaign.step.data.StepDataBuildException;
import com.extole.model.service.campaign.transition.rule.TransitionRuleAlreadyExistsForActionType;
import com.extole.model.service.client.security.key.built.BuildClientKeyException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.creative.exception.CreativeArchiveBuilderException;
import com.extole.model.service.creative.exception.CreativeArchiveJavascriptException;
import com.extole.model.service.creative.exception.CreativeVariableUnsupportedException;
import com.extole.model.service.event.stream.built.BuildEventStreamException;
import com.extole.model.service.prehandler.built.BuildPrehandlerException;
import com.extole.model.service.reward.supplier.built.BuildRewardSupplierException;
import com.extole.model.service.webhook.built.BuildWebhookException;

@Provider
public class BatchVariableEndpointsImpl implements BatchVariableEndpoints {

    private final CampaignService campaignService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignProvider campaignProvider;
    private final CampaignComponentSettingRestMapper settingRestMapper;
    private final CampaignComponentProvider campaignComponentProvider;
    private final SettingRequestMapperRepository settingRequestMapperRepository;

    @Autowired
    public BatchVariableEndpointsImpl(CampaignService campaignService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignProvider campaignProvider,
        CampaignComponentSettingRestMapper settingRestMapper,
        CampaignComponentProvider campaignComponentProvider,
        SettingRequestMapperRepository settingRequestMapperRepository) {
        this.campaignService = campaignService;
        this.authorizationProvider = authorizationProvider;
        this.campaignProvider = campaignProvider;
        this.settingRestMapper = settingRestMapper;
        this.campaignComponentProvider = campaignComponentProvider;
        this.settingRequestMapperRepository = settingRequestMapperRepository;
    }

    @Override
    public BatchVariableUpdateResponse update(String accessToken,
        String campaignId,
        String expectedCurrentVersion,
        String componentId,
        BatchVariableUpdateRequest updateRequest)
        throws UserAuthorizationRestException, CampaignUpdateRestException, SettingValidationRestException,
        BuildCampaignRestException, CampaignRestException, CampaignComponentRestException, SettingRestException,
        BuildWebhookRestException, BuildPrehandlerRestException, BuildRewardSupplierRestException,
        BuildClientKeyRestException, BuildAudienceRestException, EventStreamValidationRestException,
        OAuthClientKeyBuildRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignProvider.getLatestCampaign(authorization, Id.valueOf(campaignId));
            CampaignBuilder campaignBuilder =
                getCampaignBuilder(campaign.getId().getValue(), authorization, expectedCurrentVersion);
            CampaignComponent campaignComponent = campaignComponentProvider.getCampaignComponent(componentId, campaign);
            CampaignComponentBuilder campaignComponentBuilder =
                campaignBuilder.updateComponent(campaignComponent);

            Map<String, Variable> variableByName = mapVariablesByName(campaignComponent);

            for (Map.Entry<String, CampaignComponentVariableUpdateRequest> entry : updateRequest.getVariables()
                .orElse(emptyMap()).entrySet()) {
                String variableName = entry.getKey();
                CampaignComponentVariableUpdateRequest request = entry.getValue();
                Variable variable = variableByName.get(variableName);
                if (request != null) {
                    VariableBuilder variableBuilder;
                    if (variable != null) {
                        variableBuilder = campaignComponentBuilder.updateSetting(variable);
                    } else {
                        variableBuilder = (VariableBuilder) campaignComponentBuilder.addSetting();
                    }
                    if (request.getType() != null) {
                        settingRequestMapperRepository.getUpdateRequestMapper(request.getType())
                            .complete(request, variableBuilder);
                        variableBuilder.withType(SettingType.valueOf(request.getType().name()));
                    }
                    applyRequestToBuilder(request, variableBuilder);
                } else {
                    if (variable != null) {
                        campaignComponentBuilder.removeSetting(variable);
                    } else {
                        throw RestExceptionBuilder.newBuilder(SettingRestException.class)
                            .withErrorCode(SettingRestException.SETTING_NOT_FOUND)
                            .addParameter("setting_name", variableName)
                            .addParameter("component_id", componentId)
                            .build();
                    }
                }
            }

            CampaignSave campaignSave = campaignBuilder.saveAndGetBuilt();

            KeyCaseInsensitiveMap<CampaignComponentVariableResponse> variables = KeyCaseInsensitiveMap.create();
            for (Variable variable : getVariables(campaignComponentProvider
                .getCampaignComponent(componentId, campaignSave.getCampaign()).getSettings())) {
                variables.put(variable.getName(),
                    (CampaignComponentVariableResponse) settingRestMapper.toSettingResponse(variable));
            }

            KeyCaseInsensitiveMap<BuiltCampaignComponentVariableResponse> builtVariables =
                KeyCaseInsensitiveMap.create();
            BuiltCampaign builtCampaign = campaignSave.getBuiltCampaign();
            for (BuiltVariable variable : getBuiltVariables(campaignComponentProvider
                .getBuiltCampaignComponent(componentId, builtCampaign).getSettings())) {
                builtVariables.put(variable.getName(),
                    (BuiltCampaignComponentVariableResponse) settingRestMapper.toBuiltSettingResponse(builtCampaign,
                        componentId, variable));
            }
            return new BatchVariableUpdateResponse(variables, builtVariables);
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
                .addParameter("min_length", Integer.valueOf(e.getNameMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getNameMaxLength()))
                .withCause(e)
                .build();
        } catch (SettingValidationException e) {
            throw RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.SETTING_VALIDATION)
                .addParameter("name", e.getPropertyName())
                .addParameter("details", e.getDetails())
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
            | CreativeArchiveJavascriptException | CreativeArchiveBuilderException | CampaignServiceNameLengthException
            | CampaignServiceIllegalCharacterInNameException | CreativeVariableUnsupportedException
            | CampaignControllerTriggerBuildException | CampaignLabelMissingNameException
            | CampaignLabelDuplicateNameException | CampaignServiceNameMissingException
            | TransitionRuleAlreadyExistsForActionType | CampaignFlowStepException | StepDataBuildException
            | CampaignStartDateAfterStopDateException | CampaignDateBeforeStartDateException
            | CampaignDateAfterStopDateException | CampaignHasScheduledSiblingException | CampaignGlobalDeleteException
            | CampaignGlobalArchiveException | CampaignGlobalStateChangeException
            | CampaignComponentTypeValidationException | AuthorizationException | ComponentTypeNotFoundException
            | ReferencedExternalElementException | IncompatibleRewardRuleException | CampaignComponentException
            | CampaignComponentFacetsNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Map<String, Variable> mapVariablesByName(CampaignComponent campaignComponent) {
        Map<String, Variable> variableByName = KeyCaseInsensitiveMap.create();
        for (Variable variable : getVariables(campaignComponent.getSettings())) {
            variableByName.put(variable.getName(), variable);
        }
        return variableByName;
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

    private void applyRequestToBuilder(CampaignComponentVariableUpdateRequest request, VariableBuilder variableBuilder)
        throws SettingNameLengthException, SettingInvalidNameException, VariableValueKeyLengthException,
        SettingTagLengthException, SettingDisplayNameLengthException, SettingIllegalCharacterInDisplayNameException {
        if (request.getName().isPresent()) {
            variableBuilder.withName(request.getName().getValue());
        }
        if (request.getDisplayName().isPresent()) {
            if (request.getDisplayName().getValue().isPresent()) {
                variableBuilder.withDisplayName(request.getDisplayName().getValue().get());
            } else {
                variableBuilder.cleanDisplayName();
            }
        }
        request.getValues().ifPresent(values -> variableBuilder.withValues(values));
        request.getSource().ifPresent(source -> variableBuilder
            .withSource(VariableSource.valueOf(source.name())));
        request.getPriority().ifPresent(priority -> variableBuilder.withPriority(priority));

        request.getDescription().ifPresent(description -> variableBuilder.withDescription(description));
        request.getTags().ifPresent(tags -> {
            variableBuilder.withTags(tags);
        });
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

}
