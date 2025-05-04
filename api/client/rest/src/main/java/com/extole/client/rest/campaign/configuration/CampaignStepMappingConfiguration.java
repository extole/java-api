package com.extole.client.rest.campaign.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.controller.SendPolicy;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignStepMappingConfiguration {

    private static final String ID = "id";
    private static final String CREATIVE = "creative";
    private static final String ZONE = "zone";
    private static final String SCOPE = "scope";
    private static final String ENABLED = "enabled";
    private static final String SHOW_ENABLE_TOGGLE = "show_enable_toggle";
    private static final String ENABLED_ON_STATES = "enabled_on_states";
    private static final String COMPONENT_REFERENCES = "component_references";
    private static final String DATA = "data";
    private static final String JOURNEY_NAMES = "journey_names";
    private static final String SEND_POLICY = "send_policy";

    private final Omissible<Id<CampaignStepMappingConfiguration>> id;
    private final CampaignStepMappingZoneConfiguration zoneConfiguration;
    private final CreativeConfiguration creativeConfiguration;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope;
    private final Boolean enabled;
    private final Boolean showEnableToggle;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> enabledOnStates;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;
    private final List<StepDataConfiguration> data;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> sendPolicy;

    public CampaignStepMappingConfiguration(
        @JsonProperty(ID) Omissible<Id<CampaignStepMappingConfiguration>> id,
        @JsonProperty(ZONE) CampaignStepMappingZoneConfiguration zoneConfiguration,
        @Nullable @JsonProperty(CREATIVE) CreativeConfiguration creativeConfiguration,
        @JsonProperty(SCOPE) BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> scope,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(ENABLED_ON_STATES) BuildtimeEvaluatable<CampaignBuildtimeContext,
            Set<CampaignState>> enabledOnStates,
        @JsonProperty(SHOW_ENABLE_TOGGLE) Boolean showEnableToggle,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(DATA) List<StepDataConfiguration> data,
        @JsonProperty(JOURNEY_NAMES) BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> journeyNames,
        @JsonProperty(SEND_POLICY) BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> sendPolicy) {
        this.id = id;
        this.zoneConfiguration = zoneConfiguration;
        this.creativeConfiguration = creativeConfiguration;
        this.scope = scope;
        this.enabled = enabled;
        this.showEnableToggle = showEnableToggle;
        this.enabledOnStates = enabledOnStates;
        this.componentReferences = componentReferences != null ? componentReferences : Collections.emptyList();
        this.data = data != null ? Collections.unmodifiableList(data) : Collections.emptyList();
        this.journeyNames = journeyNames;
        this.sendPolicy = sendPolicy;
    }

    @JsonProperty(ID)
    public Omissible<Id<CampaignStepMappingConfiguration>> getId() {
        return id;
    }

    @JsonProperty(ZONE)
    public CampaignStepMappingZoneConfiguration getZone() {
        return zoneConfiguration;
    }

    @Nullable
    @JsonProperty(CREATIVE)
    public CreativeConfiguration getCreative() {
        return creativeConfiguration;
    }

    @JsonProperty(SCOPE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, StepScope> getScope() {
        return scope;
    }

    @JsonProperty(ENABLED)
    public Boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(SHOW_ENABLE_TOGGLE)
    public Boolean isShowEnableToggle() {
        return showEnableToggle;
    }

    @JsonProperty(ENABLED_ON_STATES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<CampaignState>> getEnabledOnStates() {
        return enabledOnStates;
    }

    @JsonProperty(COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
    }

    @JsonProperty(DATA)
    public List<StepDataConfiguration> getData() {
        return data;
    }

    @JsonProperty(JOURNEY_NAMES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getJourneyNames() {
        return journeyNames;
    }

    @JsonProperty(SEND_POLICY)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, SendPolicy> getSendPolicy() {
        return sendPolicy;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
