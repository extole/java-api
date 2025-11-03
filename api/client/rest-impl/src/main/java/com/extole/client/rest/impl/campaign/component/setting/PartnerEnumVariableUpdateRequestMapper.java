package com.extole.client.rest.impl.campaign.component.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentPartnerEnumVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.PartnerEnumListVariableOptionCreateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.PartnerEnumListVariableOption;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.PartnerEnumVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class PartnerEnumVariableUpdateRequestMapper
    implements
    SettingUpdateRequestMapper<CampaignComponentPartnerEnumVariableUpdateRequest, PartnerEnumVariableBuilder> {

    @Override
    public void complete(CampaignComponentPartnerEnumVariableUpdateRequest updateRequest,
        PartnerEnumVariableBuilder builder) throws VariableValueKeyLengthException {
        updateRequest.getValues().ifPresent(values -> builder.withValues(values));
        updateRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        updateRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        updateRequest.getWebhookId().ifPresent(builder::withWebhookId);
        updateRequest.getOptions().ifPresent(optionsList -> {
            List<PartnerEnumListVariableOption> options = new ArrayList<>();
            for (PartnerEnumListVariableOptionCreateRequest optionRequest : optionsList) {
                PartnerEnumListVariableOption option = PartnerEnumListVariableOption.builder()
                    .withId(optionRequest.getId())
                    .withName(optionRequest.getName())
                    .build();
                options.add(option);
            }
            builder.withOptions(options);
        });
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.PARTNER_ENUM);
    }
}
