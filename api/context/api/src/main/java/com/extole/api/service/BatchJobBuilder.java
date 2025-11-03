package com.extole.api.service;

import java.util.Map;

import com.extole.api.batch.BatchJob;
import com.extole.api.batch.column.BatchJobColumn;

public interface BatchJobBuilder {

    BatchJobBuilder withName(String name) throws BatchJobServiceException;

    BatchJobBuilder withEventName(String eventName) throws BatchJobServiceException;

    BatchJobBuilder withDefaultEventName(String defaultEventName) throws BatchJobServiceException;

    BatchJobBuilder withTags(String[] tags) throws BatchJobServiceException;

    BatchJobBuilder withEventData(Map<String, String> eventData);

    BatchJobBuilder withEventColumns(String[] eventColumns);

    BatchJobBuilder withColumns(BatchJobColumn[] columns);

    BatchJob save() throws BatchJobServiceException;
}
