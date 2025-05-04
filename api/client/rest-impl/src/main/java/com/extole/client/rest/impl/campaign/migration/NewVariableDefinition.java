package com.extole.client.rest.impl.campaign.migration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.extole.dewey.decimal.DeweyDecimal;

@JsonPropertyOrder({
    "Current Variable Name",
    "Category",
    "Sub-category",
    "Importance",
    "Scope",
    "Translatable",
    "Priority",
    "Proposed New Variable Display Name",
    "Proposed New Variable Description",
    "Proposed New Default",
    "Old Notes"
})
public class NewVariableDefinition {

    @JsonProperty("Current Variable Name")
    private String currentVariableName;

    @JsonProperty("Category")
    private String category;

    @JsonProperty("Sub-category")
    private String subCategory;

    @JsonProperty("Importance")
    private String importance;

    @JsonProperty("Scope")
    private String scope;

    @JsonProperty("Translatable")
    private String translatable;

    @JsonProperty("Priority")
    private DeweyDecimal priority;

    @JsonProperty("Proposed New Variable Display Name")
    private String proposedNewVariableDisplayName;

    @JsonProperty("Proposed New Variable Description")
    private String proposedNewVariableDescription;

    @JsonProperty("Proposed New Default")
    private String proposedNewDefault;

    @JsonProperty("Old Notes")
    private String oldNotes;

    public String getCurrentVariableName() {
        return currentVariableName;
    }

    public void setCurrentVariableName(String currentVariableName) {
        this.currentVariableName = currentVariableName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTranslatable() {
        return translatable;
    }

    public void setTranslatable(String translatable) {
        this.translatable = translatable;
    }

    public DeweyDecimal getPriority() {
        return priority;
    }

    public void setPriority(DeweyDecimal priority) {
        this.priority = priority;
    }

    public String getProposedNewVariableDisplayName() {
        return proposedNewVariableDisplayName;
    }

    public void setProposedNewVariableDisplayName(String proposedNewVariableDisplayName) {
        this.proposedNewVariableDisplayName = proposedNewVariableDisplayName;
    }

    public String getProposedNewVariableDescription() {
        return proposedNewVariableDescription;
    }

    public void setProposedNewVariableDescription(String proposedNewVariableDescription) {
        this.proposedNewVariableDescription = proposedNewVariableDescription;
    }

    public String getProposedNewDefault() {
        return proposedNewDefault;
    }

    public void setProposedNewDefault(String proposedNewDefault) {
        this.proposedNewDefault = proposedNewDefault;
    }

    public String getOldNotes() {
        return oldNotes;
    }

    public void setOldNotes(String oldNotes) {
        this.oldNotes = oldNotes;
    }

}
