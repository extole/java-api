package com.extole.client.rest.campaign.flow.step;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.FlowStepBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignFlowStepWordsRequest {

    private static final String JSON_SINGULAR_NOUN_NAME = "singular_noun_name";
    private static final String JSON_PLURAL_NOUN_NAME = "plural_noun_name";
    private static final String JSON_VERB_NAME = "verb_name";
    private static final String JSON_RATE_NAME = "rate_name";
    private static final String JSON_PERSON_COUNTING_NAME = "person_counting_name";

    private final Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> singularNounName;
    private final Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> pluralNounName;
    private final Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> verbName;
    private final Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> rateName;
    private final Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> personCountingName;

    @JsonCreator
    public CampaignFlowStepWordsRequest(
        @JsonProperty(JSON_SINGULAR_NOUN_NAME) Omissible<
            BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> singularNounName,
        @JsonProperty(JSON_PLURAL_NOUN_NAME) Omissible<
            BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> pluralNounName,
        @JsonProperty(JSON_VERB_NAME) Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> verbName,
        @JsonProperty(JSON_RATE_NAME) Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> rateName,
        @JsonProperty(JSON_PERSON_COUNTING_NAME) Omissible<
            BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> personCountingName) {
        this.singularNounName = singularNounName;
        this.pluralNounName = pluralNounName;
        this.verbName = verbName;
        this.rateName = rateName;
        this.personCountingName = personCountingName;
    }

    @JsonProperty(JSON_SINGULAR_NOUN_NAME)
    public Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> getSingularNounName() {
        return singularNounName;
    }

    @JsonProperty(JSON_PLURAL_NOUN_NAME)
    public Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> getPluralNounName() {
        return pluralNounName;
    }

    @JsonProperty(JSON_VERB_NAME)
    public Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> getVerbName() {
        return verbName;
    }

    @JsonProperty(JSON_RATE_NAME)
    public Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> getRateName() {
        return rateName;
    }

    @JsonProperty(JSON_PERSON_COUNTING_NAME)
    public Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> getPersonCountingName() {
        return personCountingName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> singularNounName =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> pluralNounName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> verbName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> rateName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<FlowStepBuildtimeContext, String>> personCountingName =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withSingularNounName(BuildtimeEvaluatable<FlowStepBuildtimeContext, String> singularNounName) {
            this.singularNounName = Omissible.of(singularNounName);
            return this;
        }

        public Builder withPluralNounName(BuildtimeEvaluatable<FlowStepBuildtimeContext, String> pluralNounName) {
            this.pluralNounName = Omissible.of(pluralNounName);
            return this;
        }

        public Builder withVerbName(BuildtimeEvaluatable<FlowStepBuildtimeContext, String> verbName) {
            this.verbName = Omissible.of(verbName);
            return this;
        }

        public Builder withRateName(BuildtimeEvaluatable<FlowStepBuildtimeContext, String> rateName) {
            this.rateName = Omissible.of(rateName);
            return this;
        }

        public Builder
            withPersonCountingName(BuildtimeEvaluatable<FlowStepBuildtimeContext, String> personCountingName) {
            this.personCountingName = Omissible.of(personCountingName);
            return this;
        }

        public CampaignFlowStepWordsRequest build() {
            return new CampaignFlowStepWordsRequest(singularNounName, pluralNounName, verbName, rateName,
                personCountingName);
        }
    }

}
