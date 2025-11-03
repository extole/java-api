package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Map;
import java.util.Optional;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.Variable;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.setting.VariableBuilder;

public interface SettingAssetsPullBuilder {

    SettingAssetsPullBuilder initialize(
        ClientAuthorization authorization,
        Campaign campaign,
        CampaignComponent campaignComponent,
        CampaignComponentBuilder componentBuilder);

    PullOperation buildForVariable(
        Variable variable,
        VariableBuilder variableBuilder,
        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values);

    interface PullOperation {

        void performOperation();

    }

}
