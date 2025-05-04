package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.fire.as.person.BuiltCampaignControllerActionFireAsPersonResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonJourney;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.client.rest.impl.campaign.built.controller.action.BuiltCampaignControllerActionResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers.BuiltFireAsPersonIdentificationResponseMapperRepository;
import com.extole.client.rest.person.PersonReferralReason;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionFireAsPerson;
import com.extole.model.entity.campaign.built.BuiltFireAsPersonIdentification;
import com.extole.model.entity.campaign.built.BuiltFireAsPersonJourney;

@Component
public class BuiltCampaignControllerActionFireAsPersonResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionFireAsPerson,
        BuiltCampaignControllerActionFireAsPersonResponse> {

    private final BuiltFireAsPersonIdentificationResponseMapperRepository fireAsPersonIdentificationMapperRepository;

    @Autowired
    public BuiltCampaignControllerActionFireAsPersonResponseMapper(
        BuiltFireAsPersonIdentificationResponseMapperRepository fireAsPersonIdentificationMapperRepository) {
        this.fireAsPersonIdentificationMapperRepository = fireAsPersonIdentificationMapperRepository;
    }

    @Override
    public BuiltCampaignControllerActionFireAsPersonResponse toResponse(
        BuiltCampaignControllerActionFireAsPerson action,
        ZoneId timeZone) {
        return new BuiltCampaignControllerActionFireAsPersonResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEventName(),
            toAsPersonIdentification(action.getAsPersonIdentification()),
            action.getAsPersonJourney().map(asPersonJourney -> toAsPersonJourney(asPersonJourney)),
            action.getData(),
            action.getLabels(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.FIRE_AS_PERSON;
    }

    @SuppressWarnings("unchecked")
    private FireAsPersonIdentification toAsPersonIdentification(
        BuiltFireAsPersonIdentification asPersonIdentification) {
        return fireAsPersonIdentificationMapperRepository
            .getMapper(asPersonIdentification.getPersonIdentificationType())
            .toResponse(asPersonIdentification);
    }

    private FireAsPersonJourney toAsPersonJourney(BuiltFireAsPersonJourney asPersonJourney) {
        FireAsPersonJourney.Builder builder = new FireAsPersonJourney.Builder();

        builder.withJourneyName(asPersonJourney.getJourneyName().getValue());
        asPersonJourney.getReferralReason().map(reason -> PersonReferralReason.valueOf(reason.name()))
            .ifPresent(reason -> builder.withReferralReason(reason));

        asPersonJourney.getCouponCode().ifPresent(couponCode -> builder.withCouponCode(couponCode));
        asPersonJourney.getAdvocateCode().ifPresent(advocateCode -> builder.withAdvocateCode(advocateCode));
        asPersonJourney.getShareId().ifPresent(shareId -> builder.withShareId(shareId));
        asPersonJourney.getShareableId().ifPresent(shareableId -> builder.withShareableId(shareableId));
        asPersonJourney.getCampaignId().ifPresent(campaignId -> builder.withCampaignId(campaignId));
        asPersonJourney.getContainer().ifPresent(container -> builder.withContainer(container));
        asPersonJourney.getLabel().ifPresent(label -> builder.withLabel(label));
        asPersonJourney.getReason().ifPresent(reason -> builder.withReason(reason));
        asPersonJourney.getZone().ifPresent(zone -> builder.withZone(zone));
        asPersonJourney.getPromotableCode().ifPresent(promotableCode -> builder.withPromotableCode(promotableCode));
        asPersonJourney.getAdvocatePartnerUserId()
            .ifPresent(advocatePartnerUserId -> builder.withAdvocatePartnerUserId(advocatePartnerUserId));
        return builder.build();
    }

}
