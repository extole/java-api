package com.extole.consumer.rest.impl.debug.security;

import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.consumer.rest.debug.security.ContentSecurityPolicyReportEndpoints;
import com.extole.consumer.rest.debug.security.ContentSecurityPolicyReportRequest;
import com.extole.consumer.rest.debug.security.report.ReportRequest;
import com.extole.consumer.service.debug.security.ContentSecurityPolicyReportService;
import com.extole.consumer.service.debug.security.ContentSecurityReport;

@Provider
public class ContentSecurityPolicyReportEndpointsImpl implements ContentSecurityPolicyReportEndpoints {

    private final ContentSecurityPolicyReportService contentSecurityPolicyReportService;

    @Autowired
    public ContentSecurityPolicyReportEndpointsImpl(
        ContentSecurityPolicyReportService contentSecurityPolicyReportService) {
        this.contentSecurityPolicyReportService = contentSecurityPolicyReportService;
    }

    @Override
    public Response submit(Optional<ContentSecurityPolicyReportRequest> reportRequest) {
        contentSecurityPolicyReportService.process(toReport(reportRequest));
        return Response.ok().build();
    }

    private static ContentSecurityReport toReport(Optional<ContentSecurityPolicyReportRequest> reportRequest) {
        if (!reportRequest.isPresent() || reportRequest.get().getReport() == null) {
            return ContentSecurityReport.builder().build();
        }

        ReportRequest report = reportRequest.get().getReport();
        ContentSecurityReport.Builder contentSecurityReportBuilder = ContentSecurityReport.builder();

        report.getBlockedUri().ifPresent(blockedUri -> contentSecurityReportBuilder.withBlockedUri(blockedUri));
        report.getDisposition().ifPresent(disposition -> contentSecurityReportBuilder.withDisposition(disposition));
        report.getDocumentUri().ifPresent(documentUri -> contentSecurityReportBuilder.withDocumentUri(documentUri));
        report.getOriginalPolicy().ifPresent(policy -> contentSecurityReportBuilder.withOriginalPolicy(policy));
        report.getReferrer().ifPresent(referrer -> contentSecurityReportBuilder.withReferrer(referrer));
        report.getScriptSample().ifPresent(scriptSample -> contentSecurityReportBuilder.withScriptSample(scriptSample));
        report.getStatusCode().ifPresent(statusCode -> contentSecurityReportBuilder.withStatusCode(statusCode));
        report.getEffectiveDirective()
            .ifPresent(directive -> contentSecurityReportBuilder.withEffectiveDirective(directive));
        report.getViolatedDirective()
            .ifPresent(directive -> contentSecurityReportBuilder.withViolatedDirective(directive));

        return contentSecurityReportBuilder.build();
    }
}
