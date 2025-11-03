package com.extole.client.rest.impl.component.facet;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.component.facet.ComponentFacetAllowedValueCreateRequest;
import com.extole.client.rest.component.facet.ComponentFacetAllowedValueUpdateRequest;
import com.extole.client.rest.component.facet.ComponentFacetCreateRequest;
import com.extole.client.rest.component.facet.ComponentFacetEndpoints;
import com.extole.client.rest.component.facet.ComponentFacetQueryParams;
import com.extole.client.rest.component.facet.ComponentFacetResponse;
import com.extole.client.rest.component.facet.ComponentFacetRestException;
import com.extole.client.rest.component.facet.ComponentFacetUpdateRequest;
import com.extole.client.rest.component.facet.ComponentFacetValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.model.entity.component.facet.ComponentFacet;
import com.extole.model.service.component.facet.ComponentFacetAllowedValueBuilder;
import com.extole.model.service.component.facet.ComponentFacetBuilder;
import com.extole.model.service.component.facet.ComponentFacetDisplayNameLengthException;
import com.extole.model.service.component.facet.ComponentFacetIllegalCharacterInNameException;
import com.extole.model.service.component.facet.ComponentFacetInUseException;
import com.extole.model.service.component.facet.ComponentFacetNameAlreadyExistsException;
import com.extole.model.service.component.facet.ComponentFacetNameLengthException;
import com.extole.model.service.component.facet.ComponentFacetNameMissingException;
import com.extole.model.service.component.facet.ComponentFacetNotFoundException;
import com.extole.model.service.component.facet.ComponentFacetQueryBuilder;
import com.extole.model.service.component.facet.ComponentFacetService;
import com.extole.model.service.component.facet.value.ComponentFacetIllegalCharacterInValueException;
import com.extole.model.service.component.facet.value.ComponentFacetValueDescriptionLengthException;
import com.extole.model.service.component.facet.value.ComponentFacetValueDisplayNameLengthException;
import com.extole.model.service.component.facet.value.ComponentFacetValueIconLengthException;
import com.extole.model.service.component.facet.value.ComponentFacetValueIllegalCharacterInDescriptionException;
import com.extole.model.service.component.facet.value.ComponentFacetValueIllegalCharacterInIconException;
import com.extole.model.service.component.facet.value.ComponentFacetValueInvalidColorException;
import com.extole.model.service.component.facet.value.ComponentFacetValueLengthException;
import com.extole.model.service.component.facet.value.ComponentFacetValuesSizeException;

