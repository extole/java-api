package com.extole.client.rest.impl.client.domain.pattern;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.client.domain.pattern.BuildClientDomainPatternRestException;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternCreateRequest;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternEndpoints;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternQueryParams;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternResponse;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternRestException;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternUpdateRequest;
import com.extole.client.rest.client.domain.pattern.built.BuiltClientDomainPatternQueryParams;
import com.extole.client.rest.client.domain.pattern.built.BuiltClientDomainPatternResponse;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.client.rest.impl.client.domain.pattern.built.BuiltClientDomainPatternRestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.MoreThanOneComponentReferenceException;
import com.extole.model.entity.client.domain.pattern.ClientDomainPattern;
import com.extole.model.entity.client.domain.pattern.ClientDomainPatternType;
import com.extole.model.entity.client.domain.pattern.built.BuiltClientDomainPattern;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.component.ComponentService;
import com.extole.model.service.client.domain.pattern.ClientDomainPatternBuilder;
import com.extole.model.service.client.domain.pattern.ClientDomainPatternNotFoundException;
import com.extole.model.service.client.domain.pattern.ClientDomainPatternQueryBuilder;
import com.extole.model.service.client.domain.pattern.ClientDomainPatternService;
import com.extole.model.service.client.domain.pattern.InvalidDomainClientDomainPatternException;
import com.extole.model.service.client.domain.pattern.InvalidPatternSyntaxClientDomainPatternException;
import com.extole.model.service.client.domain.pattern.MissingPatternClientDomainPatternException;
import com.extole.model.service.client.domain.pattern.built.BuildClientDomainPatternException;
import com.extole.model.service.client.domain.pattern.built.BuiltClientDomainPatternQueryBuilder;
import com.extole.model.service.client.domain.pattern.built.BuiltClientDomainPatternService;

