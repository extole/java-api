package com.extole.common.variable.evaluator;

import java.util.Optional;
import java.util.function.Supplier;

import com.extole.api.campaign.VariableContext;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.running.service.campaign.RunningCampaign;
import com.extole.sandbox.Sandbox;

public interface RuntimeVariablesProvider {

    VariableEvaluator provide(Id<ClientHandle> clientId, Id<?> elementId, Sandbox sandbox,
        String variant, Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier);

    VariableEvaluator provide(Id<ClientHandle> clientId, RunningCampaign campaign, String variant,
        Id<?> elementId, Supplier<Optional<Id<CampaignComponent>>> componentIdSupplier,
        VariableContextSupplier variableContextSupplier);

    interface VariableContextSupplier {
        VariableContext supply(VariableEvaluator evaluator);
    }
}
