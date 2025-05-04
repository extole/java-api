package com.extole.client.rest.impl.sftp.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.ExternalSftpDestinationCreateRequest;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.ExternalSftpDestination;
import com.extole.model.service.client.sftp.ExternalSftpDestinationBuilder;
import com.extole.model.service.client.sftp.SftpDestinationBuildException;
import com.extole.model.service.client.sftp.SftpDestinationClientKeyNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidDropboxPathException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidHostException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidKeyTypeException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidNameException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidUsernameException;
import com.extole.model.service.client.sftp.SftpDestinationMissingHostException;
import com.extole.model.service.client.sftp.SftpDestinationMissingKeyIdException;
import com.extole.model.service.client.sftp.SftpDestinationMissingUsernameException;
import com.extole.model.service.client.sftp.SftpDestinationNameAlreadyExistsException;
import com.extole.model.service.client.sftp.SftpDestinationOutOfBoundsPortException;
import com.extole.model.service.client.sftp.SftpDestinationService;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
final class ExternalSftpDestinationCreateRequestMapper
    implements SftpDestinationCreateRequestMapper<ExternalSftpDestinationCreateRequest, ExternalSftpDestination> {

    private final SftpDestinationService<ExternalSftpDestination,
        ExternalSftpDestinationBuilder> sftpDestinationService;

    @Autowired
    ExternalSftpDestinationCreateRequestMapper(SftpDestinationService sftpDestinationService) {
        this.sftpDestinationService = sftpDestinationService;
    }

    @Override
    public ExternalSftpDestination create(Authorization authorization,
        ExternalSftpDestinationCreateRequest createRequest)
        throws AuthorizationException, SftpDestinationValidationRestException {
        ExternalSftpDestinationBuilder builder =
            sftpDestinationService.create(authorization, getEntityDestinationType(createRequest.getType()));
        try {
            if (createRequest.getUsername() != null) {
                builder.withUsername(createRequest.getUsername());
            }
            if (createRequest.getHost() != null) {
                builder.withHost(createRequest.getHost());
            }
            if (createRequest.getKeyId() != null) {
                builder.withKeyId(Id.valueOf(createRequest.getKeyId()));
            }
            createRequest.getName().ifPresent(name -> builder.withName(name));
            createRequest.getPartnerKeyId().ifPresent(keyId -> builder.withPartnerKeyId(Id.valueOf(keyId)));
            createRequest.getExtoleKeyId().ifPresent(keyId -> builder.withExtoleKeyId(Id.valueOf(keyId)));
            createRequest.getDropboxPath().ifPresent(dropboxPath -> builder.withDropboxPath(dropboxPath));
            createRequest.getPort().ifPresent(port -> builder.withPort(port.intValue()));
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
        } catch (SftpDestinationMissingUsernameException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.MISSING_USERNAME)
                .withCause(e)
                .build();
        } catch (SftpDestinationInvalidUsernameException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_EXTERNAL_USERNAME)
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
        } catch (SftpDestinationInvalidDropboxPathException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_DROPBOX_PATH)
                .addParameter("path", e.getPath())
                .withCause(e)
                .build();
        } catch (SftpDestinationMissingHostException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.MISSING_HOST)
                .withCause(e)
                .build();
        } catch (SftpDestinationInvalidHostException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_HOST)
                .addParameter("host", e.getHost())
                .withCause(e)
                .build();
        } catch (SftpDestinationOutOfBoundsPortException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.INVALID_PORT)
                .addParameter("port", Integer.valueOf(e.getPort()))
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
        return SftpDestinationType.EXTERNAL;
    }

    private com.extole.model.entity.client.sftp.SftpDestinationType
        getEntityDestinationType(SftpDestinationType type) {
        return com.extole.model.entity.client.sftp.SftpDestinationType.valueOf(type.name());
    }
}
