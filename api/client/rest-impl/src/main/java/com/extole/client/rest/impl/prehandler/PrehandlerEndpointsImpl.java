package com.extole.client.rest.impl.prehandler;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.BuildPrehandlerExceptionMapper;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.prehandler.BuildPrehandlerRestException;
import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.PrehandlerCreateRequest;
import com.extole.client.rest.prehandler.PrehandlerEndpoints;
import com.extole.client.rest.prehandler.PrehandlerListQueryParams;
import com.extole.client.rest.prehandler.PrehandlerResponse;
import com.extole.client.rest.prehandler.PrehandlerRestException;
import com.extole.client.rest.prehandler.PrehandlerUpdateRequest;
import com.extole.client.rest.prehandler.action.request.PrehandlerActionRequest;
import com.extole.client.rest.prehandler.action.response.PrehandlerActionResponse;
import com.extole.client.rest.prehandler.built.BuiltPrehandlerResponse;
import com.extole.client.rest.prehandler.condition.request.PrehandlerConditionRequest;
import com.extole.client.rest.prehandler.condition.response.PrehandlerConditionResponse;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.prehandler.Prehandler;
import com.extole.model.entity.prehandler.PrehandlerAction;
import com.extole.model.entity.prehandler.PrehandlerCondition;
import com.extole.model.entity.prehandler.built.BuiltPrehandler;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.prehandler.PrehandlerActionBuildException;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.PrehandlerConditionBuildException;
import com.extole.model.service.prehandler.PrehandlerInvalidTagException;
import com.extole.model.service.prehandler.PrehandlerListQueryBuilder;
import com.extole.model.service.prehandler.PrehandlerNotFoundException;
import com.extole.model.service.prehandler.PrehandlerService;
import com.extole.model.service.prehandler.built.BuildPrehandlerException;
import com.extole.model.service.prehandler.built.BuiltPrehandlerListQueryBuilder;
import com.extole.model.service.prehandler.built.BuiltPrehandlerService;

