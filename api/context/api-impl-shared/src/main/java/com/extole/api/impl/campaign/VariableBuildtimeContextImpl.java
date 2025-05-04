package com.extole.api.impl.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariableBuildtimeContext;

public class VariableBuildtimeContextImpl extends ExtendableCampaignBuildtimeContextImpl
    implements VariableBuildtimeContext {
    private final String name;
    private final String source;
    private final String key;

    public VariableBuildtimeContextImpl(String name, String source, String key, CampaignBuildtimeContext context) {
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
    public String getKey() {
        return key;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Object getVariable(String name, String... keys) {
        return super.getVariable(name, key);
    }
}
