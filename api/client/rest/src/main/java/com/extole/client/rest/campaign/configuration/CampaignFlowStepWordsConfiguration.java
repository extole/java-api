package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.FlowStepBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignFlowStepWordsConfiguration {

    private static final String JSON_SINGULAR_NOUN_NAME = "singular_noun_name";
    private static final String JSON_PLURAL_NOUN_NAME = "plural_noun_name";
    private static final String JSON_VERB_NAME = "verb_name";
    private static final String JSON_RATE_NAME = "rate_name";
    private static final String JSON_PERSON_COUNTING_NAME = "person_counting_name";

    private final BuildtimeEvaluatable<FlowStepBuildtimeContext, String> singularNounName;
    private final BuildtimeEvaluatable<FlowStepBuildtimeContext, String> pluralNounName;
    private final BuildtimeEvaluatable<FlowStepBuildtimeContext, String> verbName;
    private final BuildtimeEvaluatable<FlowStepBuildtimeContext, String> rateName;
    private final BuildtimeEvaluatable<FlowStepBuildtimeContext, String> personCountingName;

    @JsonCreator
    public CampaignFlowStepWordsConfiguration(
        @JsonProperty(JSON_SINGULAR_NOUN_NAME) BuildtimeEvaluatable<FlowStepBuildtimeContext, String> singularNounName,
        @JsonProperty(JSON_PLURAL_NOUN_NAME) BuildtimeEvaluatable<FlowStepBuildtimeContext, String> pluralNounName,
        @JsonProperty(JSON_VERB_NAME) BuildtimeEvaluatable<FlowStepBuildtimeContext, String> verbName,
        @JsonProperty(JSON_RATE_NAME) BuildtimeEvaluatable<FlowStepBuildtimeContext, String> rateName,
        @JsonProperty(JSON_PERSON_COUNTING_NAME) BuildtimeEvaluatable<FlowStepBuildtimeContext,
            String> personCountingName) {
        this.singularNounName = singularNounName;
        this.pluralNounName = pluralNounName;
        this.verbName = verbName;
        this.rateName = rateName;
        this.personCountingName = personCountingName;
    }

    @JsonProperty(JSON_SINGULAR_NOUN_NAME)
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getSingularNounName() {
        return singularNounName;
    }

    @JsonProperty(JSON_PLURAL_NOUN_NAME)
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getPluralNounName() {
        return pluralNounName;
    }

    @JsonProperty(JSON_VERB_NAME)
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getVerbName() {
        return verbName;
    }

    @JsonProperty(JSON_RATE_NAME)
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getRateName() {
        return rateName;
    }

    @JsonProperty(JSON_PERSON_COUNTING_NAME)
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getPersonCountingName() {
        return personCountingName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
