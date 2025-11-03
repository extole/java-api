package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.campaign.configuration.ComponentFacetSocketFilterConfiguration;
import com.extole.client.rest.campaign.configuration.ComponentTypeSocketFilterConfiguration;
import com.extole.client.rest.campaign.configuration.SocketFilterConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
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
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class SocketUploader implements SettingUploader<CampaignComponentSocketConfiguration> {

    @SuppressWarnings("unchecked")
    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentSocketConfiguration socket) throws SettingNameLengthException, SettingInvalidNameException,
        SettingIllegalCharacterInDisplayNameException, SettingDisplayNameLengthException, SettingTagLengthException,
        VariableValueKeyLengthException {
        SocketBuilder socketBuilder = (SocketBuilder) context.get(component, socket);
        if (socket.getName() != null) {
            socketBuilder.withName(socket.getName());
        }
        if (socket.getDisplayName().isPresent()) {
            socketBuilder.withDisplayName(socket.getDisplayName().get());
        }

        if (socket.getFilters() != null && !socket.getFilters().isEmpty()) {
            for (SocketFilterConfiguration socketFilterConfiguration : socket.getFilters().stream()
                .filter(value -> Objects.nonNull(value)).toList()) {
                populateSocketFilterBuilder(socketBuilder, socketFilterConfiguration);
            }
        }

        socket.getDescription().ifDefined((value) -> socketBuilder.withDescription(value));
        socketBuilder.withTags(socket.getTags());
        socketBuilder.withPriority(socket.getPriority());
        if (socket.getType() != null) {
            socketBuilder.withType(com.extole.model.entity.campaign.SettingType.valueOf(socket.getType().name()));
        }

        if (socket.getParameters() != null) {
            for (CampaignComponentVariableConfiguration parameter : socket.getParameters().stream()
                .filter(variableRequest -> Objects.nonNull(variableRequest)).toList()) {
                uploaderRegistry.getUploader(parameter.getType())
                    .orElse(uploaderRegistry.getDefaultUploader())
                    .upload(uploaderRegistry, context, socket, component, parameter);
            }
        }
    }

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentSocketConfiguration socketConfiguration) {
        // no-op
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return List.of(SettingType.MULTI_SOCKET, SettingType.SOCKET);
    }

    private void populateSocketFilterBuilder(SocketBuilder builder, SocketFilterConfiguration filterConfiguration) {
        SocketFilterBuilder filterBuilder =
            builder.withFilter(
                com.extole.model.entity.campaign.SocketFilterType.valueOf(filterConfiguration.getType().name()));

        if (com.extole.model.entity.campaign.SocketFilterType.COMPONENT_TYPE == filterBuilder.getType()) {
            ComponentTypeSocketFilterBuilder exactBuilder = (ComponentTypeSocketFilterBuilder) filterBuilder;
            ComponentTypeSocketFilterConfiguration exactRequest =
                (ComponentTypeSocketFilterConfiguration) filterConfiguration;
            exactBuilder.withComponentType(exactRequest.getComponentType());
            return;
        }
        if (SocketFilterType.FACET == filterBuilder.getType()) {
            ComponentFacetSocketFilterBuilder exactBuilder = (ComponentFacetSocketFilterBuilder) filterBuilder;
            ComponentFacetSocketFilterConfiguration exactRequest =
                (ComponentFacetSocketFilterConfiguration) filterConfiguration;
            exactBuilder.withName(exactRequest.getName());
            exactBuilder.withValue(exactRequest.getValue());
            return;
        }
        throw new IllegalArgumentException("Can't map filter type: " + filterBuilder.getType());
    }

}
