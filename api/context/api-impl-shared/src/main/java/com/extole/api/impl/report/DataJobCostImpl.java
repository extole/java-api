package com.extole.api.impl.report;

import com.extole.api.report.DataJobCost;
import com.extole.reporting.pojo.jobcost.DataJobCostPojo;

public class DataJobCostImpl implements DataJobCost {

    private final String dataJobId;
    private final String jobType;
    private final double estimatedPriceUsd;
    private final long listCount;
    private final long readCount;
    private final long fileCount;
    private final double filesSize;
    private final double coreSeconds;
    private final double memorySeconds;

    public DataJobCostImpl(DataJobCostPojo dataJobCost) {
        this.dataJobId = dataJobCost.getDataJobId().getValue();
        this.jobType = dataJobCost.getJobType();
        this.estimatedPriceUsd = dataJobCost.getEstimatedPriceUsd();
        this.listCount = dataJobCost.getListCount();
        this.readCount = dataJobCost.getReadCount();
        this.fileCount = dataJobCost.getFileCount();
        this.filesSize = dataJobCost.getFilesSize();
        this.coreSeconds = dataJobCost.getCoreSeconds();
        this.memorySeconds = dataJobCost.getMemorySeconds();
    }

    @Override
    public String getDataJobId() {
        return dataJobId;
    }

    @Override
    public String getJobType() {
        return jobType;
    }

    @Override
    public double getEstimatedPriceUsd() {
        return estimatedPriceUsd;
    }

    @Override
    public long getListCount() {
        return listCount;
    }

    @Override
    public long getReadCount() {
        return readCount;
    }

    @Override
    public long getFileCount() {
        return fileCount;
    }

    @Override
    public double getFilesSize() {
        return filesSize;
    }

    @Override
    public double getCoreSeconds() {
        return coreSeconds;
    }

    @Override
    public double getMemorySeconds() {
        return memorySeconds;
    }
}
