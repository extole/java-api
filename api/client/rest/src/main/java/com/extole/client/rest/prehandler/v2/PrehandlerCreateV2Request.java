package com.extole.client.rest.prehandler.v2;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO to be removed in ENG-13399
public class PrehandlerCreateV2Request {
    private static final String CONDITION = "condition";
    private static final String CONDITION_TYPE = "condition_type";
    private static final String ACTION = "action";
    private static final String ACTION_TYPE = "action_type";

    private final String condition;
    private final PrehandlerV2ConditionType conditionType;
    private final String action;
    private final PrehandlerV2ActionType actionType;

    public PrehandlerCreateV2Request(@JsonProperty(CONDITION) @Nullable String condition,
        @JsonProperty(CONDITION_TYPE) PrehandlerV2ConditionType conditionType,
        @JsonProperty(ACTION) String action,
        @JsonProperty(ACTION_TYPE) PrehandlerV2ActionType actionType) {
        this.condition = condition;
        this.conditionType = conditionType;
        this.action = action;
        this.actionType = actionType;
    }

    public static PrehandlerCreateV2RequestBuilder builder() {
        return new PrehandlerCreateV2RequestBuilder();
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

    public static final class PrehandlerCreateV2RequestBuilder {
        private String condition;
        private PrehandlerV2ConditionType conditionType;
        private String action;
        private PrehandlerV2ActionType actionType;

        private PrehandlerCreateV2RequestBuilder() {
        }

        public PrehandlerCreateV2RequestBuilder withCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public PrehandlerCreateV2RequestBuilder withConditionType(PrehandlerV2ConditionType conditionType) {
            this.conditionType = conditionType;
            return this;
        }

        public PrehandlerCreateV2RequestBuilder withAction(String action) {
            this.action = action;
            return this;
        }

        public PrehandlerCreateV2RequestBuilder withActionType(PrehandlerV2ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public PrehandlerCreateV2Request build() {
            return new PrehandlerCreateV2Request(condition, conditionType, action, actionType);
        }
    }
}
