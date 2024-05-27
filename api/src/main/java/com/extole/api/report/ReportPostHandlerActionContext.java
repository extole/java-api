package com.extole.api.report;

import com.extole.api.service.BatchJobService;
import com.extole.api.service.GlobalServices;

public interface ReportPostHandlerActionContext {

    Report getReport();

    GlobalServices getGlobalServices();

    BatchJobService getBatchJobService();
}
