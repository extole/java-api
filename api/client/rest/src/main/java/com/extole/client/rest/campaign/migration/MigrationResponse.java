package com.extole.client.rest.campaign.migration;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.common.lang.ToString;

public class MigrationResponse {
    private static final String FORCED_MIGRATED_ROOT_VARIABLES = "forced_migrated_root_variables";
    private static final String FIXED_BROKEN_COLORS = "fixed_broken_colors";
    private static final String MIGRATED_CREATIVES = "migrated_creatives";

    private final Map<String, Map<String, CampaignComponentVariableResponse>> forcedMigratedRootVariables;
    private final Map<String, String> fixedBrokenColors;
    private final List<MigratedCreativeResponse> migratedCreatives;

    @JsonCreator
    public MigrationResponse(
        @JsonProperty(FORCED_MIGRATED_ROOT_VARIABLES) Map<String,
            Map<String, CampaignComponentVariableResponse>> forcedMigratedRootVariables,
        @JsonProperty(FIXED_BROKEN_COLORS) Map<String, String> fixedBrokenColors,
        @JsonProperty(MIGRATED_CREATIVES) List<MigratedCreativeResponse> migratedCreatives) {
        this.fixedBrokenColors = fixedBrokenColors;
        this.forcedMigratedRootVariables = forcedMigratedRootVariables;
        this.migratedCreatives = migratedCreatives != null ? ImmutableList.copyOf(migratedCreatives) : List.of();
    }

    @JsonProperty(FORCED_MIGRATED_ROOT_VARIABLES)
    public Map<String, Map<String, CampaignComponentVariableResponse>> getForcedMigratedRootVariables() {
        return forcedMigratedRootVariables;
    }

    @JsonProperty(FIXED_BROKEN_COLORS)
    public Map<String, String> getFixedBrokenColors() {
        return fixedBrokenColors;
    }

    @JsonProperty(MIGRATED_CREATIVES)
    public List<MigratedCreativeResponse> getMigratedCreatives() {
        return migratedCreatives;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
