package com.extole.api.service;

import java.util.List;

import com.extole.api.batch.BatchJob;

public interface BatchJobListQueryBuilder {

    BatchJobListQueryBuilder withName(String name);

    BatchJobListQueryBuilder withTags(String[] tags);

    BatchJobListQueryBuilder withStatuses(BatchJob.Status[] statuses);

    BatchJobListQueryBuilder withLimit(int limit);

    BatchJobListQueryBuilder withOffset(int offset);

    List<BatchJob> execute() throws BatchJobServiceException;
}
