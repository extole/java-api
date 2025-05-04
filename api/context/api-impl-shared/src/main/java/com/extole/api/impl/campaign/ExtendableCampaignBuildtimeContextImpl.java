package com.extole.api.impl.campaign;

import javax.annotation.Nullable;

import com.extole.api.ClientContext;
import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.Component;
import com.extole.api.campaign.ComponentAsset;
import com.extole.api.campaign.VariableContext;
import com.extole.api.service.GlobalServices;
import com.extole.evaluateable.handlebars.ShortVariableSyntaxContext;

public class ExtendableCampaignBuildtimeContextImpl implements CampaignBuildtimeContext, ShortVariableSyntaxContext {
    private final CampaignBuildtimeContext context;

    public ExtendableCampaignBuildtimeContextImpl(CampaignBuildtimeContext context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Component getComponent() {
        return context.getComponent();
    }

    @Override
    public final VariableContext getVariableContext() {
        return context.getVariableContext();
    }

    @Override
    public final VariableContext getVariableContext(String defaultKey) {
        return context.getVariableContext(defaultKey);
    }

    @Override
    public final VariableContext getVariableContext(String... defaultKeys) {
        return context.getVariableContext(defaultKeys);
    }

    @Override
    public final ComponentAsset getAsset(String assetName) {
        return context.getAsset(assetName);
    }

    @Override
    public final String getProgramLabel() {
        return context.getProgramLabel();
    }

    @Override
    public final ClientContext getClientContext() {
        return context.getClientContext();
    }

    @Override
    public final GlobalServices getGlobalServices() {
        return context.getGlobalServices();
    }

    @Override
    public final void log(String message) {
        context.log(message);
    }

    @Override
    public Object getVariable(String name, String... keys) {
        return getVariableContext().get(name, keys);
    }
}
