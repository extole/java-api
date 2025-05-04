package com.extole.reporting.rest.impl.posthandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.rest.impl.posthandler.action.ReportPostHandlerActionRequestMapper;
import com.extole.reporting.rest.impl.posthandler.condition.ReportPostHandlerConditionRequestMapper;
import com.extole.reporting.rest.posthandler.ActionType;
import com.extole.reporting.rest.posthandler.ConditionType;

@Component
public class ReportPostHandlerRequestMappersRepository {

    private final Map<ConditionType, ReportPostHandlerConditionRequestMapper> conditionMappers;
    private final Map<ActionType, ReportPostHandlerActionRequestMapper> actionMappers;

    @Autowired
    public ReportPostHandlerRequestMappersRepository(List<ReportPostHandlerConditionRequestMapper> conditionMappers,
        List<ReportPostHandlerActionRequestMapper> actionMappers) {
        this.conditionMappers = conditionMappers.stream().collect(Collectors.toMap(item -> item.getType(),
            Function.identity()));
        this.actionMappers =
            actionMappers.stream().collect(Collectors.toMap(item -> item.getType(), Function.identity()));
    }

    public ReportPostHandlerConditionRequestMapper getConditionMapper(ConditionType type) {
        ReportPostHandlerConditionRequestMapper mapper = conditionMappers.get(type);
        if (mapper == null) {
            throw new RuntimeException("Condition Mapper of type=" + type + " not found");
        }
        return mapper;
    }

    public ReportPostHandlerActionRequestMapper getActionMapper(ActionType type) {
        ReportPostHandlerActionRequestMapper mapper = actionMappers.get(type);
        if (mapper == null) {
            throw new RuntimeException("Action Mapper of type=" + type + " not found");
        }
        return mapper;
    }

}
