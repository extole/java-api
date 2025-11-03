package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.setting.BatchComponentVariableUpdateResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.SettingValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.client.rest.impl.campaign.BuildCampaignRestExceptionMapper;
import com.extole.client.rest.impl.campaign.built.component.setting.BuiltComponentSettingDefaultRestMapper;
import com.extole.client.rest.impl.campaign.built.component.setting.BuiltComponentSettingRestMapper;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.flow.step.metric.UnavailableReferencedVariableException;
import com.extole.model.service.campaign.setting.RewardSupplierIdListInvalidRewardSupplierException;
import com.extole.model.service.campaign.setting.SocketDescriptionLengthException;
import com.extole.model.service.campaign.setting.VariableDescriptionLengthException;
import com.extole.model.service.campaign.setting.VariableValueCircularReferenceException;
import com.extole.model.service.campaign.setting.VariableValueInvalidSyntaxException;
import com.extole.model.service.campaign.setting.VariableValueInvalidTypeException;

@Component
public class CampaignComponentSettingRestMapper {

    private final Map<com.extole.model.entity.campaign.SettingType,
        ComponentSettingRestMapper<? extends CampaignComponentSettingResponse>> settingResponseMappers =
            Maps.newHashMap();
    private final Map<com.extole.model.entity.campaign.SettingType,
        BuiltComponentSettingRestMapper<? extends BuiltCampaignComponentSettingResponse>> builtSettingResponseMappers =
            Maps.newHashMap();
    private final Map<com.extole.model.entity.campaign.SettingType,
        ComponentSettingConfigurationMapper<
            ? extends CampaignComponentSettingConfiguration>> settingConfigurationMappers =
                Maps.newHashMap();
    private final ComponentSettingDefaultRestMapper componentSettingDefaultRestMapper;
    private final BuiltComponentSettingDefaultRestMapper builtComponentSettingDefaultRestMapper;
    private final ComponentSettingDefaultConfigurationMapper componentSettingDefaultConfigurationMapper;

    public CampaignComponentSettingRestMapper(List<ComponentSettingRestMapper<?>> settingResponseMappers,
        List<BuiltComponentSettingRestMapper<?>> builtSettingResponseMappers,
        List<ComponentSettingConfigurationMapper<?>> settingConfigurationMappers,
        ComponentSettingDefaultRestMapper componentSettingDefaultRestMapper,
        BuiltComponentSettingDefaultRestMapper builtComponentSettingDefaultRestMapper) {
        settingResponseMappers.forEach(item -> item.getSettingTypes()
            .forEach(settingType -> this.settingResponseMappers.put(settingType, item)));
        builtSettingResponseMappers.forEach(item -> item.getSettingTypes()
            .forEach(settingType -> this.builtSettingResponseMappers.put(settingType, item)));
        settingConfigurationMappers.forEach(item -> item.getSettingTypes()
            .forEach(settingType -> this.settingConfigurationMappers.put(settingType, item)));
        this.componentSettingDefaultRestMapper = componentSettingDefaultRestMapper;
        this.builtComponentSettingDefaultRestMapper = builtComponentSettingDefaultRestMapper;
        this.componentSettingDefaultConfigurationMapper = new ComponentSettingDefaultConfigurationMapper();
    }

    public CampaignComponentSettingResponse toSettingResponse(Setting setting) {
        if (settingResponseMappers.containsKey(setting.getType())) {
            ComponentSettingRestMapper<? extends CampaignComponentSettingResponse> componentSettingRestMapper =
                settingResponseMappers.get(setting.getType());
            return componentSettingRestMapper.mapToSettingResponse(setting);
        }
        return componentSettingDefaultRestMapper.mapToSettingResponse(setting);
    }

    public CampaignComponentSettingConfiguration toSettingConfiguration(
        CampaignComponentRestMapperContext restMapperContext, Setting setting) {
        if (settingConfigurationMappers.containsKey(setting.getType())) {
            return settingConfigurationMappers.get(setting.getType()).mapToSettingConfiguration(restMapperContext,
                setting);
        }
        return componentSettingDefaultConfigurationMapper.mapToSettingConfiguration(restMapperContext, setting);
    }

    public BuiltCampaignComponentSettingResponse toBuiltSettingResponse(BuiltCampaign campaign, String componentId,
        BuiltSetting setting) {

        if (builtSettingResponseMappers.containsKey(setting.getType())) {
            return builtSettingResponseMappers.get(setting.getType()).mapToSettingResponse(campaign, componentId,
                setting);
        }
        return builtComponentSettingDefaultRestMapper.mapToSettingResponse(campaign, componentId, setting);
    }

    public BatchComponentVariableUpdateResponse mapVariableToBatchVariableUpdateResponse(String absolutePath,
        Variable variable) {
        return new BatchComponentVariableUpdateResponse(
            absolutePath,
            variable.getName(),
            variable.getDisplayName(),
            SettingType.valueOf(variable.getType().name()),
            variable.getValues());
    }

    public RestException mapToBuildSettingRestException(BuildCampaignException buildException, Id<?> componentId) {
        try {
            throw buildException;
        } catch (VariableValueCircularReferenceException e) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.CIRCULAR_REFERENCE)
                .addParameter("component_id", componentId)
                .addParameter("name", e.getName())
                .addParameter("key", e.getKey())
                .addParameter("cyclic_references", e.getCyclePath())
                .withCause(e)
                .build();
        } catch (VariableValueInvalidSyntaxException e) {
            return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.EXPRESSION_INVALID_SYNTAX)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("entity", e.getEntity())
                .addParameter("entity_id", componentId)
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .addParameter("description", e.getDescription())
                .withCause(e)
                .build();
        } catch (VariableValueInvalidTypeException e) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.INVALID_VALUE_TYPE)
                .addParameter("component_id", componentId)
                .addParameter("name", e.getName())
                .addParameter("key", e.getKey())
                .addParameter("value", e.getValue())
                .addParameter("expected_types", e.getExpectedTypes())
                .addParameter("details", e.getDetails())
                .withCause(e)
                .build();
        } catch (VariableDescriptionLengthException e) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (SocketDescriptionLengthException e) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", Integer.valueOf(e.getDescriptionMaxLength()))
                .withCause(e)
                .build();
        } catch (UnavailableReferencedVariableException e) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.UNAVAILABLE_REFERENCED_VARIABLE)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("component_id", componentId)
                .addParameter("name", e.getEntityId())
                .addParameter("unavailable_referenced_variable_name", e.getUnavailableVariable())
                .addParameter("attempted_variants", e.getAttemptedVariants())
                .addParameter("available_variants", e.getAvailableVariants())
                .addParameter("unavailability_cause", e.getType())
                .withCause(e)
                .build();
        } catch (RewardSupplierIdListInvalidRewardSupplierException e) {
            return RestExceptionBuilder.newBuilder(SettingValidationRestException.class)
                .withErrorCode(SettingValidationRestException.REWARD_SUPPLIER_ID_LIST_INVALID_CONFIGURATION)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("component_id", componentId)
                .addParameter("name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable())
                .addParameter("reward_supplier_id", e.getEntityId())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            return BuildCampaignRestExceptionMapper.getInstance().map(e);
        }
    }
}
