package com.extole.api.impl.campaign.summary;

import com.extole.api.campaign.CampaignSummary;

public final class CampaignSummaryImpl implements CampaignSummary {
    private final String clientId;
    private final String campaignId;
    private final String campaignName;
    private final String programLabel;
    private final String programType;
    private final String themeName;
    private final String description;
    private final String currentState;
    private final String firstLaunchDate;
    private final String lastStoppedDate;
    private final String lastPausedDate;
    private final String lastArchivedDate;
    private final String lastEndedDate;
    private final String[] tags;

    public CampaignSummaryImpl(String clientId,
        String campaignId,
        String campaignName,
        String programLabel,
        String programType,
        String themeName,
        String description,
        String currentState,
        String firstLaunchDate,
        String lastStoppedDate,
        String lastPausedDate,
        String lastArchivedDate,
        String lastEndedDate,
        String[] tags) {
        this.clientId = clientId;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.programLabel = programLabel;
        this.programType = programType;
        this.themeName = themeName;
        this.description = description;
        this.currentState = currentState;
        this.firstLaunchDate = firstLaunchDate;
        this.lastStoppedDate = lastStoppedDate;
        this.lastPausedDate = lastPausedDate;
        this.lastArchivedDate = lastArchivedDate;
        this.lastEndedDate = lastEndedDate;
        this.tags = tags;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public String getCampaignName() {
        return campaignName;
    }

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getProgramType() {
        return programType;
    }

    @Override
    public String getThemeName() {
        return themeName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCurrentState() {
        return currentState;
    }

    @Override
    public String getFirstLaunchDate() {
        return firstLaunchDate;
    }

    @Override
    public String getLastStoppedDate() {
        return lastStoppedDate;
    }

    @Override
    public String getLastPausedDate() {
        return lastPausedDate;
    }

    @Override
    public String getLastArchivedDate() {
        return lastArchivedDate;
    }

    @Override
    public String getLastEndedDate() {
        return lastEndedDate;
    }

    @Override
    public String[] getTags() {
        return tags;
    }
}
