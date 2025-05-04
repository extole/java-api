package com.extole.api.impl.campaign;

import javax.annotation.Nullable;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;

public class VariableDescriptionBuildtimeContextImpl extends ExtendableCampaignBuildtimeContextImpl
    implements VariableDescriptionBuildtimeContext {
    private final String name;
    private final String source;
    private final String key;

    public VariableDescriptionBuildtimeContextImpl(String name,
        String source,
        String key,
        CampaignBuildtimeContext context) {
        super(context);
        this.name = name;
        this.source = source;
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Nullable
    @Override
    public Object getValue() {
        return getVariableContext().get(name, key);
    }

    @Override
    public Object getVariable(String name, String... keys) {
        return super.getVariable(name, key);
    }

}
