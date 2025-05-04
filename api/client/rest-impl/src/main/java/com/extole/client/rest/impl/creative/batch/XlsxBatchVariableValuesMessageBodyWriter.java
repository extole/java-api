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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Provider
@Priority(Priorities.ENTITY_CODER)
@Produces(XlsxBatchVariableValuesMessageBodyWriter.XLS_MEDIA_TYPE)
public class XlsxBatchVariableValuesMessageBodyWriter
    implements MessageBodyWriter<List<BatchVariableValuesResponse>> {

    static final String XLS_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

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
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet();
            Row headerRow = sheet.createRow(0);

            List<Map<String, String>> variables = getVariables(response);
            List<String> headers = getAllHeaders(variables);

            addHeadersToSheet(headers, headerRow);
            addBodyToSheet(variables, sheet, headers);

            workbook.write(entityStream);
        }
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

    private void addHeadersToSheet(List<String> headers, Row headerRow) {
        int column = 0;

        for (String header : headers) {
            Cell cell = headerRow.createCell(column++);
            cell.setCellValue(header);
        }
    }

    private void addBodyToSheet(List<Map<String, String>> response, XSSFSheet sheet, List<String> headers) {
        int rowNumber = 1;

        for (Map<String, String> variableMap : response) {
            Row row = sheet.createRow(rowNumber++);

            int cellIndex = 0;
            for (String header : headers) {
                Cell cell = row.createCell(cellIndex++);

                if (variableMap.containsKey(header)) {
                    cell.setCellValue(variableMap.get(header));
                }
            }
        }
    }

}
