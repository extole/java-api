package com.extole.client.rest.impl.logo;

import java.time.ZoneId;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.logo.LogoEndpoints;
import com.extole.client.rest.logo.LogoResponse;
import com.extole.client.rest.logo.LogoRestException;
import com.extole.client.rest.logo.LogoValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.logo.LogoAlreadyExistsException;
import com.extole.model.service.logo.LogoBuilder;
import com.extole.model.service.logo.LogoEmptyImageException;
import com.extole.model.service.logo.LogoImageDownloadException;
import com.extole.model.service.logo.LogoImageException;
import com.extole.model.service.logo.LogoImageFormatException;
import com.extole.model.service.logo.LogoImageLengthException;
import com.extole.model.service.logo.LogoImageUploadException;
import com.extole.model.service.logo.LogoNotFoundException;
import com.extole.model.service.logo.LogoService;

@Provider
public class LogoEndpointsImpl implements LogoEndpoints {
    private static final String MIME_TYPE_IMAGE = "image/png";

    private final ClientAuthorizationProvider authorizationProvider;
    private final LogoService logoService;
    private final LogoRestMapper logoRestMapper;

    @Autowired
    public LogoEndpointsImpl(ClientAuthorizationProvider authorizationProvider, LogoService logoService,
        LogoRestMapper logoRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.logoService = logoService;
        this.logoRestMapper = logoRestMapper;
    }

    @Override
    public LogoResponse get(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException, LogoRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return logoRestMapper.toLogoResponse(logoService.get(authorization), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (LogoNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LogoRestException.class)
                .withErrorCode(LogoRestException.LOGO_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e.getCause())
                .build();
        }
    }

    @Override
    public LogoResponse create(String accessToken, FileInputStreamRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, LogoValidationRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            LogoBuilder logoBuilder = logoService.create(authorization);

            if (request.getInputStream() != null) {
                logoBuilder.withImage(request.getInputStream());
            }

            return logoRestMapper.toLogoResponse(logoBuilder.save(), timeZone);

        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (LogoImageLengthException e) {
            throw RestExceptionBuilder.newBuilder(LogoValidationRestException.class)
                .withErrorCode(LogoValidationRestException.LOGO_CONTENT_LENGTH_EXCEEDED)
                .addParameter("client_id", authorization.getClientId())
                .addParameter("max_allowed_file_size", Long.valueOf(e.getMaxSizeBytes()))
                .withCause(e.getCause())
                .build();
        } catch (LogoImageFormatException e) {
            throw RestExceptionBuilder.newBuilder(LogoValidationRestException.class)
                .withErrorCode(LogoValidationRestException.LOGO_FORMAT_NOT_SUPPORTED)
                .addParameter("client_id", authorization.getClientId())
                .addParameter("format", e.getMimeType())
                .addParameter("required_format", e.getRequiredMimeType())
                .withCause(e.getCause())
                .build();
        } catch (LogoEmptyImageException e) {
            throw RestExceptionBuilder.newBuilder(LogoValidationRestException.class)
                .withErrorCode(LogoValidationRestException.LOGO_CONTENT_EMPTY)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e.getCause())
                .build();
        } catch (LogoImageException | LogoImageUploadException e) {
            throw RestExceptionBuilder.newBuilder(LogoValidationRestException.class)
                .withErrorCode(LogoValidationRestException.LOGO_CONTENT_UPLOAD_ERROR)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e.getCause())
                .build();
        } catch (LogoAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(LogoValidationRestException.class)
                .withErrorCode(LogoValidationRestException.LOGO_ALREADY_EXISTS)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e.getCause())
                .build();
        }
    }

    @Override
    public LogoResponse archive(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException, LogoRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return logoRestMapper.toLogoResponse(logoService.archive(authorization), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (LogoNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(LogoRestException.class)
                .withErrorCode(LogoRestException.LOGO_NOT_FOUND)
                .addParameter("client_id", authorization.getClientId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadLogoImage(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        checkAccessRights(authorization);

        StreamingOutput streamer =
            outputStream -> {
                try {
                    logoService.downloadLogoImage(authorization, outputStream);
                } catch (AuthorizationException | LogoImageDownloadException exception) {
                    throw new LogoImageRuntimeWebApplicationException(exception.getMessage(), exception.getCause(),
                        LogoRestException.LOGO_CONTENT_DOWNLOAD_ERROR.getHttpCode());
                }
            };

        return Response.ok(streamer, MIME_TYPE_IMAGE).build();
    }

    private void checkAccessRights(Authorization authorization) throws UserAuthorizationRestException {
        if (!authorization.isAuthorized(authorization.getClientId(), Authorization.Scope.USER_SUPPORT)) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .build();
        }
    }
}
