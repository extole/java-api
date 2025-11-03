package com.extole.consumer.rest.impl.me.journey;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.journey.JourneyName;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.journey.JourneyResponse;
import com.extole.consumer.rest.me.journey.MeJourneyEndpoints;
import com.extole.person.service.profile.journey.PersonJourney;
import com.extole.person.service.profile.referral.PersonReferralReason;

@Provider
public class MeJourneyEndpointsImpl implements MeJourneyEndpoints {

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;

    @Autowired
    public MeJourneyEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
    }

    @Override
    public List<JourneyResponse> getJourneys(String accessToken, String container, String journeyName)
        throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        return authorization.getIdentity().getJourneys().stream()
            .filter(journey -> container == null || journey.getContainer().getName().equals(container))
            .filter(journey -> StringUtils.isBlank(journeyName)
                || journey.getJourneyName().equals(JourneyName.valueOf(journeyName)))
            .map(this::toJourneyResponse)
            .collect(Collectors.toList());
    }

    private JourneyResponse toJourneyResponse(PersonJourney journey) {
        return new JourneyResponse(
            journey.getId().getValue(),
            journey.getCampaignId().getValue(),
            journey.getEntryLabel().orElse(null),
            journey.getContainer().getName(),
            journey.getJourneyName().getValue(),
            journey.getEntryReason().orElse(null),
            journey.getEntryZone().orElse(null),
            journey.getLastZone().orElse(null),
            journey.getEntryShareId().map(entryShareId -> entryShareId.getValue()).orElse(null),
            journey.getLastShareId().map(lastShareId -> lastShareId.getValue()).orElse(null),
            journey.getEntryShareableId().map(entryShareableId -> entryShareableId.getValue()).orElse(null),
            journey.getLastShareableId().map(lastShareableId -> lastShareableId.getValue()).orElse(null),
            journey.getEntryAdvocateCode().orElse(null),
            journey.getLastAdvocateCode().orElse(null),
            journey.getEntryPromotableCode().orElse(null),
            journey.getLastPromotableCode().orElse(null),
            journey.getEntryConsumerEventId().map(entryConsumerEventId -> entryConsumerEventId.getValue()).orElse(null),
            journey.getLastConsumerEventId().map(lastConsumerEventId -> lastConsumerEventId.getValue()).orElse(null),
            journey.getEntryProfileId() != null ? journey.getEntryProfileId().getValue() : null,
            journey.getLastProfileId() != null ? journey.getLastProfileId().getValue() : null,
            journey.getEntryAdvocatePartnerId().orElse(null),
            journey.getLastAdvocatePartnerId().orElse(null),
            journey.getEntryCouponCode().orElse(null),
            journey.getLastCouponCode().orElse(null),
            journey.getEntryReferralReason().map(PersonReferralReason::name).orElse(null),
            journey.getLastReferralReason().map(PersonReferralReason::name).orElse(null),
            journey.getCreatedDate().toString(),
            journey.getUpdatedDate().toString(),
            journey.getData());
    }

}
