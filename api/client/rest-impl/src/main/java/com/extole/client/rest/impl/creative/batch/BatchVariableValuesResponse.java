package com.extole.client.rest.impl.creative.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class BatchVariableValuesResponse {

    private final String zone;
    private final Set<String> journeyNames;
    private final String name;
    private final Map<String, String> values;

    private BatchVariableValuesResponse(
        String zone,
        Set<String> journeyNames,
        String name,
        Map<String, String> values) {
        this.zone = zone;
        this.journeyNames = ImmutableSet.copyOf(journeyNames);
        this.name = name;
        this.values = ImmutableMap.copyOf(values);
    }

    public String getZone() {
        return zone;
    }

    public String getJourneyNames() {
        List<String> sortedJourneyNames = new ArrayList<>(journeyNames);
        Collections.sort(sortedJourneyNames);

        return String.join(",", sortedJourneyNames);
    }

    public String getName() {
        return name;
    }

    @JsonAnyGetter
    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String zone;
        private Set<String> journeyNames = ImmutableSet.of();
        private String name;
        private Map<String, String> values = ImmutableMap.of();

        public Builder withZone(String zone) {
            this.zone = zone;
            return this;
        }

        public Builder withJourneyNames(Set<String> journeyNames) {
            this.journeyNames = ImmutableSet.copyOf(journeyNames);
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withValues(Map<String, String> values) {
            this.values = ImmutableMap.copyOf(values);
            return this;
        }

        public BatchVariableValuesResponse build() {
            return new BatchVariableValuesResponse(zone, journeyNames, name, values);
        }

    }

}
