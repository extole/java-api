package com.extole.api.batch;

import javax.annotation.Nullable;

public interface BatchJobResult {

    @Nullable
    String getStartedDate();

    @Nullable
    String getCompletedDate();

    @Nullable
    String getErrorCode();

    @Nullable
    String getErrorMessage();

    @Nullable
    String getDebugMessage();

    @Nullable
    Long getSuccessRows();

    @Nullable
    Long getFailedRows();
}
