package com.extole.client.rest.impl.events;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.extole.common.lang.ToString;
import com.extole.id.EpochPlusRandom;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonPropertyOrder({"partner_conversion_id"})
class ApprovalRequest {

    @JsonProperty("client_id")
    private final Long clientId;
    @JsonProperty("event_id")
    private final Long eventId;
    @JsonProperty("partner_conversion_id")
    private final String partnerConversionId;
    @JsonProperty("approval_event")
    private final String approvalEvent;
    @JsonProperty("note")
    private final String note;
    @JsonProperty("force")
    private final boolean force;

    ApprovalRequest(@JsonProperty("client_id") Long clientId,
        @JsonProperty("event_id") Long eventId,
        @JsonProperty("partner_conversion_id") String partnerConversionId,
        @JsonProperty("approval_event") String approvalEvent,
        @JsonProperty("note") String note,
        @JsonProperty("force") boolean force) {
        this.clientId = clientId;
        this.eventId = eventId;
        this.partnerConversionId = partnerConversionId;
        this.approvalEvent = approvalEvent;
        this.note = note;
        this.force = force;
    }

    enum ApproveType {
        APPROVE, DECLINE, NOTHING
    }

    public Long getEventId() {
        return eventId;
    }

    public String getPartnerConversionId() {
        return partnerConversionId;
    }

    public void validate() throws ApprovalException {
        List<String> errors = new ArrayList<>();
        if (approvalEvent == null) {
            errors.add("Missing mandatary field value for approval_status");
        } else {
            try {
                ApproveType.valueOf(approvalEvent.toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("invalid approval_status");
            }
        }
        if (!errors.isEmpty()) {
            throw new ApprovalException(String.join("; ", errors));
        }
    }

    public ApproveType getApproveType() {
        return ApproveType.valueOf(approvalEvent.toUpperCase());
    }

    public String getNote() {
        return note;
    }

    public boolean getForce() {
        return force;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @SuppressWarnings("serial")
    @JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
    @JsonPropertyOrder({"code", "message", "params", "uniqueId"})
    static final class ApprovalException extends Exception {
        private final long uniqueId = new EpochPlusRandom().generateId().longValue();
        private final String code;
        private final int httpCode;

        ApprovalException(String message) {
            this(Status.BAD_REQUEST.getStatusCode(), "BAD REQUEST", message);
        }

        ApprovalException(int httpCode, String code, String message) {
            super(message);
            this.httpCode = httpCode;
            this.code = code;
        }

        @JsonProperty("message")
        @Override
        public String getMessage() {
            return super.getMessage();
        }

        @JsonProperty("code")
        public String getCode() {
            return code;
        }

        public int getHttpCode() {
            return httpCode;
        }

        @JsonProperty("uniqueId")
        public long getUniqueId() {
            return uniqueId;
        }

    }
}
