package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class DefaultSettingUploader implements SettingUploader<CampaignComponentVariableConfiguration> {

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        VariableBuilder variableBuilder = (VariableBuilder) context.get(component, variable);
        if (variable.getName() != null) {
            variableBuilder.withName(variable.getName());
        }
        if (variable.getDisplayName().isPresent()) {
            variableBuilder.withDisplayName(variable.getDisplayName().get());
        }
        if (variable.getValues() != null) {
            variableBuilder.withValues(variable.getValues());
        }
        if (variable.getSource() != null) {
            variableBuilder.withSource(VariableSource.valueOf(variable.getSource().name()));
        }

        variable.getDescription().ifDefined((value) -> variableBuilder.withDescription(value));
        variableBuilder.withTags(variable.getTags());
        variableBuilder.withPriority(variable.getPriority());
        if (variable.getType() != null) {
            variableBuilder.withType(com.extole.model.entity.campaign.SettingType.valueOf(variable.getType().name()));
        }
    }

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        VariableBuilder variableBuilder = context.get(component, socket, variable);
        if (variable.getName() != null) {
            variableBuilder.withName(variable.getName());
        }
        if (variable.getDisplayName().isPresent()) {
            variableBuilder.withDisplayName(variable.getDisplayName().get());
        }
        if (variable.getValues() != null) {
            variableBuilder.withValues(variable.getValues());
        }
        if (variable.getSource() != null) {
            variableBuilder.withSource(VariableSource.valueOf(variable.getSource().name()));
        }

        variable.getDescription().ifDefined((value) -> variableBuilder.withDescription(value));
        variableBuilder.withTags(variable.getTags());
        variableBuilder.withPriority(variable.getPriority());
        if (variable.getType() != null) {
            variableBuilder.withType(com.extole.model.entity.campaign.SettingType.valueOf(variable.getType().name()));
        }
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.STRING);
    }
}