@Provider
public class ClientDomainPatternEndpointsImpl implements ClientDomainPatternEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientDomainPatternService clientDomainPatternService;
    private final BuiltClientDomainPatternService builtClientDomainPatternService;
    private final ComponentService componentService;
    private final ClientDomainPatternRestMapper clientDomainPatternRestMapper;
    private final BuiltClientDomainPatternRestMapper builtClientDomainPatternRestMapper;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public ClientDomainPatternEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ClientDomainPatternService clientDomainPatternService,
        BuiltClientDomainPatternService builtClientDomainPatternService,
        ComponentService componentService,
        ClientDomainPatternRestMapper clientDomainPatternRestMapper,
        BuiltClientDomainPatternRestMapper builtClientDomainPatternRestMapper,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.clientDomainPatternService = clientDomainPatternService;
        this.builtClientDomainPatternService = builtClientDomainPatternService;
        this.componentService = componentService;
        this.clientDomainPatternRestMapper = clientDomainPatternRestMapper;
        this.builtClientDomainPatternRestMapper = builtClientDomainPatternRestMapper;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public List<ClientDomainPatternResponse> list(String accessToken, ClientDomainPatternQueryParams queryParams,
        ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPatternQueryBuilder queryBuilder = clientDomainPatternService.list(authorization);

            if (queryParams.getClientDomainId() != null) {
                queryBuilder.withClientDomainId(Id.valueOf(queryParams.getClientDomainId().getValue()));
            }
            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            return queryBuilder.list()
                .stream()
                .sorted(Comparator.comparing(ClientDomainPattern::getCreatedDate).reversed())
                .map(clientDomainPattern -> clientDomainPatternRestMapper
                    .toClientDomainPatternResponse(clientDomainPattern, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ClientDomainPatternResponse get(String accessToken,
        Id<com.extole.api.client.domain.pattern.ClientDomainPattern> id,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPattern clientDomainPattern =
                clientDomainPatternService.getById(authorization, Id.valueOf(id.getValue()));
            return clientDomainPatternRestMapper.toClientDomainPatternResponse(clientDomainPattern, timeZone);
        } catch (ClientDomainPatternNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientDomainPatternRestException.class)
                .withErrorCode(ClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_NOT_FOUND)
                .addParameter("id", e.getClientDomainPatternId())
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
    public List<BuiltClientDomainPatternResponse> listBuilt(String accessToken,
        BuiltClientDomainPatternQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltClientDomainPatternQueryBuilder queryBuilder = builtClientDomainPatternService.list(authorization);

            if (queryParams.getClientDomainId() != null) {
                queryBuilder.withClientDomainId(Id.valueOf(queryParams.getClientDomainId().getValue()));
            }
            if (queryParams.getIncludeArchived()) {
                queryBuilder.includeArchived();
            }

            queryBuilder.withLimit(queryParams.getLimit());
            queryBuilder.withOffset(queryParams.getOffset());

            List<BuiltClientDomainPattern> builtClientDomainPatterns = queryBuilder.list();
            return builtClientDomainPatterns.stream()
                .sorted(Comparator.comparing(BuiltClientDomainPattern::getCreatedDate).reversed())
                .map(clientDomainPattern -> builtClientDomainPatternRestMapper
                    .toBuiltClientDomainPatternResponse(clientDomainPattern, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BuiltClientDomainPatternResponse getBuilt(String accessToken,
        Id<com.extole.api.client.domain.pattern.ClientDomainPattern> id, ZoneId timeZone)
        throws UserAuthorizationRestException, ClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            BuiltClientDomainPattern builtClientDomainPattern =
                builtClientDomainPatternService.getById(authorization, Id.valueOf(id.getValue()));
            return builtClientDomainPatternRestMapper.toBuiltClientDomainPatternResponse(builtClientDomainPattern,
                timeZone);
        } catch (ClientDomainPatternNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientDomainPatternRestException.class)
                .withErrorCode(ClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_NOT_FOUND)
                .addParameter("id", e.getClientDomainPatternId())
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
    public ClientDomainPatternResponse create(String accessToken, ClientDomainPatternCreateRequest createRequest,
        ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        BuildClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPatternBuilder clientDomainPatternBuilder = clientDomainPatternService.create(authorization)
                .withPattern(createRequest.getPattern());

            createRequest.getType().ifPresent(type -> {
                clientDomainPatternBuilder.withType(ClientDomainPatternType.valueOf(type.name()));
            });
            createRequest.getClientDomainId().ifPresent(clientDomainId -> {
                clientDomainPatternBuilder.withClientDomainId(Id.valueOf(clientDomainId.getValue()));
            });
            createRequest.getTest().ifPresent(test -> {
                clientDomainPatternBuilder.withTest(test);
            });
            createRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(clientDomainPatternBuilder, componentIds);
            });
            createRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(clientDomainPatternBuilder,
                    componentReferences);
            });

            ClientDomainPattern clientDomainPattern =
                clientDomainPatternBuilder
                    .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));
            return clientDomainPatternRestMapper.toClientDomainPatternResponse(clientDomainPattern, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidPatternSyntaxClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.INVALID_CLIENT_DOMAIN_PATTERN_SYNTAX)
                .addParameter("pattern", e.getPattern())
                .addParameter("type", e.getType().name())
                .withCause(e)
                .build();
        } catch (InvalidDomainClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.INVALID_CLIENT_DOMAIN_PATTERN_DOMAIN)
                .addParameter("pattern", e.getPattern())
                .addParameter("type", e.getType().name())
                .withCause(e)
                .build();
        } catch (MissingPatternClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.MISSING_CLIENT_DOMAIN_PATTERN)
                .addParameter("client_domain_pattern_id", e.getClientDomainPatternId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        } catch (BuildClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_BUILD_FAILED)
                .addParameter("client_domain_pattern_id", e.getClientDomainPatternId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public ClientDomainPatternResponse update(String accessToken,
        Id<com.extole.api.client.domain.pattern.ClientDomainPattern> id,
        ClientDomainPatternUpdateRequest updateRequest, ZoneId timeZone) throws UserAuthorizationRestException,
        ClientDomainPatternRestException, CampaignComponentValidationRestException,
        BuildClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPatternBuilder clientDomainPatternBuilder =
                clientDomainPatternService.update(authorization, Id.valueOf(id.getValue()));

            updateRequest.getPattern().ifPresent(pattern -> {
                clientDomainPatternBuilder.withPattern(pattern);
            });
            updateRequest.getType().ifPresent(type -> {
                clientDomainPatternBuilder.withType(ClientDomainPatternType.valueOf(type.name()));
            });
            updateRequest.getClientDomainId().ifPresent(clientDomainId -> {
                if (clientDomainId.isPresent()) {
                    clientDomainPatternBuilder.withClientDomainId(Id.valueOf(clientDomainId.get().getValue()));
                } else {
                    clientDomainPatternBuilder.clearClientDomainId();
                }
            });
            updateRequest.getTest().ifPresent(test -> {
                clientDomainPatternBuilder.withTest(test);
            });
            updateRequest.getComponentIds().ifPresent(componentIds -> {
                handleComponentIds(clientDomainPatternBuilder, componentIds);
            });
            updateRequest.getComponentReferences().ifPresent(componentReferences -> {
                componentReferenceRequestMapper.handleComponentReferences(clientDomainPatternBuilder,
                    componentReferences);
            });

            ClientDomainPattern clientDomainPattern =
                clientDomainPatternBuilder
                    .save(() -> componentService.buildDefaultComponentReferenceContext(authorization));

            return clientDomainPatternRestMapper.toClientDomainPatternResponse(clientDomainPattern, timeZone);
        } catch (ClientDomainPatternNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientDomainPatternRestException.class)
                .withErrorCode(ClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_NOT_FOUND)
                .addParameter("id", e.getClientDomainPatternId())
                .withCause(e)
                .build();
        } catch (InvalidPatternSyntaxClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.INVALID_CLIENT_DOMAIN_PATTERN_SYNTAX)
                .addParameter("pattern", e.getPattern())
                .addParameter("type", e.getType().name())
                .withCause(e)
                .build();
        } catch (InvalidDomainClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.INVALID_CLIENT_DOMAIN_PATTERN_DOMAIN)
                .addParameter("pattern", e.getPattern())
                .addParameter("type", e.getType().name())
                .withCause(e)
                .build();
        } catch (MissingPatternClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.MISSING_CLIENT_DOMAIN_PATTERN)
                .addParameter("client_domain_pattern_id", e.getClientDomainPatternId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        } catch (BuildClientDomainPatternException e) {
            throw RestExceptionBuilder.newBuilder(BuildClientDomainPatternRestException.class)
                .withErrorCode(BuildClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_BUILD_FAILED)
                .addParameter("client_domain_pattern_id", e.getClientDomainPatternId())
                .addParameter("evaluatable_name", e.getEvaluatableName())
                .addParameter("evaluatable", e.getEvaluatable().toString())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (InvalidComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(CampaignComponentValidationRestException.INVALID_COMPONENT_REFERENCE)
                .addParameter("identifier", e.getIdentifier())
                .addParameter("identifier_type", e.getIdentifierType())
                .withCause(e)
                .build();
        } catch (MoreThanOneComponentReferenceException e) {
            throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                .withErrorCode(
                    CampaignComponentValidationRestException.EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES)
                .build();
        }
    }

    @Override
    public ClientDomainPatternResponse archive(String accessToken,
        Id<com.extole.api.client.domain.pattern.ClientDomainPattern> id,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPattern clientDomainPattern =
                clientDomainPatternService.archive(authorization, Id.valueOf(id.getValue()));
            return clientDomainPatternRestMapper.toClientDomainPatternResponse(clientDomainPattern, timeZone);
        } catch (ClientDomainPatternNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientDomainPatternRestException.class)
                .withErrorCode(ClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_NOT_FOUND)
                .addParameter("id", e.getClientDomainPatternId())
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
    public ClientDomainPatternResponse delete(String accessToken,
        Id<com.extole.api.client.domain.pattern.ClientDomainPattern> id,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPattern clientDomainPattern =
                clientDomainPatternService.delete(authorization, Id.valueOf(id.getValue()));
            return clientDomainPatternRestMapper.toClientDomainPatternResponse(clientDomainPattern, timeZone);
        } catch (ClientDomainPatternNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientDomainPatternRestException.class)
                .withErrorCode(ClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_NOT_FOUND)
                .addParameter("id", e.getClientDomainPatternId())
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
    public ClientDomainPatternResponse unarchive(String accessToken,
        Id<com.extole.api.client.domain.pattern.ClientDomainPattern> id,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientDomainPatternRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ClientDomainPattern clientDomainPattern =
                clientDomainPatternService.unarchive(authorization, Id.valueOf(id.getValue()));
            return clientDomainPatternRestMapper.toClientDomainPatternResponse(clientDomainPattern, timeZone);
        } catch (ClientDomainPatternNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientDomainPatternRestException.class)
                .withErrorCode(ClientDomainPatternRestException.CLIENT_DOMAIN_PATTERN_NOT_FOUND)
                .addParameter("id", e.getClientDomainPatternId())
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
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

}
