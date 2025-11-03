package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSocketUpdateRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.ComponentFacetSocketFilterUpdateRequest;
import com.extole.client.rest.campaign.component.setting.ComponentTypeSocketFilterUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.SocketFilterUpdateRequest;
import com.extole.model.entity.campaign.SocketFilterType;
import com.extole.model.service.campaign.setting.ComponentFacetSocketFilterBuilder;
import com.extole.model.service.campaign.setting.ComponentTypeSocketFilterBuilder;
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

        if (updateRequest.getFilters().isPresent()) {
            builder.clearFilters();
            List<SocketFilterUpdateRequest> filters = updateRequest.getFilters().getValue();
            for (SocketFilterUpdateRequest socketFilter : filters) {
                populateSocketFilterBuilder(builder, socketFilter);
            }
        }

        updateRequest.getDescription().ifPresent(builder::withDescription);

        if (updateRequest.getParameters().isPresent()) {
            builder.clearParameters();
            for (CampaignComponentVariableRequest parameter : updateRequest.getParameters()
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

    private void populateSocketFilterBuilder(SocketBuilder socketBuilder, SocketFilterUpdateRequest request) {
        if (com.extole.client.rest.campaign.component.setting.SocketFilterType.COMPONENT_TYPE == request.getType()) {
            ComponentTypeSocketFilterUpdateRequest exactRequest =
                (ComponentTypeSocketFilterUpdateRequest) request;
            exactRequest.getComponentType().ifPresent(componentType -> {
                ComponentTypeSocketFilterBuilder builder =
                    socketBuilder.withFilter(SocketFilterType.valueOf(request.getType().name()));
                builder.withComponentType(componentType);
            });
            return;
        }
        if (com.extole.client.rest.campaign.component.setting.SocketFilterType.FACET == request.getType()) {
            ComponentFacetSocketFilterUpdateRequest exactRequest =
                (ComponentFacetSocketFilterUpdateRequest) request;
            ComponentFacetSocketFilterBuilder builder =
                socketBuilder.withFilter(SocketFilterType.valueOf(request.getType().name()));
            exactRequest.getName().ifPresent(name -> {
                builder.withName(name);
            });
            exactRequest.getValue().ifPresent(value -> {
                builder.withValue(value);
            });
            return;
        }
        throw new IllegalArgumentException("Can't map filter type: " + request.getType());
    }

}
