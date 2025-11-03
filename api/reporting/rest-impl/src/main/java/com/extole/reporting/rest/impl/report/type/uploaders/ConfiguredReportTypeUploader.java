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
import com.extole.model.entity.report.type.ConfiguredReportType;
import com.extole.model.entity.report.type.Format;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.entity.report.type.ReportTypeVisibility;
import com.extole.model.service.report.type.ConfiguredReportTypeBuilder;
import com.extole.model.service.report.type.ParentReportTypeIdMissingException;
import com.extole.model.service.report.type.ReportTypeClientsException;
import com.extole.model.service.report.type.ReportTypeDescriptionInvalidLinkException;
import com.extole.model.service.report.type.ReportTypeDescriptionTooLongException;
import com.extole.model.service.report.type.ReportTypeDisplayNameInvalidException;
import com.extole.model.service.report.type.ReportTypeDisplayNameTooLongException;
import com.extole.model.service.report.type.ReportTypeEmptyParameterNameException;
import com.extole.model.service.report.type.ReportTypeEmptyTagNameException;
import com.extole.model.service.report.type.ReportTypeInvalidAllowedScopesException;
import com.extole.model.service.report.type.ReportTypeNotFoundException;
import com.extole.model.service.report.type.ReportTypeParameterDescriptionTooLongException;
import com.extole.model.service.report.type.ReportTypeStaticParameterChangeException;
import com.extole.model.service.report.type.ReportTypeStaticParameterCreateException;
import com.extole.model.service.report.type.ReportTypeStaticParameterDeleteException;
import com.extole.model.service.report.type.ReportTypeUpdateManagedByGitException;
import com.extole.model.service.report.type.ReportTypeVisibilityException;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.ConfiguredReportTypeCreateRequest;
import com.extole.reporting.rest.report.type.ConfiguredReportTypeUpdateRequest;
import com.extole.reporting.rest.report.type.ConfiguredReportTypeValidationRestException;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;

@Component
public class ConfiguredReportTypeUploader
    implements ReportTypeCreateUploader<ConfiguredReportTypeCreateRequest>,
    ReportTypeUpdateUploader<ConfiguredReportTypeUpdateRequest> {

    private final ReportTypeUploaderBase reportTypeUploaderBase;

    @Autowired
    public ConfiguredReportTypeUploader(ReportTypeUploaderBase reportTypeUploaderBase) {
        this.reportTypeUploaderBase = reportTypeUploaderBase;
    }

    @Override
    public ConfiguredReportType upload(Authorization authorization, ConfiguredReportTypeCreateRequest reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
        ConfiguredReportTypeBuilder<?> builder =
            (ConfiguredReportTypeBuilder<?>) reportTypeUploaderBase.builder(authorization, reportTypeRequest);
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

            if (reportTypeRequest.getParentReportTypeId() != null) {
                builder.withParentReportTypeId(reportTypeRequest.getParentReportTypeId());
            }
            if (reportTypeRequest.getParameters().isPresent()) {
                builder.withParameters(reportTypeRequest.getParameters().get().stream()
                    .map(ReportTypeParameterMapper::map)
                    .collect(Collectors.toSet()));
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
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", reportTypeRequest.getParentReportTypeId())
                .withCause(e).build();
        } catch (ReportTypeDescriptionInvalidLinkException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.INVALID_DESCRIPTION_LINK)
                .withCause(e)
                .build();
        } catch (ParentReportTypeIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(ConfiguredReportTypeValidationRestException.class)
                .withErrorCode(ConfiguredReportTypeValidationRestException.MISSING_PARENT_REPORT_TYPE_ID)
                .withCause(e).build();
        } catch (ReportTypeDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .addParameter("name", reportTypeRequest.getDisplayName())
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
    public ConfiguredReportType upload(Authorization authorization, String name,
        ConfiguredReportTypeUpdateRequest reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
        ConfiguredReportTypeBuilder<?> builder =
            (ConfiguredReportTypeBuilder<?>) reportTypeUploaderBase.builder(authorization, name);
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
            if (reportTypeRequest.getParameters().isPresent()) {
                builder.withParameters(reportTypeRequest.getParameters().get().stream()
                    .map(ReportTypeParameterMapper::map)
                    .collect(Collectors.toSet()));
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
        } catch (ReportTypeDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .addParameter("name", reportTypeRequest.getDisplayName())
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

    private ConfiguredReportType save(ConfiguredReportTypeBuilder<?> builder)
        throws AuthorizationException, ReportTypeValidationRestException {
        try {
            return builder.save();
        } catch (ReportTypeClientsException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.CLIENTS_INVALID)
                .withCause(e)
                .build();
        } catch (ParentReportTypeIdMissingException e) {
            throw RestExceptionBuilder.newBuilder(ConfiguredReportTypeValidationRestException.class)
                .withErrorCode(ConfiguredReportTypeValidationRestException.MISSING_PARENT_REPORT_TYPE_ID)
                .withCause(e)
                .build();
        } catch (ReportTypeVisibilityException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.VISIBILITY_INVALID)
                .withCause(e)
                .build();
        } catch (ReportTypeEmptyTagNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_TAG_NAME)
                .withCause(e)
                .build();
        } catch (ReportTypeEmptyParameterNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_PARAMETER_NAME)
                .withCause(e)
                .build();
        } catch (ReportTypeParameterDescriptionTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.PARAMETER_DESCRIPTION_TOO_LONG)
                .withCause(e).build();
        } catch (ReportTypeStaticParameterCreateException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.PARAMETER_STATIC_ADD)
                .withCause(e)
                .addParameter("parameters", e.getParameterNames())
                .build();
        } catch (ReportTypeStaticParameterChangeException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.PARAMETER_STATIC_UPDATE)
                .withCause(e)
                .addParameter("parameters", e.getParameterNames())
                .build();
        } catch (ReportTypeStaticParameterDeleteException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.PARAMETER_STATIC_DELETE)
                .withCause(e)
                .addParameter("parameters", e.getParameterNames())
                .build();
        }
    }

    @Override
    public ReportType.Type getType() {
        return ReportType.Type.CONFIGURED;
    }
}
