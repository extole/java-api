package com.extole.reporting.rest.impl.fixup.transformation;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.reporting.rest.fixup.FixupRestException;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationRestException;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationValidationRestException;
import com.extole.reporting.rest.fixup.transformation.ScriptFixupTransformationEndpoints;
import com.extole.reporting.rest.fixup.transformation.ScriptFixupTransformationRequest;
import com.extole.reporting.rest.fixup.transformation.ScriptFixupTransformationResponse;
import com.extole.reporting.rest.fixup.transformation.ScriptFixupTransformationValidationRestException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationAlreadyExistsException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationNotEditableException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationNotFoundException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationValidationException;
import com.extole.reporting.service.fixup.transformation.ScriptFixupTransformationBuilder;
import com.extole.reporting.service.fixup.transformation.ScriptFixupTransformationService;

@Provider
public class ScriptFixupTransformationEndpointsImpl implements ScriptFixupTransformationEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ScriptFixupTransformationService scriptFixupTransformationService;
    private final ScriptFixupTransformationRestMapper restMapper;

    @Autowired
    public ScriptFixupTransformationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ScriptFixupTransformationService scriptFixupTransformationService,
        ScriptFixupTransformationRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.scriptFixupTransformationService = scriptFixupTransformationService;
        this.restMapper = restMapper;
    }

    @Override
    public ScriptFixupTransformationResponse getTransformation(String accessToken, String fixupId,
        String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(
                scriptFixupTransformationService.get(authorization, Id.valueOf(fixupId), Id.valueOf(transformationId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .addParameter("fixup_id", e.getFixupId())
                .withCause(e)
                .build();
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
    public ScriptFixupTransformationResponse createTransformation(String accessToken, String fixupId,
        ScriptFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ScriptFixupTransformationValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ScriptFixupTransformationBuilder transformationBuilder =
                scriptFixupTransformationService.create(authorization, Id.valueOf(fixupId));
            transformationBuilder.withScript(request.getScript());
            return restMapper.toResponse(transformationBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .build();
        } catch (FixupTransformationAlreadyExistsException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationValidationRestException.class)
                .withErrorCode(FixupTransformationValidationRestException.TRANSFORMATION_ALREADY_EXISTS)
                .addParameter("fixup_id", fixupId)
                .withCause(e).build();
        } catch (FixupTransformationValidationException e) {
            throw RestExceptionBuilder.newBuilder(ScriptFixupTransformationValidationRestException.class)
                .withErrorCode(ScriptFixupTransformationValidationRestException.TRANSFORMATION_SCRIPT_INVALID)
                .withCause(e).build();
        }
    }

    @Override
    public ScriptFixupTransformationResponse updateTransformation(String accessToken, String fixupId,
        String transformationId, ScriptFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupTransformationRestException,
        ScriptFixupTransformationValidationRestException, FixupRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ScriptFixupTransformationBuilder transformationBuilder =
                scriptFixupTransformationService.update(authorization, Id.valueOf(fixupId),
                    Id.valueOf(transformationId));
            if (request.getScript() != null) {
                transformationBuilder.withScript(request.getScript());
            }
            return restMapper.toResponse(transformationBuilder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupTransformationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.TRANSFORMATION_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_transformation_id", e.getTransformationId())
                .build();
        } catch (FixupTransformationValidationException e) {
            throw RestExceptionBuilder.newBuilder(ScriptFixupTransformationValidationRestException.class)
                .withErrorCode(ScriptFixupTransformationValidationRestException.TRANSFORMATION_SCRIPT_INVALID)
                .withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .build();
        } catch (FixupTransformationNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }

    @Override
    public ScriptFixupTransformationResponse deleteTransformation(String accessToken, String fixupId,
        String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(
                scriptFixupTransformationService.delete(authorization, Id.valueOf(fixupId),
                    Id.valueOf(transformationId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (FixupNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupRestException.class)
                .withErrorCode(FixupRestException.FIXUP_NOT_FOUND)
                .withCause(e)
                .addParameter("fixup_id", e.getFixupId())
                .build();
        } catch (FixupTransformationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.TRANSFORMATION_NOT_FOUND)
                .addParameter("fixup_id", e.getFixupId())
                .addParameter("fixup_transformation_id", e.getTransformationId())
                .withCause(e)
                .build();
        } catch (FixupRuntimeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (FixupTransformationNotEditableException e) {
            throw RestExceptionBuilder.newBuilder(FixupTransformationRestException.class)
                .withErrorCode(FixupTransformationRestException.NOT_EDITABLE)
                .withCause(e).build();
        }
    }
}
