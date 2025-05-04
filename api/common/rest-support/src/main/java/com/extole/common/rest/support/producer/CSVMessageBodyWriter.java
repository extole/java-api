package com.extole.common.rest.support.producer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.extole.common.lang.date.ExtoleTimeModule;

@Provider
@Produces(CSVMessageBodyWriter.CSV_MEDIA_TYPE)
public class CSVMessageBodyWriter implements MessageBodyWriter<List> {
    public static final String CSV_MEDIA_TYPE = "text/csv;qs=.5";
    private final CsvMapper csvMapper;
    private final ObjectMapper objectMapper;

    public CSVMessageBodyWriter() {
        this.objectMapper = new ObjectMapper().registerModules(new Jdk8Module(), new ExtoleTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        this.csvMapper = new CsvMapper();
        this.csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(List t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List response, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        JsonNode tree = objectMapper.readTree(objectMapper.writeValueAsString(response));
        List<String> columns = new ArrayList<>();
        if (response != null && !response.isEmpty()) {
            Iterator<String> fieldNames = tree.get(0).fieldNames();
            while (fieldNames.hasNext()) {
                columns.add(fieldNames.next());
            }
            columns.addAll(getDataStructureColumns(tree).stream()
                .filter(item -> !columns.contains(item))
                .collect(Collectors.toList()));
            columns.forEach(item -> schemaBuilder.addColumn(item));
            schemaBuilder.setUseHeader(true);
        }
        CsvSchema csvSchema = schemaBuilder.build();

        entityStream.write(csvMapper.writer(csvSchema).writeValueAsString(tree).getBytes());
    }

    private Set<String> getDataStructureColumns(JsonNode tree) {
        Set<String> columns = new HashSet<>();
        for (Iterator<JsonNode> it = tree.elements(); it.hasNext();) {
            JsonNode node = it.next();
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                columns.add(fieldNames.next());
            }
        }
        return columns;
    }

}
