package com.extole.client.rest.person.v2;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.person.ConsentType;
import com.extole.common.lang.ToString;

@Schema(description = "Extole PersonResponse")
public class PersonV2Response {

    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PICTURE_URL = "picture_url";
    private static final String PARTNER_USER_ID = "partner_user_id";
    private static final String PARAMETERS = "parameters";
    private static final String COOKIE_CONSENT = "cookie_consent";
    private static final String COOKIE_CONSENT_TYPE = "cookie_consent_type";
    private static final String PROCESSING_CONSENT = "processing_consent";
    private static final String PROCESSING_CONSENT_TYPE = "processing_consent_type";
    private static final String BLOCKED = "blocked";
    private static final String SELF_REWARDING_BLOCKED = "self_rewarding_blocked";
    private static final String FRIEND_REWARDING_BLOCKED = "friend_rewarding_blocked";
    private static final String LOCALE = "locale";
    private static final String PROFILE_BLOCK = "profile_blocks";

    private final String id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String pictureUrl;
    private final String partnerUserId;
    private final String cookieConsent;
    private final ConsentType cookieConsentType;
    private final String processingConsent;
    private final ConsentType processingConsentType;
    private final Map<String, Object> parameters;
    private final boolean blocked;
    private final boolean selfRewardingBlocked;
    private final boolean friendRewardingBlocked;
    private final PersonLocaleV2Response locale;
    private final ProfileBlockResponse profileBlock;

    public PersonV2Response(@JsonProperty(ID) String id,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(FIRST_NAME) String firstName,
        @JsonProperty(LAST_NAME) String lastName,
        @JsonProperty(PICTURE_URL) String pictureUrl,
        @JsonProperty(PARTNER_USER_ID) String partnerUserId,
        @JsonProperty(COOKIE_CONSENT) String cookieConsent,
        @JsonProperty(COOKIE_CONSENT_TYPE) ConsentType cookieConsentType,
        @JsonProperty(PROCESSING_CONSENT) String processingConsent,
        @JsonProperty(PROCESSING_CONSENT_TYPE) ConsentType processingConsentType,
        @JsonProperty(PARAMETERS) Map<String, Object> parameters,
        @JsonProperty(BLOCKED) boolean blocked,
        @JsonProperty(SELF_REWARDING_BLOCKED) boolean selfRewardingBlocked,
        @JsonProperty(FRIEND_REWARDING_BLOCKED) boolean friendRewardingBlocked,
        @JsonProperty(LOCALE) PersonLocaleV2Response locale,
        @JsonProperty(PROFILE_BLOCK) ProfileBlockResponse profileBlock) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
        this.partnerUserId = partnerUserId;
        this.cookieConsent = cookieConsent;
        this.cookieConsentType = cookieConsentType;
        this.processingConsent = processingConsent;
        this.processingConsentType = processingConsentType;
        this.parameters = parameters;
        this.blocked = blocked;
        this.selfRewardingBlocked = selfRewardingBlocked;
        this.friendRewardingBlocked = friendRewardingBlocked;
        this.locale = locale;
        this.profileBlock = profileBlock;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @Schema(description = "The first name of the friend.")
    @JsonProperty(FIRST_NAME)
    public String getFirstName() {
        return firstName;
    }

    @Schema(description = "The last name of the friend.")
    @JsonProperty(LAST_NAME)
    public String getLastName() {
        return lastName;
    }

    @Schema(description = "A URL that has a picture of the person.")
    @JsonProperty(PICTURE_URL)
    public String getPictureUrl() {
        return pictureUrl;
    }

    @Schema(description = "Your unique identifier for this person.")
    @JsonProperty(PARTNER_USER_ID)
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @JsonProperty(COOKIE_CONSENT)
    public String getCookieConsent() {
        return cookieConsent;
    }

    @JsonProperty(COOKIE_CONSENT_TYPE)
    public ConsentType getCookieConsentType() {
        return cookieConsentType;
    }

    @JsonProperty(PROCESSING_CONSENT)
    public String getProcessingConsent() {
        return processingConsent;
    }

    @JsonProperty(PROCESSING_CONSENT_TYPE)
    public ConsentType getProcessingConsentType() {
        return processingConsentType;
    }

    @JsonProperty(PARAMETERS)
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @JsonProperty(BLOCKED)
    public boolean getBlocked() {
        return blocked;
    }

    @JsonProperty(SELF_REWARDING_BLOCKED)
    public boolean getSelfRewardingBlocked() {
        return selfRewardingBlocked;
    }

    @JsonProperty(FRIEND_REWARDING_BLOCKED)
    public boolean getFriendRewardingBlocked() {
        return friendRewardingBlocked;
    }

    @JsonProperty(LOCALE)
    public PersonLocaleV2Response getLocale() {
        return locale;
    }

    @JsonProperty(PROFILE_BLOCK)
    public ProfileBlockResponse getProfileBlock() {
        return profileBlock;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class ProfileBlockResponse {
        private static final String REASON = "reason";
        private static final String DATE = "date";

        private final String blockReason;
        private final ZonedDateTime blockDate;

        public ProfileBlockResponse(@JsonProperty(REASON) String blockReason,
            @JsonProperty(DATE) ZonedDateTime blockDate) {
            this.blockReason = blockReason;
            this.blockDate = blockDate;
        }

        @JsonProperty(REASON)
        public String getBlockReason() {
            return blockReason;
        }

        @JsonProperty(DATE)
        public ZonedDateTime getBlockDate() {
            return blockDate;
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
