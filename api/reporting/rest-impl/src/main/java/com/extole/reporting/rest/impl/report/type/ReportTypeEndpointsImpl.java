package com.extole.reporting.rest.impl.report.type;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.request.resolver.ResolvesPolymorphicType;
import com.extole.id.Id;
import com.extole.model.entity.report.type.ReportOrderDirection;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.entity.report.type.ReportType.Type;
import com.extole.model.entity.report.type.ReportTypeOrderBy;
import com.extole.model.entity.report.type.ReportTypeTagType;
import com.extole.model.entity.report.type.ReportTypeVisibility;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.report.type.ConfiguredReportTypeBuilder;
import com.extole.model.service.report.type.ReportTypeBuilder;
import com.extole.model.service.report.type.ReportTypeClientsException;
import com.extole.model.service.report.type.ReportTypeEmptyParameterNameException;
import com.extole.model.service.report.type.ReportTypeEmptyTagNameException;
import com.extole.model.service.report.type.ReportTypeException;
import com.extole.model.service.report.type.ReportTypeIsReferencedDeleteException;
import com.extole.model.service.report.type.ReportTypeNameMissingException;
import com.extole.model.service.report.type.ReportTypeNotFoundException;
import com.extole.model.service.report.type.ReportTypeParameterDescriptionTooLongException;
import com.extole.model.service.report.type.ReportTypeQueryBuilder;
import com.extole.model.service.report.type.ReportTypeService;
import com.extole.model.service.report.type.ReportTypeStaticParameterChangeException;
import com.extole.model.service.report.type.ReportTypeStaticParameterCreateException;
import com.extole.model.service.report.type.ReportTypeStaticParameterDeleteException;
import com.extole.reporting.rest.impl.report.execution.ReportRuntimeException;
import com.extole.reporting.rest.impl.report.type.mappers.ReportTypeResponseMapper;
import com.extole.reporting.rest.impl.report.type.resolver.ReportTypeRequestResolver;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeCreateUploader;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeParameterMapper;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeTagImpl;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeUpdateUploader;
import com.extole.reporting.rest.impl.report.type.uploaders.ReportTypeUploaderBase;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.DynamicReportTypeParameterDetailsRequest;
import com.extole.reporting.rest.report.type.ReportTypeCreateRequest;
import com.extole.reporting.rest.report.type.ReportTypeEndpoints;
import com.extole.reporting.rest.report.type.ReportTypeGetRequest;
import com.extole.reporting.rest.report.type.ReportTypeResponse;
import com.extole.reporting.rest.report.type.ReportTypeUpdateRequest;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;
import com.extole.reporting.rest.report.type.ReportTypeWithClientsResponse;

@Provider
public class ReportTypeEndpointsImpl implements ReportTypeEndpoints {
    private static final int DEFAULT_LIMIT = 100;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportTypeUploadersRepository reportTypeUploadersRepository;
    private final ReportTypeUploaderBase reportTypeUploaderBase;
    private final ReportTypeResponseMappersRepository reportTypeResponseMappersRepository;
    private final ReportTypeService reportTypeService;
    private final ClientService clientService;

