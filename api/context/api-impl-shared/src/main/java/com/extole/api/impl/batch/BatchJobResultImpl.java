package com.extole.api.impl.batch;

import javax.annotation.Nullable;

import com.extole.api.batch.BatchJobResult;
import com.extole.common.lang.date.ExtoleDateTimeFormatters;

public class BatchJobResultImpl implements BatchJobResult {
    private final String startedDate;
    private final String completedDate;
    private final String errorCode;
    private final String errorMessage;
    private final String debugMessage;
    private final Long successRows;
    private final Long failedRows;

    public BatchJobResultImpl(com.extole.reporting.entity.batch.BatchJobResult batchJobResult) {
        this.startedDate =
            batchJobResult.getStartedDate().map(ExtoleDateTimeFormatters.ISO_INSTANT::format).orElse(null);
        this.completedDate =
            batchJobResult.getCompletedDate().map(ExtoleDateTimeFormatters.ISO_INSTANT::format).orElse(null);
        this.errorCode = batchJobResult.getErrorCode().map(Enum::name).orElse(null);
        this.errorMessage = batchJobResult.getErrorMessage().orElse(null);
        this.debugMessage = batchJobResult.getDebugMessage().orElse(null);
        this.successRows = batchJobResult.getSuccessRows().orElse(null);
        this.failedRows = batchJobResult.getFailedRows().orElse(null);
    }

    @Nullable
    @Override
    public String getStartedDate() {
        return startedDate;
    }

    @Nullable
    @Override
    public String getCompletedDate() {
        return completedDate;
    }

    @Nullable
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Nullable
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    @Override
    public String getDebugMessage() {
        return debugMessage;
    }

    @Nullable
    @Override
    public Long getSuccessRows() {
        return successRows;
    }

    @Nullable
    @Override
    public Long getFailedRows() {
        return failedRows;
    }
}
