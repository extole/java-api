package com.extole.common.rest.support.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import com.extole.authorization.service.Authorization;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.exception.PolymorphicRequestTypeResolverFilterRestWrapperException;
import com.extole.common.rest.support.request.resolver.MissingIdRequestTypeResolverException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolver;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverContext;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverRestWrapperException;
import com.extole.common.rest.support.request.resolver.ResolvesPolymorphicType;

@Provider
@ResolvesPolymorphicType
public class PolymorphicRequestTypeResolverFilter implements ContainerRequestFilter {

    private static final Function<Method, String> DISCRIMINATOR_PROPERTY_NAME_RESOLVER = method -> {
        Class<?> requestClass = Arrays.stream(method.getParameterTypes())
            .filter(type -> AnnotationUtils.isCandidateClass(type, JsonTypeInfo.class))
            .findFirst()
            .orElseThrow(() -> new PolymorphicRequestTypeResolverFilterException(
                "Could not find suitable polymorphic request type for method: " + method.getName()
                    + ", from class: " + method.getDeclaringClass().getCanonicalName()));

        JsonTypeInfo typeInfoAnnotation = requestClass.getAnnotation(JsonTypeInfo.class);
        if (typeInfoAnnotation == null) {
            throw new PolymorphicRequestTypeResolverFilterException("Class: " + requestClass.getCanonicalName()
                + ", is not annotated with: " + JsonTypeInfo.class.getCanonicalName());
        }
        return typeInfoAnnotation.property();
    };

    private final ResourceInfo resourceInfo;
    private final Map<Class<?>, PolymorphicRequestTypeResolver> resolvers;
    private final Map<Method, String> discriminatorProperties;

    @Autowired
    public PolymorphicRequestTypeResolverFilter(@Context ResourceInfo resourceInfo,
        List<PolymorphicRequestTypeResolver> resolvers) {
        this.resourceInfo = resourceInfo;
        this.resolvers = resolvers.stream()
            .collect(Collectors.toUnmodifiableMap(resolver -> resolver.getClass(), Function.identity()));
        discriminatorProperties = new ConcurrentHashMap<>();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        JsonNode requestJson = ObjectMapperProvider.getConfiguredInstance().readTree(requestContext.getEntityStream());
        if (!isEmptyRequest(requestJson) && !(requestJson instanceof ObjectNode)) {
            requestContext.setEntityStream(
                new ByteArrayInputStream(ObjectMapperProvider.getConfiguredInstance().writeValueAsBytes(requestJson)));
            return;
        }

        Method polymorphicRequestMethod = resourceInfo.getResourceMethod();
        String discriminatorPropertyName =
            discriminatorProperties.computeIfAbsent(polymorphicRequestMethod, DISCRIMINATOR_PROPERTY_NAME_RESOLVER);

        JsonNode discriminatorPropertyNode = requestJson.get(discriminatorPropertyName);
        boolean discriminatorPropertyIsMissing = discriminatorPropertyNode == null || discriminatorPropertyNode.isNull()
            || StringUtils.isEmpty(discriminatorPropertyNode.asText());

        if (discriminatorPropertyIsMissing) {
            ResolvesPolymorphicType resolvesPolymorphicTypeAnnotation =
                polymorphicRequestMethod.getAnnotation(ResolvesPolymorphicType.class);
            PolymorphicRequestTypeResolver resolver = resolvers.get(resolvesPolymorphicTypeAnnotation.resolver());
            if (resolver == null) {
                throw new PolymorphicRequestTypeResolverFilterException(
                    "Could not find resolver for type: " + resolvesPolymorphicTypeAnnotation.resolver().getName());
            }

            Authorization authorization = getAuthorization(requestContext);

            Map<String, String> parameters = requestContext.getUriInfo().getPathParameters().entrySet().stream()
                .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().get(0)));
            PolymorphicRequestTypeResolverContext resolverContext = PolymorphicRequestTypeResolverContext.builder()
                .withAuthorization(authorization)
                .withParameters(parameters)
                .build();

            String resolvedType = resolveType(resolver, resolverContext);
            if (isEmptyRequest(requestJson)) {
                requestJson = ObjectMapperProvider.getConfiguredInstance().createObjectNode();
            }
            ((ObjectNode) requestJson).put(discriminatorPropertyName, resolvedType);
        }

        requestContext.setEntityStream(
            new ByteArrayInputStream(ObjectMapperProvider.getConfiguredInstance().writeValueAsBytes(requestJson)));
    }

    private Authorization getAuthorization(ContainerRequestContext requestContext) {
        Authorization authorization = (Authorization) requestContext
            .getProperty(RequestContextAttributeName.AUTHORIZATION.getAttributeName());
        if (authorization != null) {
            return authorization;
        }
        UserAuthorizationRestException authorizationRestException =
            RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_TOKEN_MISSING).build();
        throw new PolymorphicRequestTypeResolverFilterRestWrapperException(authorizationRestException);
    }

    private static String resolveType(PolymorphicRequestTypeResolver resolver,
        PolymorphicRequestTypeResolverContext resolverContext) {
        try {
            return resolver.resolve(resolverContext);
        } catch (PolymorphicRequestTypeResolverRestWrapperException e) {
            throw new PolymorphicRequestTypeResolverFilterRestWrapperException(e.getCause());
        } catch (MissingIdRequestTypeResolverException e) {
            throw new PolymorphicRequestTypeResolverFilterException("Could not find id field: " + e.getIdFieldName()
                + ", for resolver: " + resolver.getClass().getCanonicalName());
        }
    }

    private static boolean isEmptyRequest(JsonNode requestJson) {
        return StringUtils.trimToEmpty(requestJson.toString()).isEmpty();
    }

    private static class PolymorphicRequestTypeResolverFilterException extends RuntimeException {
        PolymorphicRequestTypeResolverFilterException(String message) {
            super(message);
        }
    }
}
