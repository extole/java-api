package com.extole.client.rest.core;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.core.ClientCoreSettingsRequest.CookieConsentPolicy;
import com.extole.client.rest.core.ClientCoreSettingsRequest.CookieDomainPolicy;
import com.extole.client.rest.core.ClientCoreSettingsRequest.CookiePolicy;
import com.extole.common.lang.ToString;

public class ClientCoreSettingsResponse {
    private static final String JSON_SOURCE = "source";
    private static final String JSON_VERSION = "version";
    private static final String JSON_LEGACY_TAGS_ENABLED = "legacy_tags_enabled";
    private static final String JSON_BACKEND_TARGETING_ENABLED = "backend_targeting_enabled";
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
    private static final String JSON_COOKIE_DOMAIN_POLICY = "cookie_domain_policy";

    private final String source;
    private final String version;
    private final boolean legacyTagsEnabled;
    private final boolean thirdPartyCookiesDisabled;
    private final boolean globalZoneParametersEnabled;
    private final boolean zonePostEnabled;
    private final boolean jsCreativeRespondsWithHtmlEnabled;
    private final boolean accessTokenIncludedInResponseEnabled;
    private final boolean deprecatedAccessTokenCookieAllowed;
    private final String originHostOverride;
    private final CookiePolicy cookiePolicy;
    private final CookieConsentPolicy cookieConsentPolicy;
    private final CookieDomainPolicy cookieDomainPolicy;

    @JsonCreator
    public ClientCoreSettingsResponse(
        @JsonProperty(JSON_SOURCE) String source,
        @JsonProperty(JSON_VERSION) String version,
        @JsonProperty(JSON_LEGACY_TAGS_ENABLED) boolean legacyTagsEnabled,
        @JsonProperty(JSON_THIRD_PARTY_COOKIES_DISABLED) boolean thirdPartyCookiesDisabled,
        @JsonProperty(JSON_GLOBAL_ZONE_PARAMETERS_ENABLED) boolean globalZoneParametersEnabled,
        @JsonProperty(JSON_ZONE_POST_ENABLED) boolean zonePostEnabled,
        @JsonProperty(JSON_JS_CREATIVE_RESPONDS_HTML_ENABLED) boolean jsCreativeRespondsWithHtmlEnabled,
        @JsonProperty(JSON_ACCESS_TOKEN_INCLUDED_IN_RESPONSE_ENABLED) boolean accessTokenIncludedInResponseEnabled,
        @JsonProperty(JSON_DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED) boolean deprecatedAccessTokenCookieAllowed,
        @JsonProperty(JSON_ORIGIN_HOST_OVERRIDE) String originHostOverride,
        @JsonProperty(JSON_COOKIE_POLICY) CookiePolicy cookiePolicy,
        @JsonProperty(JSON_COOKIE_CONSENT_POLICY) CookieConsentPolicy cookieConsentPolicy,
        @JsonProperty(JSON_COOKIE_DOMAIN_POLICY) CookieDomainPolicy cookieDomainPolicy) {
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
        this.cookieDomainPolicy = cookieDomainPolicy;
    }

    @Nullable
    @JsonProperty(JSON_SOURCE)
    public String getSource() {
        return source;
    }

    @JsonProperty(JSON_VERSION)
    public String getVersion() {
        return version;
    }

    @JsonProperty(JSON_LEGACY_TAGS_ENABLED)
    public boolean isLegacyTagsEnabled() {
        return legacyTagsEnabled;
    }

    @JsonProperty(JSON_THIRD_PARTY_COOKIES_DISABLED)
    public boolean isThirdPartyCookiesDisabled() {
        return thirdPartyCookiesDisabled;
    }

    @JsonProperty(JSON_GLOBAL_ZONE_PARAMETERS_ENABLED)
    public boolean isGlobalZoneParametersEnabled() {
        return globalZoneParametersEnabled;
    }

    @JsonProperty(JSON_ZONE_POST_ENABLED)
    public boolean isZonePostEnabled() {
        return zonePostEnabled;
    }

    @JsonProperty(JSON_JS_CREATIVE_RESPONDS_HTML_ENABLED)
    public boolean isJsCreativeRespondsWithHtmlEnabled() {
        return jsCreativeRespondsWithHtmlEnabled;
    }

    @JsonProperty(JSON_ACCESS_TOKEN_INCLUDED_IN_RESPONSE_ENABLED)
    public boolean isAccessTokenIncludedInResponseEnabled() {
        return accessTokenIncludedInResponseEnabled;
    }

    @Deprecated // TODO Remove after 2026-09 once access_token cookie is not supported - ENG-23277
    @JsonProperty(JSON_DEPRECATED_ACCESS_TOKEN_COOKIE_ALLOWED)
    public boolean isDeprecatedAccessTokenCookieAllowed() {
        return deprecatedAccessTokenCookieAllowed;
    }

    @Nullable
    @JsonProperty(JSON_ORIGIN_HOST_OVERRIDE)
    public String getOriginHostOverride() {
        return originHostOverride;
    }

    @JsonProperty(JSON_COOKIE_POLICY)
    public CookiePolicy getCookiePolicy() {
        return cookiePolicy;
    }

    @JsonProperty(JSON_COOKIE_CONSENT_POLICY)
    public CookieConsentPolicy getCookieConsentPolicy() {
        return cookieConsentPolicy;
    }

    @JsonProperty(JSON_COOKIE_DOMAIN_POLICY)
    public CookieDomainPolicy getCookieDomainPolicy() {
        return cookieDomainPolicy;
    }

    @Deprecated // TODO remove ENG-10144
    @JsonProperty(JSON_BACKEND_TARGETING_ENABLED)
    public boolean isBackendTargetingEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
