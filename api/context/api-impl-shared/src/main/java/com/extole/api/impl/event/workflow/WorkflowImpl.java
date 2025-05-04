package com.extole.api.impl.event.workflow;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

import com.extole.api.event.workflow.Workflow;
import com.extole.api.person.PersonStep;
import com.extole.common.lang.ToString;

public class WorkflowImpl implements Workflow, Serializable {
    private final Map<String, String> keys;
    private final PersonStep[] steps;

    public WorkflowImpl(Map<String, String> keys, PersonStep[] steps) {
        this.keys = Maps.newHashMap(keys);
        this.steps = steps;
    }

    @Override
    public Map<String, String> getKeys() {
        return keys;
    }

    @Override
    public PersonStep[] getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
