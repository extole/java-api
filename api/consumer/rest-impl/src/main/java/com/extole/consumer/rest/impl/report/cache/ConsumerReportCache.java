package com.extole.consumer.rest.impl.report.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.GuavaCacheMetrics;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.client.WebResourceFactory;
import com.extole.common.rest.client.exception.translation.extole.ExtoleRestExceptionTranslationStrategy;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.consumer.rest.impl.report.ReportContentDownloadException;
import com.extole.consumer.rest.impl.report.ReportContentLengthException;
import com.extole.consumer.rest.impl.report.ReportNotFoundException;
import com.extole.consumer.rest.impl.report.restclient.ReportApiProperties;
import com.extole.consumer.rest.impl.report.restclient.ReportRestRuntimeException;
import com.extole.consumer.rest.report.FormatReportInfoResponse;
import com.extole.consumer.rest.report.ReportFormat;
import com.extole.consumer.rest.report.ReportResponse;
import com.extole.id.Id;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.LatestReportRequest;
import com.extole.reporting.rest.report.execution.ReportEndpoints;
import com.extole.reporting.rest.report.execution.ReportNotFoundRestException;
import com.extole.reporting.rest.report.execution.ReportRestException;
import com.extole.reporting.rest.report.execution.ReportStatus;

@Component
public class ConsumerReportCache {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerReportCache.class);
    private static final Set<ReportStatus> VALID_STATUSES =
        Sets.newHashSet(ReportStatus.DONE, ReportStatus.SFTP_DELIVERY_FAILED);

    private final LoadingCache<IdAuthorizationKey, ReportResponse> reportByIdCache;
    private final LoadingCache<IdAuthorizationFormatKey, FormatReportInfoResponse> reportInfoByIdCache;
    private final LoadingCache<IdAuthorizationFormatKey, byte[]> reportContentByIdCache;
    private final LoadingCache<TagAuthorizationKey, Id<?>> reportByTagCache;
    private final long maxCachedContentLength;
    private final WebTarget target;

    @Autowired
    public ConsumerReportCache(
        @Value("${consumer.report.cache.size:1000}") long cacheSize,
        @Value("${consumer.report.cache.expiration.minutes:90}") long expirationMinutes,
        @Value("${consumer.report.cache.max.content-length:3145728}") long maxCachedContentLength,
        ExtoleMetricRegistry metricRegistry,
        Client reportApiClient,
        ReportApiProperties reportApiProperties) {
        this.maxCachedContentLength = maxCachedContentLength;
        this.target = reportApiClient.target(reportApiProperties.getUrl());

        this.reportByIdCache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(cacheSize)
            .expireAfterAccess(expirationMinutes, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public ReportResponse load(IdAuthorizationKey idKey) throws Exception {
                    return getReportById(idKey);
                }
            });
        metricRegistry.registerAll(GuavaCacheMetrics.metricsFor("reportByIdCache", this.reportByIdCache));

        this.reportInfoByIdCache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(cacheSize)
            .expireAfterAccess(expirationMinutes, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public FormatReportInfoResponse load(IdAuthorizationFormatKey idKey) throws Exception {
                    LOG.trace("miss reportId: {} for clientId: {} for format: {}", idKey.getId(), idKey.getClientId(),
                        idKey.getFormat());
                    try {
                        com.extole.reporting.rest.report.execution.FormatReportInfoResponse reportInfo =
                            getReportEndpoints(idKey.getClientId()).getReportInfoByFormat(
                                idKey.getAuthorization().getAccessToken(),
                                idKey.getId().getValue(), idKey.getFormat().getExtension());
                        return new FormatReportInfoResponse(reportInfo.getContentLength(), reportInfo.getTotalRows());
                    } catch (ReportRestRuntimeException e) {
                        throw new ReportNotFoundException(
                            "Unable to find report with reportId=" + idKey.getId() + " and format="
                                + idKey.getFormat(),
                            e);
                    } catch (UserAuthorizationRestException | ReportRestException e) {
                        throw new ReportNotFoundException(
                            "Unable to find report with reportId=" + idKey.getId() + "and format=" + idKey.getFormat(),
                            e);
                    }
                }
            });
        metricRegistry.registerAll(GuavaCacheMetrics.metricsFor("reportInfoByIdCache", this.reportInfoByIdCache));

        this.reportContentByIdCache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(cacheSize)
            .expireAfterAccess(expirationMinutes, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {

                @Override
                public byte[] load(IdAuthorizationFormatKey idKey) throws Exception {
                    LOG.trace("miss reportId: {} for clientId: {} for format: {}", idKey.getId(), idKey.getClientId(),
                        idKey.getFormat());
                    try {
                        Response response = getReportEndpoints(idKey.getClientId())
                            .downloadReport(idKey.getAuthorization().getAccessToken(),
                                idKey.getFormat().getMimeType(), idKey.getId().getValue(),
                                "." + idKey.getFormat().getExtension(), null, null, null);
                        try (InputStream inputStream = (InputStream) response.getEntity()) {
                            return IOUtils.toByteArray(inputStream);
                        }
                    } catch (ReportRestRuntimeException | IOException | UserAuthorizationRestException
                        | ReportRestException | QueryLimitsRestException e) {
                        throw new ReportNotFoundException("Unable to find report with reportId=" + idKey.getId()
                            + " and format=" + idKey.getFormat(), e);
                    }
                }
            });
        metricRegistry.registerAll(GuavaCacheMetrics.metricsFor("reportContentByIdCache", this.reportContentByIdCache));

        this.reportByTagCache = CacheBuilder.newBuilder()
            .recordStats()
            .maximumSize(cacheSize)
            .expireAfterAccess(expirationMinutes, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public Id<?> load(TagAuthorizationKey idKey) throws Exception {
                    LOG.trace("miss tag: {} for clientId: {}", idKey.getTag(), idKey.getClientId());
                    try {
                        com.extole.reporting.rest.report.execution.ReportResponse reportResponse =
                            getReportEndpoints(idKey.getClientId()).getLatestReport(
                                idKey.getAuthorization().getAccessToken(),
                                LatestReportRequest.builder().withHavingAnyTags(idKey.getTag()).build());
                        return Id.valueOf(reportResponse.getReportId());
                    } catch (UserAuthorizationRestException | ReportRestException | RuntimeException e) {
                        throw new ReportNotFoundException("Unable to find report with tag=" + idKey.getTag(), e);
                    }
                }
            });
        metricRegistry.registerAll(GuavaCacheMetrics.metricsFor("reportByTagCache", this.reportByTagCache));
    }

    public ReportResponse getReportById(Authorization authorization, Id<?> reportId)
        throws ReportNotFoundException {
        try {
            return reportByIdCache.get(new IdAuthorizationKey(reportId, authorization));
        } catch (ExecutionException e) {
            throw new ReportNotFoundException("Unable to find report with reportId=" + reportId, e);
        }
    }

    public FormatReportInfoResponse getReportInfoById(Authorization authorization, Id<?> reportId, ReportFormat format)
        throws ReportNotFoundException {
        try {
            reportByIdCache.get(new IdAuthorizationKey(reportId, authorization));
            return reportInfoByIdCache.get(new IdAuthorizationFormatKey(reportId, authorization, format));
        } catch (ExecutionException e) {
            throw new ReportNotFoundException("Unable to find report with reportId=" + reportId, e);
        }
    }

    public void downloadReportContentById(Authorization authorization, Id<?> reportId, ReportFormat format,
        OutputStream outputStream)
        throws ReportNotFoundException, ReportContentLengthException, ReportContentDownloadException {
        try {
            reportByIdCache.get(new IdAuthorizationKey(reportId, authorization));
            FormatReportInfoResponse reportInfoResponse =
                reportInfoByIdCache.get(new IdAuthorizationFormatKey(reportId, authorization, format));
            if (reportInfoResponse.getContentLength() > maxCachedContentLength) {
                throw new ReportContentLengthException(
                    "Report with id " + reportId + " exceed allowed content length ");
            }
            byte[] content = reportContentByIdCache.get(new IdAuthorizationFormatKey(reportId, authorization, format));

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
                ByteStreams.copy(inputStream, outputStream);
            } catch (IOException e) {
                throw new ReportContentDownloadException("Report content could not be downloaded" + reportId, e);
            }
        } catch (ExecutionException e) {
            throw new ReportNotFoundException("Unable to find report with reportId=" + reportId, e);
        }
    }

    public ReportResponse getReportByTags(Authorization authorization, List<String> tags, List<String> excludeTags)
        throws ReportNotFoundException {
        Optional<Id<?>> reportId = findReportIdByTags(authorization, tags, excludeTags);
        if (reportId.isPresent()) {
            return getReportById(authorization, reportId.get());
        } else {
            throw new ReportNotFoundException("Unable to find report with tags=" + tags);
        }
    }

    private Optional<Id<?>> findReportIdByTags(Authorization authorization, List<String> tags,
        List<String> excludeTags) throws ReportNotFoundException {
        Optional<Id<?>> resultReportId = Optional.empty();
        try {
            for (String tag : tags) {
                Id<?> reportId = reportByTagCache.get(new TagAuthorizationKey(tag, authorization));
                if (reportId != null) {
                    resultReportId = filterByExcludeTags(authorization, excludeTags, reportId);
                    if (resultReportId.isPresent()) {
                        break;
                    }
                }
            }
        } catch (ExecutionException e) {
            if (e.getCause() != null && e.getCause() instanceof ReportNotFoundException) {
                throw (ReportNotFoundException) e.getCause();
            } else {
                LOG.error("Unable to find report by reportId: {}", resultReportId, e);
            }
        }

        return resultReportId;
    }

    private Optional<Id<?>> filterByExcludeTags(Authorization authorization, List<String> excludeTags, Id<?> reportId)
        throws ExecutionException {
        Set<String> reportTags = reportByIdCache.get(new IdAuthorizationKey(reportId, authorization)).getTags();
        if (reportTags != null) {
            if (reportTags.stream()
                .noneMatch(tagValue -> excludeTags.stream().anyMatch(tagValue::equalsIgnoreCase))) {
                return Optional.of(reportId);
            }
        }
        return Optional.empty();
    }

    private ReportResponse getReportById(IdAuthorizationKey idKey) throws ReportNotFoundException {
        LOG.trace("miss reportId: {} for client: {}", idKey.getId(), idKey.getClientId());
        try {
            com.extole.reporting.rest.report.execution.ReportResponse reportResponse =
                getReportEndpoints(idKey.getClientId()).readReport(idKey.getAuthorization().getAccessToken(),
                    idKey.getId().getValue(), null);
            if (!reportResponse.getScopes().contains(ReportTypeScope.CONSUMER_PUBLIC)
                || !VALID_STATUSES.contains(reportResponse.getStatus())) {
                throw new ReportNotFoundException("Unable to find report with reportId=" + idKey.getId());
            }
            return ReportResponse.builder()
                .withCompletedDate(
                    reportResponse.getCompletedDate().map(date -> date.toInstant().toString()).orElse(null))
                .withReportId(reportResponse.getReportId())
                .withReportType(reportResponse.getReportType())
                .withDisplayName(reportResponse.getDisplayName())
                .withDownloadUri(reportResponse.getDownloadUri())
                .withTags(reportResponse.getTags())
                .withFormats(reportResponse.getFormats().stream().map(
                    format -> ReportFormat.valueOf(format.name()))
                    .collect(Collectors.toList()))
                .build();
        } catch (ReportRestRuntimeException | UserAuthorizationRestException | ReportNotFoundRestException e) {
            throw new ReportNotFoundException("Unable to find report with reportId=" + idKey.getId(), e);
        }
    }

    private ReportEndpoints getReportEndpoints(Id<ClientHandle> clientId) {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.putSingle(ExtoleHeaderType.CLIENT_ID.getHeaderName(), clientId.getValue());

        return WebResourceFactory.<ReportEndpoints>builder()
            .withResourceInterface(ReportEndpoints.class)
            .withTarget(target)
            .withRestExceptionTranslationStrategy(ExtoleRestExceptionTranslationStrategy.getSingleton())
            .withHeaders(new ImmutableMultivaluedMap<>(headers))
            .build();
    }
}
