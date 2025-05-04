package com.extole.common.rest.support.request.body.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.time.TimeZoneParam;

public class OptionalRequestBodyParamHandler implements InvocationHandler {
    private static final String METRIC_NAME = "missing_request_body";
    private static final List<? extends Class<? extends Annotation>> PARAM_ANNOTATION_CLASSES = List.of(PathParam.class,
        QueryParam.class, HeaderParam.class, CookieParam.class, MatrixParam.class, FormParam.class, FormDataParam.class,
        BeanParam.class, AccessTokenParam.class, UserAccessTokenParam.class, TimeZoneParam.class);

    private final ExtoleMetricRegistry metricRegistry;

    public OptionalRequestBodyParamHandler(ExtoleMetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (isRequestBody(parameters[i])) {
                if (parameters[i].getType().equals(Optional.class)) {
                    if (args[i] == null) {
                        args[i] = Optional.empty();
                    } else if (!args[i].getClass().isAssignableFrom(Optional.class)) {
                        args[i] = Optional.of(args[i]);
                    }
                } else if (args[i] == null) {
                    metricRegistry.counter(buildMetricName(method)).increment();

                    WebApplicationRestRuntimeException restException =
                        RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                            .withErrorCode(WebApplicationRestRuntimeException.MISSING_REQUEST_BODY)
                            .build();

                    RestExceptionResponse response = new RestExceptionResponseBuilder()
                        .withUniqueId(String.valueOf(restException.getUniqueId()))
                        .withHttpStatusCode(restException.getHttpStatusCode())
                        .withCode(restException.getErrorCode())
                        .withMessage(restException.getMessage())
                        .withParameters(restException.getParameters())
                        .build();

                    return Response.status(response.getHttpStatusCode())
                        .entity(response)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                }
            }
        }
        return method.invoke(obj, args);
    }

    private String buildMetricName(Method method) {
        return METRIC_NAME + "." + method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    private boolean isRequestBody(Parameter parameter) {
        return Arrays.stream(parameter.getAnnotations()).map(Annotation::annotationType)
            .noneMatch(PARAM_ANNOTATION_CLASSES::contains);
    }
}
