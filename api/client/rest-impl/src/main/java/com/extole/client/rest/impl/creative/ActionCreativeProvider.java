package com.extole.client.rest.impl.creative;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.extole.client.rest.creative.CreativeVariableRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CreativeArchive;

@Component
public final class ActionCreativeProvider {

    private ActionCreativeProvider() {
    }

    public CampaignControllerActionCreative getActionCreativeByCreativeId(Campaign campaign,
        Id<CreativeArchive> creativeArchiveId) throws CreativeVariableRestException {

        Supplier<CreativeVariableRestException> exceptionSupplier =
            () -> RestExceptionBuilder.newBuilder(CreativeVariableRestException.class)
                .withErrorCode(CreativeVariableRestException.INVALID_CREATIVE_ID)
                .addParameter("creative_id", creativeArchiveId)
                .build();

        return campaign.getFrontendControllers().stream()
            .flatMap(frontendController -> frontendController.getActions()
                .stream()
                .filter(value -> value.getType() == CampaignControllerActionType.CREATIVE)
                .map(CampaignControllerActionCreative.class::cast)
                .filter(action -> action.getCreativeArchiveId().isPresent()
                    && action.getCreativeArchiveId().get().getId().equals(creativeArchiveId)))
            .findFirst()
            .orElseThrow(exceptionSupplier);
    }

}
