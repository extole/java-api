package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentPartnerEnumVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.PartnerEnumListVariableOption;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.PartnerEnumVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class PartnerEnumVariableCreateRequestMapper
    implements
    SettingCreateRequestMapper<CampaignComponentPartnerEnumVariableRequest, PartnerEnumVariableBuilder> {

    @Override
    public void complete(CampaignComponentPartnerEnumVariableRequest createRequest,
        PartnerEnumVariableBuilder builder) throws VariableValueKeyLengthException {
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
                        .build())
                    .toList());
        });
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.PARTNER_ENUM);
    }

}
