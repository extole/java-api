package com.extole.client.rest.label;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabelInjectorCreateRequest {
    private static final String LABEL_NAME = "name";
    private static final String LABEL_REQUIRED = "required";
    private static final String FORWARD_FROM_LABEL = "forward_from_label";
    private static final String CHILD_LABELS = "child_labels";
    private static final String OPTIONAL_CHILD_LABELS = "optional_child_labels";
    private static final String PROGRAM_DOMAIN_ID = "program_domain_id";

    private final String name;
    private final Boolean required;
    private final String forwardFromLabel;
    private final Set<String> childLabels;
    private final Set<String> optionalChildLabels;
    private final String programDomainId;

    public LabelInjectorCreateRequest(
        @JsonProperty(LABEL_NAME) String name,
        @Nullable @JsonProperty(LABEL_REQUIRED) Boolean required,
        @Nullable @JsonProperty(FORWARD_FROM_LABEL) String forwardFromLabel,
        @Nullable @JsonProperty(CHILD_LABELS) Set<String> childLabels,
        @Nullable @JsonProperty(OPTIONAL_CHILD_LABELS) Set<String> optionalChildLabels,
        @Nullable @JsonProperty(PROGRAM_DOMAIN_ID) String programDomainId) {
        this.name = name;
        this.required = required != null ? required : Boolean.FALSE;
        this.forwardFromLabel = forwardFromLabel;
        this.childLabels = childLabels != null ? childLabels : Collections.emptySet();
        this.optionalChildLabels = optionalChildLabels != null ? optionalChildLabels : Collections.emptySet();
        this.programDomainId = programDomainId;
    }

    @JsonProperty(LABEL_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(LABEL_REQUIRED)
    public Boolean isRequired() {
        return required;
    }

    @Nullable
    @JsonProperty(FORWARD_FROM_LABEL)
    public String getForwardFromLabel() {
        return forwardFromLabel;
    }

    @JsonProperty(CHILD_LABELS)
    public Set<String> getChildLabels() {
        return childLabels;
    }

    @JsonProperty(OPTIONAL_CHILD_LABELS)
    public Set<String> getOptionalChildLabels() {
        return optionalChildLabels;
    }

    @Nullable
    @JsonProperty(PROGRAM_DOMAIN_ID)
    public String getProgramDomainId() {
        return programDomainId;
    }

}
