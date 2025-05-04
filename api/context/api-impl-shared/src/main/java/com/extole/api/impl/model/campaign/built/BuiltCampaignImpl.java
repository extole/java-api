package com.extole.api.impl.model.campaign.built;

import static java.util.Collections.unmodifiableList;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import com.extole.api.impl.model.campaign.QualityRuleImpl;
import com.extole.api.model.campaign.QualityRule;
import com.extole.api.model.campaign.built.BuiltCampaign;
import com.extole.api.model.campaign.built.BuiltCampaignComponent;
import com.extole.api.model.campaign.built.BuiltCampaignController;
import com.extole.api.model.campaign.built.BuiltCampaignFlowStep;
import com.extole.api.model.campaign.built.BuiltCampaignJourneyEntry;
import com.extole.api.model.campaign.built.BuiltCampaignLabel;
import com.extole.api.model.campaign.built.BuiltCampaignStep;
import com.extole.api.model.campaign.built.BuiltRewardRule;
import com.extole.api.model.campaign.built.BuiltTransitionRule;
import com.extole.common.lang.ToString;
import com.extole.model.entity.campaign.CampaignLabelType;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.pojo.campaign.built.BuiltCampaignLabelPojo;
import com.extole.model.pojo.campaign.built.BuiltCampaignPojo;

public final class BuiltCampaignImpl implements BuiltCampaign {
    private final BuiltCampaignPojo builtCampaignPojo;

    public BuiltCampaignImpl(BuiltCampaignPojo builtCampaignPojo) {
        this.builtCampaignPojo = builtCampaignPojo;
    }

    @Override
    public String getId() {
        return builtCampaignPojo.getId().getValue();
    }

    @Override
    public String getName() {
        return builtCampaignPojo.getName();
    }

    @Override
    public String getState() {
        return builtCampaignPojo.getState().name();
    }

    @Override
    public BuiltCampaignStep[] getSteps() {
        return builtCampaignPojo.getSteps().stream()
            .map(step -> new BuiltCampaignStepImpl(step))
            .toArray(BuiltCampaignStep[]::new);
    }

    @Override
    public BuiltCampaignController[] getControllers() {
        return builtCampaignPojo.getSteps().stream()
            .filter(step -> step.getType() == StepType.CONTROLLER)
            .map(step -> (com.extole.model.entity.campaign.built.BuiltCampaignController) step)
            .map(value -> new BuiltCampaignControllerImpl(value))
            .toArray(BuiltCampaignController[]::new);
    }

    @Override
    public BuiltCampaignJourneyEntry[] getJourneyEntries() {
        return builtCampaignPojo.getSteps().stream()
            .filter(step -> step.getType() == StepType.JOURNEY_ENTRY)
            .map(step -> (com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry) step)
            .map(value -> new BuiltCampaignJourneyEntryImpl(value))
            .toArray(BuiltCampaignJourneyEntry[]::new);
    }

    @Override
    public BuiltCampaignLabel[] getLabels() {
        return internalGetLabels().stream()
            .map(value -> new BuiltCampaignLabelImpl(value))
            .toArray(BuiltCampaignLabel[]::new);
    }

    @Override
    public BuiltCampaignLabel getProgramLabel() {
        return new BuiltCampaignLabelImpl(builtCampaignPojo.getProgramLabel());
    }

    @Override
    public Integer getVersion() {
        return builtCampaignPojo.getVersion();
    }

    @Override
    public Integer getBuildVersion() {
        return builtCampaignPojo.getBuildVersion();
    }

    @Nullable
    @Override
    public Integer getParentVersion() {
        return builtCampaignPojo.getParentVersion().orElse(null);
    }

    @Override
    public boolean isDraft() {
        return builtCampaignPojo.isDraft();
    }

    @Override
    public BuiltCampaignFlowStep[] getFlowSteps() {
        return builtCampaignPojo.getFlowSteps().stream()
            .map(value -> new BuiltCampaignFlowStepImpl(value))
            .toArray(BuiltCampaignFlowStep[]::new);
    }

    @Override
    public String[] getTags() {
        return builtCampaignPojo.getTags().toArray(String[]::new);
    }

    @Nullable
    @Override
    public String getStartDate() {
        return builtCampaignPojo.getStartDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getStopDate() {
        return builtCampaignPojo.getStopDate().map(value -> value.toString()).orElse(null);
    }

    @Override
    public String getDescription() {
        return builtCampaignPojo.getDescription();
    }

    @Override
    public String getUpdatedDate() {
        return builtCampaignPojo.getUpdatedDate().toString();
    }

    @Nullable
    @Override
    public String getArchivedDate() {
        return builtCampaignPojo.getArchivedDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getDeletedDate() {
        return builtCampaignPojo.getDeletedDate().map(value -> value.toString()).orElse(null);
    }

    @Nullable
    @Override
    public String getLastPublishedDate() {
        return builtCampaignPojo.getLastPublishedDate().map(value -> value.toString()).orElse(null);
    }

    @Override
    public BuiltRewardRule[] getRewardRules() {
        return builtCampaignPojo.getRewardRules().stream()
            .map(value -> new BuiltRewardRuleImpl(value))
            .toArray(BuiltRewardRule[]::new);
    }

    @Override
    public QualityRule[] getQualityRules() {
        return builtCampaignPojo.getQualityRules().stream()
            .map(value -> new QualityRuleImpl(value))
            .toArray(QualityRule[]::new);
    }

    @Override
    public BuiltTransitionRule[] getTransitionRules() {
        return builtCampaignPojo.getTransitionRules().stream()
            .map(value -> new BuiltTransitionRuleImpl(value))
            .toArray(BuiltTransitionRule[]::new);
    }

    @Override
    public BuiltCampaignComponent[] getComponents() {
        return builtCampaignPojo.getComponents().stream()
            .map(value -> new BuiltCampaignComponentImpl(value))
            .toArray(BuiltCampaignComponent[]::new);
    }

    @Override
    public String getProgramType() {
        return builtCampaignPojo.getProgramType();
    }

    @Nullable
    @Override
    public String getThemeName() {
        return builtCampaignPojo.getThemeName().orElse(null);
    }

    @Override
    public String getEditorId() {
        return builtCampaignPojo.getEditorId().getValue();
    }

    @Override
    public String getEditorType() {
        return builtCampaignPojo.getEditorType().name();
    }

    @Override
    public String[] getLocks() {
        return builtCampaignPojo.getLocks().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    private List<com.extole.model.entity.campaign.built.BuiltCampaignLabel> internalGetLabels() {
        List<com.extole.model.entity.campaign.built.BuiltCampaignLabel> pojoLabels = builtCampaignPojo.getLabels();
        boolean hasTarget = pojoLabels.stream()
            .anyMatch(label -> label.getType() == CampaignLabelType.PROGRAM);

        if (!hasTarget) {
            List<com.extole.model.entity.campaign.built.BuiltCampaignLabel> result = Lists.newArrayList(pojoLabels);
            result.add(new BuiltCampaignLabelPojo(getId(), CampaignLabelType.PROGRAM, Instant.now()));
            return unmodifiableList(result);
        }

        return pojoLabels;
    }

}
