package com.extole.reporting.rest.impl.report.type.uploaders;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.report.type.Format;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.entity.report.type.ReportTypeVisibility;
import com.extole.model.entity.report.type.SqlReportType;
import com.extole.model.service.report.sql.SqlReportTypeMissingDatabaseException;
import com.extole.model.service.report.sql.SqlReportTypeMissingQueryException;
import com.extole.model.service.report.sql.SqlReportTypeQueryTooLongException;
import com.extole.model.service.report.type.ReportTypeDescriptionInvalidLinkException;
import com.extole.model.service.report.type.ReportTypeDescriptionTooLongException;
import com.extole.model.service.report.type.ReportTypeDisplayNameInvalidException;
import com.extole.model.service.report.type.ReportTypeDisplayNameTooLongException;
import com.extole.model.service.report.type.ReportTypeEmptyTagNameException;
import com.extole.model.service.report.type.ReportTypeInvalidAllowedScopesException;
import com.extole.model.service.report.type.ReportTypeUpdateManagedByGitException;
import com.extole.model.service.report.type.ReportTypeVisibilityException;
import com.extole.model.service.report.type.SqlReportTypeBuilder;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.sql.SqlReportTypeValidationRestException;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;
import com.extole.reporting.rest.report.type.SqlReportTypeCreateRequest;
import com.extole.reporting.rest.report.type.SqlReportTypeUpdateRequest;

