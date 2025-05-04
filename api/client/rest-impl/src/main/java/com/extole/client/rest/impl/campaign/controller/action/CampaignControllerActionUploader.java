package com.extole.client.rest.impl.campaign.controller.action;

import java.time.ZoneId;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.creative.CreativeArchiveRestException;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;

public interface CampaignControllerActionUploader<T extends CampaignControllerActionConfiguration> {
    void upload(CampaignUploadContext context, CampaignStepConfiguration step, T action, ZoneId timeZone)
        throws CampaignControllerActionRestException, CampaignComponentValidationRestException,
        CreativeArchiveRestException;

    CampaignControllerActionType getActionType();
}
