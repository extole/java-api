package com.extole.client.rest.impl.campaign.component;

import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentDuplicateBuilder;
import com.extole.model.service.campaign.component.reference.CampaignComponentReferenceBuilder;

@Component
public class ComponentReferenceRequestMapper {

    public void handleComponentReferences(ComponentElementBuilder elementBuilder,
        List<ComponentReferenceRequest> componentReferences) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (ComponentReferenceRequest reference : componentReferences) {
            if (reference.getComponentId() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                elementBuilder.addComponentReference(Id.valueOf(reference.getComponentId().getValue()));
            reference.getSocketNames().ifPresent(referenceBuilder::withSocketNames);
        }
    }

    public void handleComponentReferences(ComponentDuplicateBuilder componentDuplicateBuilder,
        List<ComponentReferenceRequest> componentReferences) throws CampaignComponentValidationRestException {
        for (ComponentReferenceRequest reference : componentReferences) {
            if (reference.getComponentId() == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            CampaignComponentReferenceBuilder referenceBuilder =
                componentDuplicateBuilder.addComponentReference(Id.valueOf(reference.getComponentId().getValue()));
            reference.getSocketNames().ifPresent(referenceBuilder::withSocketNames);
        }
    }

}