@Provider
public class PrehandlerEndpointsImpl implements PrehandlerEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final PrehandlerService prehandlerService;
    private final BuiltPrehandlerService builtPrehandlerService;
    private final ComponentService componentService;
    private final PrehandlerRequestMapperRepository requestMappersRepository;
    private final PrehandlerResponseMapperRepository responseMappersRepository;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public PrehandlerEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PrehandlerService prehandlerService,
        BuiltPrehandlerService builtPrehandlerService,
        ComponentService componentService,
        PrehandlerRequestMapperRepository requestMappersRepository,
        PrehandlerResponseMapperRepository responseMappersRepository,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.prehandlerService = prehandlerService;
        this.builtPrehandlerService = builtPrehandlerService;
        this.componentService = componentService;
        this.requestMappersRepository = requestMappersRepository;
        this.responseMappersRepository = responseMappersRepository;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<PrehandlerResponse> list(String accessToken, PrehandlerListQueryParams requestParams, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PrehandlerListQueryBuilder listFilterBuilder = prehandlerService.list(authorization);

            if (requestParams.getOffset() != null) {
                listFilterBuilder.withOffset(requestParams.getOffset());
            }

            if (requestParams.getLimit() != null) {
                listFilterBuilder.withLimit(requestParams.getLimit());
            }

            if (requestParams.getTags().isPresent()) {
                listFilterBuilder.withTags(requestParams.getTags().get());
            }

            return listFilterBuilder.execute().stream()
                .map(prehandler -> toPrehandlerResponse(prehandler, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public List<BuiltPrehandlerResponse> listBuilt(String accessToken, PrehandlerListQueryParams requestParams,
        ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltPrehandlerListQueryBuilder listFilterBuilder = builtPrehandlerService.list(authorization);

            if (requestParams.getOffset() != null) {
                listFilterBuilder.withOffset(requestParams.getOffset());
            }

            if (requestParams.getLimit() != null) {
                listFilterBuilder.withLimit(requestParams.getLimit());
            }

            if (requestParams.getTags().isPresent()) {
                listFilterBuilder.withTags(requestParams.getTags().get());
            }

            return listFilterBuilder.execute().stream()
                .map(prehandler -> toBuiltPrehandlerResponse(prehandler, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public PrehandlerResponse get(String accessToken, String prehandlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Prehandler prehandler = prehandlerService.getById(authorization, Id.valueOf(prehandlerId));
            return toPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerRestException.class)
                .withErrorCode(PrehandlerRestException.PREHANDLER_NOT_FOUND)
                .addParameter("prehandler_id", prehandlerId)
                .withCause(e).build();
        }
    }

    @Override
    public BuiltPrehandlerResponse getBuilt(String accessToken, String prehandlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltPrehandler prehandler = builtPrehandlerService.getById(authorization, Id.valueOf(prehandlerId));
            return toBuiltPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerRestException.class)
                .withErrorCode(PrehandlerRestException.PREHANDLER_NOT_FOUND)
                .addParameter("prehandler_id", prehandlerId)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerResponse archive(String accessToken, String prehandlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Prehandler prehandler = prehandlerService.archive(authorization, Id.valueOf(prehandlerId));
            return toPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerRestException.class)
                .withErrorCode(PrehandlerRestException.PREHANDLER_NOT_FOUND)
                .addParameter("prehandler_id", prehandlerId)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerResponse unArchive(String accessToken, String prehandlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Prehandler prehandler = prehandlerService.unArchive(authorization, Id.valueOf(prehandlerId));
            return toPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerRestException.class)
                .withErrorCode(PrehandlerRestException.PREHANDLER_NOT_FOUND)
                .addParameter("prehandler_id", prehandlerId)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerResponse delete(String accessToken, String prehandlerId, ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Prehandler prehandler = prehandlerService.delete(authorization, Id.valueOf(prehandlerId));
            return toPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerRestException.class)
                .withErrorCode(PrehandlerRestException.PREHANDLER_NOT_FOUND)
                .addParameter("prehandler_id", prehandlerId)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerResponse create(String accessToken, PrehandlerCreateRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, BuildPrehandlerRestException,
        PrehandlerConditionValidationRestException, PrehandlerActionValidationRestException,
        CampaignComponentValidationRestException, OmissibleRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PrehandlerBuilder builder = prehandlerService.create(authorization)
                .withName(request.getName());
            request.getDescription().ifPresent(description -> builder.withDescription(description));
            request.isEnabled().ifPresent(enabled -> builder.withEnabled(enabled));
            request.getOrder().ifPresent(order -> builder.withOrder(order));
            request.getTags().ifPresent(builder::withTags);
            request.getConditions().ifPresent(conditions -> updateConditions(builder, conditions));
            request.getActions().ifPresent(actions -> updateActions(builder, actions));
            request.getComponentIds().ifPresent(componentIds -> handleComponentIds(builder, componentIds));
            request.getComponentReferences().ifPresent(componentReferences -> componentReferenceRequestMapper
                .handleComponentReferences(builder, componentReferences));

            Prehandler prehandler =
                builder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return toPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.TAG_INVALID)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildPrehandlerException e) {
            throw BuildPrehandlerExceptionMapper.getInstance().map(e);
        } catch (PrehandlerConditionBuildException | PrehandlerActionBuildException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public PrehandlerResponse update(String accessToken, String prehandlerId,
        PrehandlerUpdateRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, PrehandlerRestException, BuildPrehandlerRestException,
        PrehandlerConditionValidationRestException, PrehandlerActionValidationRestException,
        CampaignComponentValidationRestException, OmissibleRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PrehandlerBuilder builder = prehandlerService.update(authorization, Id.valueOf(prehandlerId));
            request.getName().ifPresent(name -> builder.withName(name));
            request.getDescription().ifPresent(description -> builder.withDescription(description));
            request.isEnabled().ifPresent(enabled -> builder.withEnabled(enabled));
            request.getOrder().ifPresent(order -> builder.withOrder(order));
            request.getTags().ifPresent(tags -> {
                if (tags.isEmpty()) {
                    builder.removeTags();
                } else {
                    builder.withTags(tags);
                }
            });
            request.getConditions().ifPresent(conditions -> updateConditions(builder, conditions));
            request.getActions().ifPresent(actions -> updateActions(builder, actions));
            request.getComponentIds().ifPresent(componentIds -> handleComponentIds(builder, componentIds));
            request.getComponentReferences().ifPresent(componentReferences -> componentReferenceRequestMapper
                .handleComponentReferences(builder, componentReferences));

            Prehandler prehandler =
                builder.save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return toPrehandlerResponse(prehandler, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PrehandlerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PrehandlerRestException.class)
                .withErrorCode(PrehandlerRestException.PREHANDLER_NOT_FOUND)
                .addParameter("prehandler_id", prehandlerId)
                .withCause(e).build();
        } catch (PrehandlerInvalidTagException e) {
            throw RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.TAG_INVALID)
                .addParameter("tag", e.getTag())
                .addParameter("tag_max_length", Integer.valueOf(e.getTagMaxLength()))
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(BuildPrehandlerRestException.class)
                .withErrorCode(BuildPrehandlerRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (BuildPrehandlerException e) {
            throw BuildPrehandlerExceptionMapper.getInstance().map(e);
        } catch (PrehandlerConditionBuildException | PrehandlerActionBuildException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    private void updateConditions(PrehandlerBuilder prehandlerBuilder,
        @Nullable List<? extends PrehandlerConditionRequest> conditions)
        throws PrehandlerConditionValidationRestException {
        if (conditions == null) {
            return;
        }
        prehandlerBuilder.removeConditions();
        for (PrehandlerConditionRequest condition : conditions) {
            requestMappersRepository.getPrehandlerConditionRequestMapper(condition.getType()).update(prehandlerBuilder,
                condition);
        }
    }

    private void updateActions(PrehandlerBuilder prehandlerBuilder,
        @Nullable List<? extends PrehandlerActionRequest> actions)
        throws PrehandlerActionValidationRestException {
        if (actions == null) {
            return;
        }
        prehandlerBuilder.removeActions();
        for (PrehandlerActionRequest action : actions) {
            requestMappersRepository.getPrehandlerActionRequestMapper(action.getType()).update(prehandlerBuilder,
                action);
        }
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }

    private PrehandlerResponse toPrehandlerResponse(Prehandler prehandler, ZoneId timeZone) {
        List<PrehandlerConditionResponse> conditions = prehandler.getConditions().stream()
            .map(condition -> toPrehandlerConditionResponse(condition)).collect(Collectors.toList());
        List<PrehandlerActionResponse> actions = prehandler.getActions().stream()
            .map(action -> toPrehandlerActionResponse(action)).collect(Collectors.toList());
        List<Id<ComponentResponse>> componentIds = prehandler.getComponentReferences().stream()
            .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
            .collect(Collectors.toList());
        List<ComponentReferenceResponse> componentReferences = prehandler.getComponentReferences().stream()
            .map(reference -> new ComponentReferenceResponse(
                Id.valueOf(reference.getComponentId().getValue()),
                reference.getSocketNames()))
            .collect(Collectors.toList());
        return new PrehandlerResponse(
            prehandler.getId().getValue(),
            prehandler.getName(),
            prehandler.getDescription(),
            prehandler.isEnabled(),
            prehandler.getOrder(),
            prehandler.getCreatedDate().atZone(timeZone),
            prehandler.getUpdatedDate().atZone(timeZone),
            prehandler.getTags(),
            conditions,
            actions,
            componentIds,
            componentReferences);
    }

    private BuiltPrehandlerResponse toBuiltPrehandlerResponse(BuiltPrehandler prehandler, ZoneId timeZone) {
        List<PrehandlerConditionResponse> conditions = prehandler.getConditions().stream()
            .map(condition -> toPrehandlerConditionResponse(condition)).collect(Collectors.toList());
        List<PrehandlerActionResponse> actions = prehandler.getActions().stream()
            .map(action -> toPrehandlerActionResponse(action)).collect(Collectors.toList());
        List<Id<ComponentResponse>> componentIds = prehandler.getComponentReferences().stream()
            .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
            .collect(Collectors.toList());
        List<ComponentReferenceResponse> componentReferences = prehandler.getComponentReferences().stream()
            .map(reference -> new ComponentReferenceResponse(
                Id.valueOf(reference.getComponentId().getValue()),
                reference.getSocketNames()))
            .collect(Collectors.toList());
        return new BuiltPrehandlerResponse(
            prehandler.getId().getValue(),
            prehandler.getName(),
            prehandler.getDescription(),
            prehandler.isEnabled(),
            prehandler.getOrder(),
            prehandler.getCreatedDate().atZone(timeZone),
            prehandler.getUpdatedDate().atZone(timeZone),
            prehandler.getTags(),
            conditions,
            actions,
            componentIds,
            componentReferences);
    }

    private PrehandlerConditionResponse toPrehandlerConditionResponse(PrehandlerCondition condition) {
        return responseMappersRepository.getPrehandlerConditionResponseMapper(condition.getType())
            .toResponse(condition);
    }

    private PrehandlerActionResponse toPrehandlerActionResponse(PrehandlerAction action) {
        return responseMappersRepository.getPrehandlerActionResponseMapper(action.getType()).toResponse(action);
    }
}
