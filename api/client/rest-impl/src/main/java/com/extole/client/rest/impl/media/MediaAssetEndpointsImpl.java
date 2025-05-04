package com.extole.client.rest.impl.media;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.media.MediaAssetEndpoints;
import com.extole.client.rest.media.MediaAssetRequest;
import com.extole.client.rest.media.MediaAssetResponse;
import com.extole.client.rest.media.MediaAssetRestException;
import com.extole.client.rest.media.MediaAssetValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.media.MediaAsset;
import com.extole.model.service.media.MediaAssetBuilder;
import com.extole.model.service.media.MediaAssetContentSizeTooBigException;
import com.extole.model.service.media.MediaAssetIllegalCharacterInNameException;
import com.extole.model.service.media.MediaAssetInvalidJavascriptContentException;
import com.extole.model.service.media.MediaAssetInvalidNameLengthException;
import com.extole.model.service.media.MediaAssetMissingContentException;
import com.extole.model.service.media.MediaAssetMissingNameException;
import com.extole.model.service.media.MediaAssetNameAlreadyExistsException;
import com.extole.model.service.media.MediaAssetNotFoundException;
import com.extole.model.service.media.MediaAssetService;

@Provider
public class MediaAssetEndpointsImpl implements MediaAssetEndpoints {
    private static final int CONTENT_FILE_THRESHOLD = 256 * 1024;

    private final ClientAuthorizationProvider authorizationProvider;
    private final MediaAssetService mediaAssetService;

    @Autowired
    public MediaAssetEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        MediaAssetService mediaAssetService) {
        this.authorizationProvider = authorizationProvider;
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    public List<MediaAssetResponse> list(String accessToken, ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return mediaAssetService.getAll(authorization).stream()
                .map(asset -> toMediaAssetResponse(asset, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public MediaAssetResponse get(String accessToken, String assetId, ZoneId timeZone)
        throws UserAuthorizationRestException, MediaAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            MediaAsset mediaAsset = mediaAssetService.getById(authorization, Id.valueOf(assetId));
            return toMediaAssetResponse(mediaAsset, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (MediaAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetRestException.class)
                .withErrorCode(MediaAssetRestException.MEDIA_ASSET_NOT_FOUND)
                .addParameter("asset_id", assetId)
                .withCause(e).build();
        }
    }

    @Override
    public MediaAssetResponse create(String accessToken, MediaAssetRequest request, InputStream inputStream,
        ZoneId timeZone) throws UserAuthorizationRestException, MediaAssetValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            MediaAssetBuilder builder = mediaAssetService.create(authorization);
            MediaAsset mediaAsset = update(builder, request, inputStream);
            return toMediaAssetResponse(mediaAsset, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public MediaAssetResponse update(String accessToken, String assetId, MediaAssetRequest request,
        InputStream inputStream, ZoneId timeZone)
        throws UserAuthorizationRestException, MediaAssetRestException, MediaAssetValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            MediaAssetBuilder builder = mediaAssetService.update(authorization, Id.valueOf(assetId));
            MediaAsset mediaAsset = update(builder, request, inputStream);
            return toMediaAssetResponse(mediaAsset, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (MediaAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetRestException.class)
                .withErrorCode(MediaAssetRestException.MEDIA_ASSET_NOT_FOUND)
                .addParameter("asset_id", assetId)
                .withCause(e).build();
        }
    }

    private MediaAsset update(MediaAssetBuilder builder, @Nullable MediaAssetRequest request,
        @Nullable InputStream inputStream) throws MediaAssetValidationRestException {
        try {
            if (request != null && !Strings.isNullOrEmpty(request.getName())) {
                builder.withName(request.getName());
            }
            if (inputStream != null) {
                builder.withContent(copyToByteSource(inputStream));
            }
            return builder.save();
        } catch (MediaAssetMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_NAME_MISSING)
                .withCause(e).build();
        } catch (MediaAssetInvalidNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_NAME_LENGTH_OUT_OF_RANGE)
                .withCause(e).build();
        } catch (MediaAssetIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (MediaAssetNameAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_NAME_DUPLICATED)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (MediaAssetMissingContentException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_CONTENT_MISSING)
                .withCause(e).build();
        } catch (MediaAssetContentSizeTooBigException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_CONTENT_SIZE_TOO_BIG)
                .addParameter("size", Long.valueOf(e.getContentSize()))
                .withCause(e).build();
        } catch (MediaAssetInvalidJavascriptContentException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(
                    MediaAssetValidationRestException.MEDIA_ASSET_INVALID_JAVASCRIPT)
                .addParameter("validation_errors", e.getValidationErrors())
                .withCause(e).build();
        } catch (IOException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetValidationRestException.class)
                .withErrorCode(MediaAssetValidationRestException.MEDIA_ASSET_CONTENT_UPLOAD_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public MediaAssetResponse delete(String accessToken, String assetId, ZoneId timeZone)
        throws UserAuthorizationRestException, MediaAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            MediaAsset mediaAsset = mediaAssetService.delete(authorization, Id.valueOf(assetId));
            return toMediaAssetResponse(mediaAsset, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (MediaAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetRestException.class)
                .withErrorCode(MediaAssetRestException.MEDIA_ASSET_NOT_FOUND)
                .addParameter("asset_id", assetId)
                .withCause(e).build();
        }
    }

    @Override
    public Response download(String accessToken, String assetId)
        throws UserAuthorizationRestException, MediaAssetRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            MediaAsset mediaAsset = mediaAssetService.getById(authorization, Id.valueOf(assetId));
            ByteSource content = mediaAssetService.getContent(authorization, Id.valueOf(assetId));
            StreamingOutput stream = outputStream -> content.copyTo(outputStream);
            return Response.ok(stream)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mediaAsset.getName())
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (MediaAssetNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(MediaAssetRestException.class)
                .withErrorCode(MediaAssetRestException.MEDIA_ASSET_NOT_FOUND)
                .addParameter("asset_id", assetId)
                .withCause(e).build();
        }
    }

    private ByteSource copyToByteSource(InputStream inputStream) throws IOException {
        try (FileBackedOutputStream outputStream = new FileBackedOutputStream(CONTENT_FILE_THRESHOLD, true)) {
            ByteStreams.copy(inputStream, outputStream);
            return outputStream.asByteSource();
        } finally {
            inputStream.close();
        }
    }

    private MediaAssetResponse toMediaAssetResponse(MediaAsset asset, ZoneId timeZone) {
        return new MediaAssetResponse(
            asset.getId().getValue(),
            asset.getName(),
            asset.getCreatedDate().atZone(timeZone),
            asset.getUpdatedDate().atZone(timeZone));
    }
}
