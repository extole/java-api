package com.extole.client.rest.impl.person.v2;

import java.time.Instant;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.person.ConsentType;
import com.extole.client.rest.person.v2.PersonLocaleV2Response;
import com.extole.client.rest.person.v2.PersonV2Response;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.ProfileBlock;
import com.extole.person.service.profile.locale.PersonLocale;

@Component
public class PersonV2RestMapper {

    public PersonV2Response toPersonResponse(Person person, ZoneId timeZone) {
        return new PersonV2Response(
            person.getId().getValue(),
            person.getEmail(),
            person.getFirstName(),
            person.getLastName(),
            person.getProfilePictureUrl() != null ? person.getProfilePictureUrl().toString() : null,
            person.getPartnerUserId(),
            person.getCookieConsentedAt().map(Instant::toString).orElse(null),
            person.getCookieConsentType()
                .map(consentType -> ConsentType.valueOf(consentType.name())).orElse(null),
            person.getProcessingConsent().map(Instant::toString).orElse(null),
            person.getProcessingConsentType()
                .map(consentType -> ConsentType.valueOf(consentType.name())).orElse(null),
            person.getData(),
            person.isBlocked(),
            person.isSelfRewardingBlocked(),
            person.isFriendRewardingBlocked(),
            toPersonLocaleResponse(person.getLocale()),
            !person.isBlocked()
                ? null
                : person.getProfileBlock().map(item -> toProfileBlockResponse(item, timeZone)).orElse(null));
    }

    private PersonLocaleV2Response toPersonLocaleResponse(PersonLocale locale) {
        return new PersonLocaleV2Response(
            locale.getLastBrowser().orElse(null),
            locale.getUserSpecified().orElse(null));
    }

    private PersonV2Response.ProfileBlockResponse toProfileBlockResponse(ProfileBlock profileBlock, ZoneId timeZone) {
        return new PersonV2Response.ProfileBlockResponse(
            profileBlock.getReason().orElse(null),
            profileBlock.getDate().atZone(timeZone));
    }

}
