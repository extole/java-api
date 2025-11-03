package com.extole.file.parser.content;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;

enum CsvProcessor {
    INSTANCE;

    String write(List<Map<String, String>> elements, boolean treatNullsAsNullStrings) throws Exception {
        Objects.requireNonNull(elements);

        Map<String, Integer> headersWithIndex = getHeadersWithRelatedIndex(elements);
        StringWriter stringWriter = new StringWriter();
        try (ICSVWriter writer = new CSVWriterBuilder(stringWriter)
            .build()) {
            writer.writeNext(headersWithIndex.keySet().toArray(String[]::new));
            for (Map<String, String> rowElements : elements) {
                String[] rowArray = new String[headersWithIndex.size()];
                rowElements.forEach((key, value) -> {
                    int headerIndex = headersWithIndex.get(key).intValue();
                    rowArray[headerIndex] = treatNullsAsNullStrings
                        ? Objects.requireNonNullElse(value, "null")
                        : value;
                });
                writer.writeNext(rowArray);
            }
            return stringWriter.toString();
        }
    }

    List<Map<String, String>> read(String csvContent) throws CsvProcessException {
        Objects.requireNonNull(csvContent);

        try {
            return internalRead(csvContent);
        } catch (Exception e) {
            throw new CsvProcessException("Was not possible to process csv: " + csvContent, e);
        }
    }

    private List<Map<String, String>> internalRead(String csvContent) throws IOException, CsvException {
        List<Map<String, String>> result = new LinkedList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new StringReader(csvContent))
            .withCSVParser(new CSVParserBuilder()
                .withSeparator(',')
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .withIgnoreQuotations(false)
                .build())
            .build()) {

            List<String[]> records = new ArrayList<>(csvReader.readAll());
            if (records.isEmpty()) {
                throw new IllegalStateException("Was not possible to extract headers");
            }

            String[] headers = records.get(0);

            for (int i = 1; i < records.size(); i++) {
                String[] rowElements = records.get(i);
                Map<String, String> collectedElements = new LinkedHashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    collectedElements.put(headers[j], rowElements[j]);
                }
                result.add(unmodifiableMap(collectedElements));
            }
        }

        return unmodifiableList(result);
    }

    private Map<String, Integer> getHeadersWithRelatedIndex(List<Map<String, String>> elements) {
        Map<String, Integer> result = new LinkedHashMap<>();

        String[] headers = elements.stream().flatMap(value -> value.keySet().stream())
            .distinct()
            .toArray(String[]::new);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i], Integer.valueOf(i));
        }

        return result;
    }
}
