package com.extole.client.rest.impl.property;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.property.PropertyCreationRequest;
import com.extole.client.rest.property.PropertyCreationRestException;
import com.extole.client.rest.property.PropertyResponse;
import com.extole.client.rest.property.PropertyRestException;
import com.extole.client.rest.property.PropertyUpdateRequest;
import com.extole.client.rest.property.PropertyValidationRestException;
import com.extole.client.rest.property.UserPropertyEndpoints;
import com.extole.client.rest.user.UserRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.property.Property;
import com.extole.model.entity.user.User;
import com.extole.model.service.property.PropertyDuplicateException;
import com.extole.model.service.property.PropertyInvalidNameException;
import com.extole.model.service.property.PropertyNameLengthException;
import com.extole.model.service.property.PropertyNotFoundException;
import com.extole.model.service.property.PropertyNullValueException;
import com.extole.model.service.property.PropertyValueTooLongException;
import com.extole.model.service.property.UserPropertyBuilder;
import com.extole.model.service.property.UserPropertyService;
import com.extole.model.service.user.UserNotFoundException;
import com.extole.model.service.user.UserService;

@Provider
public class UserPropertyEndpointsImpl implements UserPropertyEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final UserService userService;
    private final UserPropertyService userPropertyService;

    @Autowired
    public UserPropertyEndpointsImpl(ClientAuthorizationProvider authorizationProvider, UserService userService,
        UserPropertyService userPropertyService) {
        this.authorizationProvider = authorizationProvider;
        this.userService = userService;
        this.userPropertyService = userPropertyService;
    }

    @Override
    public List<PropertyResponse> list(String accessToken, String userId)
        throws UserAuthorizationRestException, UserRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUser(authorization, userId);
        List<Property> properties = userPropertyService.list(user);
        return properties.stream()
            .map(property -> new PropertyResponse(property.getName(), property.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public PropertyResponse create(String accessToken, String userId, PropertyCreationRequest request)
        throws UserAuthorizationRestException, UserRestException, PropertyCreationRestException,
        PropertyValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUser(authorization, userId);
        try {
            UserPropertyBuilder builder = userPropertyService.create(authorization, user).withName(request.getName())
                .withValue(request.getValue());
            Property property = builder.save();
            return new PropertyResponse(property.getName(), property.getValue());
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
    public PropertyResponse get(String accessToken, String userId, String name)
        throws UserAuthorizationRestException, UserRestException, PropertyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUser(authorization, userId);
        try {
            Property property = userPropertyService.get(user, name);
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
    public PropertyResponse update(String accessToken, String userId, String name,
        PropertyUpdateRequest request)
        throws UserAuthorizationRestException, UserRestException, PropertyRestException,
        PropertyValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUser(authorization, userId);
        try {
            UserPropertyBuilder builder = userPropertyService.update(authorization, user, name);
            if (!Strings.isNullOrEmpty(request.getValue())) {
                builder.withValue(request.getValue());
            }
            Property property = builder.save();
            return new PropertyResponse(property.getName(), property.getValue());
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
    public SuccessResponse delete(String accessToken, String userId, String name)
        throws UserAuthorizationRestException, UserRestException, PropertyRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        User user = getUser(authorization, userId);
        try {
            userPropertyService.update(authorization, user, name).withDeleted().save();
            return SuccessResponse.SUCCESS;
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

    private User getUser(Authorization authorization, String userId)
        throws UserRestException, UserAuthorizationRestException {
        try {
            return userService.getById(authorization, Id.valueOf(userId));
        } catch (UserNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserRestException.class)
                .withErrorCode(UserRestException.INVALID_USER_ID)
                .addParameter("user_id", userId)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

}
