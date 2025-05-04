package com.extole.client.rest.program;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ProgramResponse {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DOMAIN = "domain";
    private static final String IS_EXTOLE_DOMAIN = "is_extole_domain";
    private static final String SHARE_URI = "share_uri";
    private static final String REDIRECT_PROGRAM_ID = "redirect_program_id";
    private static final String SITE_PATTERNS = "site_patterns";
    private static final String SSL_CERTIFICATE = "ssl_certificate";
    private static final String SSL_CERTIFICATE_CHAIN = "ssl_certificate_chain";
    private static final String SSL_PRIVATE_KEY = "ssl_private_key";
    private static final String SSL_GENERATION_POLICY = "ssl_generation_policy";
    private static final String CNAME_TARGET = "cname_target";
    private static final String SECURE = "secure";

    private final String name;
    private final String domain;
    private final Boolean isExtoleDomain;
    private final String shareUri;
    private final String redirectProgramId;
    private final String sslCertificate;
    private final String sslPrivateKey;
    private final String sslCertificateChain;
    private final SslGenerationPolicy sslGenerationPolicy;
    private final String cnameTarget;

    private final List<GlobPatternResponse> sitePatterns;

    private final String id;

    public ProgramResponse(@JsonProperty(ID) String id, @JsonProperty(NAME) String name,
        @JsonProperty(DOMAIN) String domain,
        @JsonProperty(IS_EXTOLE_DOMAIN) Boolean isExtoleDomain,
        @JsonProperty(SHARE_URI) String shareUri,
        @JsonProperty(REDIRECT_PROGRAM_ID) String redirectProgramId,
        @JsonProperty(SITE_PATTERNS) List<GlobPatternResponse> sitePatterns,
        @JsonProperty(SSL_CERTIFICATE) String sslCertificate,
        @JsonProperty(SSL_CERTIFICATE_CHAIN) String sslCertificateChain,
        @JsonProperty(SSL_PRIVATE_KEY) String sslPrivateKey,
        @JsonProperty(SSL_GENERATION_POLICY) SslGenerationPolicy sslGenerationPolicy,
        @JsonProperty(CNAME_TARGET) String cnameTarget) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.isExtoleDomain = isExtoleDomain;
        this.shareUri = shareUri;
        this.redirectProgramId = redirectProgramId;
        this.sitePatterns = sitePatterns;
        this.sslCertificate = sslCertificate;
        this.sslCertificateChain = sslCertificateChain;
        this.sslPrivateKey = sslPrivateKey;
        this.sslGenerationPolicy = sslGenerationPolicy;
        this.cnameTarget = cnameTarget;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DOMAIN)
    public String getDomain() {
        return domain;
    }

    @JsonProperty(IS_EXTOLE_DOMAIN)
    public Boolean isExtoleDomain() {
        return isExtoleDomain;
    }

    @JsonProperty(SHARE_URI)
    public String getShareUri() {
        return shareUri;
    }

    @JsonProperty(REDIRECT_PROGRAM_ID)
    public String getRedirectProgramId() {
        return redirectProgramId;
    }

    @JsonProperty(SITE_PATTERNS)
    public List<GlobPatternResponse> getSitePatterns() {
        return sitePatterns;
    }

    @JsonProperty(SSL_CERTIFICATE)
    public String getSslCertificate() {
        return sslCertificate;
    }

    @JsonProperty(SSL_CERTIFICATE_CHAIN)
    public String getSslCertificateChain() {
        return sslCertificateChain;
    }

    @JsonProperty(SSL_PRIVATE_KEY)
    public String getSslPrivateKey() {
        return sslPrivateKey;
    }

    @JsonProperty(SSL_GENERATION_POLICY)
    public SslGenerationPolicy getSslGenerationPolicy() {
        return sslGenerationPolicy;
    }

    @JsonProperty(CNAME_TARGET)
    public String getCnameTarget() {
        return cnameTarget;
    }

    @Deprecated // TODO Remove secure from my.extole.com UI ENG-12863
    @JsonProperty(SECURE)
    public Boolean getSecure() {
        return Boolean.TRUE;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
