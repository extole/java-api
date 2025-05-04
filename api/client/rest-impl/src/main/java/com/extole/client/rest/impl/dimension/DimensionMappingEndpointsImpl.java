package com.extole.client.rest.impl.dimension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.dimension.DimensionMappingEndpoints;
import com.extole.client.rest.dimension.DimensionMappingRequest;
import com.extole.client.rest.dimension.DimensionMappingResponse;
import com.extole.client.rest.dimension.DimensionMappingRestException;
import com.extole.client.rest.dimension.DimensionMappingValidationRestException;
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
public final class DimensionMappingEndpointsImpl implements DimensionMappingEndpoints {
    private final DimensionMappingService dimensionMappingService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    private DimensionMappingEndpointsImpl(DimensionMappingService dimensionMappingService,
        ClientAuthorizationProvider authorizationProvider) {
        this.dimensionMappingService = dimensionMappingService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<DimensionMappingResponse> list(String accessToken, String dimension)
        throws UserAuthorizationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            return dimensionMappingService
                .findAll(authorization, Strings.isNullOrEmpty(dimension) ? Optional.empty() : Optional.of(dimension))
                .stream()
                .map(this::toDimensionMappingResponse)
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        }
    }

    @Override
    public DimensionMappingResponse getById(String accessToken, String dimensionMappingId)
        throws UserAuthorizationRestException, DimensionMappingRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            return toDimensionMappingResponse(
                dimensionMappingService.findById(authorization, Id.valueOf(dimensionMappingId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingRestException.class)
                .withErrorCode(DimensionMappingRestException.DIMENSION_MAPPING_NOT_FOUND)
                .addParameter("dimension_mapping_id", dimensionMappingId)
                .build();
        }
    }

    @Override
    public DimensionMappingResponse create(String accessToken, DimensionMappingRequest request)
        throws UserAuthorizationRestException, DimensionMappingValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            DimensionMappingBuilder builder = dimensionMappingService.create(authorization);
            request.getProgramLabel().ifPresent(builder::withProgramLabel);
            builder.withDimension(request.getDimension());
            builder.withValueFrom(request.getValueFrom());
            builder.withValueTo(request.getValueTo());

            return toDimensionMappingResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingInvalidValueToException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_VALUE_TO)
                .addParameter("value_to", request.getValueTo())
                .build();
        } catch (DimensionMappingInvalidValueFromException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_VALUE_FROM)
                .addParameter("value_from", request.getValueFrom())
                .build();
        } catch (DimensionMappingInvalidProgramLabelException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_PROGRAM_LABEL)
                .addParameter("program_label", request.getProgramLabel().orElse(null))
                .build();
        } catch (DimensionMappingInvalidDimensionException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_DIMENSION)
                .addParameter("dimension", request.getDimension())
                .build();
        } catch (DimensionMappingDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.DUPLICATE_DIMENSION)
                .addParameter("dimension", request.getDimension())
                .addParameter("program_label", request.getProgramLabel().orElse(null))
                .addParameter("value_from", request.getValueFrom())
                .build();
        }
    }

    @Override
    public DimensionMappingResponse update(String accessToken, String dimensionMappingId,
        DimensionMappingRequest request)
        throws UserAuthorizationRestException, DimensionMappingValidationRestException, DimensionMappingRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            DimensionMappingBuilder builder =
                dimensionMappingService.update(authorization, Id.valueOf(dimensionMappingId));
            request.getProgramLabel().ifPresent(builder::withProgramLabel);
            if (!Strings.isNullOrEmpty(request.getDimension())) {
                builder.withDimension(request.getDimension());
            }
            if (!Strings.isNullOrEmpty(request.getValueFrom())) {
                builder.withValueFrom(request.getValueFrom());
            }
            if (!Strings.isNullOrEmpty(request.getValueTo())) {
                builder.withValueTo(request.getValueTo());
            }
            return toDimensionMappingResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingInvalidValueToException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_VALUE_TO)
                .addParameter("value_to", request.getValueTo())
                .build();
        } catch (DimensionMappingInvalidValueFromException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_VALUE_FROM)
                .addParameter("value_from", request.getValueFrom())
                .build();
        } catch (DimensionMappingInvalidProgramLabelException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_PROGRAM_LABEL)
                .addParameter("program_label", request.getProgramLabel().orElse(null))
                .build();
        } catch (DimensionMappingNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingRestException.class)
                .withErrorCode(DimensionMappingRestException.DIMENSION_MAPPING_NOT_FOUND)
                .addParameter("dimension_mapping_id", dimensionMappingId)
                .build();
        } catch (DimensionMappingInvalidDimensionException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.INVALID_DIMENSION)
                .addParameter("dimension", request.getDimension())
                .build();
        } catch (DimensionMappingDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingValidationRestException.class)
                .withErrorCode(DimensionMappingValidationRestException.DUPLICATE_DIMENSION)
                .addParameter("dimension", request.getDimension())
                .addParameter("program_label", request.getProgramLabel().orElse(null))
                .addParameter("value_from", request.getValueFrom())
                .build();
        }
    }

    @Override
    public DimensionMappingResponse delete(String accessToken, String dimensionMappingId)
        throws UserAuthorizationRestException, DimensionMappingRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            return toDimensionMappingResponse(
                dimensionMappingService.delete(authorization, Id.valueOf(dimensionMappingId)));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).build();
        } catch (DimensionMappingNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(DimensionMappingRestException.class)
                .withErrorCode(DimensionMappingRestException.DIMENSION_MAPPING_NOT_FOUND)
                .addParameter("dimension_mapping_id", dimensionMappingId)
                .build();
        } catch (DimensionMappingDeleteException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .build();
        }
    }

    private DimensionMappingResponse toDimensionMappingResponse(DimensionMapping dimensionMapping) {
        return new DimensionMappingResponse(dimensionMapping.getId().getValue(), dimensionMapping.getProgramLabel(),
            dimensionMapping.getDimension(), dimensionMapping.getValueFrom(), dimensionMapping.getValueTo());
    }
}
