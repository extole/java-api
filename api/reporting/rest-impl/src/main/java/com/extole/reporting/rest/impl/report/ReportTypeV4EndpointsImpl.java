package com.extole.reporting.rest.impl.report;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableList;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.entity.report.type.ReportTypeColumn;
import com.extole.model.entity.report.type.ReportTypeParameterDetails;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.report.type.ReportTypeNameMissingException;
import com.extole.model.service.report.type.ReportTypeNotFoundException;
import com.extole.model.service.report.type.ReportTypeService;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.ReportTypeColumnResponse;
import com.extole.reporting.rest.report.ReportTypeParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeV4Endpoints;
import com.extole.reporting.rest.report.ReportTypeV4Response;
import com.extole.reporting.rest.report.execution.ReportFormat;

@Provider
public class ReportTypeV4EndpointsImpl implements ReportTypeV4Endpoints {
    private static final String CLIENT_ID_PARAMETER = "client_id";
    private static final String CUSTOM_CATEGORY = "custom";
    private static final String CUSTOM_CLIENT_CATEGORY_PREFIX = "client_id:";
    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportTypeService reportTypeService;
    private final ClientService clientService;

    @Inject
    public ReportTypeV4EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportTypeService reportTypeService,
        ClientService clientService) {
        this.reportTypeService = reportTypeService;
        this.authorizationProvider = authorizationProvider;
        this.clientService = clientService;
    }

    @Override
    public List<ReportTypeV4Response> listReportTypes(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        List<? extends ReportType> reportTypes = reportTypeService.getReportTypes(authorization).execute();
        ImmutableList.Builder<ReportTypeV4Response> builder = new ImmutableList.Builder<>();
        try {
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
            for (ReportType reportType : reportTypes) {
                builder.add(toResponse(authorization, reportType, clientTimezone));
            }
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
        return builder.build();
    }

    @Override
    public ReportTypeV4Response readReportType(String accessToken, String name)
        throws UserAuthorizationRestException, ReportTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportType reportType = reportTypeService.getReportTypeByName(authorization, name);
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
            return toResponse(authorization, reportType, clientTimezone);
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
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private ReportTypeV4Response toResponse(Authorization authorization, ReportType reportType, ZoneId clientTimezone) {
        return ReportTypeV4Response.builder()
            .withName(reportType.getName())
            .withDisplayName(reportType.getDisplayName())
            .withDescription(reportType.getDescription())
            .withCategories(computeCategories(authorization, reportType.getCategories()))
            .withExecutionType(ReportExecutorType.valueOf(reportType.getExecutorType().name()))
            .withScopes(toRestReportTypeScopes(authorization, reportType.getScopes()))
            .withParameters(toReportParameterTypeResponse(reportType.getParameters(authorization.getClientId())))
            .withFormats(reportType.getFormats().stream().map(format -> ReportFormat.valueOf(format.name()))
                .collect(Collectors.toList()))
            .withAllowedScopes(toRestReportTypeScopes(authorization, reportType.getAllowedScopes()))
            .withPreviewColumns(toRestReportTypeColumns(reportType.getPreviewColumns()))
            .withDataStart(reportType.getDataStart().atZone(clientTimezone))
            .build();
    }

    private static List<ReportTypeColumnResponse> toRestReportTypeColumns(List<ReportTypeColumn> previewColumns) {
        return previewColumns.stream()
            .map(column -> ReportTypeColumnResponse.builder()
                .withName(column.getName())
                .withSampleValue(column.getSampleValue().orElse(null))
                .withNote(column.getNote().orElse(null))
                .build())
            .collect(Collectors.toList());
    }

    private static Set<ReportTypeScope> toRestReportTypeScopes(Authorization authorization,
        Set<ReportType.Scope> scopes) {
        Predicate<ReportType.Scope> scopeFilter = scope -> true;
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_SUPERUSER));
        }
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_ADMIN)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_ADMIN));
        }

        return scopes.stream().filter(scopeFilter).map(ReportType.Scope::name)
            .map(ReportTypeScope::valueOf)
            .collect(Collectors.toSet());
    }

    private static List<ReportTypeParameterDetailsResponse>
        toReportParameterTypeResponse(List<ReportTypeParameterDetails> parameters) {
        return parameters.stream()
            .filter(parameter -> !parameter.getName().equalsIgnoreCase(CLIENT_ID_PARAMETER))
            .map(parameter -> new ReportTypeParameterDetailsResponse(
                parameter.getName(),
                parameter.getDisplayName(),
                parameter.getCategory().orElse(null),
                new ReportParameterTypeResponse(
                    ReportParameterTypeName.valueOf(parameter.getType().getName().name()),
                    ParameterValueType.valueOf(parameter.getType().getValueType().name()),
                    parameter.getType().getValues()),
                parameter.isRequired(),
                parameter.getOrder(),
                parameter.getDefaultValue().orElse(null),
                parameter.getDescription()))
            .distinct()
            .collect(Collectors.toList());
    }

    private static List<String> computeCategories(Authorization authorization, List<String> categories) {
        return categories.stream()
            .filter(category -> !category.startsWith(CUSTOM_CLIENT_CATEGORY_PREFIX)
                || category.equals(CUSTOM_CLIENT_CATEGORY_PREFIX + authorization.getClientId().getValue()))
            .map(category -> category.startsWith(CUSTOM_CLIENT_CATEGORY_PREFIX) ? CUSTOM_CATEGORY : category)
            .collect(Collectors.toList());
    }

    private ZoneId getClientTimeZoneId(Id<ClientHandle> clientId) throws ClientNotFoundException {
        return clientService.getPublicClientById(clientId).getTimeZone();
    }
}
