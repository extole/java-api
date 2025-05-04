package com.extole.reporting.rest.impl.fixup.transformation;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.entity.fixup.Fixup;
import com.extole.reporting.rest.fixup.FixupRestException;
import com.extole.reporting.rest.fixup.transformation.ConditionalAliasChangeFixupTransformationCreateRequest;
import com.extole.reporting.rest.fixup.transformation.ConditionalAliasChangeFixupTransformationEndpoints;
import com.extole.reporting.rest.fixup.transformation.ConditionalAliasChangeFixupTransformationResponse;
import com.extole.reporting.rest.fixup.transformation.ConditionalAliasChangeFixupTransformationUpdateRequest;
import com.extole.reporting.rest.fixup.transformation.ConditionalAliasChangeFixupTransformationValidationRestException;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationRestException;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationValidationRestException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationAlreadyExistsException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationNotEditableException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationNotFoundException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationValidationException;
import com.extole.reporting.service.fixup.transformation.alias_change.ConditionalAliasChangeFixupTransformationBuilder;
import com.extole.reporting.service.fixup.transformation.alias_change.ConditionalAliasChangeFixupTransformationService;
import com.extole.reporting.service.fixup.transformation.alias_change.FileAssetMissingColumnConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.FileAssetNotAccessibleConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.FileAssetNotFoundConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.InvalidAliasesToAddColumnNameLengthConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.InvalidAliasesToRemoveColumnNameLengthConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.InvalidClientIdColumnNameLengthConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.InvalidFileAssetFormatConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.InvalidProgramLabelColumnNameLengthConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.InvalidStepNameColumnNameLengthConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.MissingClientIdConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.MissingFileAssetIdConditionalAliasChangeFixupTransformationException;
import com.extole.reporting.service.fixup.transformation.alias_change.NoOpConditionalAliasChangeFixupTransformationException;

