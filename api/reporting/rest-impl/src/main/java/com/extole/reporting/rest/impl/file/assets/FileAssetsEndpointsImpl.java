package com.extole.reporting.rest.impl.file.assets;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.parser.QueryLimitsParser;
import com.extole.id.Id;
import com.extole.reporting.entity.FileFormat;
import com.extole.reporting.entity.assets.FileAsset;
import com.extole.reporting.rest.file.assets.FileAssetEncryptionRestException;
import com.extole.reporting.rest.file.assets.FileAssetExpiredRestException;
import com.extole.reporting.rest.file.assets.FileAssetMetadata;
import com.extole.reporting.rest.file.assets.FileAssetRequest;
import com.extole.reporting.rest.file.assets.FileAssetResponse;
import com.extole.reporting.rest.file.assets.FileAssetRestException;
import com.extole.reporting.rest.file.assets.FileAssetReviewStatus;
import com.extole.reporting.rest.file.assets.FileAssetStatus;
import com.extole.reporting.rest.file.assets.FileAssetUpdateRequest;
import com.extole.reporting.rest.file.assets.FileAssetValidationRestException;
import com.extole.reporting.rest.file.assets.FileAssetsEndpoints;
import com.extole.reporting.rest.file.assets.FileAssetsQueryParams;
import com.extole.reporting.service.file.assets.FileAssetBuildException;
import com.extole.reporting.service.file.assets.FileAssetBuilder;
import com.extole.reporting.service.file.assets.FileAssetContentDownloadException;
import com.extole.reporting.service.file.assets.FileAssetDuplicatedNameException;
import com.extole.reporting.service.file.assets.FileAssetEncryptionException;
import com.extole.reporting.service.file.assets.FileAssetExpiredException;
import com.extole.reporting.service.file.assets.FileAssetInvalidNameException;
import com.extole.reporting.service.file.assets.FileAssetInvalidTagsException;
import com.extole.reporting.service.file.assets.FileAssetMissingInputStreamException;
import com.extole.reporting.service.file.assets.FileAssetMissingPgpExtoleClientKeyException;
import com.extole.reporting.service.file.assets.FileAssetNotFoundException;
import com.extole.reporting.service.file.assets.FileAssetProcessingException;
import com.extole.reporting.service.file.assets.FileAssetsQueryBuilder;
import com.extole.reporting.service.file.assets.FileAssetsService;

@Provider
public class FileAssetsEndpointsImpl implements FileAssetsEndpoints {
    private static final int DEFAULT_LIMIT = 100;
    private static final String TEMPLATE_CONTENT_DISPOSITION = "attachment; filename = %s";

