package com.extole.client.rest.core;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

public class ClientCoreSettingsRequest {
    private static final String JSON_SOURCE = "source";
    private static final String JSON_VERSION = "version";
    private static final String JSON_LEGACY_TAGS_ENABLED = "legacy_tags_enabled";
    private static final String JSON_THIRD_PARTY_COOKIES_DISABLED = "third_party_cookies_disabled";
    private static final String JSON_GLOBAL_ZONE_PARAMETERS_ENABLED = "global_zone_parameters_enabled";
    private static final String JSON_ZONE_POST_ENABLED = "zone_post_enabled";
    private static final String JSON_JS_CREATIVE_RESPONDS_HTML_ENABLED = "js_creative_responds_html_enabled";
    private static final String JSON_ACCESS_TOKEN_INCLUDED_IN_RESPONSE_ENABLED =
        "access_token_included_in_response_enabled";
    private static final String JSON_DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED = "deprecated_access_token_cookie_allowed";
    private static final String JSON_ORIGIN_HOST_OVERRIDE = "origin_host_override";
    private static final String JSON_COOKIE_POLICY = "cookie_policy";
    private static final String JSON_COOKIE_CONSENT_POLICY = "cookie_consent_policy";

    private final String source;
    private final String version;
    private final Boolean legacyTagsEnabled;
    private final Boolean thirdPartyCookiesDisabled;
    private final Boolean globalZoneParametersEnabled;
    private final Boolean zonePostEnabled;
    private final Boolean jsCreativeRespondsWithHtmlEnabled;
    private final Boolean accessTokenIncludedInResponseEnabled;
    private final Boolean deprecatedAccessTokenCookieAllowed;
    private final String originHostOverride;
    private final CookiePolicy cookiePolicy;
    private final CookieConsentPolicy cookieConsentPolicy;

    @JsonCreator
    ClientCoreSettingsRequest(
        @Nullable @JsonProperty(JSON_SOURCE) String source,
        @Nullable @JsonProperty(JSON_VERSION) String version,
        @Nullable @JsonProperty(JSON_LEGACY_TAGS_ENABLED) Boolean legacyTagsEnabled,
        @Nullable @JsonProperty(JSON_THIRD_PARTY_COOKIES_DISABLED) Boolean thirdPartyCookiesDisabled,
        @Nullable @JsonProperty(JSON_GLOBAL_ZONE_PARAMETERS_ENABLED) Boolean globalZoneParametersEnabled,
        @Nullable @JsonProperty(JSON_ZONE_POST_ENABLED) Boolean zonePostEnabled,
        @Nullable @JsonProperty(JSON_JS_CREATIVE_RESPONDS_HTML_ENABLED) Boolean jsCreativeRespondsWithHtmlEnabled,
        @Nullable @JsonProperty(JSON_ACCESS_TOKEN_INCLUDED_IN_RESPONSE_ENABLED)
        Boolean accessTokenIncludedInResponseEnabled,
        @Nullable @JsonProperty(JSON_DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED) Boolean deprecatedAccessTokenCookieAllowed,
        @Nullable @JsonProperty(JSON_ORIGIN_HOST_OVERRIDE) String originHostOverride,
        @Nullable @JsonProperty(JSON_COOKIE_POLICY) CookiePolicy cookiePolicy,
        @Nullable @JsonProperty(JSON_COOKIE_CONSENT_POLICY) CookieConsentPolicy cookieConsentPolicy) {
        this.source = source;
        this.version = version;
        this.legacyTagsEnabled = legacyTagsEnabled;
        this.thirdPartyCookiesDisabled = thirdPartyCookiesDisabled;
        this.globalZoneParametersEnabled = globalZoneParametersEnabled;
        this.zonePostEnabled = zonePostEnabled;
        this.jsCreativeRespondsWithHtmlEnabled = jsCreativeRespondsWithHtmlEnabled;
        this.accessTokenIncludedInResponseEnabled = accessTokenIncludedInResponseEnabled;
        this.deprecatedAccessTokenCookieAllowed = deprecatedAccessTokenCookieAllowed;
        this.originHostOverride = originHostOverride;
        this.cookiePolicy = cookiePolicy;
        this.cookieConsentPolicy = cookieConsentPolicy;
    }

    @Nullable
    @JsonProperty(JSON_SOURCE)
    public String getSource() {
        return source;
    }

    @Nullable
    @JsonProperty(JSON_VERSION)
    public String getVersion() {
        return version;
    }

    @Nullable
    @JsonProperty(JSON_LEGACY_TAGS_ENABLED)
    public Boolean isLegacyTagsEnabled() {
        return legacyTagsEnabled;
    }

    @Nullable
    @JsonProperty(JSON_THIRD_PARTY_COOKIES_DISABLED)
    public Boolean isThirdPartyCookiesDisabled() {
        return thirdPartyCookiesDisabled;
    }

