package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentEnumListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.model.entity.campaign.EnumVariableMember;
import com.extole.model.service.campaign.setting.EnumVariableBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class EnumListVariableUploader implements SettingUploader<CampaignComponentEnumListVariableConfiguration> {

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentEnumListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, component, variable);

        EnumVariableBuilder variableBuilder = (EnumVariableBuilder) context.get(component, variable);
        List<EnumVariableMember> enumVariableMemberList = variable.getAllowedValues()
            .stream()
            .map(value -> EnumVariableMember.create(value).build())
            .collect(Collectors.toList());
        variableBuilder.withAllowedValues(enumVariableMemberList);
    }

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentEnumListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, socket, component, variable);

        EnumVariableBuilder variableBuilder = (EnumVariableBuilder) context.get(component, socket, variable);
        List<EnumVariableMember> enumVariableMemberList = variable.getAllowedValues()
            .stream()
            .map(value -> EnumVariableMember.create(value).build())
            .collect(Collectors.toList());
        variableBuilder.withAllowedValues(enumVariableMemberList);
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.ENUM_LIST);
    }
}
