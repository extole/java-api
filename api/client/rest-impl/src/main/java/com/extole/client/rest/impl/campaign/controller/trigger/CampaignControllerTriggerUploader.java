package com.extole.client.rest.impl.campaign.controller.trigger;

import java.time.ZoneId;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;

public interface CampaignControllerTriggerUploader<T extends CampaignControllerTriggerConfiguration> {

    void upload(CampaignUploadContext context, CampaignStepConfiguration step, T trigger, ZoneId timeZone)
        throws CampaignControllerTriggerRestException, CampaignComponentValidationRestException;

    CampaignControllerTriggerType getTriggerType();

}
