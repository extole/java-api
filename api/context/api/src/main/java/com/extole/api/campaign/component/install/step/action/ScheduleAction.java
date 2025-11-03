package com.extole.api.campaign.component.install.step.action;

import java.util.Map;
import java.util.Optional;

import com.extole.api.step.action.schedule.ScheduleActionContext;
import com.extole.evaluateable.RuntimeEvaluatable;

public interface ScheduleAction extends Action {

    RuntimeEvaluatable<ScheduleActionContext, String> getScheduleName();

    Map<String, RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>> getData();

    long[] getScheduleDelays();

    String[] getScheduleDates();

    boolean isForce();

}
