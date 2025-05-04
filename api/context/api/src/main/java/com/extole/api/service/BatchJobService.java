package com.extole.api.service;

public interface BatchJobService {
    BatchJobBuilder create() throws BatchJobBuildException;
}
