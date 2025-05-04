package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSocketUpdateRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.SocketBuilder;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class SocketUpdateRequestMapper
    implements SettingUpdateRequestMapper<CampaignComponentSocketUpdateRequest, SocketBuilder> {

    private final SettingRequestMapperRepository settingRequestMapperRepository;

    @Autowired
    public SocketUpdateRequestMapper(@Lazy SettingRequestMapperRepository settingRequestMapperRepository) {
        this.settingRequestMapperRepository = settingRequestMapperRepository;
    }

    @Override
    public void complete(CampaignComponentSocketUpdateRequest updateRequest, SocketBuilder builder)
        throws SettingIllegalCharacterInDisplayNameException, SettingTagLengthException,
        SettingDisplayNameLengthException, VariableValueKeyLengthException, SettingNameLengthException,
        SettingInvalidNameException {
        updateRequest.getFilter().ifPresent(filter -> filter.getComponentType()
            .ifPresent(componentType -> builder.withFilter().withComponentType(componentType)));

        updateRequest.getDescription().ifPresent(builder::withDescription);

        if (updateRequest.getParameters().isPresent()) {
            builder.clearParameters();
            for (CampaignComponentVariableRequest parameter : updateRequest.getParameters()
                .getValue()
                .stream()
                .filter(variableRequest -> Objects.nonNull(variableRequest)).collect(Collectors.toList())) {
                populateVariableBuilder(builder, parameter);
            }
        }
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.MULTI_SOCKET;
    }

    @SuppressWarnings({"unchecked"})
    private void populateVariableBuilder(SocketBuilder builder, CampaignComponentVariableRequest parameter)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        VariableBuilder variableBuilder;
        if (parameter.getType() != null) {
            variableBuilder = builder
                .addParameter(com.extole.model.entity.campaign.SettingType.valueOf(parameter.getType().name()));
            settingRequestMapperRepository.getCreateRequestMapper(parameter.getType())
                .complete(parameter, variableBuilder);
        } else {
            variableBuilder = builder.addParameter(com.extole.model.entity.campaign.SettingType.STRING);
        }
        if (parameter.getName() != null) {
            variableBuilder.withName(parameter.getName());
        }
        if (parameter.getDisplayName().isPresent()) {
            variableBuilder.withDisplayName(parameter.getDisplayName().getValue());
        }
        parameter.getTags().ifPresent(tags -> {
            variableBuilder.withTags(tags);
        });
        parameter.getPriority().ifPresent(value -> variableBuilder.withPriority(value));
    }

}
