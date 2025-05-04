package com.extole.reporting.rest.impl.posthandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.posthandler.action.ReportPostHandlerAction.ActionType;
import com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition.ConditionType;
import com.extole.reporting.rest.impl.posthandler.action.ReportPostHandlerActionResponseMapper;
import com.extole.reporting.rest.impl.posthandler.condition.ReportPostHandlerConditionResponseMapper;

@Component
public class ReportPostHandlerResponseMappersRepository {

    private final Map<ConditionType, ReportPostHandlerConditionResponseMapper> conditionMappers;
    private final Map<ActionType, ReportPostHandlerActionResponseMapper> actionMappers;

    @Autowired
    public ReportPostHandlerResponseMappersRepository(List<ReportPostHandlerConditionResponseMapper> conditionMappers,
        List<ReportPostHandlerActionResponseMapper> actionMappers) {
        this.conditionMappers = conditionMappers.stream()
            .collect(Collectors.toMap(item -> item.getType(), Function.identity()));
        this.actionMappers = actionMappers.stream()
            .collect(Collectors.toMap(item -> item.getType(), Function.identity()));
    }

    public ReportPostHandlerActionResponseMapper getActionMapper(ActionType actionType) {
        ReportPostHandlerActionResponseMapper actionMapper = actionMappers.get(actionType);
        if (actionMapper == null) {
            throw new RuntimeException("Action Mapper of type=" + actionType + " not found");
        }
        return actionMapper;
    }

    public ReportPostHandlerConditionResponseMapper getConditionMapper(ConditionType conditionType) {
        ReportPostHandlerConditionResponseMapper conditionMapper = conditionMappers.get(conditionType);
        if (conditionMapper == null) {
            throw new RuntimeException("Condition Mapper of type=" + conditionMapper + " not found");
        }
        return conditionMapper;
    }
}
