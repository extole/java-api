package com.extole.api.event.workflow;

import java.util.Map;

import com.extole.api.person.PersonStep;

public interface Workflow {
    Map<String, String> getKeys();

    PersonStep[] getSteps();
}
