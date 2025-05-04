package com.extole.api.impl.person;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.extole.api.person.JourneyKey;
import com.extole.api.person.PartnerEventId;
import com.extole.api.person.PersonStep;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.person.service.CampaignHandle;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.journey.JourneyName;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.person.service.profile.step.StepQuality;
import com.extole.person.service.profile.step.data.StepDataScope;
import com.extole.profile.step.ProfileStepPojo;
import com.extole.profile.step.data.ProfileStepDataPojo;

public class PersonStepImpl implements PersonStep {

    private final String personId;
    private final String campaignId;
    private final String programLabel;
    private final String stepName;
    private final String eventId;
    private final String rootEventId;
    private final String causeEventId;
    private final String eventDate;
    private final String createdDate;
    private final String value;
    private final boolean isAliasName;
    private final String quality;
    private final PartnerEventId partnerEventId;
    private final Map<String, Object> data;
    private final Map<String, Object> publicData;
    private final Map<String, Object> privateData;
    private final String scope;
    private final String container;
    private final String journeyName;
    private final Optional<JourneyKey> journeyKey;

    public PersonStepImpl(Id<PersonHandle> personId, com.extole.person.service.profile.step.PersonStep personStep) {
        this.personId = personId.getValue();
        this.campaignId = personStep.getCampaignId().map(Id::getValue).orElse(null);
        this.programLabel = personStep.getProgramLabel().orElse(null);
        this.stepName = personStep.getStepName();
        this.eventId = personStep.getEventId().getValue();
        this.rootEventId = personStep.getRootEventId().getValue();
        this.causeEventId = personStep.getCauseEventId().getValue();
        this.eventDate = personStep.getEventDate().toString();
        this.createdDate = personStep.getCreatedDate().toString();
        this.value = personStep.getValue().map(BigDecimal::toPlainString).orElse(null);
        this.isAliasName = personStep.isAliasName();
        this.quality = personStep.getQuality().name();
        this.partnerEventId = personStep.getPartnerEventId().map(PartnerEventIdImpl::new).orElse(null);
        this.data = Collections.unmodifiableMap(personStep.getData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue)));
        this.publicData = Collections.unmodifiableMap(personStep.getPublicData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue)));
        this.privateData = Collections.unmodifiableMap(personStep.getPrivateData().stream()
            .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue)));
        this.scope = personStep.getScope() != null ? personStep.getScope().name() : null;
        this.container = personStep.getContainer().getName();
        this.journeyName = personStep.getJourneyName().map(value -> value.getValue()).orElse(null);
        this.journeyKey = personStep.getJourneyKey().map(value -> new JourneyKeyImpl(value));
    }

    public PersonStepImpl(
        Id<PersonHandle> personId,
        ProfileStepPojo personStep,
        List<ProfileStepDataPojo> publicData,
        List<ProfileStepDataPojo> privateData,
        List<ProfileStepDataPojo> clientData) {
        this(personId, personStep.getCampaignId(), personStep.getProgramLabel(), personStep.getStepName(),
            personStep.getEventId(), personStep.getRootEventId(), personStep.getEventDate(),
            personStep.getCreatedAt(), personStep.getValue(), personStep.isAliasName(), personStep.getQuality(),
            personStep.getPartnerEventId(), publicData, privateData, clientData, personStep.getScope(),
            personStep.getCauseEventId(), personStep.getContainer().getName(), personStep.getJourneyName(),
            personStep.getJourneyKey());
    }

    public PersonStepImpl(Id<PersonHandle> personId, ProfileStepPojo personStep) {
        this(personId, personStep.getCampaignId(), personStep.getProgramLabel(), personStep.getStepName(),
            personStep.getEventId(), personStep.getRootEventId(), personStep.getEventDate(),
            personStep.getCreatedAt(), personStep.getValue(),
            personStep.isAliasName(), personStep.getQuality(), personStep.getPartnerEventId(),
            personStep.getData().stream()
                .filter(value -> value.getScope() == StepDataScope.PUBLIC)
                .collect(Collectors.toList()),
            personStep.getData().stream()
                .filter(value -> value.getScope() == StepDataScope.PRIVATE)
                .collect(Collectors.toList()),
            personStep.getData().stream()
                .filter(value -> value.getScope() == StepDataScope.CLIENT)
                .collect(Collectors.toList()),
            personStep.getScope(), personStep.getCauseEventId(), personStep.getContainer().getName(),
            personStep.getJourneyName(),
            personStep.getJourneyKey());
    }

    public PersonStepImpl(
        Id<PersonHandle> personId,
        Optional<Id<CampaignHandle>> campaignId,
        Optional<String> programLabel,
        String stepName,
        Id<?> eventId,
        Id<?> rootEventId,
        Instant eventDate,
        Instant createdDate,
        Optional<BigDecimal> value,
        boolean isAliasName,
        StepQuality quality,
        Optional<com.extole.person.service.profile.step.PartnerEventId> partnerEventId,
        List<ProfileStepDataPojo> publicData,
        List<ProfileStepDataPojo> privateData,
        List<ProfileStepDataPojo> clientData,
        com.extole.person.service.profile.step.PersonStep.Scope scope,
        Id<?> causeEventId,
        String container,
        Optional<JourneyName> journeyName,
        Optional<com.extole.person.service.profile.journey.JourneyKey> journeyKey) {
        this.personId = personId.getValue();
        this.campaignId = campaignId.map(Id::getValue).orElse(null);
        this.programLabel = programLabel.orElse(null);
        this.stepName = stepName;
        this.eventId = eventId.getValue();
        this.rootEventId = rootEventId != null ? rootEventId.getValue() : causeEventId.getValue();
        this.causeEventId = causeEventId.getValue();
        this.eventDate = eventDate.toString();
        this.createdDate = createdDate.toString();
        this.value = value.map(BigDecimal::toPlainString).orElse(null);
        this.isAliasName = isAliasName;
        this.quality = quality.name();
        this.partnerEventId = partnerEventId.map(PartnerEventIdImpl::new).orElse(null);
        this.publicData = Collections.unmodifiableMap(publicData.stream()
            .collect(Collectors.toMap(ProfileStepDataPojo::getName, ProfileStepDataPojo::getValue)));
        this.privateData = Collections.unmodifiableMap(privateData.stream()
            .collect(Collectors.toMap(ProfileStepDataPojo::getName, ProfileStepDataPojo::getValue)));
        Map<String, Object> clientDataMap = Collections.unmodifiableMap(clientData.stream()
            .collect(Collectors.toMap(ProfileStepDataPojo::getName, ProfileStepDataPojo::getValue)));
        KeyCaseInsensitiveMap<Object> allDataBuilder = KeyCaseInsensitiveMap.create(this.publicData);
        allDataBuilder.putAll(this.privateData);
        allDataBuilder.putAll(clientDataMap);
        this.data = Collections.unmodifiableMap(allDataBuilder);
        this.scope = scope != null ? scope.name() : null;
        this.container = container;
        this.journeyName = journeyName.map(journeyNameValue -> journeyNameValue.getValue()).orElse(null);
        this.journeyKey = journeyKey.map(journeyKeyValue -> new JourneyKeyImpl(journeyKeyValue));
    }

    @Override
    public String getPersonId() {
        return personId;
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public String getCauseEventId() {
        return causeEventId;
    }

    @Override
    public String getEventDate() {
        return eventDate;
    }

    @Override
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isAliasName() {
        return isAliasName;
    }

    @Override
    public String getQuality() {
        return quality;
    }

    @Override
    public PartnerEventId getPartnerEventId() {
        return partnerEventId;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Deprecated // TODO remove, use getData() - ENG-15534
    @Override
    public Map<String, Object> getPublicData() {
        return publicData;
    }

    @Deprecated // TODO remove, use getData() - ENG-15534
    @Override
    public Map<String, Object> getPrivateData() {
        return privateData;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getContainer() {
        return container;
    }

    @Override
    public String getJourneyName() {
        return journeyName;
    }

    @Nullable
    @Override
    public JourneyKey getJourneyKey() {
        return journeyKey.orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
