package com.extole.api.impl.campaign.component.install.step.action;

import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.extole.api.campaign.component.install.step.action.ScheduleAction;
import com.extole.api.step.action.schedule.ScheduleActionContext;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionSchedule;

final class ScheduleActionImpl implements ScheduleAction {

    private final ZoneId clientTimezone;
    private final BuiltCampaignControllerActionSchedule action;

    ScheduleActionImpl(ZoneId clientTimezone, BuiltCampaignControllerActionSchedule action) {
        this.clientTimezone = clientTimezone;
        this.action = action;
    }

    @Override
    public String getId() {
        return action.getId().getValue();
    }

    @Override
    public String getType() {
        return action.getType().name();
    }

    @Override
    public String getScheduleName() {
        return action.getScheduleName();
    }

    @Override
    public Map<String, RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>> getData() {
        return action.getData();
    }

    @Override
    public long[] getScheduleDelays() {
        return action.getScheduleDelays()
            .stream()
            .mapToLong(value -> value.toMillis())
            .toArray();
    }

    @Override
    public String[] getScheduleDates() {
        return action.getScheduleDates().stream()
            .map(value -> value.atZone(clientTimezone).toString())
            .collect(Collectors.toUnmodifiableList())
            .toArray(new String[action.getScheduleDates().size()]);
    }

    @Override
    public boolean isForce() {
        return action.isForce();
    }

}
