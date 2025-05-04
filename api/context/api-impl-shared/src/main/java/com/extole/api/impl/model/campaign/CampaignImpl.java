package com.extole.api.impl.model.campaign;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.Campaign;
import com.extole.api.model.campaign.Component;
import com.extole.api.model.campaign.Controller;
import com.extole.api.model.campaign.FlowStep;
import com.extole.api.model.campaign.JourneyEntry;
import com.extole.api.model.campaign.Label;
import com.extole.api.model.campaign.QualityRule;
import com.extole.api.model.campaign.RewardRule;
import com.extole.api.model.campaign.Step;
import com.extole.api.model.campaign.TransitionRule;
import com.extole.common.lang.ToString;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.pojo.campaign.CampaignPojo;

public final class CampaignImpl implements Campaign {
    private final CampaignPojo campaignPojo;

    public CampaignImpl(CampaignPojo campaignPojo) {
        this.campaignPojo = campaignPojo;
    }

    @Override
    public String getId() {
        return campaignPojo.getId().getValue();
    }

    @Override
    public String getName() {
        return campaignPojo.getName();
    }

    @Override
    public String getState() {
        return campaignPojo.getState().name();
    }

    @Override
    public Step[] getSteps() {
        return campaignPojo.getSteps().stream()
            .map(value -> new StepImpl(value))
            .toArray(Step[]::new);
    }

    @Override
    public Controller[] getControllers() {
        return campaignPojo.getSteps().stream()
            .filter(step -> step.getType() == StepType.CONTROLLER)
            .map(step -> (com.extole.model.entity.campaign.CampaignController) step)
            .map(controller -> new ControllerImpl(controller))
            .toArray(Controller[]::new);
    }

    @Override
    public JourneyEntry[] getJourneyEntries() {
        return campaignPojo.getSteps().stream()
            .filter(step -> step.getType() == StepType.JOURNEY_ENTRY)
            .map(step -> (com.extole.model.entity.campaign.CampaignJourneyEntry) step)
            .map(value -> new JourneyEntryImpl(value))
            .toArray(JourneyEntry[]::new);
    }

    @Override
    public Label[] getLabels() {
        return campaignPojo.getLabels().stream()
            .map(value -> new LabelImpl(value))
            .toArray(Label[]::new);
    }

    @Override
    public Label getProgramLabel() {
        return new LabelImpl(campaignPojo.getProgramLabel());
    }

    @Override
    public int getVersion() {
        return campaignPojo.getVersion().intValue();
    }

    @Nullable
    @Override
    public Integer getParentVersion() {
        return campaignPojo.getParentVersion().orElse(null);
    }

    @Override
    public boolean isDraft() {
        return campaignPojo.isDraft();
    }

    @Override
    public FlowStep[] getFlowSteps() {
        return campaignPojo.getFlowSteps().stream()
            .map(value -> new FlowStepImpl(value))
            .toArray(desiredArraySize -> new FlowStep[desiredArraySize]);
    }

    @Nullable
    @Override
    public String getStartDate() {
        return campaignPojo.getStartDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getStopDate() {
        return campaignPojo.getStopDate().map(value -> value.toString()).orElse(null);
    }

    @Override
    public String getDescription() {
        return campaignPojo.getDescription();
    }

    @Override
    public String getUpdatedDate() {
        return campaignPojo.getUpdatedDate().toString();
    }

    @Nullable
    @Override
    public String getArchivedDate() {
        return campaignPojo.getArchivedDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getDeletedDate() {
        return campaignPojo.getDeletedDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getLastPublishedDate() {
        return campaignPojo.getLastPublishedDate().map(value -> value.toString()).orElse(null);
    }

    @Override
    public RewardRule[] getRewardRules() {
        return campaignPojo.getRewardRules().stream()
            .map(value -> new RewardRuleImpl(value))
            .toArray(RewardRule[]::new);
    }

    @Override
    public QualityRule[] getQualityRules() {
        return campaignPojo.getQualityRules().stream()
            .map(value -> new QualityRuleImpl(value))
            .toArray(QualityRule[]::new);
    }

    @Override
    public TransitionRule[] getTransitionRules() {
        return campaignPojo.getTransitionRules().stream()
            .map(value -> new TransitionRuleImpl(value))
            .toArray(TransitionRule[]::new);
    }

    @Override
    public Component[] getComponents() {
        return campaignPojo.getComponents().stream()
            .map(value -> new ComponentImpl(value))
            .toArray(Component[]::new);
    }

    @Override
    public String getProgramType() {
        return campaignPojo.getProgramType();
    }

    @Nullable
    @Override
    public String getThemeName() {
        return campaignPojo.getThemeName().orElse(null);
    }

    @Override
    public String getEditorId() {
        return campaignPojo.getEditorId().getValue();
    }

    @Override
    public String getEditorType() {
        return campaignPojo.getEditorType().name();
    }

    @Override
    public String[] getLocks() {
        return campaignPojo.getLocks().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String[] getTags() {
        return campaignPojo.getTags().toArray(String[]::new);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
