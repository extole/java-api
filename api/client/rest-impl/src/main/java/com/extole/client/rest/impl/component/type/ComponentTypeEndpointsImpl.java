package com.extole.client.rest.impl.component.type;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.component.type.ComponentTypeArchiveRestException;
import com.extole.client.rest.component.type.ComponentTypeCreateRequest;
import com.extole.client.rest.component.type.ComponentTypeEndpoints;
import com.extole.client.rest.component.type.ComponentTypeQueryParams;
import com.extole.client.rest.component.type.ComponentTypeResponse;
import com.extole.client.rest.component.type.ComponentTypeRestException;
import com.extole.client.rest.component.type.ComponentTypeUpdateRequest;
import com.extole.client.rest.component.type.ComponentTypeValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.component.type.ComponentType;
import com.extole.model.service.component.type.ComponentTypeBuilder;
import com.extole.model.service.component.type.ComponentTypeDisplayNameLengthException;
import com.extole.model.service.component.type.ComponentTypeIllegalCharacterInDisplayNameException;
import com.extole.model.service.component.type.ComponentTypeIllegalCharacterInNameException;
import com.extole.model.service.component.type.ComponentTypeNameAlreadyUsedException;
import com.extole.model.service.component.type.ComponentTypeNameLengthException;
import com.extole.model.service.component.type.ComponentTypeNotFoundException;
import com.extole.model.service.component.type.ComponentTypeQueryBuilder;
import com.extole.model.service.component.type.ComponentTypeService;
import com.extole.model.service.component.type.InvalidComponentTypeParentException;
import com.extole.model.service.component.type.InvalidComponentTypeSchemaException;
import com.extole.model.service.component.type.MissingComponentTypeNameException;
import com.extole.model.service.component.type.MissingComponentTypeSchemaException;
import com.extole.model.service.component.type.ParentAssociatedWithComponentTypeException;

