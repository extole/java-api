package com.extole.api.report;

import com.extole.api.ClientContext;
import com.extole.api.LoggerContext;
import com.extole.api.service.BatchJobService;
import com.extole.api.service.GlobalServices;

public interface ReportPostHandlerActionContext extends LoggerContext {
    ClientContext getClientContext();

    Report getReport();

    GlobalServices getGlobalServices();

    BatchJobService getBatchJobService();
}