    private final FileAssetsService fileAssetsService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public FileAssetsEndpointsImpl(FileAssetsService fileAssetsService,
        ClientAuthorizationProvider authorizationProvider) {
        this.fileAssetsService = fileAssetsService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public FileAssetResponse create(String accessToken, FileAssetRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, FileAssetValidationRestException, FileAssetEncryptionRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            FileAssetBuilder builder = fileAssetsService.create(authorization);
            if (createRequest.getFileAssetMetadata().isPresent()) {
                FileAssetMetadata metadata = createRequest.getFileAssetMetadata().get();
                if (metadata.getName().isPresent()) {
                    builder.withName(metadata.getName().get());
                }
                if (metadata.getTags().isPresent()) {
                    builder.withTags(metadata.getTags().get());
                }
                if (metadata.getFormat().isPresent()) {
                    builder.withFormat(FileFormat.valueOf(metadata.getFormat().get()));
                }
            }
            if (createRequest.getFileInputStreamRequest() != null) {
                FileInputStreamRequest fileInputStreamRequest = createRequest.getFileInputStreamRequest();
                builder.withInputStream(fileInputStreamRequest.getInputStream());
                builder.withFilename(fileInputStreamRequest.getAttributes().getFileName());
            }
            return toResponse(builder.save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (FileAssetProcessingException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.FILE_PROCESSING_ERROR)
                .withCause(e).build();
        } catch (FileAssetMissingInputStreamException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.INPUT_FILE_MISSING)
                .withCause(e).build();
        } catch (FileAssetInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.NAME_INVALID)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (FileAssetDuplicatedNameException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.NAME_DUPLICATED)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (FileAssetInvalidTagsException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.TAGS_INVALID)
                .addParameter("invalid_tags", e.getInvalidTags())
                .withCause(e).build();
        } catch (FileAssetMissingPgpExtoleClientKeyException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetEncryptionRestException.class)
                .withErrorCode(FileAssetEncryptionRestException.MISSING_PGP_EXTOLE_KEY)
                .withCause(e).build();
        } catch (FileAssetEncryptionException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetEncryptionRestException.class)
                .withErrorCode(FileAssetEncryptionRestException.DECRYPTION_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public FileAssetResponse update(String accessToken, String fileId, FileAssetUpdateRequest updateRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, OmissibleRestException, FileAssetRestException,
        FileAssetValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            FileAssetBuilder builder = fileAssetsService.update(authorization, Id.valueOf(fileId));
            updateRequest.getName().ifPresent(name -> builder.withName(name));
            updateRequest.getTags().ifPresent(tags -> {
                if (tags.isEmpty()) {
                    builder.clearTags();
                } else {
                    builder.withTags(tags);
                }
            });
            return toResponse(builder.save(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (FileAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetRestException.class)
                .withErrorCode(FileAssetRestException.NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId().getValue())
                .withCause(e).build();
        } catch (FileAssetInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.NAME_INVALID)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (FileAssetDuplicatedNameException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.NAME_DUPLICATED)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (FileAssetInvalidTagsException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetValidationRestException.class)
                .withErrorCode(FileAssetValidationRestException.TAGS_INVALID)
                .addParameter("invalid_tags", e.getInvalidTags())
                .withCause(e).build();
        } catch (FileAssetBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public FileAssetResponse expire(String accessToken, String fileId, ZoneId timeZone)
        throws UserAuthorizationRestException, FileAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toResponse(fileAssetsService.expire(authorization, Id.valueOf(fileId)), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (FileAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetRestException.class)
                .withErrorCode(FileAssetRestException.NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId().getValue())
                .withCause(e).build();
        }
    }

    @Override
    public FileAssetResponse delete(String accessToken, String fileId, ZoneId timeZone)
        throws UserAuthorizationRestException, FileAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toResponse(fileAssetsService.delete(authorization, Id.valueOf(fileId)), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (FileAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetRestException.class)
                .withErrorCode(FileAssetRestException.NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId().getValue())
                .withCause(e).build();
        }
    }

    @Override
    public List<FileAssetResponse> list(String accessToken, FileAssetsQueryParams queryParams, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            FileAssetsQueryBuilder queryBuilder = fileAssetsService.list(authorization);
            if (queryParams.getName().isPresent() && !Strings.isNullOrEmpty(queryParams.getName().get())) {
                queryBuilder.withName(queryParams.getName().get());
            }
            if (queryParams.getUserId().isPresent() && !Strings.isNullOrEmpty(queryParams.getUserId().get())) {
                queryBuilder.withUserId(Id.valueOf(queryParams.getUserId().get()));
            }
            if (queryParams.getStatuses().isPresent() && !queryParams.getStatuses().get().isEmpty()) {
                queryBuilder.withStatuses(queryParams.getStatuses().get().stream()
                    .map(value -> com.extole.reporting.entity.assets.FileAssetStatus.valueOf(value.name()))
                    .collect(Collectors.toSet()));
            }
            if (queryParams.getTags().isPresent() && !queryParams.getTags().get().isEmpty()) {
                queryBuilder.withTags(queryParams.getTags().get());
            }
            if (queryParams.getLimit().isPresent()) {
                queryBuilder.withLimit(queryParams.getLimit().get());
            }
            if (queryParams.getOffset().isPresent()) {
                queryBuilder.withOffset(queryParams.getOffset().get());
            }
            return queryBuilder.execute().stream()
                .map(item -> toResponse(item, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public FileAssetResponse get(String accessToken, String fileId, ZoneId timeZone)
        throws UserAuthorizationRestException, FileAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toResponse(fileAssetsService.getById(authorization, Id.valueOf(fileId)), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (FileAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetRestException.class)
                .withErrorCode(FileAssetRestException.NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId().getValue())
                .withCause(e).build();
        }
    }

    @Override
    public Response download(String accessToken, String fileId, Optional<String> limit, Optional<String> offset)
        throws UserAuthorizationRestException, QueryLimitsRestException, FileAssetRestException,
        FileAssetExpiredRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            FileAsset fileAsset = fileAssetsService.getById(authorization, Id.valueOf(fileId));
            if (fileAsset.getStatus() == com.extole.reporting.entity.assets.FileAssetStatus.EXPIRED) {
                throw RestExceptionBuilder.newBuilder(FileAssetExpiredRestException.class)
                    .withErrorCode(FileAssetExpiredRestException.EXPIRED)
                    .addParameter("file_asset_id", fileId)
                    .build();
            }

            StreamingOutput streamer = getStreamingOutput(authorization, Id.valueOf(fileId), limit, offset);
            Response.ResponseBuilder responseBuilder = Response.ok(streamer, fileAsset.getMimeType());

            String filename = fileAsset.getName();
            if (!filename.toLowerCase().endsWith(fileAsset.getFormat().getName().toLowerCase())) {
                filename = filename + "." + fileAsset.getFormat().getName().toLowerCase();
            }
            responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
                String.format(TEMPLATE_CONTENT_DISPOSITION, filename));
            return responseBuilder.build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (FileAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetRestException.class)
                .withErrorCode(FileAssetRestException.NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId().getValue())
                .withCause(e.getCause()).build();
        }
    }

    private StreamingOutput getStreamingOutput(Authorization authorization, Id<FileAsset> fileId,
        Optional<String> limit, Optional<String> offset) throws QueryLimitsRestException {
        boolean paginate = limit.isPresent() || offset.isPresent();
        int limitValue = QueryLimitsParser.parseLimit(limit.orElse(null), DEFAULT_LIMIT);
        int offsetValue = QueryLimitsParser.parseOffset(offset.orElse(null), 0);
        return outputStream -> {
            try {
                if (paginate) {
                    fileAssetsService.download(authorization, fileId, limitValue, offsetValue, outputStream);
                } else {
                    fileAssetsService.download(authorization, fileId, outputStream);
                }
            } catch (AuthorizationException | FileAssetContentDownloadException | FileAssetNotFoundException
                | FileAssetExpiredException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e).build();
            }
        };
    }

    private static FileAssetResponse toResponse(FileAsset fileAsset, ZoneId timeZone) {
        return new FileAssetResponse(
            fileAsset.getId().getValue(),
            fileAsset.getName(),
            FileAssetStatus.valueOf(fileAsset.getStatus().name()),
            FileAssetReviewStatus.valueOf(fileAsset.getReviewStatus().name()),
            fileAsset.getTags(),
            fileAsset.getFormat().getName(),
            fileAsset.getCreatedDate().atZone(timeZone),
            fileAsset.getUpdatedDate().atZone(timeZone),
            fileAsset.getUserId().getValue(), fileAsset.getSize());
    }
}
