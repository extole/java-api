package com.extole.reporting.rest.impl.report.sql;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.ReportTypeParameterDetails;
import com.extole.reporting.entity.report.type.SqlReportType;
import com.extole.reporting.entity.report.type.SqlReportType.Database;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.ReportTypeParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;
import com.extole.reporting.rest.report.sql.SqlCreateReportTypeV4Request;
import com.extole.reporting.rest.report.sql.SqlReportTypeDatabase;
import com.extole.reporting.rest.report.sql.SqlReportTypeV4Endpoints;
import com.extole.reporting.rest.report.sql.SqlReportTypeV4Response;
import com.extole.reporting.rest.report.sql.SqlReportTypeValidationRestException;
import com.extole.reporting.rest.report.sql.SqlUpdateReportTypeV4Request;
import com.extole.reporting.service.ReportTypeNotFoundException;
import com.extole.reporting.service.report.ReportDisplayNameInvalidException;
import com.extole.reporting.service.report.sql.SqlReportTypeMissingDatabaseException;
import com.extole.reporting.service.report.sql.SqlReportTypeMissingQueryException;
import com.extole.reporting.service.report.sql.SqlReportTypeQueryTooLongException;
import com.extole.reporting.service.report.sql.SqlReportTypeService;
import com.extole.reporting.service.report.type.ReportTypeDescriptionInvalidLinkException;
import com.extole.reporting.service.report.type.ReportTypeDescriptionTooLongException;
import com.extole.reporting.service.report.type.ReportTypeDisplayNameTooLongException;
import com.extole.reporting.service.report.type.ReportTypeEmptyTagNameException;
import com.extole.reporting.service.report.type.ReportTypeInvalidAllowedScopesException;
import com.extole.reporting.service.report.type.ReportTypeInvalidNameException;
import com.extole.reporting.service.report.type.ReportTypeIsReferencedDeleteException;
import com.extole.reporting.service.report.type.ReportTypeNameDuplicationException;
import com.extole.reporting.service.report.type.ReportTypeNameTooLongException;
import com.extole.reporting.service.report.type.ReportTypeUpdateManagedByGitException;
import com.extole.reporting.service.report.type.ReportTypeVisibilityException;
import com.extole.reporting.service.report.type.SqlReportTypeBuilder;

@Provider
public class SqlReportTypeV4EndpointsImpl implements SqlReportTypeV4Endpoints {
    private static final String CLIENT_ID_PARAMETER = "client_id";
    private static final String CUSTOM_CATEGORY = "custom";
    private static final String CUSTOM_CLIENT_CATEGORY_PREFIX = "client_id:";
    private final ClientAuthorizationProvider authorizationProvider;
    private final SqlReportTypeService sqlReportTypeService;

