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
import com.extole.reporting.rest.fixup.transformation.ContainerFixupTransformationEndpoints;
import com.extole.reporting.rest.fixup.transformation.ContainerFixupTransformationRequest;
import com.extole.reporting.rest.fixup.transformation.ContainerFixupTransformationResponse;
import com.extole.reporting.rest.fixup.transformation.ContainerFixupTransformationValidationRestException;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationRestException;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationValidationRestException;
import com.extole.reporting.service.fixup.FixupNotFoundException;
import com.extole.reporting.service.fixup.FixupRuntimeException;
import com.extole.reporting.service.fixup.transformation.ContainerFixupTransformationBuilder;
import com.extole.reporting.service.fixup.transformation.ContainerFixupTransformationService;
import com.extole.reporting.service.fixup.transformation.FixupTransformationAlreadyExistsException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationNotEditableException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationNotFoundException;
import com.extole.reporting.service.fixup.transformation.FixupTransformationValidationException;
import com.extole.sandbox.Container;

@Provider
public class ContainerFixupTransformationEndpointsImpl implements ContainerFixupTransformationEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ContainerFixupTransformationService containerFixupTransformationService;
    private final ContainerFixupTransformationRestMapper restMapper;

    @Autowired
    public ContainerFixupTransformationEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ContainerFixupTransformationService containerFixupTransformationService,
        ContainerFixupTransformationRestMapper restMapper) {
        this.authorizationProvider = authorizationProvider;
        this.containerFixupTransformationService = containerFixupTransformationService;
        this.restMapper = restMapper;
    }

    @Override
    public ContainerFixupTransformationResponse getTransformation(String accessToken, String fixupId,
        String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(containerFixupTransformationService.get(authorization, Id.valueOf(fixupId),
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
    public ContainerFixupTransformationResponse createTransformation(String accessToken, String fixupId,
        ContainerFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationValidationRestException,
        ContainerFixupTransformationValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ContainerFixupTransformationBuilder transformationBuilder =
                containerFixupTransformationService.create(authorization, Id.valueOf(fixupId));
            transformationBuilder.withContainer(new Container(request.getContainer()));
            return restMapper.toResponse(transformationBuilder.save());
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
        } catch (FixupTransformationValidationException e) {
            throw RestExceptionBuilder.newBuilder(ContainerFixupTransformationValidationRestException.class)
                .withErrorCode(ContainerFixupTransformationValidationRestException.TRANSFORMATION_CONTAINER_INVALID)
                .withCause(e).build();
        }
    }

    @Override
    public ContainerFixupTransformationResponse updateTransformation(String accessToken, String fixupId,
        String transformationId, ContainerFixupTransformationRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException,
        ContainerFixupTransformationValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ContainerFixupTransformationBuilder transformationBuilder =
                containerFixupTransformationService.update(authorization, Id.valueOf(fixupId),
                    Id.valueOf(transformationId));
            if (request.getContainer() != null) {
                transformationBuilder.withContainer(new Container(request.getContainer()));
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
            throw RestExceptionBuilder.newBuilder(ContainerFixupTransformationValidationRestException.class)
                .withErrorCode(ContainerFixupTransformationValidationRestException.TRANSFORMATION_CONTAINER_INVALID)
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
    public ContainerFixupTransformationResponse deleteTransformation(String accessToken, String fixupId,
        String transformationId)
        throws UserAuthorizationRestException, FixupRestException, FixupTransformationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return restMapper.toResponse(containerFixupTransformationService.delete(authorization, Id.valueOf(fixupId),
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
}
