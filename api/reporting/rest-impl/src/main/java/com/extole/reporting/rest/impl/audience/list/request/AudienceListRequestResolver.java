package com.extole.reporting.rest.impl.audience.list.request;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.request.resolver.InvalidResolvedTypeException;
import com.extole.common.rest.support.request.resolver.MissingIdRequestTypeResolverException;
import com.extole.common.rest.support.request.resolver.MissingSubTypesAnnotationException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolver;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverContext;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.list.AudienceList;
import com.extole.reporting.rest.audience.list.AudienceListRestException;
import com.extole.reporting.rest.audience.list.AudienceListsEndpoints;
import com.extole.reporting.rest.audience.list.request.AudienceListRequest;
import com.extole.reporting.service.audience.list.AudienceListNotFoundException;
import com.extole.reporting.service.audience.list.AudienceListService;

@Component
public final class AudienceListRequestResolver implements PolymorphicRequestTypeResolver {

    private final Set<String> subTypes;
    private final AudienceListService audienceListService;

    @Autowired
    public AudienceListRequestResolver(AudienceListService audienceListService) {
        this.audienceListService = audienceListService;

        JsonSubTypes jsonSubTypesAnnotation = AudienceListRequest.class.getDeclaredAnnotation(JsonSubTypes.class);
        if (jsonSubTypesAnnotation == null) {
            throw new MissingSubTypesAnnotationException(AudienceListRequest.class, JsonSubTypes.class);
        }
        subTypes = Arrays.stream(jsonSubTypesAnnotation.value())
            .map(type -> type.name())
            .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    @Override
    public String resolve(PolymorphicRequestTypeResolverContext context)
        throws MissingIdRequestTypeResolverException, AudienceListRequestResolverRestWrapperException {

        Authorization authorization = context.getAuthorization();
        Id<AudienceList> audienceListId =
            Id.valueOf(context.getValue(AudienceListsEndpoints.AUDIENCE_ID_PATH_PARAM_NAME)
                .orElseThrow(() -> new MissingIdRequestTypeResolverException(
                    AudienceListsEndpoints.AUDIENCE_ID_PATH_PARAM_NAME, context.getAuthorization().getClientId())));

        try {
            String resolvedType = getAudienceList(authorization, audienceListId).getType().name();
            validateResolvedType(resolvedType);
            return resolvedType;
        } catch (ExtoleRestException e) {
            throw new AudienceListRequestResolverRestWrapperException(audienceListId, authorization.getClientId(), e);
        }
    }

    private AudienceList getAudienceList(Authorization authorization, Id<AudienceList> audienceListId)
        throws UserAuthorizationRestException, AudienceListRestException {
        try {
            return audienceListService.getById(authorization, audienceListId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceListId)
                .withCause(e).build();
        }
    }

    private void validateResolvedType(String resolvedType) {
        if (!subTypes.contains(resolvedType)) {
            throw new InvalidResolvedTypeException(AudienceListRequest.class, resolvedType, JsonSubTypes.class);
        }
    }
}
