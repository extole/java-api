package com.extole.api.impl.campaign.component.install;

import java.util.Map;

import com.extole.api.campaign.component.install.JourneyEntry;
import com.extole.api.campaign.component.install.Trigger;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry;

public class JourneyEntryImpl implements JourneyEntry {

    private final String id;
    private final String journeyName;
    private final Map<Id<?>, Id<?>> anchors;

    public JourneyEntryImpl(BuiltCampaignJourneyEntry journeyEntry, Map<Id<?>, Id<?>> anchors) {
        this.id = journeyEntry.getId().getValue();
        this.journeyName = journeyEntry.getJourneyName().getValue();
        this.anchors = anchors;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getJourneyName() {
        return journeyName;
    }

    @Override
    public void anchor(Trigger trigger) {
        anchors.put(Id.valueOf(trigger.getId()), Id.valueOf(id));
    }

}
