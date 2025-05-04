package com.extole.api.impl.campaign;

import java.time.Instant;
import java.util.Optional;

import javax.annotation.Nullable;

import com.extole.api.campaign.Campaign;
import com.extole.common.lang.ToString;
import com.extole.model.pojo.campaign.built.BuiltCampaignPojo;

public final class CampaignImpl implements Campaign {
    private final String clientId;
    private final String campaignId;
    private final String campaignName;
    private final String programLabel;
    private final String programType;
    private final Optional<String> themeName;
    private final String description;
    private final String currentState;
    private final String campaignType;
    private final Optional<String> startDate;
    private final Optional<String> stopDate;
    private final Optional<String> archivedDate;
    private final Optional<String> deletedDate;
    private final Optional<String> lastPublishedDate;
    private final String[] tags;

    public CampaignImpl(BuiltCampaignPojo builtCampaign) {
        this.clientId = builtCampaign.getClientId().getValue();
        this.campaignId = builtCampaign.getId().getValue();
        this.campaignName = builtCampaign.getName();
        this.programLabel = builtCampaign.getProgramLabel().getName();
        this.programType = builtCampaign.getProgramType();
        this.themeName = builtCampaign.getThemeName();
        this.description = builtCampaign.getDescription();
        this.currentState = builtCampaign.getState().name();
        this.campaignType = builtCampaign.getCampaignType().name();
        this.startDate = builtCampaign.getStartDate().map(Instant::toString);
        this.stopDate = builtCampaign.getStopDate().map(Instant::toString);
        this.archivedDate = builtCampaign.getArchivedDate().map(Instant::toString);
        this.deletedDate = builtCampaign.getDeletedDate().map(Instant::toString);
        this.lastPublishedDate = builtCampaign.getLastPublishedDate().map(Instant::toString);
        this.tags = builtCampaign.getTags().toArray(new String[0]);
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

    @Nullable
    @Override
    public String getThemeName() {
        return themeName.orElse(null);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCurrentState() {
        return currentState;
    }

    @Nullable
    @Override
    public String getStartDate() {
        return startDate.orElse(null);
    }

    @Nullable
    @Override
    public String getStopDate() {
        return stopDate.orElse(null);
    }

    @Nullable
    @Override
    public String getArchivedDate() {
        return archivedDate.orElse(null);
    }

    @Nullable
    @Override
    public String getDeletedDate() {
        return deletedDate.orElse(null);
    }

    @Nullable
    @Override
    public String getLastPublishedDate() {
        return lastPublishedDate.orElse(null);
    }

    @Override
    public String getCampaignType() {
        return campaignType;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
