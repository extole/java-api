package com.extole.client.rest.campaign.migration.global;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.common.lang.ToString;

public class GlobalCampaignMigrationResponse {
    private static final String MIGRATED_VARIABLES = "migrated_variables";
    private static final String MIGRATED_ASSETS = "migrated_assets";
    private static final String SKIPPED_CONFLICTING_VARIABLES = "skipped_conflicting_variables";

    private final List<String> migratedVariables;
    private final List<String> migratedAssets;
    private final Map<String, Map<String, CampaignComponentVariableResponse>> skippedConflictingVariables;

    @JsonCreator
    public GlobalCampaignMigrationResponse(
        @JsonProperty(MIGRATED_VARIABLES) List<String> migratedVariables,
        @JsonProperty(MIGRATED_ASSETS) List<String> migratedAssets,
        @JsonProperty(SKIPPED_CONFLICTING_VARIABLES) Map<String, Map<String,
            CampaignComponentVariableResponse>> skippedConflictingVariables) {
        this.migratedVariables = migratedVariables;
        this.migratedAssets = migratedAssets;
        this.skippedConflictingVariables = skippedConflictingVariables;
    }

    @JsonProperty(MIGRATED_VARIABLES)
    public List<String> getMigratedVariables() {
        return migratedVariables;
    }

    @JsonProperty(MIGRATED_ASSETS)
    public List<String> getMigratedAssets() {
        return migratedAssets;
    }

    @JsonProperty(SKIPPED_CONFLICTING_VARIABLES)
    public Map<String, Map<String, CampaignComponentVariableResponse>> getSkippedConflictingVariables() {
        return skippedConflictingVariables;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
