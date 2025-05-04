package com.extole.client.rest.impl.property;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.property.DefaultPropertyEndpoints;
import com.extole.client.rest.property.PropertyCreationRequest;
import com.extole.client.rest.property.PropertyCreationRestException;
import com.extole.client.rest.property.PropertyResponse;
import com.extole.client.rest.property.PropertyRestException;
import com.extole.client.rest.property.PropertyUpdateRequest;
import com.extole.client.rest.property.PropertyValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.property.Property;
import com.extole.model.service.property.ClientPropertyBuilder;
import com.extole.model.service.property.ClientPropertyService;
import com.extole.model.service.property.PropertyDuplicateException;
import com.extole.model.service.property.PropertyInvalidNameException;
import com.extole.model.service.property.PropertyNameLengthException;
import com.extole.model.service.property.PropertyNotFoundException;
import com.extole.model.service.property.PropertyNullValueException;
import com.extole.model.service.property.PropertyValueTooLongException;

@Provider
public class DefaultPropertyEndpointsImpl implements DefaultPropertyEndpoints {

    private final ClientPropertyService clientPropertyService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public DefaultPropertyEndpointsImpl(ClientPropertyService clientPropertyService,
        ClientAuthorizationProvider authorizationProvider) {
        this.clientPropertyService = clientPropertyService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<PropertyResponse> list(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<Property> properties = clientPropertyService.listDefault(authorization);
            return properties.stream().map(
                property -> new PropertyResponse(property.getName(), property.getValue()))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PropertyResponse create(String accessToken, PropertyCreationRequest request)
        throws UserAuthorizationRestException, PropertyCreationRestException,
        PropertyValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientPropertyBuilder builder =
                clientPropertyService.createDefault(authorization).withName(request.getName())
                    .withValue(request.getValue());
            Property property = builder.save();
            return new PropertyResponse(property.getName(), property.getValue());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PropertyDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(PropertyCreationRestException.class)
                .withErrorCode(PropertyCreationRestException.DUPLICATE_PROPERTY)
                .addParameter("name", request.getName())
                .withCause(e)
                .build();
        } catch (PropertyNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.NAME_INVALID_LENGTH)
                .addParameter("name", request.getName())
                .withCause(e)
                .build();
        } catch (PropertyInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.INVALID_NAME)
                .addParameter("name", request.getName())
                .withCause(e)
                .build();
        } catch (PropertyValueTooLongException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.VALUE_TOO_LONG)
                .addParameter("value", request.getValue())
                .withCause(e)
                .build();
        } catch (PropertyNullValueException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.NULL_VALUE)
                .addParameter("value", request.getValue())
                .withCause(e)
                .build();
        }
    }

    @Override
    public PropertyResponse get(String accessToken, String name) throws PropertyRestException {
        try {
            Property property = clientPropertyService.getDefault(name);
            return new PropertyResponse(property.getName(), property.getValue());
        } catch (PropertyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PropertyRestException.class)
                .withErrorCode(PropertyRestException.INVALID_NAME)
                .addParameter("name", name)
                .withCause(e)
                .build();
        }
    }

    @Override
    public PropertyResponse update(String accessToken, String name, PropertyUpdateRequest request)
        throws UserAuthorizationRestException, PropertyRestException, PropertyValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientPropertyBuilder builder = clientPropertyService.updateDefault(authorization, name);
            if (!Strings.isNullOrEmpty(request.getValue())) {
                builder.withValue(request.getValue());
            }
            Property property = builder.save();
            return new PropertyResponse(property.getName(), property.getValue());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PropertyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PropertyRestException.class)
                .withErrorCode(PropertyRestException.INVALID_NAME)
                .addParameter("name", name)
                .withCause(e)
                .build();
        } catch (PropertyNullValueException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.NULL_VALUE)
                .addParameter("value", request.getValue())
                .withCause(e)
                .build();
        } catch (PropertyValueTooLongException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.VALUE_TOO_LONG)
                .addParameter("value", request.getValue())
                .withCause(e)
                .build();
        } catch (PropertyDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public SuccessResponse delete(String accessToken, String name)
        throws UserAuthorizationRestException, PropertyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            clientPropertyService.updateDefault(authorization, name).withDeleted().save();
            return SuccessResponse.SUCCESS;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PropertyNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PropertyRestException.class)
                .withErrorCode(PropertyRestException.INVALID_NAME)
                .addParameter("name", name)
                .withCause(e)
                .build();
        } catch (PropertyDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }
}
