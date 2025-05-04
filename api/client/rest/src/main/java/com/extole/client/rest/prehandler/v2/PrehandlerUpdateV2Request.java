package com.extole.client.rest.prehandler.v2;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO to be removed in ENG-13399
public class PrehandlerUpdateV2Request {
    private static final String CONDITION = "condition";
    private static final String CONDITION_TYPE = "condition_type";
    private static final String ACTION = "action";
    private static final String ACTION_TYPE = "action_type";

    private final String condition;
    private final PrehandlerV2ConditionType conditionType;
    private final String action;
    private final PrehandlerV2ActionType actionType;

    public PrehandlerUpdateV2Request(@JsonProperty(CONDITION) @Nullable String condition,
        @JsonProperty(CONDITION_TYPE) @Nullable PrehandlerV2ConditionType conditionType,
        @JsonProperty(ACTION) @Nullable String action,
        @JsonProperty(ACTION_TYPE) @Nullable PrehandlerV2ActionType actionType) {
        this.condition = condition;
        this.conditionType = conditionType;
        this.action = action;
        this.actionType = actionType;
    }

    public static PrehandlerUpdateV2RequestBuilder builder() {
        return new PrehandlerUpdateV2RequestBuilder();
    }

    @Nullable
    @JsonProperty(CONDITION)
    public String getCondition() {
        return condition;
    }

    @Nullable
    @JsonProperty(CONDITION_TYPE)
    public PrehandlerV2ConditionType getConditionType() {
        return conditionType;
    }

    @Nullable
    @JsonProperty(ACTION)
    public String getAction() {
        return action;
    }

    @Nullable
    @JsonProperty(ACTION_TYPE)
    public PrehandlerV2ActionType getActionType() {
        return actionType;
    }

    public static final class PrehandlerUpdateV2RequestBuilder {
        private String condition;
        private PrehandlerV2ConditionType conditionType;
        private String action;
        private PrehandlerV2ActionType actionType;

        private PrehandlerUpdateV2RequestBuilder() {
        }

        public PrehandlerUpdateV2RequestBuilder withCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public PrehandlerUpdateV2RequestBuilder withConditionType(PrehandlerV2ConditionType conditionType) {
            this.conditionType = conditionType;
            return this;
        }

        public PrehandlerUpdateV2RequestBuilder withAction(String action) {
            this.action = action;
            return this;
        }

        public PrehandlerUpdateV2RequestBuilder withActionType(PrehandlerV2ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public PrehandlerUpdateV2Request build() {
            return new PrehandlerUpdateV2Request(condition, conditionType, action, actionType);
        }
    }
}
