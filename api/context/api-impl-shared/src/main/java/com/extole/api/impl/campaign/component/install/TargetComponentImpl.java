package com.extole.api.impl.campaign.component.install;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import com.extole.api.campaign.component.install.Controller;
import com.extole.api.campaign.component.install.JourneyEntry;
import com.extole.api.campaign.component.install.TargetComponent;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignController;
import com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry;

public class TargetComponentImpl implements TargetComponent {

    private final Controller[] controllers;
    private final JourneyEntry[] journeyEntries;

    public TargetComponentImpl(
        ZoneId clientTimezone,
        List<BuiltCampaignController> controllers,
        List<BuiltCampaignJourneyEntry> journeyEntries,
        Map<Id<?>, Id<?>> anchors) {
        this.controllers = controllers.stream()
            .map(controller -> new ControllerImpl(clientTimezone, controller, anchors))
            .toArray(Controller[]::new);
        this.journeyEntries = journeyEntries.stream()
            .map(entry -> new JourneyEntryImpl(entry, anchors))
            .toArray(JourneyEntry[]::new);
    }

    @Override
    public Controller[] getControllers() {
        return controllers;
    }

    @Override
    public JourneyEntry[] getJourneyEntries() {
        return journeyEntries;
    }

}
