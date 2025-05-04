package com.extole.client.rest.person.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.person.ProfileBlockV4Request;
import com.extole.common.lang.ToString;

@Schema(description = "Extole PersonRequest")
public class PersonV4Request {

    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PICTURE_URL = "picture_url";
    private static final String PARTNER_USER_ID = "partner_user_id";
    private static final String BLOCKED = "blocked";
    private static final String SELF_REWARDING_BLOCKED = "self_rewarding_blocked";
    private static final String FRIEND_REWARDING_BLOCKED = "friend_rewarding_blocked";
    private static final String PROFILE_BLOCK = "profile_block";

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String pictureUrl;
    private final String partnerUserId;
    private final Boolean blocked;
    private final Boolean selfRewardingBlocked;
    private final Boolean friendRewardingBlocked;
    private final ProfileBlockV4Request profileBlock;

    public PersonV4Request(@Nullable @JsonProperty(EMAIL) String email,
        @Nullable @JsonProperty(FIRST_NAME) String firstName,
        @Nullable @JsonProperty(LAST_NAME) String lastName,
        @Nullable @JsonProperty(PICTURE_URL) String pictureUrl,
        @Nullable @JsonProperty(PARTNER_USER_ID) String partnerUserId,
        @Nullable @JsonProperty(BLOCKED) Boolean blocked,
        @Nullable @JsonProperty(SELF_REWARDING_BLOCKED) Boolean selfRewardingBlocked,
        @Nullable @JsonProperty(FRIEND_REWARDING_BLOCKED) Boolean friendRewardingBlocked,
        @Nullable @JsonProperty(PROFILE_BLOCK) ProfileBlockV4Request profileBlock) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureUrl = pictureUrl;
        this.partnerUserId = partnerUserId;
        this.blocked = blocked;
        this.selfRewardingBlocked = selfRewardingBlocked;
        this.friendRewardingBlocked = friendRewardingBlocked;
        this.profileBlock = profileBlock;
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

    @Nullable
    @JsonProperty(BLOCKED)
    public Boolean getBlocked() {
        return blocked;
    }

    @JsonProperty(SELF_REWARDING_BLOCKED)
    public Boolean getSelfRewardingBlocked() {
        return selfRewardingBlocked;
    }

    @JsonProperty(FRIEND_REWARDING_BLOCKED)
    public Boolean getFriendRewardingBlocked() {
        return friendRewardingBlocked;
    }

    @Nullable
    @JsonProperty(PROFILE_BLOCK)
    public ProfileBlockV4Request getProfileBlock() {
        return profileBlock;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String email;
        private String firstName;
        private String lastName;
        private String pictureUrl;
        private String partnerUserId;
        private Boolean blocked;
        private Boolean selfRewardingBlocked;
        private Boolean friendRewardingBlocked;
        private ProfileBlockV4Request profileBlock;

        private Builder() {
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withPictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        public Builder withPartnerUserId(String partnerUserId) {
            this.partnerUserId = partnerUserId;
            return this;
        }

        public Builder withBlocked(Boolean blocked) {
            this.blocked = blocked;
            return this;
        }

        public Builder withSelfRewardingBlocked(Boolean selfRewardingBlocked) {
            this.selfRewardingBlocked = selfRewardingBlocked;
            return this;
        }

        public Builder withFriendRewardingBlocked(Boolean friendRewardingBlocked) {
            this.friendRewardingBlocked = friendRewardingBlocked;
            return this;
        }

        public Builder withProfileBlock(ProfileBlockV4Request profileBlock) {
            this.profileBlock = profileBlock;
            return this;
        }

        public PersonV4Request build() {
            return new PersonV4Request(email, firstName, lastName, pictureUrl, partnerUserId, blocked,
                selfRewardingBlocked, friendRewardingBlocked, profileBlock);
        }
    }
}
