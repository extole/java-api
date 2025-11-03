package com.extole.client.rest.impl.campaign.controller.action.fire.as.person;

import java.time.ZoneId;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignComponentReferenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFireAsPersonConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.configuration.FireAsPersonIdentification;
import com.extole.client.rest.campaign.configuration.FireAsPersonJourney;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonValidationRestException;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionUploader;
import com.extole.client.rest.impl.campaign.controller.action.fire.as.person.request.FireAsPersonIdentificationRequestMapperRepository;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.common.journey.JourneyName;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.CampaignControllerActionQuality;
import com.extole.model.entity.campaign.FireAsPersonIdenticationType;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonLabelInvalidCharactersException;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonLabelLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyFieldInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.journey.FireAsPersonJourneyFieldLengthException;
import com.extole.person.service.profile.referral.PersonReferralReason;

@Component
public class CampaignControllerActionFireAsPersonUploader
    implements CampaignControllerActionUploader<CampaignControllerActionFireAsPersonConfiguration> {

    private final FireAsPersonIdentificationRequestMapperRepository fireAsPersonIdentificationRequestMapperRepository;

    public CampaignControllerActionFireAsPersonUploader(
        FireAsPersonIdentificationRequestMapperRepository fireAsPersonIdentificationRequestMapperRepository) {
        this.fireAsPersonIdentificationRequestMapperRepository = fireAsPersonIdentificationRequestMapperRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void upload(CampaignUploadContext context, CampaignStepConfiguration step,
        CampaignControllerActionFireAsPersonConfiguration action,
        ZoneId timeZone) throws CampaignControllerActionFireAsPersonValidationRestException,
        CampaignControllerActionFireAsPersonIdentificationValidationRestException,
        CampaignComponentValidationRestException {
        CampaignControllerActionFireAsPersonBuilder actionBuilder = context.get(step, action);
        try {
            if (action.getQuality() != null) {
                actionBuilder.withQuality(CampaignControllerActionQuality.valueOf(action.getQuality().name()));
            }

            Optional<FireAsPersonIdentification> asPersonIdentification = action.getAsPersonIdentification();
            if (asPersonIdentification.isPresent()) {
                FireAsPersonIdentificationBuilder asPersonIdentificationBuilder =
                    actionBuilder.withAsPersonIdentification(FireAsPersonIdenticationType.valueOf(
                        asPersonIdentification.get().getPersonIdentificationType().name()));
                fireAsPersonIdentificationRequestMapperRepository
                    .getMapper(
                        com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType
                            .valueOf(asPersonIdentification.get().getPersonIdentificationType().name()))
                    .uploadConfiguration(asPersonIdentification.get(), asPersonIdentificationBuilder);
            } else {
                actionBuilder.clearAsPersonIdentification();
            }

            Optional<FireAsPersonJourney> asPersonJourney = action.getAsPersonJourney();
            if (asPersonJourney.isPresent()) {
                addAsPersonJourney(actionBuilder, asPersonJourney.get());
            } else {
                actionBuilder.clearAsPersonJourney();
            }
            if (action.getData() != null) {
                actionBuilder.withData(action.getData());
            }
            action.getEventName().ifDefined((value) -> actionBuilder.withEventName(value));
            if (action.getLabels() != null) {
                actionBuilder.withLabels(action.getLabels());
            }
            action.getEnabled().ifDefined((value) -> actionBuilder.withEnabled(value));
            actionBuilder.clearComponentReferences();
            for (CampaignComponentReferenceConfiguration componentReference : action.getComponentReferences()) {
                if (componentReference.getAbsoluteName() == null) {
                    throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                        .withErrorCode(CampaignComponentValidationRestException.REFERENCE_ABSOLUTE_NAME_MISSING)
                        .build();
                }
                CampaignComponentReferenceBuilder referenceBuilder =
                    actionBuilder.addComponentReferenceByAbsoluteName(componentReference.getAbsoluteName());
                referenceBuilder.withTags(SetUtils.emptyIfNull(componentReference.getTags()));
                referenceBuilder.withSocketNames(ListUtils.emptyIfNull(componentReference.getSocketNames()));
            }

            action.getPersonId().ifDefined(personId -> actionBuilder.withPersonId(personId));
            action.getExtraData().ifDefined(extraData -> actionBuilder.withExtraData(extraData));
        } catch (FireAsPersonJourneyFieldLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(
                    CampaignControllerActionFireAsPersonValidationRestException.AS_PERSON_JOURNEY_FIELD_OUT_OF_RANGE)
                .addParameter("field_name", e.getFieldName())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonLabelLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.LABEL_OUT_OF_RANGE)
                .addParameter("label", e.getLabel())
                .withCause(e)
                .build();
        } catch (CampaignControllerActionFireAsPersonLabelInvalidCharactersException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonValidationRestException.class)
                .withErrorCode(CampaignControllerActionFireAsPersonValidationRestException.LABEL_ILLEGAL_CHARACTERS)
                .addParameter("label", e.getLabel())
                .withCause(e)
                .build();
        } catch (BuildCampaignException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.FIRE_AS_PERSON;
    }

    private void addAsPersonJourney(CampaignControllerActionFireAsPersonBuilder actionBuilder,
        FireAsPersonJourney asPersonJourney)
        throws FireAsPersonJourneyFieldInvalidException, FireAsPersonJourneyFieldLengthException {
        FireAsPersonJourneyBuilder asPersonJourneyBuilder = actionBuilder.withAsPersonJourney();

        if (asPersonJourney.getAdvocateCode().isPresent()) {
            asPersonJourneyBuilder.withAdvocateCode(asPersonJourney.getAdvocateCode().get());
        }
        if (asPersonJourney.getAdvocatePartnerUserId().isPresent()) {
            asPersonJourneyBuilder.withAdvocatePartnerUserId(asPersonJourney.getAdvocatePartnerUserId().get());
        }
        if (asPersonJourney.getCampaignId().isPresent()) {
            asPersonJourneyBuilder.withCampaignId(asPersonJourney.getCampaignId().get());
        }
        if (asPersonJourney.getContainer().isPresent()) {
            asPersonJourneyBuilder.withContainer(asPersonJourney.getContainer().get());
        }
        if (asPersonJourney.getCouponCode().isPresent()) {
            asPersonJourneyBuilder.withCouponCode(asPersonJourney.getCouponCode().get());
        }
        if (asPersonJourney.getJourneyName().isPresent()) {
            asPersonJourneyBuilder.withJourneyName(JourneyName.valueOf(asPersonJourney.getJourneyName().get()));
        }
        if (asPersonJourney.getLabel().isPresent()) {
            asPersonJourneyBuilder.withLabel(asPersonJourney.getLabel().get());
        }
        if (asPersonJourney.getPromotableCode().isPresent()) {
            asPersonJourneyBuilder.withPromotableCode(asPersonJourney.getPromotableCode().get());
        }
        if (asPersonJourney.getReason().isPresent()) {
            asPersonJourneyBuilder.withReason(asPersonJourney.getReason().get());
        }
        if (asPersonJourney.getReferralReason().isPresent()) {
            asPersonJourneyBuilder
                .withReferralReason(PersonReferralReason.valueOf(asPersonJourney.getReferralReason().get().name()));
        }
        if (asPersonJourney.getShareableId().isPresent()) {
            asPersonJourneyBuilder.withShareableId(asPersonJourney.getShareableId().get());
        }
        if (asPersonJourney.getShareId().isPresent()) {
            asPersonJourneyBuilder.withShareId(asPersonJourney.getShareId().get());
        }
        if (asPersonJourney.getZone().isPresent()) {
            asPersonJourneyBuilder.withZone(asPersonJourney.getZone().get());
        }

        asPersonJourneyBuilder.done();
    }

}
