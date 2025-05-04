package com.extole.client.rest.impl.sftp;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.impl.sftp.mapper.SftpDestinationMappersRepository;
import com.extole.client.rest.impl.sftp.resolver.SftpDestinationRequestResolver;
import com.extole.client.rest.sftp.SftpDestinationCreateRequest;
import com.extole.client.rest.sftp.SftpDestinationEndpoints;
import com.extole.client.rest.sftp.SftpDestinationListQuery;
import com.extole.client.rest.sftp.SftpDestinationResponse;
import com.extole.client.rest.sftp.SftpDestinationRestException;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationUpdateRequest;
import com.extole.client.rest.sftp.SftpDestinationValidationResponse;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.client.rest.sftp.SftpValidationStatus;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.request.resolver.ResolvesPolymorphicType;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.ExternalSftpDestination;
import com.extole.model.entity.client.sftp.SftpDestination;
import com.extole.model.service.client.directory.ExternalSftpDeliveryService;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationQueryBuilder;
import com.extole.model.service.client.sftp.SftpDestinationServerUsernameAlreadyExistsException;
import com.extole.model.service.client.sftp.SftpDestinationService;
import com.extole.model.service.client.sftp.SftpDestinationUnsupportedOperationException;
import com.extole.model.service.client.sftp.SftpDestinationValidationResult;

@Provider
@SuppressWarnings({"unchecked", "rawtypes"})
public class SftpDestinationEndpointsImpl implements SftpDestinationEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final SftpDestinationService sftpDestinationService;
    private final ExternalSftpDeliveryService sftpDeliveryService;
    private final SftpDestinationMappersRepository mappersRepository;

    @Autowired
    public SftpDestinationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        SftpDestinationService sftpDestinationService,
        ExternalSftpDeliveryService sftpDeliveryService,
        SftpDestinationMappersRepository mappersRepository) {
        this.authorizationProvider = authorizationProvider;
        this.sftpDestinationService = sftpDestinationService;
        this.sftpDeliveryService = sftpDeliveryService;
        this.mappersRepository = mappersRepository;
    }

    @Override
    public SftpDestinationResponse get(String accessToken, String sftpDestinationId, ZoneId timeZone)
        throws UserAuthorizationRestException, SftpDestinationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            SftpDestination sftpDestination = sftpDestinationService.get(authorization, Id.valueOf(sftpDestinationId));
            return mappersRepository.getResponseMapper(getRestDestinationType(sftpDestination.getType()))
                .toResponse(sftpDestination, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.NOT_FOUND)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<SftpDestinationResponse> list(String accessToken, SftpDestinationListQuery query, ZoneId timezone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SftpDestinationQueryBuilder queryBuilder = sftpDestinationService.list(authorization);

            query.getType().ifPresent(type -> queryBuilder
                .withType(com.extole.model.entity.client.sftp.SftpDestinationType.valueOf(type.name())));
            query.getName().ifPresent(name -> queryBuilder.withName(name));
            query.getUsername().ifPresent(username -> queryBuilder.withUsername(username));
            query.getHost().ifPresent(host -> queryBuilder.withHost(host));
            queryBuilder.withLimit(query.getLimit().intValue());
            queryBuilder.withOffset(query.getOffset().intValue());

            return queryBuilder.execute().stream()
                .map(destination -> mappersRepository.getResponseMapper(getRestDestinationType(destination.getType()))
                    .toResponse(destination, timezone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SftpDestinationResponse create(String accessToken, SftpDestinationCreateRequest createRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, SftpDestinationValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            SftpDestinationType type = createRequest.getType();
            SftpDestination sftpDestination =
                mappersRepository.getCreateRequestMapper(type).create(authorization, createRequest);
            return mappersRepository.getResponseMapper(type).toResponse(sftpDestination, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    @ResolvesPolymorphicType(resolver = SftpDestinationRequestResolver.class)
    public SftpDestinationResponse update(String accessToken, String sftpDestinationId,
        SftpDestinationUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, SftpDestinationRestException, SftpDestinationValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            SftpDestinationType type = updateRequest.getType();
            SftpDestination sftpDestination = mappersRepository.getUpdateRequestMapper(type).update(authorization,
                Id.valueOf(sftpDestinationId), updateRequest);
            return mappersRepository.getResponseMapper(type).toResponse(sftpDestination, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SftpDestinationResponse delete(String accessToken, String sftpDestinationId, ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SftpDestination sftpDestination =
                sftpDestinationService.delete(authorization, Id.valueOf(sftpDestinationId));
            return mappersRepository.getResponseMapper(getRestDestinationType(sftpDestination.getType()))
                .toResponse(sftpDestination, timezone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.NOT_FOUND)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SftpDestinationValidationResponse validate(String accessToken, String sftpDestinationId, ZoneId timeZone)
        throws UserAuthorizationRestException, SftpDestinationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SftpDestination sftpDestination = sftpDestinationService.get(authorization, Id.valueOf(sftpDestinationId));
            if (sftpDestination.getType() != com.extole.model.entity.client.sftp.SftpDestinationType.EXTERNAL) {
                throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                    .withErrorCode(SftpDestinationRestException.UNSUPPORTED_OPERATION)
                    .addParameter("sftp_destination_id", sftpDestinationId)
                    .addParameter("type", sftpDestination.getType().name())
                    .build();
            }

            SftpDestinationValidationResult validationResult =
                sftpDeliveryService.validate(authorization, (ExternalSftpDestination) sftpDestination);
            SftpDestinationResponse sftpDestinationResponse =
                mappersRepository.getResponseMapper(getRestDestinationType(sftpDestination.getType()))
                    .toResponse(sftpDestination, timeZone);
            return SftpDestinationValidationResponse.builder()
                .withSftpDestination(sftpDestinationResponse)
                .withStatus(SftpValidationStatus.valueOf(validationResult.getStatus().name()))
                .withStatusReason(validationResult.getStatusReason().getReason())
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.NOT_FOUND)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SftpDestinationResponse sync(String accessToken, String sftpDestinationId, ZoneId timeZone)
        throws UserAuthorizationRestException, SftpDestinationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SftpDestination sftpDestination =
                sftpDestinationService.synchronize(authorization, Id.valueOf(sftpDestinationId));
            return mappersRepository.getResponseMapper(getRestDestinationType(sftpDestination.getType()))
                .toResponse(sftpDestination, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.NOT_FOUND)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .withCause(e)
                .build();
        } catch (SftpDestinationUnsupportedOperationException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.UNSUPPORTED_OPERATION)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .addParameter("type", e.getType())
                .build();
        } catch (SftpDestinationServerUsernameAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.USERNAME_EXISTS_ON_SFTP_SERVER)
                .addParameter("username", e.getUsername())
                .withCause(e)
                .build();
        }
    }

    private SftpDestinationType
        getRestDestinationType(com.extole.model.entity.client.sftp.SftpDestinationType type) {
        return SftpDestinationType.valueOf(type.name());
    }
}
