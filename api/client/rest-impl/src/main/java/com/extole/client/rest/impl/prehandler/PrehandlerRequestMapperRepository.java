package com.extole.client.rest.impl.prehandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.prehandler.action.request.PrehandlerActionRequestMapper;
import com.extole.client.rest.impl.prehandler.condition.request.PrehandlerConditionRequestMapper;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;

@Component
@SuppressWarnings("rawtypes")
public class PrehandlerRequestMapperRepository {
    private final Map<PrehandlerConditionType, PrehandlerConditionRequestMapper> conditionMappersByType;
    private final Map<PrehandlerActionType, PrehandlerActionRequestMapper> actionMappersByType;

    @Autowired
    public PrehandlerRequestMapperRepository(List<PrehandlerConditionRequestMapper> conditionMappers,
        List<PrehandlerActionRequestMapper> actionMappers) {
        Map<PrehandlerConditionType, PrehandlerConditionRequestMapper> conditionMappersMap = new HashMap<>();
        for (PrehandlerConditionRequestMapper conditionMapper : conditionMappers) {
            if (conditionMappersMap.containsKey(conditionMapper.getType())) {
                throw new RuntimeException(
                    "Found multiple instances of PrehandlerConditionRequestMapper for the same type: "
                        + conditionMapper.getType());
            }
            conditionMappersMap.put(conditionMapper.getType(), conditionMapper);
        }
        this.conditionMappersByType = Collections.unmodifiableMap(conditionMappersMap);

        Map<PrehandlerActionType, PrehandlerActionRequestMapper> actionMappersMap = new HashMap<>();
        for (PrehandlerActionRequestMapper actionMapper : actionMappers) {
            if (actionMappersMap.containsKey(actionMapper.getType())) {
                throw new RuntimeException(
                    "Found multiple instances of PrehandlerActionRequestMapper for the same type: "
                        + actionMapper.getType());
            }
            actionMappersMap.put(actionMapper.getType(), actionMapper);
        }
        this.actionMappersByType = Collections.unmodifiableMap(actionMappersMap);
    }

    public PrehandlerConditionRequestMapper getPrehandlerConditionRequestMapper(PrehandlerConditionType type) {
        PrehandlerConditionRequestMapper conditionMapper = conditionMappersByType.get(type);
        if (conditionMapper == null) {
            throw new RuntimeException("Unsupported prehandler condition type: " + type);
        }
        return conditionMapper;
    }

    public PrehandlerActionRequestMapper getPrehandlerActionRequestMapper(PrehandlerActionType type) {
        PrehandlerActionRequestMapper actionMapper = actionMappersByType.get(type);
        if (actionMapper == null) {
            throw new RuntimeException("Unsupported prehandler action type: " + type);
        }
        return actionMapper;
    }
}
