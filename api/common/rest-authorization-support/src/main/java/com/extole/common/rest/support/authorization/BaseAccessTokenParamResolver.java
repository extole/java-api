package com.extole.common.rest.support.authorization;

import java.util.function.Function;

import javax.inject.Provider;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractValueParamProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.ExtoleAuthorizationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RequestContextAttributeName;

public class BaseAccessTokenParamResolver {

    public static class AccessTokenParamValueProvider extends AbstractValueParamProvider {

        public AccessTokenParamValueProvider(Provider<MultivaluedParameterExtractorProvider> extractorProvider) {
            super(extractorProvider, Parameter.Source.ENTITY, Parameter.Source.UNKNOWN);
        }

        @Override
        protected Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
            Class<?> classType = parameter.getRawType();
            if (classType == null || (!classType.equals(String.class))) {
                return null;
            }

            AccessTokenParam accessTokenParamAnnotation = parameter.getAnnotation(AccessTokenParam.class);
            if (accessTokenParamAnnotation != null) {
                return new AccessTokenValueProvider(accessTokenParamAnnotation.required(),
                    accessTokenParamAnnotation.requiredScope());
            } else {
                UserAccessTokenParam userAccessTokenAnnotation = parameter.getAnnotation(UserAccessTokenParam.class);
                if (userAccessTokenAnnotation != null) {
                    return new AccessTokenValueProvider(userAccessTokenAnnotation.required(),
                        userAccessTokenAnnotation.requiredScope());
                } else {
                    return null;
                }
            }
        }
    }

    private static final class AccessTokenValueProvider implements Function<ContainerRequest, String> {
        private final boolean authorizationRequired;
        private final Scope requiredScope;

        AccessTokenValueProvider(boolean authorizationRequired, Scope requiredScope) {
            this.authorizationRequired = authorizationRequired;
            this.requiredScope = requiredScope;
        }

        @Override
        public String apply(ContainerRequest containerRequest) {
            Authorization authorization =
                (Authorization) containerRequest
                    .getProperty(RequestContextAttributeName.AUTHORIZATION.getAttributeName());
            if (authorizationRequired || requiredScope != Scope.ANY) {
                Object accessToken =
                    containerRequest.getProperty(RequestContextAttributeName.ACCESS_TOKEN.getAttributeName());
                if (accessToken == null) {
                    throw RestExceptionBuilder.newBuilder(ExtoleAuthorizationRestException.class)
                        .withErrorCode(ExtoleAuthorizationRestException.ACCESS_TOKEN_MISSING)
                        .build();
                } else if (authorization == null) {
                    throw RestExceptionBuilder.newBuilder(ExtoleAuthorizationRestException.class)
                        .withErrorCode(ExtoleAuthorizationRestException.ACCESS_DENIED)
                        .build();
                }

                if (requiredScope != Scope.ANY) {
                    if (!authorization.getScopes().contains(Authorization.Scope.valueOf(requiredScope.name()))) {
                        throw RestExceptionBuilder.newBuilder(ExtoleAuthorizationRestException.class)
                            .withErrorCode(ExtoleAuthorizationRestException.ACCESS_DENIED)
                            .build();
                    }
                }
            }
            return authorization != null ? authorization.getAccessToken()
                : (String) containerRequest
                    .getProperty(RequestContextAttributeName.ACCESS_TOKEN.getAttributeName());
        }
    }

    public static class BaseAccessTokenBinder extends AbstractBinder {

        @Override
        protected void configure() {

            Provider<MultivaluedParameterExtractorProvider> extractorProvider =
                createManagedInstanceProvider(MultivaluedParameterExtractorProvider.class);
            Provider<ContainerRequest> requestProvider =
                createManagedInstanceProvider(ContainerRequest.class);

            AccessTokenParamValueProvider valueProvider =
                new AccessTokenParamValueProvider(extractorProvider);
            bind(Bindings.service(valueProvider).to(ValueParamProvider.class));
            bind(Bindings.injectionResolver(
                new ParamInjectionResolver<>(valueProvider, UserAccessTokenParam.class, requestProvider)));
            bind(Bindings.injectionResolver(
                new ParamInjectionResolver<>(valueProvider, AccessTokenParam.class, requestProvider)));
        }
    }
}
