package com.extole.client.rest.impl.property;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.property.ClientPropertyCreationRequest;
import com.extole.client.rest.property.ClientPropertyEndpoints;
import com.extole.client.rest.property.ClientPropertyResponse;
import com.extole.client.rest.property.ClientPropertyScope;
import com.extole.client.rest.property.ClientPropertyUpdateRequest;
import com.extole.client.rest.property.PropertyCreationRestException;
import com.extole.client.rest.property.PropertyRestException;
import com.extole.client.rest.property.PropertyValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.service.property.ClientProperty;
import com.extole.model.service.property.ClientPropertyBuilder;
import com.extole.model.service.property.ClientPropertyService;
import com.extole.model.service.property.PropertyDuplicateException;
import com.extole.model.service.property.PropertyInvalidNameException;
import com.extole.model.service.property.PropertyNameLengthException;
import com.extole.model.service.property.PropertyNotFoundException;
import com.extole.model.service.property.PropertyNullValueException;
import com.extole.model.service.property.PropertyValueTooLongException;

@Provider
public class ClientPropertyEndpointsImpl implements ClientPropertyEndpoints {

    private final ClientPropertyService clientPropertyService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Autowired
    public ClientPropertyEndpointsImpl(ClientPropertyService clientPropertyService,
        ClientAuthorizationProvider authorizationProvider) {
        this.clientPropertyService = clientPropertyService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public List<ClientPropertyResponse> list(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<ClientProperty> properties = clientPropertyService.list(authorization);
            return properties.stream().map(
                property -> new ClientPropertyResponse(property.getName(), property.getValue(),
                    ClientPropertyScope.valueOf(property.getScope().name())))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientPropertyResponse create(String accessToken, ClientPropertyCreationRequest property)
        throws UserAuthorizationRestException, PropertyCreationRestException, PropertyValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientPropertyBuilder builder = clientPropertyService.create(authorization).withName(property.getName())
                .withValue(property.getValue());
            property.getScope().ifPresent(
                scope -> builder.withScope(com.extole.model.entity.property.ClientPropertyScope.valueOf(scope.name())));
            ClientProperty createdProperty = builder.save();
            return new ClientPropertyResponse(createdProperty.getName(), createdProperty.getValue(),
                ClientPropertyScope.valueOf(createdProperty.getScope().name()));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (PropertyDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(PropertyCreationRestException.class)
                .withErrorCode(PropertyCreationRestException.DUPLICATE_PROPERTY)
                .addParameter("name", property.getName())
                .withCause(e)
                .build();
        } catch (PropertyNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.NAME_INVALID_LENGTH)
                .addParameter("name", property.getName())
                .withCause(e)
                .build();
        } catch (PropertyInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.INVALID_NAME)
                .addParameter("name", property.getName())
                .withCause(e)
                .build();
        } catch (PropertyValueTooLongException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.VALUE_TOO_LONG)
                .addParameter("value", property.getValue())
                .withCause(e)
                .build();
        } catch (PropertyNullValueException e) {
            throw RestExceptionBuilder.newBuilder(PropertyValidationRestException.class)
                .withErrorCode(PropertyValidationRestException.NULL_VALUE)
                .addParameter("value", property.getValue())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientPropertyResponse get(String accessToken, String name)
        throws UserAuthorizationRestException, PropertyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientProperty property = clientPropertyService.get(authorization, name);
            return new ClientPropertyResponse(property.getName(), property.getValue(),
                ClientPropertyScope.valueOf(property.getScope().name()));
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
        }
    }

    @Override
    public ClientPropertyResponse update(String accessToken, String name, ClientPropertyUpdateRequest request)
        throws PropertyRestException, PropertyValidationRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientPropertyBuilder builder = clientPropertyService.update(authorization, name);
            if (request.getValue().isPresent()) {
                builder.withValue(request.getValue().getValue());
            }
            request.getScope()
                .ifPresent(scope -> scope.map(ClientPropertyScope::name)
                    .map(com.extole.model.entity.property.ClientPropertyScope::valueOf)
                    .map(builder::withScope).orElseGet(builder::clearScope));
            ClientProperty property = builder.save();
            return new ClientPropertyResponse(property.getName(), property.getValue(),
                ClientPropertyScope.valueOf(property.getScope().name()));
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
            clientPropertyService.update(authorization, name).withDeleted().save();
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
