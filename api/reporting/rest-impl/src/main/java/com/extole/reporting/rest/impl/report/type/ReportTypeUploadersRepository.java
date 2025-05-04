package com.extole.reporting.rest.impl.report.type;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.ReportType.Type;
import com.extole.reporting.rest.impl.report.execution.ReportRuntimeException;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeCreateUploader;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeUpdateUploader;

@Component
public class ReportTypeUploadersRepository {
    private final Map<Type, ReportTypeCreateUploader<?>> reportTypeCreateUploadersByType;
    private final Map<Type, ReportTypeUpdateUploader<?>> reportTypeUpdateUploadersByType;

    @Autowired
    public ReportTypeUploadersRepository(List<ReportTypeCreateUploader<?>> reportTypeCreateUploaders,
        List<ReportTypeUpdateUploader<?>> reportTypeUpdateUploaders) {

        Map<Type, ReportTypeCreateUploader<?>> reportTypeCreateUploadersMap = Maps.newHashMap();
        for (ReportTypeCreateUploader<?> reportTypeUploader : reportTypeCreateUploaders) {
            if (reportTypeCreateUploadersMap.containsKey(reportTypeUploader.getType())) {
                throw new ReportRuntimeException(
                    "Found multiple instances of ReportTypeCreateUploader for the same type: "
                        + reportTypeUploader.getType());
            }
            reportTypeCreateUploadersMap.put(reportTypeUploader.getType(), reportTypeUploader);
        }

        Map<Type, ReportTypeUpdateUploader<?>> reportTypeUpdateUploadersMap = Maps.newHashMap();
        for (ReportTypeUpdateUploader<?> reportTypeUploader : reportTypeUpdateUploaders) {
            if (reportTypeUpdateUploadersMap.containsKey(reportTypeUploader.getType())) {
                throw new ReportRuntimeException(
                    "Found multiple instances of ReportTypeUpdateUploader for the same type: "
                        + reportTypeUploader.getType());
            }
            reportTypeUpdateUploadersMap.put(reportTypeUploader.getType(), reportTypeUploader);
        }
        this.reportTypeCreateUploadersByType = Collections.unmodifiableMap(reportTypeCreateUploadersMap);
        this.reportTypeUpdateUploadersByType = Collections.unmodifiableMap(reportTypeUpdateUploadersMap);
    }

    public ReportTypeCreateUploader<?> getReportTypeCreateUploader(Type type) {
        ReportTypeCreateUploader<?> mapper = reportTypeCreateUploadersByType.get(type);
        if (mapper == null) {
            throw new ReportRuntimeException("Unsupported report type: " + type);
        }
        return mapper;
    }

    public ReportTypeUpdateUploader<?> getReportTypeUpdateUploader(Type type) {
        ReportTypeUpdateUploader<?> mapper = reportTypeUpdateUploadersByType.get(type);
        if (mapper == null) {
            throw new ReportRuntimeException("Unsupported report type: " + type);
        }
        return mapper;
    }
}
