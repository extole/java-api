package com.extole.client.rest.impl.sftp.resolver;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.SftpDestinationEndpoints;
import com.extole.client.rest.sftp.SftpDestinationRestException;
import com.extole.client.rest.sftp.SftpDestinationUpdateRequest;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.request.resolver.InvalidResolvedTypeException;
import com.extole.common.rest.support.request.resolver.MissingIdRequestTypeResolverException;
import com.extole.common.rest.support.request.resolver.MissingSubTypesAnnotationException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolver;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverContext;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverRestWrapperException;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.SftpDestination;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationService;

@Component
@SuppressWarnings("rawtypes")
public final class SftpDestinationRequestResolver implements PolymorphicRequestTypeResolver {

    private final Set<String> subTypes;
    private final SftpDestinationService sftpDestinationService;

    @Autowired
    SftpDestinationRequestResolver(SftpDestinationService sftpDestinationService) {
        this.sftpDestinationService = sftpDestinationService;

        JsonSubTypes subTypesAnnotation = SftpDestinationUpdateRequest.class.getDeclaredAnnotation(JsonSubTypes.class);
        if (subTypesAnnotation == null) {
            throw new MissingSubTypesAnnotationException(SftpDestinationUpdateRequest.class, JsonSubTypes.class);
        }
        this.subTypes = Arrays.stream(subTypesAnnotation.value())
            .map(JsonSubTypes.Type::name)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String resolve(PolymorphicRequestTypeResolverContext context)
        throws PolymorphicRequestTypeResolverRestWrapperException, MissingIdRequestTypeResolverException {

        Authorization authorization = context.getAuthorization();
        Id<SftpDestination> sftpDestinationId =
            Id.valueOf(context.getValue(SftpDestinationEndpoints.SFTP_DESTINATION_ID_PATH_PARAM_NAME)
                .orElseThrow(() -> new MissingIdRequestTypeResolverException(
                    SftpDestinationEndpoints.SFTP_DESTINATION_ID_PATH_PARAM_NAME,
                    context.getAuthorization().getClientId())));

        try {
            String resolvedType = getSftpDestination(authorization, sftpDestinationId).getType().name();
            validateResolvedType(resolvedType);
            return resolvedType;
        } catch (ExtoleRestException e) {
            throw new SftpDestinationRequestResolverRestWrapperException(authorization.getClientId(), sftpDestinationId,
                e);
        }
    }

    @SuppressWarnings("unchecked")
    private SftpDestination getSftpDestination(Authorization authorization, Id<SftpDestination> sftpDestinationId)
        throws UserAuthorizationRestException, SftpDestinationRestException {
        try {
            return sftpDestinationService.get(authorization, sftpDestinationId);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(SftpDestinationRestException.class)
                .withErrorCode(SftpDestinationRestException.NOT_FOUND)
                .addParameter("sftp_destination_id", sftpDestinationId)
                .withCause(e).build();
        }
    }

    private void validateResolvedType(String resolvedType) {
        if (!subTypes.contains(resolvedType)) {
            throw new InvalidResolvedTypeException(SftpDestinationUpdateRequest.class, resolvedType,
                JsonSubTypes.class);
        }
    }
}