    @Inject
    public SqlReportTypeV4EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        SqlReportTypeService sqlReportTypeService) {
        this.authorizationProvider = authorizationProvider;
        this.sqlReportTypeService = sqlReportTypeService;
    }

    @Override
    public List<SqlReportTypeV4Response> listSqlReportTypes(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        List<SqlReportType> reportTypes = sqlReportTypeService.getReportTypes(authorization);
        return reportTypes.stream().map(reportType -> toResponse(authorization, reportType, timeZone))
            .collect(Collectors.toList());
    }

    @Override
    public SqlReportTypeV4Response readSqlReportType(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SqlReportType reportType = sqlReportTypeService.getReportTypeByName(authorization, name);
            return toResponse(authorization, reportType, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", name)
                .withCause(e).build();
        }
    }

    @Override
    public SqlReportTypeV4Response createSqlReportType(String accessToken, SqlCreateReportTypeV4Request request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, SqlReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SqlReportTypeBuilder builder = sqlReportTypeService.createReportType(authorization);
            if (!Strings.isNullOrEmpty(request.getName())) {
                builder.withName(request.getName());
            }
            if (!Strings.isNullOrEmpty(request.getDisplayName())) {
                builder.withDisplayName(request.getDisplayName());
            }
            if (!Strings.isNullOrEmpty(request.getDescription())) {
                builder.withDescription(request.getDescription());
            }
            builder.withQuery(request.getQuery());
            if (request.getScopes().isPresent()) {
                builder.withScopes(toReportTypeScopes(request.getScopes().get()));
            }
            if (request.getCategories() != null) {
                builder.withCategories(request.getCategories());
            }
            if (request.getDatabase() != null) {
                builder.withDatabase(Database.valueOf(request.getDatabase().name()));
            }
            if (request.getVisibility() != null) {
                builder.withVisibility(
                    com.extole.reporting.entity.report.ReportTypeVisibility.valueOf(request.getVisibility().name()));
            }
            if (request.getAllowedScopes() != null) {
                builder.withAllowedScopes(toReportTypeScopes(request.getAllowedScopes()));
            } else {
                if (ReportTypeVisibility.EXTOLE_ONLY.equals(request.getVisibility())) {
                    builder.withAllowedScopes(ImmutableSet.of(ReportType.Scope.CLIENT_SUPERUSER));
                } else {
                    builder.withAllowedScopes(
                        ImmutableSet.of(ReportType.Scope.CLIENT_SUPERUSER, ReportType.Scope.CLIENT_ADMIN));
                }
            }
            SqlReportType reportType = builder.save();
            return toResponse(authorization, reportType, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (SqlReportTypeQueryTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.QUERY_TOO_LONG).withCause(e).build();
        } catch (ReportTypeInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.NAME_INVALID)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (ReportTypeNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.NAME_TOO_LONG).withCause(e).build();
        } catch (ReportTypeNameDuplicationException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.NAME_DUPLICATED)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (SqlReportTypeMissingDatabaseException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.MISSING_DATABASE).withCause(e).build();
        } catch (SqlReportTypeMissingQueryException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.MISSING_QUERY).withCause(e).build();
        } catch (ReportTypeVisibilityException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.VISIBILITY_INVALID).withCause(e).build();
        } catch (ReportTypeInvalidAllowedScopesException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.INVALID_ALLOWED_SCOPES).withCause(e).build();
        } catch (ReportTypeUpdateManagedByGitException | ReportTypeEmptyTagNameException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (ReportTypeDisplayNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DISPLAY_NAME_TOO_LONG).withCause(e).build();
        } catch (ReportTypeDescriptionTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DESCRIPTION_TOO_LONG).withCause(e).build();
        } catch (ReportTypeDescriptionInvalidLinkException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.INVALID_DESCRIPTION_LINK)
                .withCause(e)
                .build();
        } catch (ReportDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SqlReportTypeV4Response updateSqlReportType(String accessToken, String name,
        SqlUpdateReportTypeV4Request request, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportTypeRestException, SqlReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            SqlReportTypeBuilder builder = sqlReportTypeService.updateReportType(authorization, name);
            if (!Strings.isNullOrEmpty(request.getName())) {
                builder.withName(request.getName());
            }
            if (!Strings.isNullOrEmpty(request.getDisplayName())) {
                builder.withDisplayName(request.getDisplayName());
            }
            if (!Strings.isNullOrEmpty(request.getDescription())) {
                builder.withDescription(request.getDescription());
            }
            if (!Strings.isNullOrEmpty(request.getQuery())) {
                builder.withQuery(request.getQuery());
            }
            if (request.getScopes() != null) {
                builder.withScopes(toReportTypeScopes(request.getScopes()));
            }
            if (request.getCategories() != null) {
                builder.withCategories(request.getCategories());
            }
            if (request.getDatabase() != null) {
                builder.withDatabase(Database.valueOf(request.getDatabase().name()));
            }
            if (request.getVisibility() != null) {
                builder.withVisibility(
                    com.extole.reporting.entity.report.ReportTypeVisibility.valueOf(request.getVisibility().name()));
            }
            if (request.getAllowedScopes() != null) {
                builder.withAllowedScopes(toReportTypeScopes(request.getAllowedScopes()));
            }
            SqlReportType reportType = builder.save();
            return toResponse(authorization, reportType, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", name)
                .withCause(e).build();
        } catch (SqlReportTypeQueryTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.QUERY_TOO_LONG).withCause(e).build();
        } catch (ReportTypeInvalidNameException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.NAME_INVALID)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (ReportTypeNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.NAME_TOO_LONG).withCause(e).build();
        } catch (ReportTypeNameDuplicationException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.NAME_DUPLICATED)
                .addParameter("name", request.getName())
                .withCause(e).build();
        } catch (ReportTypeVisibilityException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.VISIBILITY_INVALID).withCause(e).build();
        } catch (SqlReportTypeMissingDatabaseException | SqlReportTypeMissingQueryException
            | ReportTypeEmptyTagNameException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (ReportTypeInvalidAllowedScopesException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.INVALID_ALLOWED_SCOPES).withCause(e).build();
        } catch (ReportTypeUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.INVALID_UPDATE).withCause(e).build();
        } catch (ReportTypeDisplayNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DISPLAY_NAME_TOO_LONG).withCause(e).build();
        } catch (ReportTypeDescriptionTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DESCRIPTION_TOO_LONG).withCause(e).build();
        } catch (ReportTypeDescriptionInvalidLinkException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.INVALID_DESCRIPTION_LINK)
                .withCause(e)
                .build();
        } catch (ReportDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .withCause(e)
                .build();
        }
    }

    @Override
    public SqlReportTypeV4Response deleteSqlReportType(String accessToken, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            SqlReportType reportType = sqlReportTypeService.deleteReportType(authorization, name);
            return toResponse(authorization, reportType, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", name)
                .withCause(e).build();
        } catch (ReportTypeIsReferencedDeleteException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_HAS_DEPENDENT_TYPES)
                .addParameter("id", name)
                .addParameter("dependent_report_type_ids", e.getChildIds())
                .withCause(e).build();
        }
    }

    // TODO switch to scopes in ENG-9584
    private SqlReportTypeV4Response toResponse(Authorization authorization, SqlReportType sqlReportType,
        ZoneId timeZone) {
        return new SqlReportTypeV4Response(sqlReportType.getName(), sqlReportType.getDisplayName(),
            sqlReportType.getDescription(), computeCategories(authorization, sqlReportType.getCategories()),
            toRestReportTypeScopes(authorization, sqlReportType.getScopes()),
            SqlReportTypeDatabase.valueOf(sqlReportType.getDatabase().name()),
            sqlReportType.getCreatedDate().atZone(timeZone), sqlReportType.getUpdatedDate().atZone(timeZone),
            sqlReportType.getQuery(),
            toReportParameterTypeResponse(sqlReportType.getParameters(authorization.getClientId())),
            ReportTypeVisibility.valueOf(sqlReportType.getVisibility().name()),
            toRestReportTypeScopes(authorization, sqlReportType.getAllowedScopes()));
    }

    private Set<ReportTypeScope> toRestReportTypeScopes(Authorization authorization, Set<ReportType.Scope> scopes) {
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

    private Set<ReportType.Scope> toReportTypeScopes(Set<ReportTypeScope> scopes) {
        return scopes.stream().map(scope -> ReportType.Scope.valueOf(scope.name())).collect(Collectors.toSet());
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
}