    @Nullable
    @JsonProperty(JSON_GLOBAL_ZONE_PARAMETERS_ENABLED)
    public Boolean isGlobalZoneParametersEnabled() {
        return globalZoneParametersEnabled;
    }

    @Nullable
    @JsonProperty(JSON_ZONE_POST_ENABLED)
    public Boolean isZonePostEnabled() {
        return zonePostEnabled;
    }

    @Nullable
    @JsonProperty(JSON_JS_CREATIVE_RESPONDS_HTML_ENABLED)
    public Boolean isJsCreativeRespondsWithHtmlEnabled() {
        return jsCreativeRespondsWithHtmlEnabled;
    }

    @Nullable
    @JsonProperty(JSON_ACCESS_TOKEN_INCLUDED_IN_RESPONSE_ENABLED)
    public Boolean isAccessTokenIncludedInResponseEnabled() {
        return accessTokenIncludedInResponseEnabled;
    }

    @Nullable
    @JsonProperty(JSON_DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED)
    public Boolean isDeprecatedAccessTokenCookieAllowed() {
        return deprecatedAccessTokenCookieAllowed;
    }

    @Nullable
    @JsonProperty(JSON_ORIGIN_HOST_OVERRIDE)
    public String getOriginHostOverride() {
        return originHostOverride;
    }

    @Nullable
    @JsonProperty(JSON_COOKIE_POLICY)
    public CookiePolicy getCookiePolicy() {
        return cookiePolicy;
    }

    @Nullable
    @JsonProperty(JSON_COOKIE_CONSENT_POLICY)
    public CookieConsentPolicy getCookieConsentPolicy() {
        return cookieConsentPolicy;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Schema
    public enum CookiePolicy {
        DEFAULT_TO_YEAR, REQUIRE_COOKIE_CONSENT
    }

    @Schema
    public enum CookieConsentPolicy {
        COOKIE_CONSENT_OR_SESSION_COOKIE, COOKIE_CONSENT_OR_NO_COOKIE
    }

    public static final class Builder {
        private String source;
        private String version;
        private Boolean legacyTagsEnabled;
        private Boolean thirdPartyCookiesDisabled;
        private Boolean globalZoneParametersEnabled;
        private Boolean zonePostEnabled;
        private Boolean jsCreativeRespondsWithHtmlEnabled;
        private Boolean accessTokenIncludedInResponseEnabled;
        private Boolean deprecatedAccessTokenCookieAllowed;
        private String originHostOverride;
        private CookiePolicy cookiePolicy;
        private CookieConsentPolicy cookieConsentPolicy;

        private Builder() {
        }

        public Builder withSource(String source) {
            this.source = source;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withLegacyTagsEnabled(Boolean legacyTagsEnabled) {
            this.legacyTagsEnabled = legacyTagsEnabled;
            return this;
        }

        public Builder withThirdPartyCookiesDisabled(Boolean thirdPartyCookiesDisabled) {
            this.thirdPartyCookiesDisabled = thirdPartyCookiesDisabled;
            return this;
        }

        public Builder withGlobalZoneParametersEnabled(Boolean globalZoneParametersEnabled) {
            this.globalZoneParametersEnabled = globalZoneParametersEnabled;
            return this;
        }

        public Builder withZonePostEnabled(Boolean zonePostEnabled) {
            this.zonePostEnabled = zonePostEnabled;
            return this;
        }

        public Builder withJsCreativeRespondsWithHtmlEnabled(Boolean isJsCreativeRespondsWithHtmlEnabled) {
            this.jsCreativeRespondsWithHtmlEnabled = isJsCreativeRespondsWithHtmlEnabled;
            return this;
        }

        public Builder withAccessTokenIncludedInResponseEnabled(Boolean accessTokenIncludedInResponseEnabled) {
            this.accessTokenIncludedInResponseEnabled = accessTokenIncludedInResponseEnabled;
            return this;
        }

        public Builder withDeprecatedAccessTokenCookieAllowed(Boolean deprecatedAccessTokenCookieAllowed) {
            this.deprecatedAccessTokenCookieAllowed = deprecatedAccessTokenCookieAllowed;
            return this;
        }

        public Builder withOriginHostOverride(String originHostOverride) {
            this.originHostOverride = originHostOverride;
            return this;
        }

        public Builder withCookiePolicy(CookiePolicy cookiePolicy) {
            this.cookiePolicy = cookiePolicy;
            return this;
        }

        public Builder withCookieConsentPolicy(CookieConsentPolicy cookieConsentPolicy) {
            this.cookieConsentPolicy = cookieConsentPolicy;
            return this;
        }

        public ClientCoreSettingsRequest build() {
            return new ClientCoreSettingsRequest(source, version, legacyTagsEnabled, thirdPartyCookiesDisabled,
                globalZoneParametersEnabled, zonePostEnabled, jsCreativeRespondsWithHtmlEnabled,
                accessTokenIncludedInResponseEnabled, deprecatedAccessTokenCookieAllowed, originHostOverride,
                cookiePolicy, cookieConsentPolicy);
        }
    }

}
