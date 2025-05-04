package com.extole.api.impl.campaign;

import javax.annotation.Nullable;

import com.extole.api.ClientContext;
import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.Component;
import com.extole.api.campaign.ComponentAsset;
import com.extole.api.campaign.SocketDescriptionBuildtimeContext;
import com.extole.api.campaign.VariableContext;
import com.extole.api.service.GlobalServices;

public class SocketDescriptionBuildtimeContextImpl implements SocketDescriptionBuildtimeContext {

    private final CampaignBuildtimeContext context;
    private final String name;

    public SocketDescriptionBuildtimeContextImpl(String name, CampaignBuildtimeContext context) {
        this.context = context;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ClientContext getClientContext() {
        return context.getClientContext();
    }

    @Override
    public GlobalServices getGlobalServices() {
        return context.getGlobalServices();
    }

    @Override
    public void log(String message) {
        context.log(message);
    }

    @Override
    public String getProgramLabel() {
        return context.getProgramLabel();
    }

    @Nullable
    @Override
    public Component getComponent() {
        return context.getComponent();
    }

    @Override
    public VariableContext getVariableContext() {
        return context.getVariableContext();
    }

    @Override
    public VariableContext getVariableContext(String defaultKey) {
        return context.getVariableContext(defaultKey);
    }

    @Override
    public VariableContext getVariableContext(String... defaultKeys) {
        return context.getVariableContext(defaultKeys);
    }

    @Nullable
    @Override
    public ComponentAsset getAsset(String assetName) {
        return context.getAsset(assetName);
    }

}
