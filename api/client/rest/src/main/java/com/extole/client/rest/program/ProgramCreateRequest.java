package com.extole.client.rest.program;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public final class ProgramCreateRequest {
    private static final String NAME = "name";
    private static final String DOMAIN = "domain";
    private static final String SHARE_URI = "share_uri";
    private static final String REDIRECT_PROGRAM_ID = "redirect_program_id";
    private static final String SSL_CERTIFICATE = "ssl_certificate";
    private static final String SSL_CERTIFICATE_CHAIN = "ssl_certificate_chain";
    private static final String SSL_PRIVATE_KEY = "ssl_private_key";
    private static final String SSL_GENERATION_POLICY = "ssl_generation_policy";
    private static final String CNAME_TARGET = "cname_target";

    private final String name;
    private final String domain;
    @Deprecated // TODO remove, only the scheme/path is used; ENG-9292
    private final String shareUri;
    private final String redirectProgramId;
    private final String sslCertificate;
    private final String sslPrivateKey;
    private final String sslCertificateChain;
    private final SslGenerationPolicy sslGenerationPolicy;
    private final String cnameTarget;

    @JsonCreator
    ProgramCreateRequest(
        @JsonProperty(NAME) String name,
        @JsonProperty(DOMAIN) String domain,
        @JsonProperty(SHARE_URI) String shareUri,
        @JsonProperty(REDIRECT_PROGRAM_ID) String redirectProgramId,
        @JsonProperty(SSL_CERTIFICATE) String sslCertificate,
        @JsonProperty(SSL_CERTIFICATE_CHAIN) String sslCertificateChain,
        @JsonProperty(SSL_PRIVATE_KEY) String sslPrivateKey,
        @JsonProperty(SSL_GENERATION_POLICY) SslGenerationPolicy sslGenerationPolicy,
        @JsonProperty(CNAME_TARGET) String cnameTarget) {
        this.name = name;
        this.domain = domain;
        this.shareUri = shareUri;
        this.redirectProgramId = redirectProgramId;
        this.sslCertificate = sslCertificate;
        this.sslCertificateChain = sslCertificateChain;
        this.sslPrivateKey = sslPrivateKey;
        this.sslGenerationPolicy = sslGenerationPolicy;
        this.cnameTarget = cnameTarget;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DOMAIN)
    public String getDomain() {
        return domain;
    }

    @JsonProperty(SHARE_URI)
    public Optional<String> getShareUri() {
        return Optional.ofNullable(shareUri);
    }

    @JsonProperty(REDIRECT_PROGRAM_ID)
    public Optional<String> getRedirectProgramId() {
        return Optional.ofNullable(redirectProgramId);
    }

    @JsonProperty(SSL_CERTIFICATE)
    public Optional<String> getSslCertificate() {
        return Optional.ofNullable(sslCertificate);
    }

    @JsonProperty(SSL_CERTIFICATE_CHAIN)
    public Optional<String> getSslCertificateChain() {
        return Optional.ofNullable(sslCertificateChain);
    }

    @JsonProperty(SSL_PRIVATE_KEY)
    public Optional<String> getSslPrivateKey() {
        return Optional.ofNullable(sslPrivateKey);
    }

    @JsonProperty(SSL_GENERATION_POLICY)
    public Optional<SslGenerationPolicy> getSslGenerationPolicy() {
        return Optional.ofNullable(sslGenerationPolicy);
    }

    @JsonProperty(CNAME_TARGET)
    public Optional<String> getCnameTarget() {
        return Optional.ofNullable(cnameTarget);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String name;
        private String domain;
        private String shareUri;
        private String redirectProgramId;
        private String sslCertificate;
        private String sslPrivateKey;
        private String sslCertificateChain;
        private SslGenerationPolicy sslGenerationPolicy;
        private String cnameTarget;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder withShareUri(String shareUri) {
            this.shareUri = shareUri;
            return this;
        }

        public Builder withRedirectProgramId(String redirectProgramId) {
            this.redirectProgramId = redirectProgramId;
            return this;
        }

        public Builder withSslCertificate(String sslCertificate) {
            this.sslCertificate = sslCertificate;
            return this;
        }

        public Builder withSslPrivateKey(String sslPrivateKey) {
            this.sslPrivateKey = sslPrivateKey;
            return this;
        }

        public Builder withSslCertificateChain(String sslCertificateChain) {
            this.sslCertificateChain = sslCertificateChain;
            return this;
        }

        public Builder withSslGenerationPolicy(SslGenerationPolicy sslGenerationPolicy) {
            this.sslGenerationPolicy = sslGenerationPolicy;
            return this;
        }

        public Builder withCnameTarget(String cnameTarget) {
            this.cnameTarget = cnameTarget;
            return this;
        }

        public ProgramCreateRequest build() {
            return new ProgramCreateRequest(name, domain, shareUri, redirectProgramId, sslCertificate,
                sslCertificateChain, sslPrivateKey, sslGenerationPolicy, cnameTarget);
        }
    }
}
