package com.extole.api.service;

public interface BatchJobService {
    BatchJobBuilder create() throws BatchJobServiceException;

    BatchJobListQueryBuilder list() throws BatchJobServiceException;
}
