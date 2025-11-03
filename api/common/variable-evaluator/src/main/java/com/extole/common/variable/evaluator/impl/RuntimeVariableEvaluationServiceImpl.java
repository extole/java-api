package com.extole.common.variable.evaluator.impl;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.api.impl.campaign.EnumListRuntimeFlatSetting;
import com.extole.api.impl.campaign.EnumRuntimeFlatSetting;
import com.extole.api.impl.campaign.PartnerEnumListRuntimeFlatSetting;
import com.extole.api.impl.campaign.PartnerEnumRuntimeFlatSetting;
import com.extole.api.impl.campaign.RuntimeFlatSetting;
import com.extole.common.lang.LazyLoadingSupplier;
import com.extole.common.variable.evaluator.RuntimeVariableEvaluationService;
import com.extole.evaluateable.Evaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluation.EvaluationException;
import com.extole.evaluation.EvaluationService;
import com.extole.event.client.ClientEventService;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.shared.campaign.component.setting.VariableEnumListValueTransformer;
import com.extole.model.shared.campaign.component.setting.VariableEnumValueTransformer;
import com.extole.model.shared.campaign.component.setting.VariableRuntimeValidator;
import com.extole.model.shared.campaign.component.setting.VariableRuntimeValidatorEntityDetails;
import com.extole.model.shared.campaign.component.setting.VariableSocketValueTransformer;
import com.extole.running.service.campaign.RunningCampaign;

@Component
class RuntimeVariableEvaluationServiceImpl implements RuntimeVariableEvaluationService {
    private static final RuntimeVariableValidator NO_OP_RUNTIME_VARIABLE_VALIDATOR =
        (ClientEventService clientEventService, RunningCampaign campaign, RuntimeFlatSetting variable,
            Object evaluatedTransformedValue) -> {};
    private static final UnaryOperator<Object> DEFAULT_EVALUATED_RUNTIME_VALUE_TRANSFORMER = UnaryOperator.identity();
    private static final Map<SettingType, UnaryOperator<Object>> EVALUATED_RUNTIME_VALUE_TRANSFORMER_BY_TYPE =
        ImmutableMap.<SettingType, UnaryOperator<Object>>builder()
            .put(SettingType.ENUM, VariableEnumValueTransformer.getInstance())
            .put(SettingType.ENUM_LIST, VariableEnumListValueTransformer.getInstance())
            .put(SettingType.MULTI_SOCKET, VariableSocketValueTransformer.getInstance())
            .put(SettingType.SOCKET, VariableSocketValueTransformer.getInstance())
            .build();
    private static final Map<SettingType, RuntimeVariableValidator> EVALUATED_RUNTIME_VALUE_VALIDATOR_BY_TYPE =
        ImmutableMap.<SettingType, RuntimeVariableValidator>builder()
            .put(SettingType.ENUM, RuntimeVariableEvaluationServiceImpl::validateEnumVariable)
            .put(SettingType.ENUM_LIST, RuntimeVariableEvaluationServiceImpl::validateEnumListVariable)
            .put(SettingType.PARTNER_ENUM, RuntimeVariableEvaluationServiceImpl::validatePartnerEnumVariable)
            .put(SettingType.PARTNER_ENUM_LIST, RuntimeVariableEvaluationServiceImpl::validatePartnerEnumListVariable)
            .put(SettingType.DELAY_LIST, RuntimeVariableEvaluationServiceImpl::validateDelayListVariable)
            .put(SettingType.ADMIN_ICON, RuntimeVariableEvaluationServiceImpl::validateAdminIconVariable)
            .put(SettingType.DELAY, RuntimeVariableEvaluationServiceImpl::validateDelayVariable)
            .build();

    private final EvaluationService evaluationService;
    private final ClientEventService clientEventService;

    @Autowired
    RuntimeVariableEvaluationServiceImpl(EvaluationService evaluationService, ClientEventService clientEventService) {
        this.evaluationService = evaluationService;
        this.clientEventService = clientEventService;
    }

    @Override
    public <CONTEXT> Optional<Object> evaluate(Evaluatable<CONTEXT, Optional<Object>> evaluatable,
        LazyLoadingSupplier<CONTEXT> contextSupplier, RuntimeFlatSetting variable)
        throws EvaluationException {

        Optional<Object> evaluatedRuntimeValue = evaluationService.evaluate(evaluatable, contextSupplier);
        RunningCampaign campaign = variable.getCampaign();
        if (isRuntime(evaluatable) && evaluatedRuntimeValue.isPresent()) {
            Object transformedValue = EVALUATED_RUNTIME_VALUE_TRANSFORMER_BY_TYPE
                .getOrDefault(variable.getType(), DEFAULT_EVALUATED_RUNTIME_VALUE_TRANSFORMER)
                .apply(evaluatedRuntimeValue.get());
            EVALUATED_RUNTIME_VALUE_VALIDATOR_BY_TYPE.getOrDefault(variable.getType(), NO_OP_RUNTIME_VARIABLE_VALIDATOR)
                .validate(clientEventService, campaign, variable, transformedValue);
            return Optional.of(transformedValue);
        }
        return evaluatedRuntimeValue;
    }

    private <CONTEXT> boolean isRuntime(Evaluatable<CONTEXT, Optional<Object>> evaluatable) {
        return evaluatable instanceof RuntimeEvaluatable;
    }

    private static void validateAdminIconVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        VariableRuntimeValidator
            .create(clientEventService)
            .validateAdminIconVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue));
    }

    private static void validateDelayListVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        VariableRuntimeValidator
            .create(clientEventService)
            .validateDelayListVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue));
    }

    private static void validateEnumListVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        VariableRuntimeValidator
            .create(clientEventService)
            .validateEnumListVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue),
                ((EnumListRuntimeFlatSetting) variable).getEnumVariableMembers());
    }

    private static void validateEnumVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {

        VariableRuntimeValidator
            .create(clientEventService)
            .validateEnumVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue),
                ((EnumRuntimeFlatSetting) variable).getEnumVariableMembers());
    }

    private static void validatePartnerEnumListVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        VariableRuntimeValidator
            .create(clientEventService)
            .validatePartnerEnumListVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue),
                ((PartnerEnumListRuntimeFlatSetting) variable).getOptions());
    }

    private static void validatePartnerEnumVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        VariableRuntimeValidator
            .create(clientEventService)
            .validatePartnerEnumVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue),
                ((PartnerEnumRuntimeFlatSetting) variable).getOptions());
    }

    private static void validateDelayVariable(ClientEventService clientEventService, RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        VariableRuntimeValidator
            .create(clientEventService)
            .validateDelayVariableAndFireAnEventIfNeeded(
                getEntityDetails(campaign, variable, evaluatedTransformedValue));
    }

    private static VariableRuntimeValidatorEntityDetails getEntityDetails(RunningCampaign campaign,
        RuntimeFlatSetting variable, Object evaluatedTransformedValue) {
        return new VariableRuntimeValidatorEntityDetails(campaign.getClientId(), campaign.getId().getValue(),
            campaign.getName(),
            variable.getName(),
            variable.getValue(),
            evaluatedTransformedValue);
    }

    @FunctionalInterface
    private interface RuntimeVariableValidator {
        void validate(ClientEventService clientEventService, RunningCampaign campaign, RuntimeFlatSetting variable,
            Object evaluatedTransformedValue);
    }

}
