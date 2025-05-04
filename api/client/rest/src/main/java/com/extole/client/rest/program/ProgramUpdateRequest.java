package com.extole.client.rest.program;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ProgramUpdateRequest {

    private static final String NAME = "name";
    private static final String REDIRECT_PROGRAM_ID = "redirect_program_id";
    private static final String SSL_CERTIFICATE = "ssl_certificate";
    private static final String SSL_CERTIFICATE_CHAIN = "ssl_certificate_chain";
    private static final String SSL_PRIVATE_KEY = "ssl_private_key";
    private static final String SSL_GENERATION_POLICY = "ssl_generation_policy";
    private static final String CNAME_TARGET = "cname_target";

    private final Omissible<String> name;
    private final Omissible<Optional<String>> redirectProgramId;
    private final Omissible<String> sslCertificate;
    private final Omissible<String> sslPrivateKey;
    private final Omissible<String> sslCertificateChain;
    private final Omissible<SslGenerationPolicy> sslGenerationPolicy;
    private final Omissible<String> cnameTarget;

    @JsonCreator
    ProgramUpdateRequest(@JsonProperty(NAME) Omissible<String> name,
        @JsonProperty(REDIRECT_PROGRAM_ID) Omissible<Optional<String>> redirectProgramId,
        @JsonProperty(SSL_CERTIFICATE) Omissible<String> sslCertificate,
        @JsonProperty(SSL_CERTIFICATE_CHAIN) Omissible<String> sslCertificateChain,
        @JsonProperty(SSL_PRIVATE_KEY) Omissible<String> sslPrivateKey,
        @JsonProperty(SSL_GENERATION_POLICY) Omissible<SslGenerationPolicy> sslGenerationPolicy,
        @JsonProperty(CNAME_TARGET) Omissible<String> cnameTarget) {
        this.name = name;
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
    public Omissible<String> getName() {
        return name;
    }

    // Empty string disables redirect
    @JsonProperty(REDIRECT_PROGRAM_ID)
    public Omissible<Optional<String>> getRedirectProgramId() {
        return redirectProgramId;
    }

    @JsonProperty(SSL_CERTIFICATE)
    public Omissible<String> getSslCertificate() {
        return sslCertificate;
    }

    @JsonProperty(SSL_CERTIFICATE_CHAIN)
    public Omissible<String> getSslCertificateChain() {
        return sslCertificateChain;
    }

    @JsonProperty(SSL_PRIVATE_KEY)
    public Omissible<String> getSslPrivateKey() {
        return sslPrivateKey;
    }

    @JsonProperty(SSL_GENERATION_POLICY)
    public Omissible<SslGenerationPolicy> getSslGenerationPolicy() {
        return sslGenerationPolicy;
    }

    @JsonProperty(CNAME_TARGET)
    public Omissible<String> getCnameTarget() {
        return cnameTarget;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private Omissible<String> name = Omissible.omitted();
        private Omissible<Optional<String>> redirectProgramId = Omissible.omitted();
        private Omissible<String> sslCertificate = Omissible.omitted();
        private Omissible<String> sslPrivateKey = Omissible.omitted();
        private Omissible<String> sslCertificateChain = Omissible.omitted();
        private Omissible<SslGenerationPolicy> sslGenerationPolicy = Omissible.omitted();
        private Omissible<String> cnameTarget = Omissible.omitted();

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withRedirectProgramId(String redirectProgramId) {
            this.redirectProgramId = Omissible.of(Optional.ofNullable(redirectProgramId));
            return this;
        }

        public Builder withSslCertificate(String sslCertificate) {
            this.sslCertificate = Omissible.of(sslCertificate);
            return this;
        }

        public Builder withSslPrivateKey(String sslPrivateKey) {
            this.sslPrivateKey = Omissible.of(sslPrivateKey);
            return this;
        }

        public Builder withSslCertificateChain(String sslCertificateChain) {
            this.sslCertificateChain = Omissible.of(sslCertificateChain);
            return this;
        }

        public Builder withSslGenerationPolicy(SslGenerationPolicy sslGenerationPolicy) {
            this.sslGenerationPolicy = Omissible.of(sslGenerationPolicy);
            return this;
        }

        public Builder withCnameTarget(String cnameTarget) {
            this.cnameTarget = Omissible.of(cnameTarget);
            return this;
        }

        public ProgramUpdateRequest build() {
            return new ProgramUpdateRequest(name, redirectProgramId, sslCertificate, sslCertificateChain,
                sslPrivateKey, sslGenerationPolicy, cnameTarget);
        }
    }
}
