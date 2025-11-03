package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentClientKeyFlowVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.id.Id;
import com.extole.model.service.campaign.setting.ClientKeyFlowVariableBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class ClientKeyFlowVariableUploader
    implements SettingUploader<CampaignComponentClientKeyFlowVariableConfiguration> {

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentClientKeyFlowVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, component, variable);

        ClientKeyFlowVariableBuilder variableBuilder = (ClientKeyFlowVariableBuilder) context.get(component, variable);
        variableBuilder.withRedirectUri(variable.getRedirectUri());
        variableBuilder.withClientKeyUrl(variable.getClientKeyUrl());
        if (variable.getClientKeyOauthFlow() != null) {
            variableBuilder.withClientKeyOAuthFlow(Id.valueOf(variable.getClientKeyOauthFlow()));
        }
    }

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentClientKeyFlowVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, socket, component, variable);

        ClientKeyFlowVariableBuilder variableBuilder =
            (ClientKeyFlowVariableBuilder) context.get(component, socket, variable);
        variableBuilder.withRedirectUri(variable.getRedirectUri());
        variableBuilder.withClientKeyUrl(variable.getClientKeyUrl());
        if (variable.getClientKeyOauthFlow() != null) {
            variableBuilder.withClientKeyOAuthFlow(Id.valueOf(variable.getClientKeyOauthFlow()));
        }
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.CLIENT_KEY_FLOW);
    }
}
