package com.extole.client.rest.impl.prehandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.prehandler.action.response.PrehandlerActionResponseMapper;
import com.extole.client.rest.impl.prehandler.condition.response.PrehandlerConditionResponseMapper;
import com.extole.model.entity.prehandler.PrehandlerActionType;
import com.extole.model.entity.prehandler.PrehandlerConditionType;

@Component
@SuppressWarnings("rawtypes")
public class PrehandlerResponseMapperRepository {
    private final Map<PrehandlerConditionType, PrehandlerConditionResponseMapper> conditionMappersByType;
    private final Map<PrehandlerActionType, PrehandlerActionResponseMapper> actionMappersByType;

    @Autowired
    public PrehandlerResponseMapperRepository(List<PrehandlerConditionResponseMapper> conditionMappers,
        List<PrehandlerActionResponseMapper> actionMappers) {
        Map<PrehandlerConditionType, PrehandlerConditionResponseMapper> conditionMappersMap = new HashMap<>();
        for (PrehandlerConditionResponseMapper conditionMapper : conditionMappers) {
            if (conditionMappersMap.containsKey(conditionMapper.getType())) {
                throw new RuntimeException(
                    "Found multiple instances of PrehandlerConditionResponseMapper for the same type: "
                        + conditionMapper.getType());
            }
            conditionMappersMap.put(conditionMapper.getType(), conditionMapper);
        }
        this.conditionMappersByType = Collections.unmodifiableMap(conditionMappersMap);

        Map<PrehandlerActionType, PrehandlerActionResponseMapper> actionMappersMap = new HashMap<>();
        for (PrehandlerActionResponseMapper actionMapper : actionMappers) {
            if (actionMappersMap.containsKey(actionMapper.getType())) {
                throw new RuntimeException(
                    "Found multiple instances of PrehandlerActionResponseMapper for the same type: "
                        + actionMapper.getType());
            }
            actionMappersMap.put(actionMapper.getType(), actionMapper);
        }
        this.actionMappersByType = Collections.unmodifiableMap(actionMappersMap);
    }

    public PrehandlerConditionResponseMapper getPrehandlerConditionResponseMapper(PrehandlerConditionType type) {
        PrehandlerConditionResponseMapper conditionMapper = conditionMappersByType.get(type);
        if (conditionMapper == null) {
            throw new RuntimeException("Unsupported prehandler condition type: " + type);
        }
        return conditionMapper;
    }

    public PrehandlerActionResponseMapper getPrehandlerActionResponseMapper(PrehandlerActionType type) {
        PrehandlerActionResponseMapper actionMapper = actionMappersByType.get(type);
        if (actionMapper == null) {
            throw new RuntimeException("Unsupported prehandler action type: " + type);
        }
        return actionMapper;
    }
}
