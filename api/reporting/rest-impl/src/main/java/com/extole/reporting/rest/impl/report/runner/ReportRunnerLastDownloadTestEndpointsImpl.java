package com.extole.reporting.rest.impl.report.runner;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ext.Provider;

import com.google.common.annotations.Beta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.report.runner.ReportRunnerDownload;
import com.extole.reporting.service.report.runner.ReportRunnerDownloadService;

@Provider
@Singleton
@Beta
@Path("/v6/test/report-runners")
public class ReportRunnerLastDownloadTestEndpointsImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ReportRunnerLastDownloadTestEndpointsImpl.class);
    private final ClientAuthorizationProvider authorizationProvider;

    private final ReportRunnerDownloadService reportRunnerDownloadService;

    @Autowired
    public ReportRunnerLastDownloadTestEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportRunnerDownloadService reportRunnerDownloadService) {
        this.authorizationProvider = authorizationProvider;
        this.reportRunnerDownloadService = reportRunnerDownloadService;
    }

    @GET
    @Path("/{reportRunnerId}/last-download")
    public String get(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("reportRunnerId") String reportRunnerId) {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            Optional<ReportRunnerDownload> reportRunnerDownload =
                reportRunnerDownloadService.getLastDownloadByRunnerId(authorization, Id.valueOf(reportRunnerId));
            return toStringResponse(reportRunnerDownload);
        } catch (Exception e) {
            LOG.error("Failed to retrieve report runner for id: {}", reportRunnerId, e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class).withCause(e)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build();
        }
    }

    @PUT
    @Path("/{reportRunnerId}/last-download")
    public void put(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("reportRunnerId") String reportRunnerId, @RequestBody Instant lastDownloadDate) {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            reportRunnerDownloadService.createOrUpdate(authorization, Id.valueOf(reportRunnerId), lastDownloadDate);
        } catch (Exception e) {
            LOG.error("Failed to retrieve report runner for id: {}", reportRunnerId, e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withCause(e)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build();
        }
    }

    private String toStringResponse(Optional<ReportRunnerDownload> value) {
        return value.map(Object::toString).orElse(null);
    }
}
