package com.extole.client.rest.impl.campaign.component.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentPartnerEnumListVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.PartnerEnumListVariableOptionCreateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.PartnerEnumListVariableOption;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.PartnerEnumListVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class PartnerEnumListVariableUpdateRequestMapper
    implements SettingUpdateRequestMapper<CampaignComponentPartnerEnumListVariableUpdateRequest,
    PartnerEnumListVariableBuilder> {

    @Override
    public void complete(CampaignComponentPartnerEnumListVariableUpdateRequest updateRequest,
        PartnerEnumListVariableBuilder builder) throws VariableValueKeyLengthException {
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
                    .withDefault(optionRequest.getDefault())
                    .build();
                options.add(option);
            }
            builder.withOptions(options);
        });
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.PARTNER_ENUM_LIST;
    }
}
