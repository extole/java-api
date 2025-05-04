package com.extole.client.rest.impl.source;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.source.SourceMappingEndpoints;
import com.extole.client.rest.source.SourceMappingRequest;
import com.extole.client.rest.source.SourceMappingResponse;
import com.extole.client.rest.source.SourceMappingRestException;
import com.extole.client.rest.source.SourceMappingValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.dimension.DimensionMapping;
import com.extole.model.service.dimension.DimensionMappingBuilder;
import com.extole.model.service.dimension.DimensionMappingService;
import com.extole.model.service.dimension.exception.DimensionMappingDeleteException;
import com.extole.model.service.dimension.exception.DimensionMappingDuplicateException;
import com.extole.model.service.dimension.exception.DimensionMappingInvalidDimensionException;
import com.extole.model.service.dimension.exception.DimensionMappingInvalidProgramLabelException;
import com.extole.model.service.dimension.exception.DimensionMappingInvalidValueFromException;
import com.extole.model.service.dimension.exception.DimensionMappingInvalidValueToException;
import com.extole.model.service.dimension.exception.DimensionMappingNotFoundException;

@Provider
public final class SourceMappingEndpointsImpl implements SourceMappingEndpoints {
    private final DimensionMappingService dimensionMappingService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    private SourceMappingEndpointsImpl(DimensionMappingService dimensionMappingService,
        ClientAuthorizationProvider authorizationProvider) {
        this.dimensionMappingService = dimensionMappingService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<SourceMappingResponse> get(String accessToken) throws UserAuthorizationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            return dimensionMappingService.findAll(authorization, Optional.of("source"))
                .stream()
                .map(this::toSourceMappingResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public SourceMappingResponse getById(String accessToken, String sourceMappingId)
        throws UserAuthorizationRestException, SourceMappingRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            return toSourceMappingResponse(
                dimensionMappingService.findById(authorization, Id.valueOf(sourceMappingId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingRestException.class)
                .withErrorCode(SourceMappingRestException.SOURCE_MAPPING_NOT_FOUND)
                .addParameter("identity_id", sourceMappingId)
                .build();
        }
    }

    @Override
    public SourceMappingResponse create(String accessToken, SourceMappingRequest request)
        throws UserAuthorizationRestException, SourceMappingValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            DimensionMappingBuilder builder = dimensionMappingService.create(authorization);
            request.getProgramLabel().ifPresent(label -> builder.withProgramLabel(label));
            request.getSourceFrom().ifPresent((sourceFrom -> builder.withValueFrom(sourceFrom)));
            request.getSourceTo().ifPresent((sourceTo -> builder.withValueTo(sourceTo)));
            builder.withDimension("source");
            return toSourceMappingResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingInvalidValueToException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.INVALID_SOURCE_TO)
                .addParameter("source_to", request.getSourceFrom())
                .build();
        } catch (DimensionMappingInvalidValueFromException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.INVALID_SOURCE_FROM)
                .addParameter("source_from", request.getSourceTo())
                .build();
        } catch (DimensionMappingInvalidProgramLabelException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.INVALID_PROGRAM_LABEL)
                .addParameter("program_label", request.getProgramLabel())
                .build();
        } catch (DimensionMappingDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.DUPLICATED_MAPPING)
                .addParameter("program_label", e.getProgramLabel().orElse(StringUtils.EMPTY))
                .addParameter("source_from", e.getValueFrom())
                .withCause(e)
                .build();
        } catch (DimensionMappingInvalidDimensionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SourceMappingResponse update(String accessToken, String sourceMappingId, SourceMappingRequest request)
        throws UserAuthorizationRestException, SourceMappingValidationRestException, SourceMappingRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            DimensionMappingBuilder builder =
                dimensionMappingService.update(authorization, Id.valueOf(sourceMappingId));
            request.getProgramLabel().ifPresent(label -> builder.withProgramLabel(label));
            request.getSourceFrom().ifPresent((sourceFrom -> builder.withValueFrom(sourceFrom)));
            request.getSourceTo().ifPresent((sourceTo -> builder.withValueTo(sourceTo)));
            return toSourceMappingResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingInvalidValueToException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.INVALID_SOURCE_TO)
                .addParameter("source_to", request.getSourceFrom())
                .build();
        } catch (DimensionMappingInvalidValueFromException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.INVALID_SOURCE_FROM)
                .addParameter("source_from", request.getSourceTo())
                .build();
        } catch (DimensionMappingInvalidProgramLabelException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.INVALID_PROGRAM_LABEL)
                .addParameter("program_label", request.getProgramLabel())
                .build();
        } catch (DimensionMappingNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingRestException.class)
                .withErrorCode(SourceMappingRestException.SOURCE_MAPPING_NOT_FOUND)
                .addParameter("identity_id", sourceMappingId)
                .build();
        } catch (DimensionMappingDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingValidationRestException.class)
                .withErrorCode(SourceMappingValidationRestException.DUPLICATED_MAPPING)
                .addParameter("program_label", e.getProgramLabel().orElse(StringUtils.EMPTY))
                .addParameter("source_from", e.getValueFrom())
                .withCause(e)
                .build();
        } catch (DimensionMappingInvalidDimensionException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SourceMappingResponse delete(String accessToken, String sourceMappingId)
        throws UserAuthorizationRestException, SourceMappingRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            return toSourceMappingResponse(dimensionMappingService.delete(authorization, Id.valueOf(sourceMappingId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SourceMappingRestException.class)
                .withErrorCode(SourceMappingRestException.SOURCE_MAPPING_NOT_FOUND)
                .addParameter("identity_id", sourceMappingId)
                .build();
        } catch (DimensionMappingDeleteException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private SourceMappingResponse toSourceMappingResponse(DimensionMapping sourceMapping) {
        return new SourceMappingResponse(sourceMapping.getId().getValue(), sourceMapping.getProgramLabel().orElse(null),
            sourceMapping.getValueFrom(), sourceMapping.getValueTo());
    }
}