@Provider
public class ConditionalAliasChangeFixupTransformationEndpointsImpl
    implements ConditionalAliasChangeFixupTransformationEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ConditionalAliasChangeFixupTransformationService fixupTransformationService;
    private final ConditionalAliasChangeFixupTransformationRestMapper restMapper;

    @Autowired
    public ConditionalAliasChangeFixupTransformationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ConditionalAliasChangeFixupTransformationService fixupTransformationService,
        ConditionalAliasChangeFixupTransformationRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.fixupTransformationService = fixupTransformationService;
        this.restMapper = restMapper;
    }

    @Override
    public ConditionalAliasChangeFixupTransformationResponse getTransformation(String accessToken, String fixupId,
        String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(fixupTransformationService.get(authorization, Id.valueOf(fixupId),
                Id.valueOf(transformationId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .addParameter("fixup_id", e.getFixupId())
                .withCause(e).build();
        } catch (FixupTransformationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.TRANSFORMATION_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_transformation_id", e.getTransformationId())
                .build();
        }
    }

    @Override
    public ConditionalAliasChangeFixupTransformationResponse createTransformation(String accessToken, String fixupId,
        ConditionalAliasChangeFixupTransformationCreateRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ConditionalAliasChangeFixupTransformationValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        return handleExceptions(Id.valueOf(fixupId), () -> {
            ConditionalAliasChangeFixupTransformationBuilder transformationBuilder =
                fixupTransformationService.create(authorization, Id.valueOf(fixupId))
                    .withFileAssetId(Id.valueOf(Strings.nullToEmpty(request.getFileAssetId())))
                    .withClientIdFilterColumn(request.getClientIdFilterColumn());
            request.getStepNameFilterColumn()
                .ifPresent(value -> value.ifPresent(transformationBuilder::withStepNameFilterColumn));
            request.getProgramLabelFilterColumn()
                .ifPresent(value -> value.ifPresent(transformationBuilder::withProgramLabelFilterColumn));
            request.getAliasesToAddColumn()
                .ifPresent(value -> value.ifPresent(transformationBuilder::withAliasesToAddColumn));
            request.getAliasesToRemoveColumn()
                .ifPresent(value -> value.ifPresent(transformationBuilder::withAliasesToRemoveColumn));
            return restMapper.toResponse(transformationBuilder.save());
        });
    }

    @Override
    public ConditionalAliasChangeFixupTransformationResponse updateTransformation(String accessToken, String fixupId,
        String transformationId, ConditionalAliasChangeFixupTransformationUpdateRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ConditionalAliasChangeFixupTransformationValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        return handleExceptions(Id.valueOf(fixupId), () -> {
            try {
                ConditionalAliasChangeFixupTransformationBuilder transformationBuilder =
                    fixupTransformationService.update(authorization, Id.valueOf(fixupId),
                        Id.valueOf(transformationId));
                request.getClientIdFilterColumn()
                    .ifPresent(transformationBuilder::withClientIdFilterColumn);
                request.getProgramLabelFilterColumn()
                    .ifPresent(value -> transformationBuilder.withProgramLabelFilterColumn(value.orElse(null)));
                request.getStepNameFilterColumn()
                    .ifPresent(value -> transformationBuilder.withStepNameFilterColumn(value.orElse(null)));
                request.getAliasesToAddColumn()
                    .ifPresent(value -> transformationBuilder.withAliasesToAddColumn(value.orElse(null)));
                request.getAliasesToRemoveColumn()
                    .ifPresent(value -> transformationBuilder.withAliasesToRemoveColumn(value.orElse(null)));
                request.getFileAssetId().ifPresent(value -> transformationBuilder.withFileAssetId(Id.valueOf(value)));
                return restMapper.toResponse(transformationBuilder.save());
            } catch (FixupTransformationNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                    .withErrorCode(FixupTransformationRestException.TRANSFORMATION_NOT_FOUND)
                    .withCause(e)
                    .addParameter("fixup_id", e.getFixupId())
                    .addParameter("fixup_transformation_id", e.getTransformationId())
                    .build();
            } catch (FixupTransformationNotEditableException e) {
                throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                    .withErrorCode(FixupTransformationRestException.NOT_EDITABLE)
                    .withCause(e).build();
            }
        });
    }

    @Override
    public ConditionalAliasChangeFixupTransformationResponse deleteTransformation(String accessToken, String fixupId,
        String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(fixupTransformationService.delete(authorization, Id.valueOf(fixupId),
                Id.valueOf(transformationId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId()).build();
        } catch (FixupTransformationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.TRANSFORMATION_NOT_FOUND)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_transformation_id", e.getTransformationId())
                .withCause(e).build();
        } catch (FixupRuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (FixupTransformationNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }

    private ConditionalAliasChangeFixupTransformationResponse handleExceptions(Id<Fixup> fixupId,
        Supplier supplier)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ConditionalAliasChangeFixupTransformationValidationRestException {
        try {
            return supplier.execute();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId()).build();
        } catch (FixupTransformationAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationValidationRestException.class)
                .withErrorCode(FixupTransformationValidationRestException.TRANSFORMATION_ALREADY_EXISTS)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (MissingClientIdConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(ConditionalAliasChangeFixupTransformationValidationRestException.CLIENT_ID_MISSING)
                .withCause(e).build();
        } catch (InvalidClientIdColumnNameLengthConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(ConditionalAliasChangeFixupTransformationValidationRestException.CLIENT_ID_FILTER_LENGTH)
                .withCause(e).build();
        } catch (InvalidProgramLabelColumnNameLengthConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.PROGRAM_LABEL_FILTER_LENGTH)
                .withCause(e).build();
        } catch (InvalidStepNameColumnNameLengthConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.STEP_NAME_FILTER_LENGTH)
                .withCause(e).build();
        } catch (InvalidAliasesToAddColumnNameLengthConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.ALIASES_TO_ADD_LENGTH)
                .withCause(e).build();
        } catch (InvalidAliasesToRemoveColumnNameLengthConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.ALIASES_TO_REMOVE_LENGTH)
                .withCause(e).build();
        } catch (MissingFileAssetIdConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.FILE_ASSET_ID_MISSING)
                .withCause(e).build();
        } catch (FileAssetNotFoundConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.FILE_ASSET_NOT_FOUND)
                .withCause(e).build();
        } catch (FileAssetNotAccessibleConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.FILE_ASSET_NOT_ACCESSIBLE)
                .withCause(e).build();
        } catch (InvalidFileAssetFormatConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.FILE_ASSET_INVALID_FORMAT)
                .withCause(e).build();
        } catch (FileAssetMissingColumnConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.FILE_ASSET_MISSING_COLUMN)
                .addParameter("column", e.getMissingColumn())
                .withCause(e).build();
        } catch (NoOpConditionalAliasChangeFixupTransformationException e) {
            throw RestExceptionBuilder
                .newBuilder(ConditionalAliasChangeFixupTransformationValidationRestException.class)
                .withErrorCode(
                    ConditionalAliasChangeFixupTransformationValidationRestException.NO_OPERATION)
                .withCause(e).build();
        } catch (Exception e) {
            throw RestExceptionBuilder
                .newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private interface Supplier {
        ConditionalAliasChangeFixupTransformationResponse execute()
            throws AuthorizationException, FixupNotFoundException, FixupTransformationAlreadyExistsException,
            FixupTransformationValidationException, FixupTransformationNotFoundException,
            FixupTransformationNotEditableException, FixupTransformationRestException;
    }
}
