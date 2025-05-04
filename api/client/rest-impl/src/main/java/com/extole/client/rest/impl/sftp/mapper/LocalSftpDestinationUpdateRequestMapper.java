package com.extole.client.rest.impl.sftp.mapper;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.LocalSftpDestinationUpdateRequest;
import com.extole.client.rest.sftp.SftpDestinationRestException;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.sftp.LocalSftpDestination;
import com.extole.model.entity.client.sftp.SftpDestination;
import com.extole.model.service.client.sftp.LocalSftpDestinationBuilder;
import com.extole.model.service.client.sftp.SftpDestinationBuildException;
import com.extole.model.service.client.sftp.SftpDestinationClientKeyNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidKeyContentException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidKeyTypeException;
import com.extole.model.service.client.sftp.SftpDestinationInvalidNameException;
import com.extole.model.service.client.sftp.SftpDestinationMissingKeyIdException;
import com.extole.model.service.client.sftp.SftpDestinationNameAlreadyExistsException;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationService;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
final class LocalSftpDestinationUpdateRequestMapper implements
    SftpDestinationUpdateRequestMapper<LocalSftpDestinationUpdateRequest, LocalSftpDestination> {

    private final SftpDestinationService<LocalSftpDestination, LocalSftpDestinationBuilder> sftpDestinationService;

    LocalSftpDestinationUpdateRequestMapper(SftpDestinationService sftpDestinationService) {
        this.sftpDestinationService = sftpDestinationService;
    }

    @Override
    public LocalSftpDestination update(Authorization authorization, Id<SftpDestination> sftpDestinationId,
        LocalSftpDestinationUpdateRequest updateRequest) throws AuthorizationException, SftpDestinationRestException,
        SftpDestinationValidationRestException {
        try {
            LocalSftpDestinationBuilder builder = sftpDestinationService.update(authorization, sftpDestinationId);
            updateRequest.getName().ifPresent(name -> builder.withName(name));
            updateRequest.getKeyIds().ifPresent(
                keyIds -> builder.withKeyIds(keyIds.stream()
                    .filter(Objects::nonNull)
                    .map(Id::<ClientKey>valueOf)
                    .collect(Collectors.toSet())));
            updateRequest.getPartnerKeyId().ifPresent(keyId -> builder.withPartnerKeyId(Id.valueOf(keyId)));
            updateRequest.getExtoleKeyId().ifPresent(keyId -> builder.withExtoleKeyId(Id.valueOf(keyId)));
            updateRequest.getFileProcessingEnabled().ifPresent(
                fileProcessingEnabled -> builder.withFileProcessingEnabled(fileProcessingEnabled.booleanValue()));
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
        } catch (SftpDestinationMissingKeyIdException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationValidationRestException.class)
                .withErrorCode(SftpDestinationValidationRestException.MISSING_KEY_ID)
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
}
