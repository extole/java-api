package com.extole.client.rest.impl.client.variables;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.client.variables.ClientVariableCreateRequest;
import com.extole.client.rest.client.variables.ClientVariableCreationRestException;
import com.extole.client.rest.client.variables.ClientVariableEndpoints;
import com.extole.client.rest.client.variables.ClientVariableResponse;
import com.extole.client.rest.client.variables.ClientVariableRestException;
import com.extole.client.rest.client.variables.ClientVariableUpdateRequest;
import com.extole.client.rest.client.variables.ClientVariableValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.client.variables.ClientVariable;
import com.extole.model.service.client.variables.ClientVariableBuilder;
import com.extole.model.service.client.variables.ClientVariableDescriptionLengthException;
import com.extole.model.service.client.variables.ClientVariableNameDuplicateException;
import com.extole.model.service.client.variables.ClientVariableNameInvalidException;
import com.extole.model.service.client.variables.ClientVariableNameLengthException;
import com.extole.model.service.client.variables.ClientVariableNameMissingException;
import com.extole.model.service.client.variables.ClientVariableNotFoundException;
import com.extole.model.service.client.variables.ClientVariableService;
import com.extole.model.service.client.variables.ClientVariableValueLengthException;

@Provider
public class ClientVariableEndpointsImpl implements ClientVariableEndpoints {

    private final ClientVariableService clientVariableService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public ClientVariableEndpointsImpl(ClientVariableService clientVariableService,
        ClientAuthorizationProvider authorizationProvider) {
        this.clientVariableService = clientVariableService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public ClientVariableResponse create(String accessToken, ClientVariableCreateRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableCreationRestException,
        ClientVariableValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientVariableBuilder clientVariableBuilder = clientVariableService.create(authorization);
            if (request.getName() != null) {
                clientVariableBuilder.withName(request.getName());
            }
            request.getValue().ifPresent(value -> {
                if (value.isPresent()) {
                    clientVariableBuilder.withValue(value.get());
                } else {
                    clientVariableBuilder.clearValue();
                }
            });
            request.getDescription().ifPresent(description -> {
                if (description.isPresent()) {
                    clientVariableBuilder.withDescription(description.get());
                } else {
                    clientVariableBuilder.clearDescription();
                }
            });
            ClientVariable updatedVariable = clientVariableBuilder.save();
            return toVariableResponse(updatedVariable, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientVariableNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableCreationRestException.class)
                .withErrorCode(ClientVariableCreationRestException.INVALID_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ClientVariableNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableValidationRestException.class)
                .withErrorCode(ClientVariableValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getName())
                .addParameter("min_length", e.getNameMinLength())
                .addParameter("max_length", e.getNameMaxLength())
                .withCause(e)
                .build();
        } catch (ClientVariableNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableCreationRestException.class)
                .withErrorCode(ClientVariableCreationRestException.DUPLICATED_NAME)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ClientVariableValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableValidationRestException.class)
                .withErrorCode(ClientVariableValidationRestException.VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("value", e.getValue())
                .addParameter("min_length", e.getValueMinLength())
                .addParameter("max_length", e.getValueMaxLength())
                .withCause(e)
                .build();
        } catch (ClientVariableNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableCreationRestException.class)
                .withErrorCode(ClientVariableCreationRestException.NAME_MISSING)
                .withCause(e)
                .build();
        } catch (ClientVariableDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableValidationRestException.class)
                .withErrorCode(ClientVariableValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", e.getDescriptionMaxLength())
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<ClientVariableResponse> list(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return clientVariableService.getAll(authorization).stream()
                .map(variable -> toVariableResponse(variable, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public ClientVariableResponse get(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientVariable variable = clientVariableService.get(authorization, name);
            return toVariableResponse(variable, timeZone);
        } catch (ClientVariableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableRestException.class)
                .withErrorCode(ClientVariableRestException.NOT_FOUND)
                .addParameter("name", name)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientVariableResponse update(String accessToken, String name, ClientVariableUpdateRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientVariableValidationRestException,
        ClientVariableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {

            ClientVariableBuilder clientVariableBuilder = clientVariableService.update(authorization, name);
            request.getValue().ifPresent(value -> {
                if (value.isPresent()) {
                    clientVariableBuilder.withValue(value.get());
                } else {
                    clientVariableBuilder.clearValue();
                }
            });
            request.getDescription().ifPresent(description -> {
                if (description.isPresent()) {
                    clientVariableBuilder.withDescription(description.get());
                } else {
                    clientVariableBuilder.clearDescription();
                }
            });

            ClientVariable updatedVariable = clientVariableBuilder.save();
            return toVariableResponse(updatedVariable, timeZone);
        } catch (ClientVariableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableRestException.class)
                .withErrorCode(ClientVariableRestException.NOT_FOUND)
                .addParameter("name", name)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ClientVariableValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableValidationRestException.class)
                .withErrorCode(ClientVariableValidationRestException.VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("value", e.getValue())
                .addParameter("min_length", e.getValueMinLength())
                .addParameter("max_length", e.getValueMaxLength())
                .withCause(e)
                .build();
        } catch (ClientVariableDescriptionLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableValidationRestException.class)
                .withErrorCode(ClientVariableValidationRestException.DESCRIPTION_LENGTH_OUT_OF_RANGE)
                .addParameter("description", e.getDescription())
                .addParameter("max_length", e.getDescriptionMaxLength())
                .withCause(e)
                .build();
        } catch (ClientVariableNameMissingException | ClientVariableNameDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientVariableResponse delete(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientVariable deletedVariable = clientVariableService.delete(authorization, name);
            return toVariableResponse(deletedVariable, timeZone);
        } catch (ClientVariableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientVariableRestException.class)
                .withErrorCode(ClientVariableRestException.NOT_FOUND)
                .addParameter("name", name)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private ClientVariableResponse toVariableResponse(ClientVariable clientVariable, ZoneId timeZone) {
        return new ClientVariableResponse(clientVariable.getName(),
            clientVariable.getValue(),
            clientVariable.getDescription(),
            clientVariable.getCreatedDate().atZone(timeZone),
            clientVariable.getUpdatedDate().atZone(timeZone));
    }

}
