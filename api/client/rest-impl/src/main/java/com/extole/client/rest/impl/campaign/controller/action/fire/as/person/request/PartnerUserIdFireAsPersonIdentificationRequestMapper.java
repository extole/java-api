package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.request;

import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException.AS_PERSON_IDENTIFICATION_VALUE_MISSING;
import static com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException.AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonIdentificationValidationRestException;
import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType;
import com.extole.client.rest.campaign.controller.action.fire.as.person.identification.PartnerUserIdFireAsPersonIdentification;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationValueInvalidException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.FireAsPersonIdentificationValueLengthException;
import com.extole.model.service.campaign.controller.action.fire.as.person.identification.PartnerUserIdFireAsPersonIdentificationBuilder;

@Component
public class PartnerUserIdFireAsPersonIdentificationRequestMapper implements
    FireAsPersonIdentificationRequestMapper<PartnerUserIdFireAsPersonIdentification,
        com.extole.client.rest.campaign.configuration.PartnerUserIdFireAsPersonIdentification,
        PartnerUserIdFireAsPersonIdentificationBuilder> {
    @Override
    public void upload(PartnerUserIdFireAsPersonIdentification identification,
        PartnerUserIdFireAsPersonIdentificationBuilder asPersonIdentificationBuilder)
        throws CampaignControllerActionFireAsPersonIdentificationValidationRestException {
        try {
            if (identification.getValue() != null) {
                asPersonIdentificationBuilder.withValue(identification.getValue());
            }
            asPersonIdentificationBuilder.done();
        } catch (FireAsPersonIdentificationValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_MISSING)
                .withCause(e)
                .build();
        }

    }

    @Override
    public void uploadConfiguration(
        com.extole.client.rest.campaign.configuration.PartnerUserIdFireAsPersonIdentification identification,
        PartnerUserIdFireAsPersonIdentificationBuilder asPersonIdentificationBuilder)
        throws CampaignControllerActionFireAsPersonIdentificationValidationRestException {
        try {
            if (identification.getValue() != null) {
                asPersonIdentificationBuilder.withValue(identification.getValue());
            }
            asPersonIdentificationBuilder.done();
        } catch (FireAsPersonIdentificationValueLengthException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_OUT_OF_RANGE)
                .withCause(e)
                .build();
        } catch (FireAsPersonIdentificationValueInvalidException e) {
            throw RestExceptionBuilder
                .newBuilder(CampaignControllerActionFireAsPersonIdentificationValidationRestException.class)
                .withErrorCode(AS_PERSON_IDENTIFICATION_VALUE_MISSING)
                .withCause(e)
                .build();
        }

    }

    @Override
    public FireAsPersonIdenticationType getType() {
        return FireAsPersonIdenticationType.PARTNER_USER_ID;
    }
}
