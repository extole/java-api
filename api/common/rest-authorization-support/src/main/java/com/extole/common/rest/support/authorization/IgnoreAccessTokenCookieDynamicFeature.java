package com.extole.common.rest.support.authorization;

import java.lang.annotation.Annotation;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.model.AnnotatedMethod;

import com.extole.common.rest.authorization.AccessTokenParam;

@Provider
public class IgnoreAccessTokenCookieDynamicFeature implements DynamicFeature {

    public static final String IGNORE_ACCESS_TOKEN_COOKIE_PROPERTY = "ignore_access_token_cookie";

    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {
        final AnnotatedMethod annotatedMethod = new AnnotatedMethod(resourceInfo.getResourceMethod());
        for (Annotation[] parameterAnnotations : annotatedMethod.getParameterAnnotations()) {
            for (Annotation parameterAnnotation : parameterAnnotations) {
                if (parameterAnnotation instanceof AccessTokenParam
                    && !((AccessTokenParam) parameterAnnotation).readCookie()) {
                    configuration.register(new IgnoreAccessTokenCookieRequestFilter());
                }
            }
        }
    }

    @Priority(BaseAuthorizationFilter.AUTH_FILTER_PRIORITY - 1)
    private static final class IgnoreAccessTokenCookieRequestFilter implements ContainerRequestFilter {

        @Override
        public void filter(ContainerRequestContext requestContext) {
            requestContext.setProperty(IGNORE_ACCESS_TOKEN_COOKIE_PROPERTY, Boolean.TRUE);
        }
    }

}
