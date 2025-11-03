package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSocketRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.ComponentFacetSocketFilterCreateRequest;
import com.extole.client.rest.campaign.component.setting.ComponentTypeSocketFilterCreateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.SocketFilterCreateRequest;
import com.extole.model.entity.campaign.SocketFilterType;
import com.extole.model.service.campaign.setting.ComponentFacetSocketFilterBuilder;
import com.extole.model.service.campaign.setting.ComponentTypeSocketFilterBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.SocketBuilder;
import com.extole.model.service.campaign.setting.SocketFilterBuilder;
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

        if (createRequest.getFilters().isPresent()) {
            for (SocketFilterCreateRequest filterCreateRequest : createRequest.getFilters().getValue()
                .stream()
                .filter(value -> Objects.nonNull(value)).toList()) {
                populateSocketFilterBuilder(builder, filterCreateRequest);
            }
        }

        createRequest.getDescription().ifPresent(builder::withDescription);
        if (createRequest.getParameters().isPresent()) {
            for (CampaignComponentVariableRequest parameter : createRequest.getParameters()
                .getValue()
                .stream()
                .filter(variableRequest -> Objects.nonNull(variableRequest)).toList()) {
                populateVariableBuilder(builder, parameter);
            }
        }
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return List.of(SettingType.MULTI_SOCKET, SettingType.SOCKET);
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

    private void populateSocketFilterBuilder(SocketBuilder builder, SocketFilterCreateRequest filterCreateRequest) {
        SocketFilterBuilder filterBuilder =
            builder.withFilter(SocketFilterType.valueOf(filterCreateRequest.getType().name()));

        if (SocketFilterType.COMPONENT_TYPE == filterBuilder.getType()) {
            ComponentTypeSocketFilterBuilder exactBuilder = (ComponentTypeSocketFilterBuilder) filterBuilder;
            ComponentTypeSocketFilterCreateRequest exactRequest =
                (ComponentTypeSocketFilterCreateRequest) filterCreateRequest;
            exactBuilder.withComponentType(exactRequest.getComponentType());
            return;
        }
        if (SocketFilterType.FACET == filterBuilder.getType()) {
            ComponentFacetSocketFilterBuilder exactBuilder = (ComponentFacetSocketFilterBuilder) filterBuilder;
            ComponentFacetSocketFilterCreateRequest exactRequest =
                (ComponentFacetSocketFilterCreateRequest) filterCreateRequest;
            exactBuilder.withName(exactRequest.getName());
            exactBuilder.withValue(exactRequest.getValue());
            return;
        }
        throw new IllegalArgumentException("Can't map filter type: " + filterBuilder.getType());
    }

}
