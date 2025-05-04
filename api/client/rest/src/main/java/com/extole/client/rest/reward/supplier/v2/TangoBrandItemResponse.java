package com.extole.client.rest.reward.supplier.v2;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.reward.supplier.FaceValueType;

public class TangoBrandItemResponse {
    private static final String JSON_PROPERTY_UTID = "utid";
    private static final String JSON_PROPERTY_REWARD_NAME = "reward_name";
    private static final String JSON_PROPERTY_VALUE_TYPE = "value_type";
    private static final String JSON_PROPERTY_FACE_VALUE = "face_value";
    private static final String JSON_PROPERTY_MIN_VALUE = "min_value";
    private static final String JSON_PROPERTY_MAX_VALUE = "max_value";
    private static final String JSON_PROPERTY_CURRENCY_CODE = "currency_code";
    private static final String JSON_PROPERTY_COUNTRIES = "countries";

    private final String utid;
    private final String rewardName;
    private final TangoBrandItemValueType valueType;
    private final BigDecimal faceValue;
    private final BigDecimal minValue;
    private final BigDecimal maxValue;
    private final FaceValueType currencyCode;
    private final List<String> countries;

    @JsonCreator
    public TangoBrandItemResponse(@JsonProperty(JSON_PROPERTY_UTID) String utid,
        @JsonProperty(JSON_PROPERTY_REWARD_NAME) String rewardName,
        @JsonProperty(JSON_PROPERTY_VALUE_TYPE) TangoBrandItemValueType valueType,
        @JsonProperty(JSON_PROPERTY_FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(JSON_PROPERTY_MIN_VALUE) BigDecimal minValue,
        @JsonProperty(JSON_PROPERTY_MAX_VALUE) BigDecimal maxValue,
        @JsonProperty(JSON_PROPERTY_CURRENCY_CODE) FaceValueType currencyCode,
        @JsonProperty(JSON_PROPERTY_COUNTRIES) List<String> countries) {
        this.utid = utid;
        this.rewardName = rewardName;
        this.valueType = valueType;
        this.faceValue = faceValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currencyCode = currencyCode;
        this.countries = countries;
    }

    @JsonProperty(JSON_PROPERTY_UTID)
    public String getUtid() {
        return utid;
    }

    @JsonProperty(JSON_PROPERTY_REWARD_NAME)
    public String getRewardName() {
        return rewardName;
    }

    @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
    public TangoBrandItemValueType getValueType() {
        return valueType;
    }

    @JsonProperty(JSON_PROPERTY_FACE_VALUE)
    @Nullable
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @JsonProperty(JSON_PROPERTY_MIN_VALUE)
    @Nullable
    public BigDecimal getMinValue() {
        return minValue;
    }

    @JsonProperty(JSON_PROPERTY_MAX_VALUE)
    @Nullable
    public BigDecimal getMaxValue() {
        return maxValue;
    }

    @JsonProperty(JSON_PROPERTY_CURRENCY_CODE)
    public FaceValueType getCurrencyCode() {
        return currencyCode;
    }

    @JsonProperty(JSON_PROPERTY_COUNTRIES)
    public List<String> getCountries() {
        return countries;
    }

}
