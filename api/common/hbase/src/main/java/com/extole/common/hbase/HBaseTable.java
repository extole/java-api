package com.extole.common.hbase;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.hbase.HBaseTable.RowBuilder.RowBuildException;
import com.extole.common.metrics.ExtoleMetricRegistry;

public class HBaseTable {
    private static final Logger LOG = LoggerFactory.getLogger(HBaseTable.class);

    @FunctionalInterface
    public interface RowBuilder<ROW> {
        Optional<ROW> build(String rowKey, Map<String, byte[]> columnValues) throws RowBuildException;

        final class RowBuildException extends Exception {
            public RowBuildException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }

    private final Connection connection;
    private final TableName tableName;
    private final ExtoleMetricRegistry metricRegistry;
    private final String metricPrefix;

    public HBaseTable(Connection connection, TableName tableName, ExtoleMetricRegistry metricRegistry) {
        this.connection = connection;
        this.tableName = tableName;
        this.metricRegistry = metricRegistry;
        this.metricPrefix = "hbase.persistence." + tableName.getNameAsString();
    }

    @WithSpan
    public Optional<Integer> getAsInteger(String rowKey, String family, String columnKey) {
        Instant startTime = Instant.now();
        Get get = new Get(rowKey.getBytes());
        get.addColumn(family.getBytes(), columnKey.getBytes());
        try (Table table = connection.getTable(tableName)) {
            Result result = getFromTable(table, get, startTime);
            byte[] value = result.getValue(family.getBytes(), columnKey.getBytes());
            return value != null ? Optional.of(Integer.valueOf(Bytes.toInt(value))) : Optional.empty();
        } catch (IOException e) {
            throw new HbaseServiceRuntimeException(
                "Failed to retrieve schema version for rowKey" + rowKey + " with column " + columnKey, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".getAsInteger").update(startTime, Instant.now());
            LOG.debug("HBaseTable.getAsInteger took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }

    }

    @WithSpan
    public List<String> getColumnKeys(String rowKey, List<String> columnPrefixes, Optional<PageRequest> pageRequest) {
        Instant startTime = Instant.now();
        List<Filter> filters = Lists.newArrayList(
            new FilterList(FilterList.Operator.MUST_PASS_ONE, columnPrefixes.stream()
                .map(columnPrefix -> new ColumnPrefixFilter(columnPrefix.getBytes()))
                .collect(Collectors.toList())),
            new KeyOnlyFilter());
        pageRequest.ifPresent(
            request -> filters.add(new ColumnPaginationFilter(request.limit(), request.offset())));
        try (Table table = connection.getTable(tableName)) {
            Result result = getFromTable(table, new Get(rowKey.getBytes())
                .setFilter(new FilterList(filters)),
                startTime);
            metricRegistry.histogram(metricPrefix + ".get").update(startTime, Instant.now());
            if (result.isEmpty()) {
                return Lists.newArrayList();
            }
            return result.listCells().stream()
                .map(cell -> Bytes.toString(CellUtil.cloneQualifier(cell)))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new HbaseServiceRuntimeException(
                "Failed to retrieve column keys with prefix " + columnPrefixes + " for row " + rowKey, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".getColumnKeys").update(startTime, Instant.now());
            LOG.debug("HBaseTable.getColumnKeys took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }
    }

    @WithSpan
    public <T> List<T> getColumnValues(String rowKey, String family, Collection<String> columnKeys,
        Function<String, T> valueMapper) {
        return columnKeys.isEmpty()
            ? Lists.newArrayList()
            : getColumnValues(HBaseTableGetColumnsQuery.<T>builder()
                .withRowKey(rowKey)
                .withFamily(family)
                .withColumnQualifiers(columnKeys)
                .withColumnBuilder(valueMapper)
                .build());
    }

    @WithSpan
    public <T> List<T> getColumnValues(HBaseTableGetColumnsQuery<T> query) {
        Instant startTime = Instant.now();
        try (Table table = connection.getTable(tableName)) {
            Collection<Cell> values = getColumnValues(table, query).values();
            List<T> elements = Lists.newArrayListWithCapacity(values.size());
            Instant cellReadOperationStartTime = Instant.now();
            List<String> strings = Lists.newArrayListWithCapacity(values.size());
            for (Cell cell : values) {
                strings.add(Bytes.toString(CellUtil.cloneValue(cell)));
            }
            LOG.debug("HBaseTable.getColumnValues cell read took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - cellReadOperationStartTime.toEpochMilli()));
            Instant mappingOperationStartTime = Instant.now();
            for (String elementJson : strings) {
                elements.add(query.getColumnBuilder().apply(elementJson));
            }
            LOG.debug("HBaseTable.getColumnValues mapping took {} ms for {} elements",
                Long.valueOf(Instant.now().toEpochMilli() - mappingOperationStartTime.toEpochMilli()),
                Integer.valueOf(elements.size()));

            return elements;
        } catch (IOException e) {
            throw new HbaseServiceRuntimeException(
                "Failed to retrieve column values for " + query, e);
        } finally {
            LOG.debug("HBaseTable.getColumnValues for {} took {} ms", query,
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
            metricRegistry.histogram(metricPrefix + ".getColumnValues").update(startTime, Instant.now());
        }
    }

    @WithSpan
    public <ROW> List<ROW> getRowsForKeyPrefix(HBaseTableScanQuery<ROW> query) throws HBaseException {
        Instant startTime = Instant.now();
        List<ROW> rows = Lists.newArrayList();
        try (Table table = connection.getTable(tableName)) {
            List<Result> scanResults = scanFromTable(table, query.getQuery(), startTime);
            if (scanResults.isEmpty()) {
                return rows;
            }
            for (Result scanResult : scanResults) {
                mapResultToRow(
                    query.getRowPrefix(),
                    query.getFamily(),
                    query.getColumnQualifiers(),
                    query.getClazz(),
                    query.getRowBuilder(), scanResult)
                        .ifPresent(rows::add);
            }
        } catch (IOException e) {
            throw new HBaseException("Failed to retrieve rows for query " + query, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".getRowsForKeyPrefix").update(startTime, Instant.now());
            LOG.debug("HBaseTable.getRowsForKeyPrefix took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }

        return rows;
    }

    @WithSpan
    public <ROW> Optional<ROW> getRow(HBaseTableGetRowQuery request, Class<ROW> clazz, RowBuilder<ROW> rowBuilder)
        throws HBaseException {
        Instant startTime = Instant.now();
        try {
            Get get = request.getQuery();

            try (Table table = connection.getTable(tableName)) {
                Result result = getFromTable(table, get, startTime);
                return mapResultToRow(request.getRowKey(), request.getFamily(), request.getColumnQualifiers(), clazz,
                    rowBuilder, result);
            }
        } catch (IOException e) {
            throw new HBaseException("Failed to retrieve row for " + request, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".getRow").update(startTime, Instant.now());
            LOG.debug("HBaseTable.getRow took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }
    }

    @WithSpan
    public <ROW> Map<String, ROW> getRowsBatch(List<HBaseTableGetRowQuery> requests, Class<ROW> clazz,
        RowBuilder<ROW> rowBuilder) throws HBaseException {
        validateRequestsConsistency(requests);
        Instant startTime = Instant.now();
        if (requests.isEmpty()) {
            return new HashMap<>();
        }
        String family = requests.get(0).getFamily();
        List<String> columnQualifiers = requests.get(0).getColumnQualifiers();
        try {
            List<Get> gets = requests.stream().map(request -> request.getQuery()).collect(Collectors.toList());

            try (Table table = connection.getTable(tableName)) {
                return getFromTableBatch(table, gets, startTime).stream()
                    .filter(result -> Bytes.toString(result.getRow()) != null)
                    .collect(Collectors.<Result, String, Optional<ROW>>toMap(
                        result -> Bytes.toString(result.getRow()),
                        result -> mapResultToRow(Bytes.toString(result.getRow()), family, columnQualifiers,
                            clazz, rowBuilder, result),
                        (value1, value2) -> {
                            LOG.warn("Obtained multiple values for one of the keys, requests: {}", gets);
                            return value1;
                        }))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue().isPresent())
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
            }
        } catch (IOException e) {
            throw new HBaseException(
                "Failed to retrieve rows for keys " + requests + " family " + family + " and columns "
                    + columnQualifiers,
                e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".getRowsBatch").update(startTime, Instant.now());
            LOG.debug("HBaseTable.getRowsBatch took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }
    }

    @WithSpan
    public boolean checkAndMutate(String rowKey, String family, String qualifier,
        CompareOperator compareOperation, byte[] expectedCompareOperationValue, List<Mutation> content)
        throws HBaseException {
        Instant startTime = Instant.now();
        try (Table table = connection.getTable(tableName)) {
            return table.checkAndMutate(rowKey.getBytes(), family.getBytes())
                .qualifier(qualifier.getBytes())
                .ifMatches(compareOperation, expectedCompareOperationValue)
                .thenMutate(RowMutations.of(content));
        } catch (IOException e) {
            throw new HBaseException(
                "Failed to execute mutation " + content + " for row " + rowKey, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".checkAndMutate").update(startTime, Instant.now());
            LOG.debug("HBaseTable.checkAndMutate took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }
    }

    @WithSpan
    public boolean checkAndAdd(String rowKey, String family, String qualifier, Put content) throws HBaseException {
        try {
            return checkAndAdd(rowKey, family, qualifier, RowMutations.of(List.of(content)));
        } catch (IOException e) {
            throw new HbaseServiceRuntimeException("Failed to build mutations for " + content, e);
        }
    }

    @WithSpan
    public boolean checkAndAdd(String rowKey, String family, String qualifier, RowMutations content)
        throws HBaseException {
        Instant startTime = Instant.now();
        try (Table table = connection.getTable(tableName)) {
            return table.checkAndMutate(rowKey.getBytes(), family.getBytes())
                .qualifier(qualifier.getBytes())
                .ifNotExists()
                .thenMutate(content);
        } catch (IOException e) {
            throw new HBaseException(
                "Failed to execute add of " + content + " for row " + rowKey, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".checkAndAdd").update(startTime, Instant.now());
            LOG.debug("HBaseTable.checkAndAdd took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }
    }

    private void validateRequestsConsistency(List<HBaseTableGetRowQuery> requests) {
        if (requests.size() < 2) {
            return;
        }
        HBaseTableGetRowQuery referenceRequest = requests.get(0);
        for (int i = 1; i < requests.size(); i++) {
            HBaseTableGetRowQuery request = requests.get(i);
            if (!referenceRequest.getFamily().equals(request.getFamily())) {
                throw new IllegalArgumentException("All requests must have the same family");
            }
            if (!referenceRequest.getColumnQualifiers().equals(request.getColumnQualifiers())) {
                throw new IllegalArgumentException("All requests must have the same column qualifiers");
            }
        }
    }

    private <ROW> Optional<ROW> mapResultToRow(String row, String family, List<String> columnQualifiers,
        Class<ROW> clazz, RowBuilder<ROW> rowBuilder, Result queryResult) {
        if (queryResult == null || queryResult.isEmpty()) {
            return Optional.empty();
        }

        String rowKey = Bytes.toString(queryResult.getRow());

        Map<String, byte[]> valuesPerQualifier = new HashMap<>();
        if (!columnQualifiers.isEmpty()) {
            for (String columnQualifier : columnQualifiers) {
                byte[] value = queryResult.getValue(family.getBytes(), columnQualifier.getBytes());
                if (value == null) {
                    continue;
                }

                valuesPerQualifier.put(columnQualifier, value);
            }
        } else {
            queryResult.getFamilyMap(family.getBytes())
                .forEach((columnQualifier, value) -> valuesPerQualifier.put(Bytes.toString(columnQualifier), value));
        }

        try {
            return rowBuilder.build(rowKey, valuesPerQualifier);
        } catch (RowBuildException e) {
            throw new HbaseServiceRuntimeException(
                "Failed to map column values from row " + row + " family " + family + " " +
                    "qualifiers " + columnQualifiers + ": " + valuesPerQualifier + " to " + clazz.getName(),
                e);
        }
    }

    private Map<String, Cell> getColumnValues(Table table, HBaseTableGetColumnsQuery<?> query) {
        Instant startTime = Instant.now();
        Map<String, Cell> columnValues = new HashMap<>();
        try {
            Result result = getFromTable(table, query.getQuery(), startTime);
            if (result == null || result.isEmpty()) {
                return columnValues;
            }
            for (Cell cell : result.listCells()) {
                columnValues.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                    cell.getQualifierLength()), cell);
            }
            return columnValues;
        } catch (IOException e) {
            throw new HbaseServiceRuntimeException("Failed to retrieve column values for " + query, e);
        } finally {
            metricRegistry.histogram(metricPrefix + ".getColumnValues (internal)").update(startTime, Instant.now());
            LOG.debug("HBaseTable.getColumnValues (internal) for {} took {} ms", query,
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
        }
    }

    private Result getFromTable(Table table, Get get, Instant startTime) throws IOException {
        try {
            return table.get(get);
        } finally {
            LOG.debug("HBaseTable.getFromTable took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
            metricRegistry.histogram(metricPrefix + ".get").update(startTime, Instant.now());
        }
    }

    private List<Result> getFromTableBatch(Table table, List<Get> gets, Instant startTime) throws IOException {
        try {
            return List.of(table.get(gets));
        } finally {
            LOG.debug("HBaseTable.getFromTableBatch took {} ms",
                Long.valueOf(Instant.now().toEpochMilli() - startTime.toEpochMilli()));
            metricRegistry.histogram(metricPrefix + ".getBatch").update(startTime, Instant.now());
        }
    }

    private List<Result> scanFromTable(Table table, Scan scan, Instant startTime) throws IOException {
        List<Result> results = new ArrayList<>();
        try (ResultScanner scanner = table.getScanner(scan)) {
            Result result = scanner.next();
            while (result != null) {
                results.add(result);
                result = scanner.next();
            }
            return results;
        } finally {
            metricRegistry.histogram(metricPrefix + ".scan").update(startTime, Instant.now());
        }
    }
}
