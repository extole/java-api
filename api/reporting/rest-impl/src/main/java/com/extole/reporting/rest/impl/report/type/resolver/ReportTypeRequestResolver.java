package com.extole.reporting.rest.impl.report.type.resolver;

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
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.service.report.type.ReportTypeNameMissingException;
import com.extole.model.service.report.type.ReportTypeNotFoundException;
import com.extole.model.service.report.type.ReportTypeService;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.ReportTypeEndpoints;
import com.extole.reporting.rest.report.type.ReportTypeUpdateRequest;

@Component
public final class ReportTypeRequestResolver implements PolymorphicRequestTypeResolver {

    private final Set<String> subTypes;
    private final ReportTypeService reportTypeService;

    @Autowired
    public ReportTypeRequestResolver(ReportTypeService reportTypeService) {
        this.reportTypeService = reportTypeService;

        JsonSubTypes jsonSubTypesAnnotation = ReportTypeUpdateRequest.class.getDeclaredAnnotation(JsonSubTypes.class);
        if (jsonSubTypesAnnotation == null) {
            throw new MissingSubTypesAnnotationException(ReportTypeUpdateRequest.class, JsonSubTypes.class);
        }
        subTypes = Arrays.stream(jsonSubTypesAnnotation.value())
            .map(type -> type.name())
            .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    @Override
    public String resolve(PolymorphicRequestTypeResolverContext context)
        throws MissingIdRequestTypeResolverException, ReportTypeRequestResolverRestWrapperException {

        Authorization authorization = context.getAuthorization();
        String name = context.getValue(ReportTypeEndpoints.REPORT_ID_PATH_PARAM_NAME)
            .orElseThrow(() -> new MissingIdRequestTypeResolverException(ReportTypeEndpoints.REPORT_ID_PATH_PARAM_NAME,
                authorization.getClientId()));

        try {
            String resolvedType = getReportByName(authorization, name).getType().name();
            validateResolvedType(resolvedType);
            return resolvedType;
        } catch (ExtoleRestException e) {
            throw new ReportTypeRequestResolverRestWrapperException(name, authorization.getClientId(), e);
        }
    }

    private ReportType getReportByName(Authorization authorization, String name)
        throws ReportTypeRestException, UserAuthorizationRestException {
        try {
            return reportTypeService.getReportTypeByName(authorization, name);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.MISSING_TYPE)
                .withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", name)
                .withCause(e).build();
        }
    }

    private void validateResolvedType(String resolvedType) {
        if (!subTypes.contains(resolvedType)) {
            throw new InvalidResolvedTypeException(ReportTypeUpdateRequest.class, resolvedType, JsonSubTypes.class);
        }
    }
}
