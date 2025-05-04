package com.extole.api.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface DataJobCost {
    String getDataJobId();

    String getJobType();

    double getEstimatedPriceUsd();

    long getListCount();

    long getReadCount();

    long getFileCount();

    double getFilesSize();

    double getCoreSeconds();

    double getMemorySeconds();
}
