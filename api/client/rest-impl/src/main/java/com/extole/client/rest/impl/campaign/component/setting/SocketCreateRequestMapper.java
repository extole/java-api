package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSocketRequest;
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
public class SocketCreateRequestMapper
    implements SettingCreateRequestMapper<CampaignComponentSocketRequest, SocketBuilder> {

    private final SettingRequestMapperRepository settingRequestMapperRepository;

    @Autowired
    public SocketCreateRequestMapper(@Lazy SettingRequestMapperRepository settingRequestMapperRepository) {
        this.settingRequestMapperRepository = settingRequestMapperRepository;
    }

    @Override
    public void complete(CampaignComponentSocketRequest createRequest, SocketBuilder builder)
        throws SettingIllegalCharacterInDisplayNameException, SettingTagLengthException,
        SettingDisplayNameLengthException, VariableValueKeyLengthException, SettingNameLengthException,
        SettingInvalidNameException {
        if (createRequest.getFilter().isPresent() && createRequest.getFilter().getValue().getComponentType() != null) {
            builder.withFilter().withComponentType(createRequest.getFilter().getValue().getComponentType());
        }

        createRequest.getDescription().ifPresent(builder::withDescription);
        if (createRequest.getParameters().isPresent()) {
            for (CampaignComponentVariableRequest parameter : createRequest.getParameters()
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
    private void populateVariableBuilder(SocketBuilder builder,
        CampaignComponentVariableRequest parameter)
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
