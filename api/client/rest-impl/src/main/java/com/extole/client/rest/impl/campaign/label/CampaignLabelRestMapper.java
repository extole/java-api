package com.extole.client.rest.impl.campaign.label;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.configuration.CampaignLabelConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignLabelType;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.model.entity.campaign.CampaignLabel;
import com.extole.model.entity.campaign.built.BuiltCampaignLabel;

@Component
public class CampaignLabelRestMapper {

    public CampaignLabelResponse toCampaignLabelResponse(CampaignLabel label, ZoneId timeZone) {
        return new CampaignLabelResponse(label.getName(),
            com.extole.client.rest.campaign.label.CampaignLabelType.valueOf(label.getType().name()));
    }

    public CampaignLabelConfiguration toCampaignLabelConfiguration(CampaignLabel label, ZoneId timeZone) {
        return new CampaignLabelConfiguration(label.getName(),
            CampaignLabelType.valueOf(label.getType().name()));
    }

    public CampaignLabelResponse toCampaignLabelResponse(BuiltCampaignLabel label, ZoneId timeZone) {
        return new CampaignLabelResponse(label.getName(),
            com.extole.client.rest.campaign.label.CampaignLabelType.valueOf(label.getType().name()));
    }

}
