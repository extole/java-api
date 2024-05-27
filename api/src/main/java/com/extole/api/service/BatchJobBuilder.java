package com.extole.api.service;

import java.util.Map;

import com.extole.api.batch.BatchJob;
import com.extole.api.batch.column.BatchJobColumn;

public interface BatchJobBuilder {

    BatchJobBuilder withName(String name) throws BatchJobBuildException;

    BatchJobBuilder withEventName(String eventName) throws BatchJobBuildException;

    BatchJobBuilder withDefaultEventName(String defaultEventName) throws BatchJobBuildException;

    BatchJobBuilder withTags(String[] tags) throws BatchJobBuildException;

    BatchJobBuilder withEventData(Map<String, String> eventData);

    BatchJobBuilder withEventColumns(String[] eventColumns);

    BatchJobBuilder withColumns(BatchJobColumn[] columns);

    BatchJob save() throws BatchJobBuildException;
}
