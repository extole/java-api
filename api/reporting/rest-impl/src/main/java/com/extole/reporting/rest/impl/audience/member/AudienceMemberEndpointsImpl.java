package com.extole.reporting.rest.impl.audience.member;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.audience.membership.service.AudienceMemberNotFoundException;
import com.extole.audience.membership.service.AudienceMembershipService;
import com.extole.audience.membership.service.QueryLimitsAudienceMembershipServiceException;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.audience.Audience;
import com.extole.model.service.audience.AudienceNotFoundException;
import com.extole.model.service.audience.AudienceService;
import com.extole.person.service.audience.membership.AudienceMembershipNotFoundException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.locale.PersonLocale;
import com.extole.reporting.rest.audience.member.AudienceMemberDownloadParameters;
import com.extole.reporting.rest.audience.member.AudienceMemberDownloadRestException;
import com.extole.reporting.rest.audience.member.AudienceMemberEndpoints;
import com.extole.reporting.rest.audience.member.AudienceMemberQueryParameters;
import com.extole.reporting.rest.audience.member.AudienceMemberResponse;
import com.extole.reporting.rest.audience.member.AudienceMemberRestException;
import com.extole.reporting.rest.audience.member.PersonLocaleResponse;

@Provider
public class AudienceMemberEndpointsImpl implements AudienceMemberEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(AudienceMemberEndpointsImpl.class);

    private static final int BATCH_SIZE = 1000;
    private static final int PARTITION_SIZE = 10;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 60;

    private static final String CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s.%s";
    private static final String FILENAME_FORMATTER = "audience-%s-%s";

    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceService audienceService;
    private final AudienceMembershipService audienceMembershipService;
    private final AudienceMemberWriterFactory audienceMemberWriterFactory;
    private final ExecutorService executorService;

    @Autowired
    public AudienceMemberEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        AudienceService audienceService,
        AudienceMembershipService audienceMembershipService,
        AudienceMemberWriterFactory audienceMemberWriterFactory,
        @Value("${audience.member.download.service.thread.pool.size:10}") int threadPoolSize) {
        this.authorizationProvider = authorizationProvider;
        this.audienceService = audienceService;
        this.audienceMembershipService = audienceMembershipService;
        this.audienceMemberWriterFactory = audienceMemberWriterFactory;
        this.executorService =
            Executors.newFixedThreadPool(threadPoolSize, ExtoleThreadFactory.of("audience-download"));
    }

    @Override
    public List<Id<com.extole.api.person.Person>> list(String accessToken,
        Id<com.extole.api.audience.Audience> audienceId,
        AudienceMemberQueryParameters queryParameters)
        throws UserAuthorizationRestException, AudienceMemberRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            if (queryParameters.getEmail().isPresent()) {
                try {
                    Person member = audienceMembershipService.getMemberByEmail(authorization,
                        audience.getId(), queryParameters.getEmail().get());
                    return List.of(Id.valueOf(member.getId().getValue()));
                } catch (AudienceMembershipNotFoundException | AudienceMemberNotFoundException e) {
                    return Collections.emptyList();
                }
            }

            return audienceMembershipService.listMemberIds(authorization, audience.getId(),
                queryParameters.getLimit().orElse(Integer.valueOf(AudienceMemberQueryParameters.DEFAULT_LIMIT))
                    .intValue(),
                queryParameters.getOffset().orElse(Integer.valueOf(AudienceMemberQueryParameters.DEFAULT_OFFSET))
                    .intValue())
                .stream()
                .map(member -> Id.<com.extole.api.person.Person>valueOf(member.getValue()))
                .collect(Collectors.toList());
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberRestException.class)
                .withErrorCode(AudienceMemberRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (QueryLimitsAudienceMembershipServiceException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberRestException.class)
                .withErrorCode(AudienceMemberRestException.INVALID_QUERY_LIMIT)
                .addParameter("limit", queryParameters.getLimit())
                .addParameter("offset", queryParameters.getOffset())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<AudienceMemberResponse> listPeople(String accessToken, Id<com.extole.api.audience.Audience> audienceId,
        AudienceMemberQueryParameters queryParameters)
        throws UserAuthorizationRestException, AudienceMemberRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Audience audience = audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            if (queryParameters.getEmail().isPresent()) {
                try {
                    Person member = audienceMembershipService.getMemberByEmail(authorization,
                        audience.getId(), queryParameters.getEmail().get());
                    return List.of(toPersonResponse(member));
                } catch (AudienceMembershipNotFoundException | AudienceMemberNotFoundException e) {
                    return Collections.emptyList();
                }
            }

            return audienceMembershipService.listPeople(authorization, audience.getId(),
                queryParameters.getLimit().orElse(Integer.valueOf(AudienceMemberQueryParameters.DEFAULT_LIMIT))
                    .intValue(),
                queryParameters.getOffset().orElse(Integer.valueOf(AudienceMemberQueryParameters.DEFAULT_OFFSET))
                    .intValue())
                .stream()
                .map(member -> toPersonResponse(member))
                .collect(Collectors.toList());
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberRestException.class)
                .withErrorCode(AudienceMemberRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (QueryLimitsAudienceMembershipServiceException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberRestException.class)
                .withErrorCode(AudienceMemberRestException.INVALID_QUERY_LIMIT)
                .addParameter("limit", queryParameters.getLimit())
                .addParameter("offset", queryParameters.getOffset())
                .addParameter("audience_id", audienceId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response download(String accessToken, Id<com.extole.api.audience.Audience> audienceId, String contentType,
        String format, AudienceMemberDownloadParameters downloadParameters)
        throws UserAuthorizationRestException, AudienceMemberRestException, AudienceMemberDownloadRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            Audience audience = audienceService.getById(authorization, Id.valueOf(audienceId.getValue()));

            AudienceMemberDownloadFormat responseFormat =
                getFormat(Optional.ofNullable(format), Optional.ofNullable(contentType), audienceId);

            StreamingOutput streamingOutput = createStreamingOutput(authorization, audience.getId(),
                downloadParameters, responseFormat);

            return Response.ok(streamingOutput)
                .type(responseFormat.getMimeType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    String.format(CONTENT_DISPOSITION_FORMATTER,
                        downloadParameters.getFilename()
                            .orElse(String.format(FILENAME_FORMATTER, authorization.getClientId(), audience.getId())),
                        responseFormat.getExtension()))
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (AudienceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberRestException.class)
                .withErrorCode(AudienceMemberRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    private static AudienceMemberResponse toPersonResponse(Person person) {
        return new AudienceMemberResponse(
            person.getId().getValue(),
            person.getIdentityKey().getName(),
            person.getIdentityKeyValue(),
            person.getEmail(),
            person.getFirstName(),
            person.getLastName(),
            person.getProfilePictureUrl() != null ? person.getProfilePictureUrl().toString() : null,
            person.getPartnerUserId(),
            toPersonLocaleResponse(person.getLocale()),
            person.getVersion(),
            person.isBlocked());
    }

    private static PersonLocaleResponse toPersonLocaleResponse(PersonLocale locale) {
        return new PersonLocaleResponse(locale.getLastBrowser().orElse(null),
            locale.getUserSpecified().orElse(null));
    }

    private AudienceMemberDownloadFormat getFormat(Optional<String> format, Optional<String> contentType,
        Id<com.extole.api.audience.Audience> audienceId)
        throws AudienceMemberDownloadRestException {
        if (format.isPresent() && !format.get().isEmpty()) {
            return getFormat(format.get().split("\\.")[1], audienceId);
        } else if (contentType.isPresent() && !contentType.get().isEmpty()) {
            try {
                return AudienceMemberDownloadFormat.valueOfMimeType(contentType.get());
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(AudienceMemberDownloadRestException.class)
                    .withErrorCode(AudienceMemberDownloadRestException.AUDIENCE_MEMBER_LIST_CONTENT_TYPE_NOT_SUPPORTED)
                    .addParameter("audience_id", audienceId)
                    .addParameter("content_type", contentType.get())
                    .withCause(e)
                    .build();
            }
        }
        return AudienceMemberDownloadFormat.JSON;
    }

    private AudienceMemberDownloadFormat getFormat(String format, Id<com.extole.api.audience.Audience> audienceId)
        throws AudienceMemberDownloadRestException {
        try {
            return AudienceMemberDownloadFormat.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(AudienceMemberDownloadRestException.class)
                .withErrorCode(AudienceMemberDownloadRestException.AUDIENCE_MEMBER_LIST_FORMAT_NOT_SUPPORTED)
                .addParameter("audience_id", audienceId)
                .addParameter("format", format)
                .withCause(e)
                .build();
        }
    }

    private StreamingOutput createStreamingOutput(Authorization authorization, Id<Audience> audienceId,
        AudienceMemberDownloadParameters downloadParameters, AudienceMemberDownloadFormat format) {
        return outputStream -> {
            int offset = downloadParameters.getOffset().intValue();
            int limit = downloadParameters.getLimit().orElse(Integer.valueOf(Integer.MAX_VALUE)).intValue();
            int batchSize = Math.min(limit, BATCH_SIZE);

            AudienceMemberWriter audienceMemberWriter = audienceMemberWriterFactory.getWriter(format);
            audienceMemberWriter.writeFirstLine(outputStream);

            List<Map.Entry<Integer, Integer>> ranges = Lists.newArrayList();
            int iterations = (int) Math.round((double) limit / batchSize);
            AtomicBoolean isEmpty = new AtomicBoolean(false);
            for (int i = 0; i < iterations; i++) {
                int remainingLimit = limit - i * batchSize;
                int batchLimit = Math.min(remainingLimit, batchSize);
                int newOffSet = offset + i * batchSize;
                ranges.add(new AbstractMap.SimpleEntry<>(Integer.valueOf(batchLimit), Integer.valueOf(newOffSet)));

                if (ranges.size() == PARTITION_SIZE || i == iterations - 1) {
                    List<Future<List<AudienceMemberResponse>>> futures =
                        ranges.stream().map(entry -> executorService.submit(() -> {
                            try {
                                return fetchPeople(authorization, audienceId, entry.getKey().intValue(),
                                    entry.getValue().intValue());
                            } catch (QueryLimitsAudienceMembershipServiceException | AuthorizationException
                                | AudienceNotFoundException e) {
                                throw new AudienceMemberRuntimeException(
                                    "Could not fetch people for clientId " + authorization.getClientId() +
                                        " audienceId " + audienceId + " limit " + entry.getKey() + " offset "
                                        + entry.getValue(),
                                    e);
                            }
                        })).collect(Collectors.toList());

                    for (Future<List<AudienceMemberResponse>> future : futures) {
                        try {
                            if (isEmpty.get()) {
                                future.cancel(true);
                            }

                            List<AudienceMemberResponse> batch = future.get();
                            if (batch.isEmpty()) {
                                isEmpty.set(true);
                                break;
                            }
                            try {
                                audienceMemberWriter.write(batch, outputStream);
                            } catch (IOException e) {
                                throw new AudienceMemberRuntimeException(
                                    "Could not write audience member file for clientId " + authorization.getClientId()
                                        + " and audienceId " + audienceId,
                                    e);
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            throw new AudienceMemberRuntimeException(
                                "A fetch future terminated unsuccessfully for clientId " + authorization.getClientId()
                                    + " and audienceId " + audienceId,
                                e);
                        }
                    }
                    ranges.clear();
                }
                if (isEmpty.get()) {
                    break;
                }
            }

            audienceMemberWriter.writeLastLine(outputStream);
        };
    }

    private List<AudienceMemberResponse> fetchPeople(Authorization authorization, Id<Audience> audienceId, int limit,
        int offset)
        throws QueryLimitsAudienceMembershipServiceException, AuthorizationException, AudienceNotFoundException {
        return audienceMembershipService.listPeople(authorization, audienceId,
            limit, offset)
            .stream()
            .map(member -> toPersonResponse(member))
            .collect(Collectors.toList());
    }

    @PreDestroy
    public void shutdownExecutor() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOG.error("Could not shutdown audience executor service in {} seconds",
                    Long.valueOf(SHUTDOWN_TIMEOUT_SECONDS));
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            LOG.error("Interrupted while waiting for audience executor service to shutdown", e);
            Thread.currentThread().interrupt();
        }
    }
}
