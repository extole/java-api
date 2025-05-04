package com.extole.client.rest.prehandler.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO to be removed in ENG-13399
public class PrehandlerV2Response {

    private static final String ID = "id";
    private static final String CONDITION = "condition";
    private static final String CONDITION_TYPE = "condition_type";
    private static final String ACTION = "action";
    private static final String ACTION_TYPE = "action_type";

    private final String id;
    private final String condition;
    private final PrehandlerV2ConditionType conditionType;
    private final String action;
    private final PrehandlerV2ActionType actionType;

    public PrehandlerV2Response(
        @JsonProperty(ID) String id,
        @JsonProperty(CONDITION) String condition,
        @JsonProperty(CONDITION_TYPE) PrehandlerV2ConditionType conditionType,
        @JsonProperty(ACTION) String action,
        @JsonProperty(ACTION_TYPE) PrehandlerV2ActionType actionType) {
        this.id = id;
        this.condition = condition;
        this.conditionType = conditionType;
        this.action = action;
        this.actionType = actionType;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(CONDITION)
    public String getCondition() {
        return condition;
    }

    @JsonProperty(CONDITION_TYPE)
    public PrehandlerV2ConditionType getConditionType() {
        return conditionType;
    }

    @JsonProperty(ACTION)
    public String getAction() {
        return action;
    }

    @JsonProperty(ACTION_TYPE)
    public PrehandlerV2ActionType getActionType() {
        return actionType;
    }
}
