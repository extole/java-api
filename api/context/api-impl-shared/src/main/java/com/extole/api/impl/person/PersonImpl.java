package com.extole.api.impl.person;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.extole.api.person.Location;
import com.extole.api.person.PersonAudienceMembership;
import com.extole.api.person.PersonJourney;
import com.extole.api.person.PersonReferral;
import com.extole.api.person.PersonReward;
import com.extole.api.person.PersonStep;
import com.extole.api.person.RequestContext;
import com.extole.api.person.Shareable;
import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.sandbox.SandboxService;

public class PersonImpl implements com.extole.api.person.Person {

    private final Person person;
    private final SandboxService sandboxService;

    public PersonImpl(Person person, SandboxService sandboxService) {
        this.person = person;
        this.sandboxService = sandboxService;
    }

    @Override
    public String getId() {
        return person.getId().getValue();
    }

    @Nullable
    @Override
    public String getIdentityId() {
        return person.getIdentityKeyValue().isPresent() ? person.getIdentityId().getValue() : null;
    }

    @Override
    public String getIdentityKey() {
        return person.getIdentityKey().getName();
    }

    @Nullable
    @Override
    public String getIdentityKeyValue() {
        return person.getIdentityKeyValue().orElse(null);
    }

    @Override
    public String getDisplacedPersonId() {
        return person.getDisplacedPersonId().map(Id::getValue).orElse(null);
    }

    @Nullable
    @Override
    public String getFirstName() {
        return person.getFirstName();
    }

    @Override
    public boolean isBlocked() {
        return person.isBlocked();
    }

    @Deprecated // TODO remove, use getData() - ENG-15534
    @Override
    public Map<String, Object> getPrivateData() {
        return Collections.unmodifiableMap(person.getPrivateData());
    }

    @Override
    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(person.getData());
    }

    @Deprecated // TODO remove, use getData() - ENG-15534
    @Override
    public Map<String, Object> getPublicData() {
        return Collections.unmodifiableMap(person.getPublicData());
    }

    @Nullable
    @Override
    public String getEmail() {
        return person.getEmail();
    }

    @Nullable
    @Override
    public String getNormalizedEmail() {
        return person.getNormalizedEmail();
    }

    @Nullable
    @Override
    public String getLastName() {
        return person.getLastName();
    }

    @Deprecated // TODO remove in favor of getLocale(), ENG-10118
    @Override
    public String getPreferredLocale() {
        return person.getPreferredLocale();
    }

    @Override
    public String getLocale() {
        return person.getPreferredLocale();
    }

    @Override
    public PersonReward[] getRewards() {
        return person.getRewards().stream()
            .map(personReward -> new PersonRewardImpl(person.getClientId(), sandboxService, personReward))
            .toArray(PersonRewardImpl[]::new);
    }

    @Override
    public PersonReferral[] getRecentAssociatedFriends() {
        return person.getIdentifiedFriends().stream().map(PersonReferralImpl::new)
            .toArray(PersonReferralImpl[]::new);
    }

    @Override
    public PersonReferral[] getRecentAssociatedAdvocates() {
        return person.getIdentifiedAdvocates().stream().map(PersonReferralImpl::new)
            .toArray(PersonReferralImpl[]::new);
    }

    @Deprecated // TODO remove, use getRecentRequestContexts() - ENG-24826
    @Override
    public RequestContext[] getRecentRequestContexts() {
        return person.getRecentRequestContexts().stream().map(RequestContextImpl::new)
            .toArray(RequestContextImpl[]::new);
    }

    @Override
    public Location[] getRecentLocations() {
        return person.getRecentLocations().stream().map(LocationImpl::new)
                .toArray(LocationImpl[]::new);
    }

    @Override
    public PersonStep[] getSteps() {
        return person.getSteps().stream().map(step -> new PersonStepImpl(person.getId(), step))
            .toArray(PersonStep[]::new);
    }

    @Override
    public Shareable[] getShareables() {
        return person.getShareables().stream().map(ShareableImpl::new).toArray(Shareable[]::new);
    }

    @Nullable
    @Override
    public String getPartnerUserId() {
        return person.getPartnerUserId();
    }

    @Nullable
    @Override
    public String getProfilePictureUrl() {
        if (person.getProfilePictureUrl() != null) {
            return person.getProfilePictureUrl().toString();
        }
        return null;
    }

    @Override
    public PersonJourney[] getJourneys() {
        return person.getJourneys().stream().map(PersonJourneyImpl::new)
            .collect(Collectors.toList()).toArray(new PersonJourney[] {});
    }

    @Override
    public PersonAudienceMembership[] getAudienceMemberships() {
        return person.getAudienceMemberships().stream().map(PersonAudienceMembershipImpl::new)
            .collect(Collectors.toList()).toArray(new PersonAudienceMembership[person.getAudienceMemberships().size()]);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
