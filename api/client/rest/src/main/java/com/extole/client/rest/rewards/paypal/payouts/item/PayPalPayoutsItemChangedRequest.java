package com.extole.client.rest.rewards.paypal.payouts.item;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

/**
 * @see PayPalPayoutsItemChangedRequestReader
 */
public final class PayPalPayoutsItemChangedRequest {

    private String eventId;
    private String batchId;
    private String receiver;
    private String rewardId;
    private String clientId;
    private Type eventType;
    private TransactionStatus transactionStatus;
    private String summary;
    private String errorCode;
    private String errorMessage;
    private Instant createdAt;
    private String authenticationAlgorithm;
    private String publicKeyCertificateUrl;
    private String transmissionId;
    private String transmissionSignature;
    private String transmissionTime;
    private Object payload;

    private PayPalPayoutsItemChangedRequest() {

    }

    public String getEventId() {
        return eventId;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getRewardId() {
        return rewardId;
    }

    public String getClientId() {
        return clientId;
    }

    public Type getEventType() {
        return eventType;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public String getSummary() {
        return summary;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getAuthenticationAlgorithm() {
        return authenticationAlgorithm;
    }

    public String getPublicKeyCertificateUrl() {
        return publicKeyCertificateUrl;
    }

    public String getTransmissionId() {
        return transmissionId;
    }

    public String getTransmissionSignature() {
        return transmissionSignature;
    }

    public String getTransmissionTime() {
        return transmissionTime;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {

        private String eventId;
        private String batchId;
        private String receiver;
        private String rewardId;
        private String clientId;
        private Type eventType;
        private TransactionStatus transactionStatus;
        private String summary;
        private String errorCode;
        private String errorMessage;
        private Instant createdAt;
        private String authenticationAlgorithm;
        private String publicKeyCertificateUrl;
        private String transmissionId;
        private String transmissionSignature;
        private String transmissionTime;
        private Object payload;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withBatchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        public Builder withReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder withRewardId(String rewardId) {
            this.rewardId = rewardId;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withEventType(Type eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder withTransactionStatus(TransactionStatus transactionStatus) {
            this.transactionStatus = transactionStatus;
            return this;
        }

        public Builder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withAuthenticationAlgorithm(String authenticationAlgorithm) {
            this.authenticationAlgorithm = authenticationAlgorithm;
            return this;
        }

        public Builder withPublicKeyCertificateUrl(String publicKeyCertificateUrl) {
            this.publicKeyCertificateUrl = publicKeyCertificateUrl;
            return this;
        }

        public Builder withTransmissionId(String transmissionId) {
            this.transmissionId = transmissionId;
            return this;
        }

        public Builder withTransmissionSignature(String transmissionSignature) {
            this.transmissionSignature = transmissionSignature;
            return this;
        }

        public Builder withTransmissionTime(String transmissionTime) {
            this.transmissionTime = transmissionTime;
            return this;
        }

        public Builder withPayload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public PayPalPayoutsItemChangedRequest build() {
            PayPalPayoutsItemChangedRequest payPalPayoutsItemWebHookEvent = new PayPalPayoutsItemChangedRequest();
            payPalPayoutsItemWebHookEvent.eventId = this.eventId;
            payPalPayoutsItemWebHookEvent.batchId = this.batchId;
            payPalPayoutsItemWebHookEvent.receiver = this.receiver;
            payPalPayoutsItemWebHookEvent.rewardId = this.rewardId;
            payPalPayoutsItemWebHookEvent.clientId = this.clientId;
            payPalPayoutsItemWebHookEvent.eventType = this.eventType;
            payPalPayoutsItemWebHookEvent.transactionStatus = this.transactionStatus;
            payPalPayoutsItemWebHookEvent.summary = this.summary;
            payPalPayoutsItemWebHookEvent.errorCode = this.errorCode;
            payPalPayoutsItemWebHookEvent.errorMessage = this.errorMessage;
            payPalPayoutsItemWebHookEvent.createdAt = this.createdAt;
            payPalPayoutsItemWebHookEvent.authenticationAlgorithm = this.authenticationAlgorithm;
            payPalPayoutsItemWebHookEvent.publicKeyCertificateUrl = this.publicKeyCertificateUrl;
            payPalPayoutsItemWebHookEvent.transmissionId = this.transmissionId;
            payPalPayoutsItemWebHookEvent.transmissionSignature = this.transmissionSignature;
            payPalPayoutsItemWebHookEvent.transmissionTime = this.transmissionTime;
            payPalPayoutsItemWebHookEvent.payload = this.payload;
            return payPalPayoutsItemWebHookEvent;
        }
    }

    @Schema
    public enum Type {
        BLOCKED("PAYMENT.PAYOUTS-ITEM.BLOCKED"),
        CANCELED("PAYMENT.PAYOUTS-ITEM.CANCELED"),
        DENIED("PAYMENT.PAYOUTS-ITEM.DENIED"),
        FAILED("PAYMENT.PAYOUTS-ITEM.FAILED"),
        HELD("PAYMENT.PAYOUTS-ITEM.HELD"),
        REFUNDED("PAYMENT.PAYOUTS-ITEM.REFUNDED"),
        RETURNED("PAYMENT.PAYOUTS-ITEM.RETURNED"),
        SUCCEEDED("PAYMENT.PAYOUTS-ITEM.SUCCEEDED"),
        UNCLAIMED("PAYMENT.PAYOUTS-ITEM.UNCLAIMED");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public static Type from(String value) {
            return Stream.of(Type.values()).filter(type -> type.value.equals(value)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "No enum constant " + Type.class.getCanonicalName() + " for value " + value));
        }

    }

    @Schema
    public enum TransactionStatus {

        NEW,
        SUCCESS,
        DENIED,
        PENDING,
        FAILED,
        UNCLAIMED,
        RETURNED,
        ONHOLD,
        BLOCKED,
        REFUNDED,
        REVERSED
    }
}