    @Autowired
    public ReportTypeEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        ReportTypeUploadersRepository reportTypeUploadersRepository,
        ReportTypeResponseMappersRepository reportTypeResponseMappersRepository,
        ReportTypeUploaderBase reportTypeUploaderBase,
        ReportTypeService reportTypeService,
        ClientService clientService) {
        this.authorizationProvider = authorizationProvider;
        this.reportTypeUploadersRepository = reportTypeUploadersRepository;
        this.reportTypeUploaderBase = reportTypeUploaderBase;
        this.reportTypeResponseMappersRepository = reportTypeResponseMappersRepository;
        this.reportTypeService = reportTypeService;
        this.clientService = clientService;
    }

    @Override
    public ReportTypeResponse createReportType(String accessToken, ReportTypeCreateRequest request)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ReportType reportType = upload(new CreateReportTypeSupplier(request, authorization));
        ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
        return toReportTypeResponse(authorization, clientTimezone, reportType);
    }

    @Override
    @ResolvesPolymorphicType(resolver = ReportTypeRequestResolver.class)
    public ReportTypeResponse updateReportType(String accessToken, String id, ReportTypeUpdateRequest request)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ReportType reportType = upload(new UpdateReportTypeSupplier(authorization, id, request));
        ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
        return toReportTypeResponse(authorization, clientTimezone, reportType);
    }

    @Override
    public ReportTypeResponse readReportType(String accessToken, String id)
        throws UserAuthorizationRestException, ReportTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportType reportType = reportTypeService.getReportTypeByName(authorization, id);
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
            return toReportTypeResponse(authorization, clientTimezone, reportType);
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
                .addParameter("id", id)
                .withCause(e).build();
        }
    }

    @Override
    public ReportTypeResponse deleteReportType(String accessToken, String id)
        throws UserAuthorizationRestException, ReportTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportType reportType = reportTypeService.deleteReportType(authorization, id);
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
            return toReportTypeResponse(authorization, clientTimezone, reportType);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", id)
                .withCause(e).build();
        } catch (ReportTypeIsReferencedDeleteException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_HAS_DEPENDENT_TYPES)
                .addParameter("id", id)
                .addParameter("dependent_report_type_ids", e.getChildIds())
                .withCause(e).build();
        }
    }

    @Override
    public List<? extends ReportTypeResponse> listReportTypes(String accessToken, ReportTypeGetRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());

        ReportTypeQueryBuilder listFilterBuilder = reportTypeService.getReportTypes(authorization);

        request.getReportTypeId().map(Id::<ReportType>valueOf).ifPresent(listFilterBuilder::withReportTypeId);
        request.getDisplayName().ifPresent(listFilterBuilder::withDisplayName);
        request.getDescription().ifPresent(listFilterBuilder::withDescription);
        request.getVisibility()
            .map(reportTypeVisibility -> ReportTypeVisibility.valueOf(reportTypeVisibility.name()))
            .ifPresent(listFilterBuilder::withVisibility);
        request.getTags()
            .ifPresent(tags -> listFilterBuilder.withTags(Arrays.stream(tags.split(",")).collect(Collectors.toSet())));
        request.getExcludeTags().ifPresent(excludeTags -> listFilterBuilder
            .withExcludeTags(Arrays.stream(excludeTags.split(",")).collect(Collectors.toSet())));
        request.getSearchQuery().ifPresent(listFilterBuilder::withSearchQuery);
        if (request.getLimit().isPresent()) {
            listFilterBuilder.withLimit(parseLimit(request.getLimit().get().toString(), DEFAULT_LIMIT));
        }
        if (request.getOffset().isPresent()) {
            listFilterBuilder
                .withOffset(parseOffset(request.getOffset().get().toString(), BigDecimal.ZERO.intValue()));
        }

        request.getOrderBy()
            .map(reportTypeOrderBy -> ReportTypeOrderBy
                .valueOf(reportTypeOrderBy.name()))
            .ifPresent(listFilterBuilder::withOrderBy);
        request.getOrderDirection()
            .map(reportOrderDirection -> ReportOrderDirection
                .valueOf(reportOrderDirection.name()))
            .ifPresent(listFilterBuilder::withOrder);

        return toReportTypesResponse(authorization, clientTimezone, listFilterBuilder.execute());
    }

    @Override
    public List<String> listReportTypeClients(String accessToken, String id)
        throws UserAuthorizationRestException, ReportTypeRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            checkSuperuserAccessRights(authorization);
            ReportType reportType = reportTypeService.getReportTypeByName(authorization, id);
            return reportType.getClients().stream().map(Id::getValue).collect(Collectors.toList());
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
                .addParameter("id", id)
                .withCause(e).build();
        }
    }

    @Override
    public List<ReportTypeWithClientsResponse> listReportTypesWithClients(String accessToken,
        ReportTypeGetRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            checkSuperuserAccessRights(authorization);
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());

            ReportTypeQueryBuilder listFilterBuilder = reportTypeService.getAllReportTypes(authorization);

            request.getReportTypeId().map(Id::<ReportType>valueOf).ifPresent(listFilterBuilder::withReportTypeId);
            request.getDisplayName().ifPresent(listFilterBuilder::withDisplayName);
            request.getDescription().ifPresent(listFilterBuilder::withDescription);
            request.getVisibility()
                .map(reportTypeVisibility -> ReportTypeVisibility.valueOf(reportTypeVisibility.name()))
                .ifPresent(listFilterBuilder::withVisibility);
            request.getTags().ifPresent(
                tags -> listFilterBuilder.withTags(Arrays.stream(tags.split(",")).collect(Collectors.toSet())));
            request.getExcludeTags().ifPresent(excludeTags -> listFilterBuilder
                .withExcludeTags(Arrays.stream(excludeTags.split(",")).collect(Collectors.toSet())));
            if (request.getLimit().isPresent()) {
                listFilterBuilder.withLimit(parseLimit(request.getLimit().get().toString(), DEFAULT_LIMIT));
            }
            if (request.getOffset().isPresent()) {
                listFilterBuilder
                    .withOffset(parseOffset(request.getOffset().get().toString(), BigDecimal.ZERO.intValue()));
            }

            request.getOrderBy()
                .map(reportTypeOrderBy -> ReportTypeOrderBy
                    .valueOf(reportTypeOrderBy.name()))
                .ifPresent(listFilterBuilder::withOrderBy);
            request.getOrderDirection()
                .map(reportOrderDirection -> ReportOrderDirection
                    .valueOf(reportOrderDirection.name()))
                .ifPresent(listFilterBuilder::withOrder);

            List<? extends ReportType> reportTypes = listFilterBuilder.execute();

            return reportTypes.stream()
                .map(reportType -> ReportTypeWithClientsResponse.builder()
                    .withReportType(toReportTypeResponse(authorization, clientTimezone, reportType))
                    .withClientIds(reportType.getClients().stream().map(Id::getValue).collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ReportTypeResponse updateReportTypeClients(String accessToken, String id, List<String> clientIds)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ReportType reportType = upload(new UpdateReportTypeClientsSupplier(authorization, id,
            clientIds.stream().map(clientId -> Id.<ClientHandle>valueOf(clientId)).collect(Collectors.toSet())));
        ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
        return toReportTypeResponse(authorization, clientTimezone, reportType);
    }

    @Override
    public ReportTypeResponse deleteReportTypeClients(String accessToken, String id, List<String> clientIds)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ReportType reportType = upload(new DeleteReportTypeClientsSupplier(authorization, id,
            clientIds.stream().map(clientId -> Id.<ClientHandle>valueOf(clientId)).collect(Collectors.toSet())));
        ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
        return toReportTypeResponse(authorization, clientTimezone, reportType);
    }

    @Override
    public ReportTypeResponse addReportTypeTags(String accessToken, String id, List<String> tags)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportTypeBuilder<?, ?> builder = reportTypeUploaderBase.builder(authorization, id);
            builder.addTags(tags.stream()
                .map(tag -> new ReportTypeTagImpl(tag, ReportTypeTagType.PRIVATE))
                .collect(Collectors.toSet()));
            ReportType reportType = (ReportType) builder.save();
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
            return toReportTypeResponse(authorization, clientTimezone, reportType);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeEmptyTagNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_TAG_NAME)
                .withCause(e)
                .build();
        } catch (ReportTypeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportTypeResponse deleteReportTypeTags(String accessToken, String id, List<String> tags)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportTypeBuilder<?, ?> builder = reportTypeUploaderBase.builder(authorization, id);
            builder.removeTags(tags.stream()
                .map(tag -> new ReportTypeTagImpl(tag, ReportTypeTagType.PRIVATE))
                .collect(Collectors.toSet()));
            ReportType reportType = (ReportType) builder.save();
            ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
            return toReportTypeResponse(authorization, clientTimezone, reportType);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeEmptyTagNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_TAG_NAME)
                .withCause(e)
                .build();
        } catch (ReportTypeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportTypeResponse addReportTypeParameters(String accessToken, String id,
        List<DynamicReportTypeParameterDetailsRequest> parameters)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportTypeBuilder<?, ?> builder = reportTypeUploaderBase.builder(authorization, id);
            if (builder instanceof ConfiguredReportTypeBuilder) {
                ConfiguredReportTypeBuilder<?> configuredBuilder = (ConfiguredReportTypeBuilder<?>) builder;
                configuredBuilder.addParameters(parameters.stream()
                    .map(ReportTypeParameterMapper::map)
                    .collect(Collectors.toSet()));
                ReportType reportType = configuredBuilder.save();
                ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
                return toReportTypeResponse(authorization, clientTimezone, reportType);
            } else {
                throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                    .withErrorCode(ReportTypeValidationRestException.INVALID_UPDATE)
                    .build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeEmptyTagNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_TAG_NAME)
                .withCause(e)
                .build();
        } catch (ReportTypeParameterDescriptionTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.PARAMETER_DESCRIPTION_TOO_LONG)
                .withCause(e)
                .build();
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
        } catch (ReportTypeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportTypeResponse deleteReportTypeParameters(String accessToken, String id, List<String> parameters)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportTypeBuilder<?, ?> builder = reportTypeUploaderBase.builder(authorization, id);
            if (builder instanceof ConfiguredReportTypeBuilder) {
                ConfiguredReportTypeBuilder<?> configuredBuilder = (ConfiguredReportTypeBuilder<?>) builder;
                configuredBuilder.removeParameters(new HashSet<>(parameters));
                ReportType reportType = configuredBuilder.save();
                ZoneId clientTimezone = getClientTimeZoneId(authorization.getClientId());
                return toReportTypeResponse(authorization, clientTimezone, reportType);
            } else {
                throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                    .withErrorCode(ReportTypeValidationRestException.INVALID_UPDATE)
                    .build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeEmptyParameterNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_PARAMETER_NAME)
                .withCause(e)
                .build();
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
        } catch (ReportTypeException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T extends ReportTypeResponse> T toReportTypeResponse(Authorization authorization, ZoneId clientTimezone,
        ReportType reportType) {
        ReportTypeResponseMapper responseMapper =
            reportTypeResponseMappersRepository.getReportTypeResponseMapper(reportType.getType());
        return (T) responseMapper.toReportTypeResponse(authorization, clientTimezone, reportType);
    }

    private List<ReportTypeResponse> toReportTypesResponse(Authorization authorization, ZoneId clientTimezone,
        List<? extends ReportType> reportTypes) {
        List<ReportTypeResponse> responses = Lists.newArrayList();
        for (ReportType reportType : reportTypes) {
            responses.add(toReportTypeResponse(authorization, clientTimezone, reportType));
        }
        return responses;
    }

    private interface Supplier {
        ReportType execute()
            throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException;
    }

    private ReportType upload(Supplier supplier)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException {
        try {
            return supplier.execute();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private void checkSuperuserAccessRights(Authorization authorization) throws AuthorizationException {
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            throw new AuthorizationException("Access denied");
        }
    }

    private final class CreateReportTypeSupplier implements Supplier {
        private final ReportTypeCreateRequest request;
        private final Authorization authorization;

        private CreateReportTypeSupplier(ReportTypeCreateRequest request, Authorization authorization) {
            this.request = request;
            this.authorization = authorization;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public ReportType execute()
            throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
            ReportTypeCreateUploader reportTypeUploader =
                reportTypeUploadersRepository.getReportTypeCreateUploader(Type.valueOf(request.getType().name()));
            return reportTypeUploader.upload(authorization, request);
        }
    }

    private final class UpdateReportTypeSupplier implements Supplier {
        private final Authorization authorization;
        private final String id;
        private final ReportTypeUpdateRequest request;

        private UpdateReportTypeSupplier(Authorization authorization, String id,
            ReportTypeUpdateRequest request) {
            this.authorization = authorization;
            this.id = id;
            this.request = request;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public ReportType execute()
            throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
            try {
                ReportType reportType = reportTypeService.getReportTypeByName(authorization, id);
                if (!reportType.getType().name().equals(request.getType().name())) {
                    throw new ReportRuntimeException("Unsupported report type: " + request.getType());
                }
                ReportTypeUpdateUploader reportTypeUploader =
                    reportTypeUploadersRepository.getReportTypeUpdateUploader(reportType.getType());
                return reportTypeUploader.upload(authorization, id, request);
            } catch (ReportTypeNameMissingException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            } catch (ReportTypeNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                    .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                    .addParameter("id", id)
                    .withCause(e)
                    .build();
            }
        }
    }

    private final class UpdateReportTypeClientsSupplier implements Supplier {
        private final Authorization authorization;
        private final String id;
        private final Set<Id<ClientHandle>> clientIds;

        private UpdateReportTypeClientsSupplier(Authorization authorization, String id,
            Set<Id<ClientHandle>> clientIds) {
            this.authorization = authorization;
            this.id = id;
            this.clientIds = clientIds;
        }

        @Override
        public ReportType execute()
            throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
            try {
                return reportTypeService.updateReportTypeClients(authorization, id).addClients(clientIds).save();
            } catch (ReportTypeNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                    .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                    .addParameter("id", id)
                    .withCause(e)
                    .build();
            } catch (ReportTypeClientsException e) {
                throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                    .withErrorCode(ReportTypeValidationRestException.CLIENTS_INVALID)
                    .withCause(e)
                    .build();
            } catch (ReportTypeException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        }
    }

    private final class DeleteReportTypeClientsSupplier implements Supplier {
        private final Authorization authorization;
        private final String id;
        private final Set<Id<ClientHandle>> clientIds;

        private DeleteReportTypeClientsSupplier(Authorization authorization, String id,
            Set<Id<ClientHandle>> clientIds) {
            this.authorization = authorization;
            this.id = id;
            this.clientIds = clientIds;
        }

        @Override
        public ReportType execute()
            throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
            try {
                return reportTypeService.updateReportTypeClients(authorization, id).removeClients(clientIds).save();
            } catch (ReportTypeNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                    .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                    .addParameter("id", id)
                    .withCause(e)
                    .build();
            } catch (ReportTypeClientsException e) {
                throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                    .withErrorCode(ReportTypeValidationRestException.CLIENTS_INVALID)
                    .withCause(e)
                    .build();
            } catch (ReportTypeException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        }
    }

    private ZoneId getClientTimeZoneId(Id<ClientHandle> clientId) {
        try {
            return clientService.getPublicClientById(clientId).getTimeZone();
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }
}