@Provider
public class ComponentFacetEndpointsImpl implements ComponentFacetEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ComponentFacetService componentFacetService;
    private final ComponentFacetRestMapper componentFacetRestMapper;

    @Inject
    public ComponentFacetEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ComponentFacetService componentFacetService,
        ComponentFacetRestMapper componentFacetRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.componentFacetService = componentFacetService;
        this.componentFacetRestMapper = componentFacetRestMapper;
    }

    @Override
    public List<ComponentFacetResponse> list(String accessToken, ComponentFacetQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ComponentFacetQueryBuilder facetQueryBuilder = componentFacetService.list(authorization)
                .withLimit(queryParams.getLimit())
                .withOffset(queryParams.getOffset());
            if (queryParams.getIncludeArchived()) {
                facetQueryBuilder.includeArchived();
            }

            return facetQueryBuilder.list().stream()
                .map(componentFacet -> componentFacetRestMapper.toComponentFacetResponse(componentFacet, timeZone))
                .toList();
        } catch (AuthorizationException e) {
            throw mapAuthorizationException(e);
        }
    }

    @Override
    public ComponentFacetResponse get(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ComponentFacet componentFacet = componentFacetService.getByName(authorization, name);
            return componentFacetRestMapper.toComponentFacetResponse(componentFacet, timeZone);
        } catch (AuthorizationException e) {
            throw mapAuthorizationException(e);
        } catch (ComponentFacetNotFoundException e) {
            throw mapComponentFacetNotFoundException(e);
        }
    }

    @Override
    public ComponentFacetResponse create(String accessToken, ComponentFacetCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException, CampaignComponentValidationRestException,
        ComponentFacetValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ComponentFacet createdFacet = internalCreate(authorization, createRequest);
            return componentFacetRestMapper.toComponentFacetResponse(createdFacet, timeZone);
        } catch (AuthorizationException e) {
            throw mapAuthorizationException(e);
        } catch (ComponentFacetNameMissingException e) {
            throw mapComponentFacetNameMissingException(e);
        } catch (ComponentFacetNameLengthException e) {
            throw mapComponentFacetNameLengthException(e);
        } catch (ComponentFacetIllegalCharacterInNameException e) {
            throw mapComponentFacetIllegalCharacterInNameException(e);
        } catch (ComponentFacetValueIconLengthException e) {
            throw mapComponentFacetIconLengthException(e);
        } catch (ComponentFacetValueDescriptionLengthException e) {
            throw mapComponentFacetDescriptionLengthException(e);
        } catch (ComponentFacetDisplayNameLengthException e) {
            throw mapComponentFacetDisplayNameLengthException(e);
        } catch (ComponentFacetValueDisplayNameLengthException e) {
            throw mapComponentFacetValueDisplayNameLengthException(e);
        } catch (ComponentFacetValueInvalidColorException e) {
            throw mapComponentFacetInvalidColorException(e);
        } catch (ComponentFacetNameAlreadyExistsException e) {
            throw mapComponentFacetNameAlreadyExistsException(e);
        } catch (ComponentFacetValuesSizeException e) {
            throw mapComponentFacetValuesSizeException(e);
        } catch (ComponentFacetValueLengthException e) {
            throw mapComponentFacetValueLengthException(e);
        } catch (ComponentFacetInUseException e) {
            throw mapComponentFacetIsReferencedByComponentsException(e);
        } catch (ComponentFacetValueIllegalCharacterInDescriptionException e) {
            throw mapComponentFacetIllegalCharacterInDescriptionException(e);
        } catch (ComponentFacetValueIllegalCharacterInIconException e) {
            throw mapComponentFacetIllegalCharacterInIconException(e);
        } catch (ComponentFacetIllegalCharacterInValueException e) {
            throw mapComponentFacetIllegalCharacterInValueException(e);
        }
    }

    @Override
    public ComponentFacetResponse update(String accessToken, String name, ComponentFacetUpdateRequest updateRequest,
        ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException, CampaignComponentValidationRestException,
        ComponentFacetValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ComponentFacet createdFacet = internalUpdate(authorization, name, updateRequest);
            return componentFacetRestMapper.toComponentFacetResponse(createdFacet, timeZone);
        } catch (AuthorizationException e) {
            throw mapAuthorizationException(e);
        } catch (ComponentFacetNameMissingException e) {
            throw mapComponentFacetNameMissingException(e);
        } catch (ComponentFacetNameLengthException e) {
            throw mapComponentFacetNameLengthException(e);
        } catch (ComponentFacetIllegalCharacterInNameException e) {
            throw mapComponentFacetIllegalCharacterInNameException(e);
        } catch (ComponentFacetNotFoundException e) {
            throw mapComponentFacetNotFoundException(e);
        } catch (ComponentFacetValueIconLengthException e) {
            throw mapComponentFacetIconLengthException(e);
        } catch (ComponentFacetValueDescriptionLengthException e) {
            throw mapComponentFacetDescriptionLengthException(e);
        } catch (ComponentFacetDisplayNameLengthException e) {
            throw mapComponentFacetDisplayNameLengthException(e);
        } catch (ComponentFacetValueDisplayNameLengthException e) {
            throw mapComponentFacetValueDisplayNameLengthException(e);
        } catch (ComponentFacetValueInvalidColorException e) {
            throw mapComponentFacetInvalidColorException(e);
        } catch (ComponentFacetNameAlreadyExistsException e) {
            throw mapComponentFacetNameAlreadyExistsException(e);
        } catch (ComponentFacetValuesSizeException e) {
            throw mapComponentFacetValuesSizeException(e);
        } catch (ComponentFacetValueLengthException e) {
            throw mapComponentFacetValueLengthException(e);
        } catch (ComponentFacetInUseException e) {
            throw mapComponentFacetIsReferencedByComponentsException(e);
        } catch (ComponentFacetValueIllegalCharacterInDescriptionException e) {
            throw mapComponentFacetIllegalCharacterInDescriptionException(e);
        } catch (ComponentFacetValueIllegalCharacterInIconException e) {
            throw mapComponentFacetIllegalCharacterInIconException(e);
        } catch (ComponentFacetIllegalCharacterInValueException e) {
            throw mapComponentFacetIllegalCharacterInValueException(e);
        }
    }

    @Override
    public ComponentFacetResponse archive(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ComponentFacetRestException, ComponentFacetValidationRestException {
        try {
            ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            ComponentFacet archivedFacet = componentFacetService.archive(authorization, name);
            return componentFacetRestMapper.toComponentFacetResponse(archivedFacet, timeZone);
        } catch (AuthorizationException e) {
            throw mapAuthorizationException(e);
        } catch (ComponentFacetNotFoundException e) {
            throw mapComponentFacetNotFoundException(e);
        } catch (ComponentFacetInUseException e) {
            throw mapComponentFacetIsReferencedByComponentsException(e);
        }
    }

    @Override
    public List<ComponentFacetResponse> listDefault(String accessToken,
        ZoneId timeZone) throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            return componentFacetService.listDefault(authorization)
                .stream()
                .map(componentFacet -> componentFacetRestMapper.toComponentFacetResponse(componentFacet, timeZone))
                .toList();
        } catch (AuthorizationException e) {
            throw mapAuthorizationException(e);
        }
    }

    private ComponentFacet internalUpdate(ClientAuthorization authorization,
        String name, ComponentFacetUpdateRequest updateRequest)
        throws AuthorizationException, ComponentFacetNameMissingException, ComponentFacetNameLengthException,
        ComponentFacetIllegalCharacterInNameException, ComponentFacetNotFoundException,
        ComponentFacetValueIconLengthException, ComponentFacetValueDescriptionLengthException,
        ComponentFacetDisplayNameLengthException, ComponentFacetNameAlreadyExistsException,
        ComponentFacetInUseException, ComponentFacetValuesSizeException, ComponentFacetValueLengthException,
        ComponentFacetValueIllegalCharacterInDescriptionException, ComponentFacetValueIllegalCharacterInIconException,
        ComponentFacetIllegalCharacterInValueException, ComponentFacetValueInvalidColorException,
        ComponentFacetValueDisplayNameLengthException {

        ComponentFacetBuilder componentFacetBuilder = componentFacetService.update(authorization, name);
        updateRequest.getAllowedValues().ifPresent(allowedValues -> {
            List<ComponentFacetAllowedValueUpdateRequest> facetAllowedValueUpdateRequests = allowedValues.stream()
                .filter(Objects::nonNull)
                .toList();
            componentFacetBuilder.clearAllowedValues();
            for (ComponentFacetAllowedValueUpdateRequest facetUpdateRequest : facetAllowedValueUpdateRequests) {
                ComponentFacetAllowedValueBuilder builder = componentFacetBuilder.addAllowedValue();
                facetUpdateRequest.getValue().ifPresent(value -> builder.withValue(value));
                facetUpdateRequest.getIcon().ifPresent(value -> {
                    if (value.isPresent()) {
                        builder.withIcon(value.get());
                    } else {
                        builder.clearIcon();
                    }
                });
                facetUpdateRequest.getColor().ifPresent(value -> {
                    if (value.isPresent()) {
                        builder.withColor(value.get());
                    } else {
                        builder.clearColor();
                    }
                });
                facetUpdateRequest.getDisplayName().ifPresent(value -> {
                    if (value.isPresent()) {
                        builder.withDisplayName(value.get());
                    } else {
                        builder.clearDisplayName();
                    }
                });
                facetUpdateRequest.getDescription().ifPresent(value -> {
                    if (value.isPresent()) {
                        builder.withDescription(value.get());
                    } else {
                        builder.clearDescription();
                    }
                });
            }
        });
        updateRequest.getDisplayName().ifPresent(value -> {
            if (value.isPresent()) {
                componentFacetBuilder.withDisplayName(value.get());
            } else {
                componentFacetBuilder.clearDisplayName();
            }
        });
        return componentFacetBuilder.save();
    }

    private ComponentFacet internalCreate(ClientAuthorization authorization,
        ComponentFacetCreateRequest createRequest)
        throws AuthorizationException, ComponentFacetNameMissingException, ComponentFacetNameLengthException,
        ComponentFacetIllegalCharacterInNameException, ComponentFacetValueIconLengthException,
        ComponentFacetValueDescriptionLengthException, ComponentFacetDisplayNameLengthException,
        ComponentFacetNameAlreadyExistsException, ComponentFacetValuesSizeException, ComponentFacetValueLengthException,
        ComponentFacetInUseException, ComponentFacetValueIllegalCharacterInDescriptionException,
        ComponentFacetValueIllegalCharacterInIconException, ComponentFacetIllegalCharacterInValueException,
        ComponentFacetValueInvalidColorException, ComponentFacetValueDisplayNameLengthException {

        ComponentFacetBuilder componentFacetBuilder = componentFacetService.create(authorization);
        componentFacetBuilder.withName(createRequest.getName());

        List<ComponentFacetAllowedValueCreateRequest> allowedValueCreateRequests = Objects
            .requireNonNullElse(createRequest.getAllowedValues(),
                List.<ComponentFacetAllowedValueCreateRequest>of())
            .stream()
            .filter(Objects::nonNull)
            .toList();
        for (ComponentFacetAllowedValueCreateRequest valueCreateRequest : allowedValueCreateRequests) {
            ComponentFacetAllowedValueBuilder valueBuilder = componentFacetBuilder.addAllowedValue()
                .withValue(valueCreateRequest.getValue());
            valueCreateRequest.getColor().ifPresent(value -> valueBuilder.withColor(value));
            valueCreateRequest.getDisplayName().ifPresent(value -> valueBuilder.withDisplayName(value));
            valueCreateRequest.getDescription().ifPresent(value -> valueBuilder.withDescription(value));
            valueCreateRequest.getIcon().ifPresent(value -> valueBuilder.withIcon(value));
        }
        createRequest.getDisplayName().ifPresent(value -> componentFacetBuilder.withDisplayName(value));

        return componentFacetBuilder.save();
    }

    private ComponentFacetRestException mapComponentFacetNotFoundException(ComponentFacetNotFoundException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetRestException.class)
            .withErrorCode(ComponentFacetRestException.COMPONENT_FACET_NOT_FOUND)
            .addParameter("name", e.getName())
            .withCause(e)
            .build();
    }

    private UserAuthorizationRestException mapAuthorizationException(AuthorizationException e) {
        return RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
            .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetIconLengthException(ComponentFacetValueIconLengthException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_ICON_LENGTH_OUT_OF_RANGE)
            .addParameter("value", e.getValue())
            .addParameter("icon", e.getIcon())
            .addParameter("min_length", Integer.valueOf(e.getMinLength()))
            .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetDescriptionLengthException(ComponentFacetValueDescriptionLengthException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_DESCRIPTION_LENGTH_OUT_OF_RANGE)
            .addParameter("value", e.getValue())
            .addParameter("description", e.getDescription())
            .addParameter("min_length", Integer.valueOf(e.getMinLength()))
            .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetValueDisplayNameLengthException(ComponentFacetValueDisplayNameLengthException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
            .addParameter("value", e.getValue())
            .addParameter("display_name", e.getDisplayName())
            .addParameter("min_length", Integer.valueOf(e.getMinLength()))
            .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetInvalidColorException(ComponentFacetValueInvalidColorException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_COLOR_INVALID)
            .addParameter("color", e.getColor())
            .addParameter("value", e.getValue())
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetDisplayNameLengthException(ComponentFacetDisplayNameLengthException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.DISPLAY_NAME_LENGTH_OUT_OF_RANGE)
            .addParameter("display_name", e.getDisplayName())
            .addParameter("min_length", Integer.valueOf(e.getMinLength()))
            .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetNameLengthException(ComponentFacetNameLengthException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.NAME_LENGTH_OUT_OF_RANGE)
            .addParameter("name", e.getName())
            .addParameter("min_length", Integer.valueOf(e.getMinLength()))
            .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetNameMissingException(ComponentFacetNameMissingException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.NAME_IS_MISSING)
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetIllegalCharacterInNameException(ComponentFacetIllegalCharacterInNameException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.NAME_ILLEGAL_CHARACTER)
            .addParameter("name", e.getName())
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetNameAlreadyExistsException(ComponentFacetNameAlreadyExistsException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.NAME_ALREADY_EXISTS)
            .addParameter("name", e.getName())
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetValueLengthException(ComponentFacetValueLengthException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_LENGTH_OUT_OF_RANGE)
            .addParameter("name", e.getName())
            .addParameter("value", e.getValue())
            .addParameter("min_length", Integer.valueOf(e.getMinLength()))
            .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetValuesSizeException(ComponentFacetValuesSizeException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUES_SIZE_OUT_OF_RANGE)
            .addParameter("name", e.getName())
            .addParameter("max_size", Integer.valueOf(e.getMaxSize()))
            .withCause(e)
            .build();
    }

    private ComponentFacetRestException
        mapComponentFacetIsReferencedByComponentsException(ComponentFacetInUseException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetRestException.class)
            .withErrorCode(ComponentFacetRestException.FACET_ASSOCIATED_WITH_COMPONENTS)
            .addParameter("facet_name", e.getFacetName())
            .addParameter("component_ids", e.getAssociatedEntityIds())
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetIllegalCharacterInValueException(ComponentFacetIllegalCharacterInValueException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_ILLEGAL_CHARACTER)
            .addParameter("value", e.getValue())
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetIllegalCharacterInDescriptionException(
            ComponentFacetValueIllegalCharacterInDescriptionException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_DESCRIPTION_ILLEGAL_CHARACTER)
            .addParameter("value", e.getValue())
            .addParameter("description", e.getDescription())
            .withCause(e)
            .build();
    }

    private ComponentFacetValidationRestException
        mapComponentFacetIllegalCharacterInIconException(
            ComponentFacetValueIllegalCharacterInIconException e) {
        return RestExceptionBuilder.newBuilder(ComponentFacetValidationRestException.class)
            .withErrorCode(ComponentFacetValidationRestException.VALUE_ICON_ILLEGAL_CHARACTER)
            .addParameter("value", e.getValue())
            .addParameter("icon", e.getIcon())
            .withCause(e)
            .build();
    }

}
