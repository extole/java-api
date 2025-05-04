package com.extole.client.rest.campaign.built.flow.step;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BuiltCampaignFlowStepWordsResponse {

    private static final String JSON_SINGULAR_NOUN_NAME = "singular_noun_name";
    private static final String JSON_PLURAL_NOUN_NAME = "plural_noun_name";
    private static final String JSON_VERB_NAME = "verb_name";
    private static final String JSON_RATE_NAME = "rate_name";
    private static final String JSON_PERSON_COUNTING_NAME = "person_counting_name";

    private final String singularNounName;
    private final String pluralNounName;
    private final String verbName;
    private final String rateName;
    private final String personCountingName;

    @JsonCreator
    public BuiltCampaignFlowStepWordsResponse(
        @JsonProperty(JSON_SINGULAR_NOUN_NAME) String singularNounName,
        @JsonProperty(JSON_PLURAL_NOUN_NAME) String pluralNounName,
        @JsonProperty(JSON_VERB_NAME) String verbName,
        @JsonProperty(JSON_RATE_NAME) String rateName,
        @JsonProperty(JSON_PERSON_COUNTING_NAME) String personCountingName) {
        this.singularNounName = singularNounName;
        this.pluralNounName = pluralNounName;
        this.verbName = verbName;
        this.rateName = rateName;
        this.personCountingName = personCountingName;
    }

    @JsonProperty(JSON_SINGULAR_NOUN_NAME)
    public String getSingularNounName() {
        return singularNounName;
    }

    @JsonProperty(JSON_PLURAL_NOUN_NAME)
    public String getPluralNounName() {
        return pluralNounName;
    }

    @JsonProperty(JSON_VERB_NAME)
    public String getVerbName() {
        return verbName;
    }

    @JsonProperty(JSON_RATE_NAME)
    public String getRateName() {
        return rateName;
    }

    @JsonProperty(JSON_PERSON_COUNTING_NAME)
    public String getPersonCountingName() {
        return personCountingName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
