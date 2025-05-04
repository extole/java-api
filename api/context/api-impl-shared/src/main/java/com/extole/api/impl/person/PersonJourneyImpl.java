package com.extole.api.impl.person;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import com.extole.api.person.JourneyKey;
import com.extole.api.person.PersonJourney;
import com.extole.common.lang.ToString;

public class PersonJourneyImpl implements PersonJourney {

    private final String campaignId;
    private final String journeyName;
    private final String container;
    private final Optional<String> programLabel;
    private final Map<String, Object> data;
    private final Optional<JourneyKey> key;

    public PersonJourneyImpl(com.extole.person.service.profile.journey.PersonJourney personJourney) {
        this.campaignId = personJourney.getCampaignId().getValue();
        this.journeyName = personJourney.getJourneyName().getValue();
        this.container = personJourney.getContainer().getName();
        this.programLabel = personJourney.getEntryLabel();
        this.data = Collections.unmodifiableMap(personJourney.getData());
        this.key = personJourney.getKey().map(value -> new JourneyKeyImpl(value));
    }

    public PersonJourneyImpl(
        String campaignId,
        String journeyName,
        String container,
        Optional<String> programLabel,
        Map<String, Object> data,
        Optional<com.extole.person.service.profile.journey.JourneyKey> key) {
        this.campaignId = campaignId;
        this.journeyName = journeyName;
        this.container = container;
        this.programLabel = programLabel;
        this.data = ImmutableMap.copyOf(data);
        this.key = key.map(value -> new JourneyKeyImpl(value));
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String getJourneyName() {
        return journeyName;
    }

    @Override
    public String getContainer() {
        return container;
    }

    @Nullable
    @Override
    public String getProgramLabel() {
        return programLabel.orElse(null);
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Nullable
    @Override
    public JourneyKey getKey() {
        return key.orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
