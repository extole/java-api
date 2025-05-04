package com.extole.consumer.rest.debug.security.report;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportRequest {

    private static final String JSON_BLOCKED_URI = "blocked-uri";
    private static final String JSON_DISPOSITION = "disposition";
    private static final String JSON_DOCUMENT_URI = "document-uri";
    private static final String JSON_EFFECTIVE_DIRECTIVE = "effective-directive";
    private static final String JSON_ORIGINAL_POLICY = "original-policy";
    private static final String JSON_REFERRER = "referrer";
    private static final String JSON_SCRIPT_SAMPLE = "script-sample";
    private static final String JSON_STATUS_CODE = "status-code";
    private static final String JSON_VIOLATED_DIRECTIVE = "violated-directive";

    private final String blockedUri;
    private final String disposition;
    private final String documentUri;
    private final String effectiveDirective;
    private final String originalPolicy;
    private final String referrer;
    private final String scriptSample;
    private final String statusCode;
    private final String violatedDirective;

    @JsonCreator
    public ReportRequest(@Nullable @JsonProperty(JSON_BLOCKED_URI) String blockedUri,
        @Nullable @JsonProperty(JSON_DISPOSITION) String disposition,
        @Nullable @JsonProperty(JSON_DOCUMENT_URI) String documentUri,
        @Nullable @JsonProperty(JSON_EFFECTIVE_DIRECTIVE) String effectiveDirective,
        @Nullable @JsonProperty(JSON_ORIGINAL_POLICY) String originalPolicy,
        @Nullable @JsonProperty(JSON_REFERRER) String referrer,
        @Nullable @JsonProperty(JSON_SCRIPT_SAMPLE) String scriptSample,
        @Nullable @JsonProperty(JSON_STATUS_CODE) String statusCode,
        @Nullable @JsonProperty(JSON_VIOLATED_DIRECTIVE) String violatedDirective) {
        this.blockedUri = blockedUri;
        this.disposition = disposition;
        this.documentUri = documentUri;
        this.effectiveDirective = effectiveDirective;
        this.originalPolicy = originalPolicy;
        this.referrer = referrer;
        this.scriptSample = scriptSample;
        this.statusCode = statusCode;
        this.violatedDirective = violatedDirective;
    }

    @JsonProperty(JSON_BLOCKED_URI)
    public Optional<String> getBlockedUri() {
        return Optional.ofNullable(blockedUri);
    }

    @JsonProperty(JSON_DISPOSITION)
    public Optional<String> getDisposition() {
        return Optional.ofNullable(disposition);
    }

    @JsonProperty(JSON_DOCUMENT_URI)
    public Optional<String> getDocumentUri() {
        return Optional.ofNullable(documentUri);
    }

    @JsonProperty(JSON_EFFECTIVE_DIRECTIVE)
    public Optional<String> getEffectiveDirective() {
        return Optional.ofNullable(effectiveDirective);
    }

    @JsonProperty(JSON_ORIGINAL_POLICY)
    public Optional<String> getOriginalPolicy() {
        return Optional.ofNullable(originalPolicy);
    }

    @JsonProperty(JSON_REFERRER)
    public Optional<String> getReferrer() {
        return Optional.ofNullable(referrer);
    }

    @JsonProperty(JSON_SCRIPT_SAMPLE)
    public Optional<String> getScriptSample() {
        return Optional.ofNullable(scriptSample);
    }

    @JsonProperty(JSON_STATUS_CODE)
    public Optional<String> getStatusCode() {
        return Optional.ofNullable(statusCode);
    }

    @JsonProperty(JSON_VIOLATED_DIRECTIVE)
    public Optional<String> getViolatedDirective() {
        return Optional.ofNullable(violatedDirective);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String blockedUri;
        private String disposition;
        private String documentUri;
        private String effectiveDirective;
        private String originalPolicy;
        private String referrer;
        private String scriptSample;
        private String statusCode;
        private String violatedDirective;

        public Builder withBlockedUri(String blockedUri) {
            this.blockedUri = blockedUri;
            return this;
        }

        public Builder withDisposition(String disposition) {
            this.disposition = disposition;
            return this;
        }

        public Builder withDocumentUri(String documentUri) {
            this.documentUri = documentUri;
            return this;
        }

        public Builder withEffectiveDirective(String effectiveDirective) {
            this.effectiveDirective = effectiveDirective;
            return this;
        }

        public Builder withOriginalPolicy(String originalPolicy) {
            this.originalPolicy = originalPolicy;
            return this;
        }

        public Builder withReferrer(String referrer) {
            this.referrer = referrer;
            return this;
        }

        public Builder withScriptSample(String scriptSample) {
            this.scriptSample = scriptSample;
            return this;
        }

        public Builder withStatusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withViolatedDirective(String violatedDirective) {
            this.violatedDirective = violatedDirective;
            return this;
        }

        public ReportRequest build() {
            return new ReportRequest(blockedUri, disposition, documentUri, effectiveDirective, originalPolicy, referrer,
                scriptSample, statusCode, violatedDirective);
        }
    }
}
