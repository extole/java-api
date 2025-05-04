package com.extole.api.impl.event.internal.reward;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import com.extole.api.event.internal.reward.RewardContext;
import com.extole.common.lang.CurrencyValueParser;
import com.extole.common.lang.ToString;

public class RewardContextImpl implements RewardContext {

    private static final String DATA_FIELD_EARNED_EVENT_VALUE = "earned_event_value";

    private final String id;
    private final String name;
    private final Optional<String> partnerRewardId;
    private final Set<String> tags;
    private final BigDecimal faceValue;
    private final String faceValueType;
    private final Map<String, String> data;
    private final String state;
    private final String programLabel;
    private final String supplierType;

    public RewardContextImpl(
        String id,
        String name,
        Optional<String> partnerRewardId,
        Set<String> tags,
        BigDecimal faceValue,
        String faceValueType,
        Map<String, String> data,
        String state,
        String programLabel,
        String supplierType) {
        this.id = id;
        this.name = name;
        this.partnerRewardId = partnerRewardId;
        this.tags = tags;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.data = ImmutableMap.copyOf(data);
        this.state = state;
        this.programLabel = programLabel;
        this.supplierType = supplierType;
    }

    @Override
    public String getRewardId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPartnerRewardId() {
        return partnerRewardId.orElse(null);
    }

    @Deprecated // TODO should be removed after switch ENG-15542
    @Override
    public String[] getSlots() {
        return tags.toArray(new String[] {});
    }

    @Override
    public String[] getTags() {
        return tags.toArray(new String[] {});
    }

    @Override
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @Override
    public String getFaceValueType() {
        return faceValueType;
    }

    @Override
    public BigDecimal getValueOfRewardedEvent() {
        if (data.containsKey(DATA_FIELD_EARNED_EVENT_VALUE)) {
            return CurrencyValueParser.parseValue(data.get(DATA_FIELD_EARNED_EVENT_VALUE));
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getSupplierType() {
        return supplierType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
