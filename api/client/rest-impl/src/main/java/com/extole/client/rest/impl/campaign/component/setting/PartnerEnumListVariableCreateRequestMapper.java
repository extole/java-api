package com.extole.client.rest.impl.campaign.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentPartnerEnumListVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.PartnerEnumListVariableOption;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.PartnerEnumListVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class PartnerEnumListVariableCreateRequestMapper
    implements
    SettingCreateRequestMapper<CampaignComponentPartnerEnumListVariableRequest, PartnerEnumListVariableBuilder> {

    @Override
    public void complete(CampaignComponentPartnerEnumListVariableRequest createRequest,
        PartnerEnumListVariableBuilder builder) throws VariableValueKeyLengthException {
        createRequest.getValues().ifPresent(values -> builder.withValues(values));
        createRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        createRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        createRequest.getWebhookId().ifPresent(value -> builder.withWebhookId(value));
        createRequest.getOptions().ifPresent(options -> {
            builder.withOptions(
                options.stream()
                    .map(optionRequest -> PartnerEnumListVariableOption.builder()
                        .withId(optionRequest.getId())
                        .withName(optionRequest.getName())
                        .withDefault(optionRequest.getDefault())
                        .build())
                    .collect(Collectors.toUnmodifiableList()));
        });
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.PARTNER_ENUM_LIST;
    }

}
