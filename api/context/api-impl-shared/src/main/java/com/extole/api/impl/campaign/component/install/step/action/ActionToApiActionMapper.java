package com.extole.api.impl.campaign.component.install.step.action;

import java.time.ZoneId;

import com.extole.api.campaign.component.install.step.action.Action;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionFireAsPerson;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionSchedule;

public final class ActionToApiActionMapper {

    private ActionToApiActionMapper() {
    }

    public static Action map(ZoneId clientTimezone, BuiltCampaignControllerAction action) {
        switch (action.getType()) {
            case FIRE_AS_PERSON:
                return mapFireAsPersonAction((BuiltCampaignControllerActionFireAsPerson) action);
            case SCHEDULE:
                return mapScheduleAction(clientTimezone, (BuiltCampaignControllerActionSchedule) action);
            default:
                return new ActionImpl(action);
        }
    }

    private static Action mapFireAsPersonAction(BuiltCampaignControllerActionFireAsPerson action) {
        return new FireAsPersonActionImpl(action);
    }

    private static Action mapScheduleAction(ZoneId clientTimezone, BuiltCampaignControllerActionSchedule action) {
        return new ScheduleActionImpl(clientTimezone, action);
    }

}