@Provider
public class ComponentTypeEndpointsImpl implements ComponentTypeEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ComponentTypeService componentTypeService;
    private final ComponentTypeRestMapper componentTypeRestMapper;

    @Autowired
    public ComponentTypeEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ComponentTypeService componentTypeService,
        ComponentTypeRestMapper componentTypeRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.componentTypeService = componentTypeService;
        this.componentTypeRestMapper = componentTypeRestMapper;
    }

    @Override
    public List<ComponentTypeResponse> list(String accessToken, ComponentTypeQueryParams queryParams, ZoneId timeZone)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentTypeQueryBuilder queryBuilder = componentTypeService.list(authorization);

            if (queryParams.getParent().isPresent()) {
                queryBuilder.withParent(queryParams.getParent().get());
            }

            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            return queryBuilder.list()
                .stream()
                .map(componentType -> componentTypeRestMapper.toComponentTypeResponse(componentType, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentTypeResponse get(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentType componentType = componentTypeService.getByName(authorization, name);
            return componentTypeRestMapper.toComponentTypeResponse(componentType, timeZone);
        } catch (ComponentTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeRestException.class)
                .withErrorCode(ComponentTypeRestException.COMPONENT_TYPE_NOT_FOUND)
                .addParameter("name", e.getName())
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
    public ComponentTypeResponse create(String accessToken, ComponentTypeCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentTypeBuilder componentTypeBuilder = componentTypeService.create(authorization)
                .withName(createRequest.getName())
                .withSchema(createRequest.getSchema());

            createRequest.getDisplayName().ifPresent(displayName -> componentTypeBuilder.withDisplayName(displayName));
            createRequest.getParent().ifPresent(parent -> componentTypeBuilder.withParent(parent));

            ComponentType componentType = componentTypeBuilder.save();
            return componentTypeRestMapper.toComponentTypeResponse(componentType, timeZone);
        } catch (MissingComponentTypeNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.MISSING_COMPONENT_TYPE_NAME)
                .withCause(e)
                .build();
        } catch (ComponentTypeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.INVALID_COMPONENT_TYPE_NAME_LENGTH)
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentTypeIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.COMPONENT_TYPE_NAME_CONTAINS_ILLEGAL_CHARACTERS)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentTypeDisplayNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.INVALID_COMPONENT_TYPE_DISPLAY_NAME_LENGTH)
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (ComponentTypeIllegalCharacterInDisplayNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(
                    ComponentTypeValidationRestException.COMPONENT_TYPE_DISPLAY_NAME_CONTAINS_ILLEGAL_CHARACTERS)
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (ComponentTypeNameAlreadyUsedException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.COMPONENT_TYPE_NAME_ALREADY_USED)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (InvalidComponentTypeParentException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.INVALID_COMPONENT_TYPE_PARENT)
                .addParameter("parent", e.getParent())
                .withCause(e)
                .build();
        } catch (MissingComponentTypeSchemaException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.MISSING_COMPONENT_TYPE_SCHEMA)
                .withCause(e)
                .build();
        } catch (InvalidComponentTypeSchemaException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.INVALID_COMPONENT_TYPE_SCHEMA)
                .addParameter("schema", e.getSchema())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ParentAssociatedWithComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentTypeResponse update(String accessToken, String name, ComponentTypeUpdateRequest updateRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, ComponentTypeRestException,
        ComponentTypeValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentTypeBuilder componentTypeBuilder = componentTypeService.update(authorization, name);

            updateRequest.getDisplayName().ifPresent(displayName -> {
                if (displayName.isPresent()) {
                    componentTypeBuilder.withDisplayName(displayName.get());
                } else {
                    componentTypeBuilder.clearDisplayName();
                }
            });

            ComponentType componentType = componentTypeBuilder.save();
            return componentTypeRestMapper.toComponentTypeResponse(componentType, timeZone);
        } catch (ComponentTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeRestException.class)
                .withErrorCode(ComponentTypeRestException.COMPONENT_TYPE_NOT_FOUND)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (ComponentTypeDisplayNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(ComponentTypeValidationRestException.INVALID_COMPONENT_TYPE_DISPLAY_NAME_LENGTH)
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (ComponentTypeIllegalCharacterInDisplayNameException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeValidationRestException.class)
                .withErrorCode(
                    ComponentTypeValidationRestException.COMPONENT_TYPE_DISPLAY_NAME_CONTAINS_ILLEGAL_CHARACTERS)
                .addParameter("display_name", e.getDisplayName())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ParentAssociatedWithComponentTypeException | MissingComponentTypeNameException
            | ComponentTypeNameLengthException | ComponentTypeIllegalCharacterInNameException
            | ComponentTypeNameAlreadyUsedException | InvalidComponentTypeParentException
            | MissingComponentTypeSchemaException | InvalidComponentTypeSchemaException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ComponentTypeResponse archive(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentTypeRestException, ComponentTypeArchiveRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ComponentType componentType = componentTypeService.archive(authorization, name);
            return componentTypeRestMapper.toComponentTypeResponse(componentType, timeZone);
        } catch (ComponentTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ComponentTypeRestException.class)
                .withErrorCode(ComponentTypeRestException.COMPONENT_TYPE_NOT_FOUND)
                .addParameter("name", e.getName())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ParentAssociatedWithComponentTypeException e) {
            throw RestExceptionBuilder.newBuilder(
                ComponentTypeArchiveRestException.class)
                .withErrorCode(ComponentTypeArchiveRestException.PARENT_ASSOCIATED_WITH_COMPONENT_TYPE)
                .addParameter("parent", e.getParent())
                .addParameter("associated_component_types", e.getAssociatedComponentTypes())
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<ComponentTypeResponse> listDefault(String accessToken, ComponentTypeQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<ComponentType> componentTypes = componentTypeService.listDefault(authorization);
            return componentTypes.stream()
                .map(componentType -> componentTypeRestMapper.toComponentTypeResponse(componentType, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

}
