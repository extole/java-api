package com.extole.client.rest.impl.sftp.mapper;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.LocalSftpDestinationCreateRequest;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.sftp.LocalSftpDestination;
import com.extole.model.service.client.sftp.LocalSftpDestinationBuilder;
import com.extole.model.service.client.sftp.SftpDestinationBuildException;
import com.extole.model.service.client.sftp.SftpDestinationClientKeyNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidKeyContentException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidKeyTypeException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidNameException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidUsernameException;
import com.extole.model.service.client.sftp.SftpDestinationMissingKeyIdException;
import com.extole.model.service.client.sftp.SftpDestinationNameAlreadyExistsException;
import com.extole.model.service.client.sftp.SftpDestinationServerUsernameAlreadyExistsException;
import com.extole.model.service.client.sftp.SftpDestinationService;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
final class LocalSftpDestinationCreateRequestMapper implements
    SftpDestinationCreateRequestMapper<LocalSftpDestinationCreateRequest, LocalSftpDestination> {

    private final SftpDestinationService<LocalSftpDestination, LocalSftpDestinationBuilder> sftpDestinationService;

    @Autowired
    LocalSftpDestinationCreateRequestMapper(SftpDestinationService sftpDestinationService) {
        this.sftpDestinationService = sftpDestinationService;
    }

    @Override
    public LocalSftpDestination create(Authorization authorization, LocalSftpDestinationCreateRequest createRequest)
        throws AuthorizationException, SftpDestinationValidationRestException {
        try {
            LocalSftpDestinationBuilder builder =
                sftpDestinationService.create(authorization, getEntityDestinationType(createRequest.getType()));
            createRequest.getName().ifPresent(name -> builder.withName(name));
            createRequest.getPartnerKeyId().ifPresent(keyId -> builder.withPartnerKeyId(Id.valueOf(keyId)));
            createRequest.getExtoleKeyId().ifPresent(keyId -> builder.withExtoleKeyId(Id.valueOf(keyId)));
            createRequest.getUsername().ifPresent(username -> builder.withUsername(username));
            createRequest.getFileProcessingEnabled().ifPresent(
                fileProcessingEnabled -> builder.withFileProcessingEnabled(fileProcessingEnabled.booleanValue()));
            if (createRequest.getKeyIds() != null) {
                builder.withKeyIds(createRequest.getKeyIds().stream()
                    .filter(Objects::nonNull)
                    .map(Id::<ClientKey>valueOf).collect(Collectors.toSet()));
            }
            return builder.save();
        } catch (SftpDestinationInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (SftpDestinationNameAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (SftpDestinationInvalidUsernameException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_LOCAL_USERNAME)
                .addParameter("username", e.getUsername())
                .withCause(e)
                .build();
        } catch (SftpDestinationServerUsernameAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.DUPLICATED_USERNAME)
                .addParameter("username", e.getUsername())
                .withCause(e)
                .build();
        } catch (SftpDestinationMissingKeyIdException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.MISSING_KEY_ID)
                .withCause(e)
                .build();
        } catch (SftpDestinationClientKeyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.CLIENT_KEY_NOT_FOUND)
                .addParameter("client_key_id", e.getClientKeyId())
                .withCause(e)
                .build();
        } catch (SftpDestinationInvalidKeyTypeException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_KEY_TYPE)
                .addParameter("client_key_id", e.getClientKeyId())
                .addParameter("type", e.getType())
                .addParameter("supported_types", e.getSupportedTypes())
                .withCause(e)
                .build();
        } catch (SftpDestinationInvalidKeyContentException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_KEY_CONTENT)
                .addParameter("client_key_id", e.getClientKeyId())
                .withCause(e)
                .build();
        } catch (SftpDestinationBuildException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SftpDestinationType getType() {
        return SftpDestinationType.LOCAL;
    }

    private com.extole.model.entity.client.sftp.SftpDestinationType
        getEntityDestinationType(SftpDestinationType type) {
        return com.extole.model.entity.client.sftp.SftpDestinationType.valueOf(type.name());
    }
}
