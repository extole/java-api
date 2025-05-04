package com.extole.client.rest.impl.sftp.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.ExternalSftpDestinationUpdateRequest;
import com.extole.client.rest.sftp.SftpDestinationRestException;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.ExternalSftpDestination;
import com.extole.model.entity.client.sftp.SftpDestination;
import com.extole.model.service.client.sftp.ExternalSftpDestinationBuilder;
import com.extole.model.service.client.sftp.SftpDestinationBuildException;
import com.extole.model.service.client.sftp.SftpDestinationClientKeyNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidDropboxPathException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidHostException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidKeyTypeException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidNameException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidUsernameException;
import com.extole.model.service.client.sftp.SftpDestinationNameAlreadyExistsException;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationOutOfBoundsPortException;
import com.extole.model.service.client.sftp.SftpDestinationService;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
final class ExternalSftpDestinationUpdateRequestMapper implements
    SftpDestinationUpdateRequestMapper<ExternalSftpDestinationUpdateRequest, ExternalSftpDestination> {

    private final SftpDestinationService<ExternalSftpDestination,
        ExternalSftpDestinationBuilder> sftpDestinationService;

    @Autowired
    ExternalSftpDestinationUpdateRequestMapper(SftpDestinationService sftpDestinationService) {
        this.sftpDestinationService = sftpDestinationService;
    }

    @Override
    public ExternalSftpDestination update(Authorization authorization, Id<SftpDestination> sftpDestinationId,
        ExternalSftpDestinationUpdateRequest updateRequest) throws AuthorizationException, SftpDestinationRestException,
        SftpDestinationValidationRestException {
        try {
            ExternalSftpDestinationBuilder builder = sftpDestinationService.update(authorization, sftpDestinationId);
            updateRequest.getName().ifPresent(name -> builder.withName(name));
            updateRequest.getUsername().ifPresent(username -> builder.withUsername(username));
            updateRequest.getKeyId().ifPresent(keyId -> builder.withKeyId(Id.valueOf(keyId)));
            updateRequest.getPartnerKeyId().ifPresent(keyId -> builder.withPartnerKeyId(Id.valueOf(keyId)));
            updateRequest.getExtoleKeyId().ifPresent(keyId -> builder.withExtoleKeyId(Id.valueOf(keyId)));
            updateRequest.getDropboxPath().ifPresent(dropboxPath -> builder.withDropboxPath(dropboxPath));
            updateRequest.getHost().ifPresent(host -> builder.withHost(host));
            updateRequest.getPort().ifPresent(port -> builder.withPort(port.intValue()));
            return builder.save();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.NOT_FOUND)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .withCause(e)
                .build();
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
                .withErrorCode(SftpDestinationValidationRestException.INVALID_EXTERNAL_USERNAME)
                .addParameter("username", e.getUsername())
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
}
