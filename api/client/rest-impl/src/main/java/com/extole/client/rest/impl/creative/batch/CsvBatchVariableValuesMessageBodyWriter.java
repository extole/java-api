package com.extole.client.rest.impl.creative.batch;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import com.extole.common.lang.ObjectMapperProvider;

@Provider
@Priority(Priorities.ENTITY_CODER)
@Produces(CsvBatchVariableValuesMessageBodyWriter.CSV_MEDIA_TYPE)
public class CsvBatchVariableValuesMessageBodyWriter implements MessageBodyWriter<List<BatchVariableValuesResponse>> {

    static final String CSV_MEDIA_TYPE = "text/csv";

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    private final CsvMapper csvMapper;

    public CsvBatchVariableValuesMessageBodyWriter() {
        this.csvMapper = new CsvMapper();
        this.csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Arrays.stream(annotations)
            .anyMatch(annotation -> annotation.annotationType().equals(BatchVariableValuesResponseBinding.class));
    }

    @Override
    public long getSize(List<BatchVariableValuesResponse> response, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<BatchVariableValuesResponse> response, Class<?> type, Type genericType,
        Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        List<Map<String, String>> variables = getVariables(response);
        List<String> headers = getAllHeaders(variables);

        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        for (String header : headers) {
            schemaBuilder.addColumn(header);
        }
        if (!variables.isEmpty()) {
            schemaBuilder.setUseHeader(true);
        }

        JsonNode tree = OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(response));
        CsvSchema csvSchema = schemaBuilder.build();
        entityStream.write(csvMapper.writer(csvSchema).writeValueAsString(tree).getBytes());
    }

    private List<Map<String, String>> getVariables(List<BatchVariableValuesResponse> responses) {
        List<Map<String, String>> variables = new ArrayList<>();
        responses.forEach(response -> {
            Map<String, String> variableMap = new LinkedHashMap<>();
            variableMap.put("zone", response.getZone());
            variableMap.put("journeyNames", response.getJourneyNames());
            variableMap.put("name", response.getName());
            variableMap.putAll(response.getValues());
            variables.add(variableMap);
        });

        return variables;
    }

    private List<String> getAllHeaders(List<Map<String, String>> response) {
        return response.stream().map(variableMap -> variableMap.keySet())
            .flatMap(variableMapKeySet -> variableMapKeySet.stream()).distinct()
            .collect(Collectors.toList());
    }

}
