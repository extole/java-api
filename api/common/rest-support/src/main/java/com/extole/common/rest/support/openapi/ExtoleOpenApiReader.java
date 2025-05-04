package com.extole.common.rest.support.openapi;

import static io.swagger.v3.core.util.RefUtils.constructRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.google.common.collect.ImmutableList;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.id.Id;

public class ExtoleOpenApiReader extends Reader {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleOpenApiReader.class);
    private static final List<SecurityScheme.In> SECURITY_REQUIREMENTS = ImmutableList.of(
        SecurityScheme.In.HEADER, SecurityScheme.In.COOKIE, SecurityScheme.In.QUERY);
    private static final String EXTOLE_API_PACKAGE = "com.extole.api";
    private static final List<String> PACKAGES_TO_SCAN =
        ImmutableList.of("com.extole.api", "com.extole.client.rest", "com.extole.consumer.rest",
            "com.extole.evaluateable");

    static {
        List<OpenAPIExtension> extensions = new ArrayList<>(OpenAPIExtensions.getExtensions());
        extensions.add(0, new ExtoleOpenAPIExtension());
        OpenAPIExtensions.setExtensions(extensions);
    }

    @Override
    public OpenAPI read(Set<Class<?>> classes, Map<String, Object> resources) {
        OpenAPI openAPI = read(classes);
        if (openAPI.getComponents() != null) {
            openAPI
                .getComponents()
                .addSchemas(RestExceptionResponse.class.getSimpleName(),
                    ModelConverters.getInstance().readAllAsResolvedSchema(RestExceptionResponse.class).schema);

            if (this.config.getResourcePackages().contains(EXTOLE_API_PACKAGE)) {
                addSchemasForContextApi(openAPI);
                openAPI
                    .getComponents()
                    .addSchemas(CampaignBuildtimeContext.class.getSimpleName(),
                        ModelConverters.getInstance().readAllAsResolvedSchema(CampaignBuildtimeContext.class).schema);
            }
            openAPI
                .getComponents()
                .addSchemas(Id.class.getSimpleName(),
                    ModelConverters.getInstance().readAllAsResolvedSchema(Id.class).schema);
        }
        return openAPI;
    }

    private void addSchemasForContextApi(OpenAPI openAPI) {
        Reflections reflections = new Reflections(PACKAGES_TO_SCAN);
        Set<Class<?>> typesAnnotatedWithSchema = reflections
            .getTypesAnnotatedWith(io.swagger.v3.oas.annotations.media.Schema.class)
            .stream()
            .filter(Objects::nonNull)
            .filter(item -> item.isInterface() || item.isEnum())
            .collect(Collectors.toSet());
        typesAnnotatedWithSchema.forEach(clazz -> addSchemaFor(openAPI, clazz));
    }

    private <T> void addSchemaFor(OpenAPI openAPI, Class<T> clazz) {
        Schema<?> resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(clazz).schema;
        openAPI.getComponents().addSchemas(clazz.getSimpleName(), resolvedSchema);
    }

    @Override
    public Operation parseMethod(Method method, List<Parameter> globalParameters, Produces methodProduces,
        Produces classProduces, Consumes methodConsumes, Consumes classConsumes,
        List<SecurityRequirement> classSecurityRequirements, Optional<ExternalDocumentation> classExternalDocs,
        Set<String> classTags, List<Server> classServers, boolean isSubresource, RequestBody parentRequestBody,
        ApiResponses parentResponses, JsonView jsonViewAnnotation,
        io.swagger.v3.oas.annotations.responses.ApiResponse[] classResponses,
        AnnotatedMethod annotatedMethod) {

        Operation operation = super.parseMethod(method, globalParameters, methodProduces, classProduces, methodConsumes,
            classConsumes, classSecurityRequirements, classExternalDocs, classTags, classServers, isSubresource,
            parentRequestBody, parentResponses, jsonViewAnnotation, classResponses, annotatedMethod);

        replaceDefaultResponseWith200Response(operation);
        addErrorResponsesFromMethodExceptions(operation, method);
        addSecurityRequirements(operation);

        return operation;
    }

    @Override
    protected String getOperationId(String operationId) {
        boolean operationIdUsed = existOperationId(operationId);
        String operationIdToFind = null;
        int counter = 0;
        while (operationIdUsed) {
            operationIdToFind = String.format("%s_%d", operationId, ++counter);
            operationIdUsed = existOperationId(operationIdToFind);
        }
        if (operationIdToFind != null && counter > 1) {
            operationId = operationIdToFind;
        }
        return operationId;
    }

    private static void replaceDefaultResponseWith200Response(Operation operation) {
        ApiResponse defaultResponse = operation.getResponses().getDefault();
        operation.getResponses().remove(ApiResponses.DEFAULT);
        operation.getResponses().addApiResponse("200", defaultResponse);
    }

    private static void addSecurityRequirements(Operation operation) {
        if (operation.getSecurity() == null || operation.getSecurity().isEmpty()) {
            SECURITY_REQUIREMENTS
                .forEach(item -> operation.addSecurityItem(new SecurityRequirement().addList(item.name())));
        }
    }

    private static void addErrorResponsesFromMethodExceptions(Operation operation, Method method) {
        List<Pair<String, ApiResponse>> pairs = buildErrorApiResponsesFromMethodExceptions(method);
        Map<String, ApiResponse> apiResponses = pairs.stream().collect(
            Collectors.toMap(Pair::getLeft, Pair::getRight, (oldItem, newItem) -> {
                newItem.setDescription(String.format("%s\n\n%s", oldItem.getDescription(), newItem.getDescription()));
                return newItem;
            }));
        apiResponses.forEach((key, value) -> operation.getResponses().addApiResponse(key, value));
    }

    private static List<Pair<String, ApiResponse>> buildErrorApiResponsesFromMethodExceptions(Method method) {
        List<Pair<String, ApiResponse>> methodExceptions = Stream.of(method.getExceptionTypes())
            .flatMap(exceptionType -> Stream.of(exceptionType.getFields()))
            .filter(field -> field.getType().equals(ErrorCode.class))
            .map(ExtoleOpenApiReader::toErrorApiResponse)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        List<Pair<String, ApiResponse>> defaultRuntimeExceptions =
            Arrays.stream(WebApplicationRestRuntimeException.class.getFields())
                .filter(field -> field.getType().equals(ErrorCode.class))
                .map(ExtoleOpenApiReader::toErrorApiResponse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        methodExceptions.addAll(defaultRuntimeExceptions);
        return methodExceptions;
    }

    private static Optional<Pair<String, ApiResponse>> toErrorApiResponse(Field field) {
        try {
            ErrorCode<?> errorCode = (ErrorCode<?>) field.get(null);
            return Optional.of(Pair.of(String.valueOf(errorCode.getHttpCode()), buildErrorApiResponse(errorCode)));
        } catch (IllegalAccessException e) {
            LOG.error("Unable to build ApiResponses for OpenApi documentation", e);
        }

        return Optional.empty();
    }

    private static ApiResponse buildErrorApiResponse(ErrorCode<?> errorCode) {
        Schema<?> schema = new Schema<>().$ref(constructRef(RestExceptionResponse.class.getSimpleName()));
        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setContent(new Content().addMediaType(javax.ws.rs.core.MediaType.APPLICATION_JSON, mediaType));
        apiResponse.setDescription(buildErrorDescription(errorCode));
        return apiResponse;
    }

    private static String buildErrorDescription(ErrorCode<?> errorCode) {
        return String.format("%s=%s, %s=%s", RestExceptionResponse.JSON_CODE, errorCode.getName(),
            RestExceptionResponse.JSON_MESSAGE, errorCode.getMessage());
    }

    private boolean existOperationId(String operationId) {
        if (getOpenAPI() == null) {
            return false;
        }
        if (getOpenAPI().getPaths() == null || getOpenAPI().getPaths().isEmpty()) {
            return false;
        }
        for (PathItem path : getOpenAPI().getPaths().values()) {
            Set<String> pathOperationIds = extractOperationIdFromPathItem(path);
            if (pathOperationIds.contains(operationId)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> extractOperationIdFromPathItem(PathItem path) {
        Set<String> ids = new HashSet<>();
        if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
            ids.add(path.getGet().getOperationId());
        }
        if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
            ids.add(path.getPost().getOperationId());
        }
        if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
            ids.add(path.getPut().getOperationId());
        }
        if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
            ids.add(path.getDelete().getOperationId());
        }
        if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
            ids.add(path.getOptions().getOperationId());
        }
        if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
            ids.add(path.getHead().getOperationId());
        }
        if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
            ids.add(path.getPatch().getOperationId());
        }
        return ids;
    }
}
