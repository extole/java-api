package com.extole.file.parser.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.extole.common.lang.ObjectMapperProvider;

final class XlsxFileContentReadWriteStrategy implements FileContentWriteStrategy, FileContentReadStrategy {
    private static final GridConverter GRID_CONVERTER = GridConverter.getInstance();
    private final ObjectMapper objectMapper;

    XlsxFileContentReadWriteStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ParsedFileContent writeFileContent(List<T> list) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet();
            Row headerRow = sheet.createRow(0);

            List<Map<String, String>> grid = GRID_CONVERTER.convertListToGrid(list);
            List<String> headers = getAllHeaders(grid);

            addHeadersToSheet(headers, headerRow);
            addBodyToSheet(grid, sheet, headers);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return ParsedFileContent.ofXlsx(outputStream.toByteArray());
        }
    }

    @Override
    public <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference)
        throws IOException {

        return readFileContent(fileContentInputStream, typeReference, Function.identity()::apply);
    }

    @Override
    public <T> List<T> readFileContent(InputStream fileContentInputStream, TypeReference<T> typeReference,
        Consumer<ObjectNode> valueModifier) throws IOException {

        byte[] bytes = IOUtils.toByteArray(fileContentInputStream);
        List<ObjectNode> cells = readCellsFromXlsx(bytes);

        List<T> results = new ArrayList<>(cells.size());

        for (ObjectNode objectNode : cells) {

            valueModifier.accept(objectNode);
            T object = objectMapper.convertValue(objectNode, typeReference);
            results.add(object);
        }

        return Collections.unmodifiableList(results);
    }

    private List<ObjectNode> readCellsFromXlsx(byte[] bytes) throws IOException {
        if (bytes.length == 0) {
            return Collections.emptyList();
        }

        List<ObjectNode> variableMap = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            int rowCount = 0;
            List<String> headers = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                ObjectNode objectNode = ObjectMapperProvider.getConfiguredInstance().createObjectNode();
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (rowCount == 0) {
                        headers.add(cell.getStringCellValue());
                    } else {
                        String header = headers.get(cell.getColumnIndex());
                        if (!cell.getStringCellValue().isEmpty()) {
                            objectNode.set(header,
                                objectMapper.getNodeFactory().textNode(cell.getStringCellValue()));
                        }
                    }
                }
                if (rowCount > 0 && !objectNode.isEmpty()) {
                    variableMap.add(deserializeEachValueSeparately(objectNode));
                }
                rowCount++;
            }
        } catch (NotOfficeXmlFileException e) {
            throw new IOException(e);
        }

        return variableMap;
    }

    private ObjectNode deserializeEachValueSeparately(ObjectNode objectNode) throws JsonProcessingException {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = objectNode.fields();
        ObjectNode newObjectNode = objectMapper.createObjectNode();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            JsonNode newNode = convertXlsxCellNodeToPojoFieldNode(fieldValue);
            newObjectNode.set(fieldName, newNode);
        }
        return newObjectNode;
    }

    private JsonNode convertXlsxCellNodeToPojoFieldNode(JsonNode fieldValue) throws JsonProcessingException {
        JsonNode result;
        try {
            result = objectMapper.readValue(fieldValue.asText(), JsonNode.class);
            if (result instanceof NumericNode && !NumberUtils.isCreatable(fieldValue.asText())) {
                result = objectMapper.readValue(fieldValue.toString(), JsonNode.class);
            }
        } catch (JsonProcessingException e) {
            result = objectMapper.readValue(fieldValue.toString(), JsonNode.class);
        }
        return result;
    }

    private List<String> getAllHeaders(List<Map<String, String>> response) {
        return response.stream().map(variableMap -> variableMap.keySet())
            .flatMap(variableMapKeySet -> variableMapKeySet.stream()).distinct()
            .collect(Collectors.toUnmodifiableList());
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
                    String cellValue = variableMap.get(header);
                    if (cellValue == null) {
                        cell.setCellValue("null");
                    } else if (cellValue.isEmpty()) {
                        cell.setCellValue("\"\"");
                    } else {
                        cell.setCellValue(cellValue);
                    }
                } else {
                    cell.setCellValue("");
                }
            }
        }
    }
}
