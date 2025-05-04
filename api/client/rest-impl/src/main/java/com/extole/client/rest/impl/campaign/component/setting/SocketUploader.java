package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.SocketBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class SocketUploader implements SettingUploader<CampaignComponentSocketConfiguration> {

    @SuppressWarnings({"unchecked"})
    private final List<SettingUploader> settingUploaders;
    private final DefaultSettingUploader defaultSettingUploader;

    public SocketUploader(List<SettingUploader> settingUploaders,
        DefaultSettingUploader defaultSettingUploader) {
        this.settingUploaders = settingUploaders;
        this.defaultSettingUploader = defaultSettingUploader;
    }

    @Override
    public void upload(CampaignUploadContext context, CampaignComponentConfiguration component,
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
        if (socket.getFilter() != null) {
            socketBuilder.withFilter()
                .withComponentType(socket.getFilter().getComponentType());
        }
        socket.getDescription().ifDefined((value) -> socketBuilder.withDescription(value));
        socketBuilder.withTags(socket.getTags());
        socketBuilder.withPriority(socket.getPriority());
        if (socket.getType() != null) {
            socketBuilder.withType(com.extole.model.entity.campaign.SettingType.valueOf(socket.getType().name()));
        }

        if (socket.getParameters() != null) {
            for (CampaignComponentVariableConfiguration parameter : socket.getParameters().stream()
                .filter(variableRequest -> Objects.nonNull(variableRequest)).collect(Collectors.toList())) {
                settingUploaders.stream()
                    .filter(settingUploader -> settingUploader.getSettingType().equals(parameter.getType()))
                    .findFirst()
                    .orElse(defaultSettingUploader)
                    .upload(context, socket, component, parameter);
            }
        }
    }

    @Override
    public void upload(CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentSocketConfiguration socketConfiguration) {
        // no-op
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.MULTI_SOCKET;
    }
}
