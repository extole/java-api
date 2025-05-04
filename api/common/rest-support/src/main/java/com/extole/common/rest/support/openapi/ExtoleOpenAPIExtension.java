package com.extole.common.rest.support.openapi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.jaxrs2.DefaultParameterExtension;
import io.swagger.v3.jaxrs2.ResolvedParameter;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Components;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.time.TimeZoneParam;

public class ExtoleOpenAPIExtension extends DefaultParameterExtension {

    @Override
    public ResolvedParameter extractParameters(List<Annotation> annotations, Type type, Set<Type> typesToSkip,
        Components components, Consumes classConsumes, Consumes methodConsumes, boolean includeRequestBody,
        JsonView jsonViewAnnotation, Iterator<OpenAPIExtension> chain) {
        for (Annotation annotation : annotations) {
            if (shouldIgnore(annotation)) {
                return new ResolvedParameter();
            }
        }
        return super.extractParameters(annotations, type, typesToSkip, components, classConsumes, methodConsumes,
            includeRequestBody, jsonViewAnnotation, chain);
    }

    private static boolean shouldIgnore(Annotation annotation) {
        // make sure our custom annotated parameters are not treated by the Swagger as a request body
        return annotation instanceof UserAccessTokenParam
            || annotation instanceof AccessTokenParam
            || annotation instanceof TimeZoneParam;
    }
}
