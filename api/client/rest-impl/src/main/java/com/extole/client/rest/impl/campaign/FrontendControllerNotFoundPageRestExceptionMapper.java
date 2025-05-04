package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.campaign.controller.CampaignFrontendControllerValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerNotFoundPageForbiddenEvaluatablePhaseException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerNotFoundPageInvalidActionException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerNotFoundPageMisconfigurationException;

public final class FrontendControllerNotFoundPageRestExceptionMapper {

    private static final FrontendControllerNotFoundPageRestExceptionMapper INSTANCE =
        new FrontendControllerNotFoundPageRestExceptionMapper();

    public static FrontendControllerNotFoundPageRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    private FrontendControllerNotFoundPageRestExceptionMapper() {
    }

    public CampaignFrontendControllerValidationRestException
        map(CampaignFrontendControllerNotFoundPageMisconfigurationException e, String campaignId)
            throws CampaignFrontendControllerValidationRestException {
        if (e instanceof CampaignFrontendControllerNotFoundPageForbiddenEvaluatablePhaseException) {
            return RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(
                    CampaignFrontendControllerValidationRestException.NOT_FOUND_PAGE_FORBIDDEN_RUNTIME_EXPRESSIONS)
                .addParameter("campaign_id", campaignId)
                .addParameter("controller_id", e.getEntityId())
                .addParameter("action_id",
                    ((CampaignFrontendControllerNotFoundPageForbiddenEvaluatablePhaseException) e).getActionId())
                .withCause(e)
                .build();
        }
        if (e instanceof CampaignFrontendControllerNotFoundPageInvalidActionException) {
            return RestExceptionBuilder.newBuilder(CampaignFrontendControllerValidationRestException.class)
                .withErrorCode(
                    CampaignFrontendControllerValidationRestException.NOT_FOUND_PAGE_INVALID_ACTION)
                .addParameter("campaign_id", campaignId)
                .addParameter("controller_id", e.getEntityId())
                .withCause(e)
                .build();
        }
        throw new IllegalStateException();
    }
}
