package com.extole.api.impl.person;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.person.PersonReward;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.sandbox.SandboxNotFoundException;
import com.extole.sandbox.SandboxService;

public class PersonRewardImpl implements PersonReward {

    private static final Logger LOG = LoggerFactory.getLogger(PersonRewardImpl.class);

    private final Id<?> clientId;
    private final SandboxService sandboxService;
    private final com.extole.person.service.profile.reward.PersonReward personReward;

    public PersonRewardImpl(
        Id<?> clientId,
        SandboxService sandboxService,
        com.extole.person.service.profile.reward.PersonReward personReward) {
        this.clientId = clientId;
        this.sandboxService = sandboxService;
        this.personReward = personReward;
    }

    @Override
    public String getName() {
        return personReward.getRewardName().orElse(null);
    }

    @Override
    public String getRewardSupplierId() {
        return personReward.getRewardSupplierId().getValue();
    }

    @Override
    public Map<String, String> getData() {
        return KeyCaseInsensitiveMap.create(personReward.getData());
    }

    @Override
    public String getPersonRole() {
        return personReward.getPersonRole().name();
    }

    @Override
    public String getFaceValue() {
        return formatFaceValue(personReward.getFaceValue());
    }

    @Override
    public String getFaceValueType() {
        return personReward.getFaceValueType().name();
    }

    @Override
    public String getRewardedDate() {
        return personReward.getRewardedDate().toString();
    }

    @Override
    public String getState() {
        return Optional.ofNullable(personReward.getState()).map(Enum::name).orElse(null);
    }

    @Nullable
    @Override
    public String getPartnerRewardId() {
        return personReward.getPartnerRewardId().orElse(null);
    }

    @Override
    public String getSandbox() {
        return personReward.getSandbox();
    }

    @Override
    public String getId() {
        return personReward.getId().getValue();
    }

    @Override
    public String getRewardId() {
        return Optional.ofNullable(personReward.getRewardId()).map(Id::getValue).orElse(null);
    }

    @Override
    public String[] getRewardSlots() {
        return personReward.getRewardSlots().toArray(new String[0]);
    }

    @Override
    public String getDateEarned() {
        return personReward.getRewardedDate().toString();
    }

    @Override
    public String getProgramLabel() {
        return personReward.getProgramLabel();
    }

    @Override
    public String getCampaignId() {
        return Optional.ofNullable(personReward.getCampaignId()).map(Id::getValue).orElse(null);
    }

    @Override
    public Optional<String> expiryDate() {
        return personReward.getExpiryDate().map(Instant::toString);
    }

    @Nullable
    @Override
    public String getRedeemedDate() {
        return personReward.getRedeemedDate().map(Instant::toString).orElse(null);
    }

    @Override
    public String getContainer() {
        if (personReward.getSandbox() != null) {
            try {
                return sandboxService.getById(clientId, Id.valueOf(personReward.getSandbox())).getContainer().getName();
            } catch (SandboxNotFoundException e) {
                LOG.error("Sandbox with ID={} not found. Reward ID={}, client ID={}", personReward.getSandbox(),
                    personReward.getId(), clientId);
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    private String formatFaceValue(BigDecimal faceValue) {
        if (faceValue == null) {
            return null;
        }
        return faceValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

}
