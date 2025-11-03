package com.extole.reporting.rest.impl.report.runner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.impl.report.execution.ReportRuntimeException;
import com.extole.reporting.rest.impl.report.runner.uploaders.ReportRunnerUploader;

@Component
public class ReportRunnerUploadersRepository {
    private final Map<ReportRunnerType, ReportRunnerUploader<?, ?>> reportRunnerUploadersByType;

    @Autowired
    public ReportRunnerUploadersRepository(List<ReportRunnerUploader<?, ?>> reportRunnerUploaders) {
        Map<ReportRunnerType, ReportRunnerUploader<?, ?>> reportRunnerUploadersMap = Maps.newHashMap();
        for (ReportRunnerUploader<?, ?> reportRunnerUploader : reportRunnerUploaders) {
            if (reportRunnerUploadersMap.containsKey(reportRunnerUploader.getType())) {
                throw new ReportRunnerRestRuntimeException(
                    "Found multiple instances of ReportRunnerUploader for the same type: "
                        + reportRunnerUploader.getType());
            }
            reportRunnerUploadersMap.put(reportRunnerUploader.getType(), reportRunnerUploader);
        }
        this.reportRunnerUploadersByType = Collections.unmodifiableMap(reportRunnerUploadersMap);
    }

    public ReportRunnerUploader<?, ?> getReportRunnerUploader(ReportRunnerType type) {
        ReportRunnerUploader<?, ?> mapper = reportRunnerUploadersByType.get(type);
        if (mapper == null) {
            throw new ReportRuntimeException("Unsupported report runner type: " + type);
        }
        return mapper;
    }
}
