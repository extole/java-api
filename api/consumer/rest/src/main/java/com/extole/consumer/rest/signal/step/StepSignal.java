package com.extole.consumer.rest.signal.step;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class StepSignal {

    private static final String NAME = "name";
    private static final String FIRST_SITE_VISIT = "first_site_visit";
    private static final String PARTNER_EVENT_ID = "partner_event_id";
    private static final String DATA = "data";
    private static final String ALIASES = "aliases";
    private static final String QUALITY_RESULTS = "quality_results";

    private final String name;
    private final boolean firstSiteVisit;
    private final Optional<PartnerEventIdResponse> partnerEventId;
    private final Map<String, Object> data;
    private final Set<String> aliases;
    private final QualityResults qualityResults;

    public StepSignal(@JsonProperty(NAME) String name,
        @JsonProperty(FIRST_SITE_VISIT) boolean firstSiteVisit,
        @JsonProperty(PARTNER_EVENT_ID) Optional<PartnerEventIdResponse> partnerEventId,
        @JsonProperty(DATA) Map<String, Object> data,
        @JsonProperty(ALIASES) Set<String> aliases,
        @JsonProperty(QUALITY_RESULTS) QualityResults qualityResults) {
        this.name = name;
        this.firstSiteVisit = firstSiteVisit;
        this.partnerEventId = partnerEventId;
        this.data = ImmutableMap.copyOf(data);
        this.aliases = aliases;
        this.qualityResults = qualityResults;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(FIRST_SITE_VISIT)
    public boolean isFirstSiteVisit() {
        return firstSiteVisit;
    }

    @JsonProperty(PARTNER_EVENT_ID)
    public Optional<PartnerEventIdResponse> getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(DATA)
    public Map<String, Object> getData() {
        return data;
    }

    @JsonProperty(ALIASES)
    public Set<String> getAliases() {
        return aliases;
    }

    @JsonProperty(QUALITY_RESULTS)
    public QualityResults getQualityResults() {
        return qualityResults;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
