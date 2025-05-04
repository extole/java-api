package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.request;

import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException.AS_PERSON_EVENT_KEY_MISSING;
import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException.AS_PERSON_EVENT_KEY_OUT_OF_RANGE;
import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException.AS_PERSON_IDENTIFICATION_VALUE_MISSING;
import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException.AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PartnerEventIdFireAsPersonIdentification;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationPartnerEventKeyInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationPartnerEventKeyLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationValueInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationValueLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.PartnerEventIdFireAsPersonIdentificationBuilder;

@Component
public class PartnerEventIdFireAsPersonIdentificationRequestMapper implements
    FireAsPersonIdentificationRequestMapper<PartnerEventIdFireAsPersonIdentification,
        com.extole.client.rest.campaign.configuration.PartnerEventIdFireAsPersonIdentification,
        PartnerEventIdFireAsPersonIdentificationBuilder> {

    @Override
    public void upload(PartnerEventIdFireAsPersonIdentification identification,
        PartnerEventIdFireAsPersonIdentificationBuilder asPersonIdentificationBuilder)
        throws CampaignControllerActionFireAsPersonIdentificationValidationRestException {
        try {
            if (identification.getValue() != null) {
                asPersonIdentificationBuilder.withValue(identification.getValue());
            }
            if (identification.getPartnerEventKey() != null) {
                asPersonIdentificationBuilder.withPartnerEventKey(identification.getPartnerEventKey());
            }
            asPersonIdentificationBuilder.done();
        } catch (FireAsPersonIdentificationValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationPartnerEventKeyLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_EVENT_KEY_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_MISSING)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationPartnerEventKeyInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_EVENT_KEY_MISSING)
                .withCause(e)
                .build();
        }
    }

    @Override
    public void uploadConfiguration(
        com.extole.client.rest.campaign.configuration.PartnerEventIdFireAsPersonIdentification identification,
        PartnerEventIdFireAsPersonIdentificationBuilder asPersonIdentificationBuilder)
        throws CampaignControllerActionFireAsPersonIdentificationValidationRestException {
        try {
            if (identification.getValue() != null) {
                asPersonIdentificationBuilder.withValue(identification.getValue());
            }
            if (identification.getPartnerEventKey() != null) {
                asPersonIdentificationBuilder.withPartnerEventKey(identification.getPartnerEventKey());
            }
            asPersonIdentificationBuilder.done();
        } catch (FireAsPersonIdentificationValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationPartnerEventKeyLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_EVENT_KEY_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_MISSING)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationPartnerEventKeyInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_EVENT_KEY_MISSING)
                .withCause(e)
                .build();
        }
    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PARTNER_EVENT_ID;
    }
}
