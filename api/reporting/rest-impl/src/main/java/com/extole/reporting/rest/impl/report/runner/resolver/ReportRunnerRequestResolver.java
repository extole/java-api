package com.extole.reporting.rest.impl.report.runner.resolver;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
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
import com.extole.model.entity.report.runner.ReportRunner;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.model.service.report.runner.ReportRunnerService;
import com.extole.reporting.rest.report.runner.ReportRunnerEndpoints;
import com.extole.reporting.rest.report.runner.ReportRunnerRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerUpdateRequest;
import com.extole.reporting.rest.report.type.ReportTypeUpdateRequest;

@Component
public final class ReportRunnerRequestResolver implements PolymorphicRequestTypeResolver {

    private final Set<String> subTypes;
    private final ReportRunnerService reportRunnerService;

    @Autowired
    public ReportRunnerRequestResolver(ReportRunnerService reportRunnerService) {
        this.reportRunnerService = reportRunnerService;

        JsonSubTypes jsonSubTypesAnnotation = ReportRunnerUpdateRequest.class.getDeclaredAnnotation(JsonSubTypes.class);
        if (jsonSubTypesAnnotation == null) {
            throw new MissingSubTypesAnnotationException(ReportRunnerUpdateRequest.class, JsonSubTypes.class);
        }
        subTypes = Arrays.stream(jsonSubTypesAnnotation.value())
            .map(type -> type.name())
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String resolve(PolymorphicRequestTypeResolverContext context)
        throws MissingIdRequestTypeResolverException, ReportRunnerRequestResolverRestWrapperException {

        Authorization authorization = context.getAuthorization();
        String id = context.getValue(ReportRunnerEndpoints.REPORT_RUNNER_ID_PATH_PARAM_NAME)
            .orElseThrow(
                () -> new MissingIdRequestTypeResolverException(ReportRunnerEndpoints.REPORT_RUNNER_ID_PATH_PARAM_NAME,
                    authorization.getClientId()));

        try {
            String resolvedType = getReportRunnerById(authorization, Id.valueOf(id)).getType().name();
            validateResolvedType(resolvedType);
            return resolvedType;
        } catch (ExtoleRestException e) {
            throw new ReportRunnerRequestResolverRestWrapperException(authorization.getClientId(), Id.valueOf(id),
                e);
        }
    }

    private ReportRunner getReportRunnerById(Authorization authorization, Id<ReportRunner> id)
        throws ReportRunnerRestException, UserAuthorizationRestException {
        try {
            return reportRunnerService.getById(authorization, id);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("id", id)
                .withCause(e).build();
        }
    }

    private void validateResolvedType(String resolvedType) {
        if (!subTypes.contains(resolvedType)) {
            throw new InvalidResolvedTypeException(ReportTypeUpdateRequest.class, resolvedType, JsonSubTypes.class);
        }
    }
}
