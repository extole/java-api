package com.extole.client.rest.impl.campaign.controller.action.fire.as.person;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFireAsPersonConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonResponse;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonJourney;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentification;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response.FireAsPersonIdentificationResponseMapperRepository;
import com.extole.client.rest.person.PersonReferralReason;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionFireAsPerson;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionFireAsPersonResponseMapper implements
    CampaignControllerActionResponseMapper<
        CampaignControllerActionFireAsPerson,
        CampaignControllerActionFireAsPersonResponse,
        CampaignControllerActionFireAsPersonConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;
    private final FireAsPersonIdentificationResponseMapperRepository fireAsPersonIdentificationResponseMapperRepository;

    @Autowired
    public CampaignControllerActionFireAsPersonResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper,
        FireAsPersonIdentificationResponseMapperRepository fireAsPersonIdentificationResponseMapperRepository) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
        this.fireAsPersonIdentificationResponseMapperRepository = fireAsPersonIdentificationResponseMapperRepository;
    }

    @Override
    public CampaignControllerActionFireAsPersonResponse toResponse(CampaignControllerActionFireAsPerson action,
        ZoneId timeZone) {
        return new CampaignControllerActionFireAsPersonResponse(
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
    public CampaignControllerActionFireAsPersonConfiguration toConfiguration(
        CampaignControllerActionFireAsPerson action,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionFireAsPersonConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getEventName(),
            toConfigurationAsPersonIdentification(action.getAsPersonIdentification()),
            action.getAsPersonJourney().map(asPersonJourney -> toConfigurationAsPersonJourney(asPersonJourney)),
            action.getData(),
            action.getLabels(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.FIRE_AS_PERSON;
    }

    @SuppressWarnings("unchecked")
    private FireAsPersonIdentification toAsPersonIdentification(
        com.extole.model.entity.campaign.FireAsPersonIdentification asPersonIdentification) {
        return fireAsPersonIdentificationResponseMapperRepository
            .getMapper(asPersonIdentification.getPersonIdentificationType())
            .toResponse(asPersonIdentification);
    }

    @SuppressWarnings("unchecked")
    private com.extole.client.rest.campaign.configuration.FireAsPersonIdentification
        toConfigurationAsPersonIdentification(
            com.extole.model.entity.campaign.FireAsPersonIdentification asPersonIdentification) {
        return fireAsPersonIdentificationResponseMapperRepository
            .getMapper(asPersonIdentification.getPersonIdentificationType())
            .toConfiguration(asPersonIdentification);
    }

    private FireAsPersonJourney toAsPersonJourney(
        com.extole.model.entity.campaign.FireAsPersonJourney asPersonJourney) {
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

    private com.extole.client.rest.campaign.configuration.FireAsPersonJourney toConfigurationAsPersonJourney(
        com.extole.model.entity.campaign.FireAsPersonJourney asPersonJourney) {
        com.extole.client.rest.campaign.configuration.FireAsPersonJourney.Builder builder =
            new com.extole.client.rest.campaign.configuration.FireAsPersonJourney.Builder();

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