@Component
public class SqlReportTypeUploader
    implements ReportTypeCreateUploader<SqlReportTypeCreateRequest>,
    ReportTypeUpdateUploader<SqlReportTypeUpdateRequest> {

    private final ReportTypeUploaderBase reportTypeUploaderBase;

    @Autowired
    public SqlReportTypeUploader(ReportTypeUploaderBase reportTypeUploaderBase) {
        this.reportTypeUploaderBase = reportTypeUploaderBase;
    }

    @Override
    public SqlReportType upload(Authorization authorization, SqlReportTypeCreateRequest reportTypeRequest)
        throws AuthorizationException, ReportTypeValidationRestException {
        SqlReportTypeBuilder<?> builder =
            (SqlReportTypeBuilder<?>) reportTypeUploaderBase.builder(authorization, reportTypeRequest);
        reportTypeUploaderBase.upload(reportTypeRequest, builder);
        try {
            if (reportTypeRequest.getDisplayName().isPresent()
                && !Strings.isNullOrEmpty(reportTypeRequest.getDisplayName().get())) {
                builder.withDisplayName(reportTypeRequest.getDisplayName().get());
            }
            if (reportTypeRequest.getDescription().isPresent()
                && !Strings.isNullOrEmpty(reportTypeRequest.getDescription().get())) {
                builder.withDescription(reportTypeRequest.getDescription().get());
            }
            reportTypeRequest.getCategories().ifPresent(builder::withCategories);
            Optional<Set<ReportType.Scope>> scopes = reportTypeRequest.getScopes()
                .map(value -> value.stream()
                    .map(reportTypeScope -> ReportType.Scope.valueOf(reportTypeScope.name()))
                    .collect(Collectors.toSet()));
            if (scopes.isPresent()) {
                builder.withScopes(scopes.get());
            }
            if (reportTypeRequest.getVisibility().isPresent()) {
                builder
                    .withVisibility(ReportTypeVisibility.valueOf(reportTypeRequest.getVisibility().get().name()));
            }
            Optional<Set<Format>> formats = reportTypeRequest.getFormats()
                .map(value -> value.stream()
                    .map(reportFormat -> Format.valueOf(reportFormat.name()))
                    .collect(Collectors.toSet()));
            if (formats.isPresent()) {
                builder.withFormats(formats.get());
            }
            if (reportTypeRequest.getAllowedScopes().isPresent()) {
                builder.withAllowedScopes(reportTypeRequest.getAllowedScopes().get().stream()
                    .map(reportTypeScope -> ReportType.Scope.valueOf(reportTypeScope.name()))
                    .collect(Collectors.toSet()));
            }
            if (reportTypeRequest.getDataStart().isPresent()) {
                builder.withDataStart(reportTypeRequest.getDataStart().get());
            }

            if (reportTypeRequest.getDatabase() != null) {
                builder.withDatabase(SqlReportType.Database.valueOf(reportTypeRequest.getDatabase().name()));
            }
            if (reportTypeRequest.getQuery() != null) {
                builder.withQuery(reportTypeRequest.getQuery());
            }
        } catch (ReportTypeDescriptionTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.DESCRIPTION_TOO_LONG)
                .withCause(e)
                .build();
        } catch (ReportTypeVisibilityException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.VISIBILITY_INVALID)
                .withCause(e)
                .build();
        } catch (ReportTypeInvalidAllowedScopesException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_ALLOWED_SCOPES)
                .withCause(e)
                .build();
        } catch (ReportTypeDisplayNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.DISPLAY_NAME_TOO_LONG)
                .withCause(e)
                .build();
        } catch (ReportTypeDescriptionInvalidLinkException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_DESCRIPTION_LINK)
                .withCause(e)
                .build();
        } catch (SqlReportTypeQueryTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.QUERY_TOO_LONG)
                .withCause(e)
                .build();
        } catch (ReportTypeDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .withCause(e)
                .build();
        } catch (ReportTypeUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_UPDATE)
                .withCause(e)
                .build();
        }
        return save(builder);
    }

    @Override
    public SqlReportType upload(Authorization authorization, String name, SqlReportTypeUpdateRequest reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
        SqlReportTypeBuilder<?> builder =
            (SqlReportTypeBuilder<?>) reportTypeUploaderBase.builder(authorization, name);
        reportTypeUploaderBase.upload(reportTypeRequest, builder);
        try {
            if (reportTypeRequest.getDisplayName().isPresent()
                && !Strings.isNullOrEmpty(reportTypeRequest.getDisplayName().get())) {
                builder.withDisplayName(reportTypeRequest.getDisplayName().get());
            }
            if (reportTypeRequest.getDescription().isPresent()
                && !Strings.isNullOrEmpty(reportTypeRequest.getDescription().get())) {
                builder.withDescription(reportTypeRequest.getDescription().get());
            }
            reportTypeRequest.getCategories().ifPresent(builder::withCategories);
            Optional<Set<ReportType.Scope>> scopes = reportTypeRequest.getScopes()
                .map(value -> value.stream()
                    .map(reportTypeScope -> ReportType.Scope.valueOf(reportTypeScope.name()))
                    .collect(Collectors.toSet()));
            if (scopes.isPresent()) {
                builder.withScopes(scopes.get());
            }
            if (reportTypeRequest.getVisibility().isPresent()) {
                builder.withVisibility(ReportTypeVisibility.valueOf(reportTypeRequest.getVisibility().get().name()));
            }
            Optional<Set<Format>> formats = reportTypeRequest.getFormats()
                .map(value -> value.stream()
                    .map(reportFormat -> Format.valueOf(reportFormat.name()))
                    .collect(Collectors.toSet()));
            if (formats.isPresent()) {
                builder.withFormats(formats.get());
            }
            if (reportTypeRequest.getAllowedScopes().isPresent()) {
                builder.withAllowedScopes(reportTypeRequest.getAllowedScopes().get().stream()
                    .map(reportTypeScope -> ReportType.Scope.valueOf(reportTypeScope.name()))
                    .collect(Collectors.toSet()));
            }
            if (reportTypeRequest.getDataStart().isPresent()) {
                builder.withDataStart(reportTypeRequest.getDataStart().get());
            }
            if (reportTypeRequest.getDatabase() != null) {
                builder.withDatabase(SqlReportType.Database.valueOf(reportTypeRequest.getDatabase().name()));
            }
            if (reportTypeRequest.getQuery() != null) {
                builder.withQuery(reportTypeRequest.getQuery());
            }
        } catch (ReportTypeDescriptionTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.DESCRIPTION_TOO_LONG)
                .withCause(e)
                .build();
        } catch (ReportTypeVisibilityException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.VISIBILITY_INVALID)
                .withCause(e)
                .build();
        } catch (ReportTypeInvalidAllowedScopesException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_ALLOWED_SCOPES)
                .withCause(e)
                .build();
        } catch (ReportTypeDisplayNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.DISPLAY_NAME_TOO_LONG)
                .withCause(e)
                .build();
        } catch (ReportTypeDescriptionInvalidLinkException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_DESCRIPTION_LINK)
                .withCause(e)
                .build();
        } catch (SqlReportTypeQueryTooLongException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.QUERY_TOO_LONG)
                .withCause(e)
                .build();
        } catch (ReportTypeDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .withCause(e)
                .build();
        } catch (ReportTypeUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_UPDATE)
                .withCause(e)
                .build();
        }
        return save(builder);
    }

    private SqlReportType save(SqlReportTypeBuilder<?> builder)
        throws AuthorizationException, ReportTypeValidationRestException {
        try {
            return builder.save();
        } catch (SqlReportTypeMissingDatabaseException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.MISSING_DATABASE)
                .withCause(e)
                .build();
        } catch (SqlReportTypeMissingQueryException e) {
            throw RestExceptionBuilder.newBuilder(SqlReportTypeValidationRestException.class)
                .withErrorCode(SqlReportTypeValidationRestException.MISSING_QUERY)
                .withCause(e)
                .build();
        } catch (ReportTypeEmptyTagNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_TAG_NAME)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportType.Type getType() {
        return ReportType.Type.SQL;
    }
}
