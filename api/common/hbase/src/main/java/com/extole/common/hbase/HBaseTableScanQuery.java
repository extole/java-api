package com.extole.common.hbase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import org.apache.hadoop.hbase.client.Scan;

public final class HBaseTableScanQuery<ROW> {

    private final String rowPrefix;
    private final String family;
    private final List<String> columnQualifiers;
    private final Class<ROW> clazz;
    private final HBaseTable.RowBuilder<ROW> rowBuilder;
    private final Scan scan;

    private HBaseTableScanQuery(String rowPrefix,
        String family,
        List<String> columnQualifiers,
        Class<ROW> clazz,
        HBaseTable.RowBuilder<ROW> rowBuilder,
        Scan scan) {
        this.rowPrefix = rowPrefix;
        this.family = family;
        this.columnQualifiers = columnQualifiers;
        this.clazz = clazz;
        this.rowBuilder = rowBuilder;
        this.scan = scan;
    }

    public String getRowPrefix() {
        return rowPrefix;
    }

    public String getFamily() {
        return family;
    }

    public List<String> getColumnQualifiers() {
        return columnQualifiers;
    }

    public Class<ROW> getClazz() {
        return clazz;
    }

    public HBaseTable.RowBuilder<ROW> getRowBuilder() {
        return rowBuilder;
    }

    public Scan getQuery() {
        return scan;
    }

    public static <ROW> Builder<ROW> builder() {
        return new Builder<>();
    }

    @Override
    public String toString() {
        return "HBaseTableScanRequest{" + "scan='" + scan + '}';
    }

    public static final class Builder<ROW> {
        private String rowPrefix;
        private String family;
        private final List<String> columnQualifiers = new ArrayList<>();
        private Class<ROW> clazz;
        private HBaseTable.RowBuilder<ROW> rowBuilder;
        private Optional<String> startRowKeyExclusive = Optional.empty();
        private Optional<Integer> rowsLimit = Optional.empty();

        private Builder() {
        }

        public Builder<ROW> withRowPrefix(String rowPrefix) {
            this.rowPrefix = rowPrefix;
            return this;
        }

        public Builder<ROW> withFamily(String family) {
            this.family = family;
            return this;
        }

        public Builder<ROW> withColumnQualifiers(List<String> columnQualifiers) {
            this.columnQualifiers.clear();
            this.columnQualifiers.addAll(columnQualifiers);
            return this;
        }

        public Builder<ROW> withRowBuilder(Class<ROW> clazz, HBaseTable.RowBuilder<ROW> rowBuilder) {
            this.clazz = clazz;
            this.rowBuilder = rowBuilder;
            return this;
        }

        public Builder<ROW> withRowsLimit(int rowsLimit) {
            this.rowsLimit = Optional.of(Integer.valueOf(rowsLimit));
            return this;
        }

        public Builder<ROW> withStartRowKeyExclusive(String startRowKeyExclusive) {
            this.startRowKeyExclusive = Optional.of(startRowKeyExclusive);
            return this;
        }

        public HBaseTableScanQuery<ROW> build() {
            validate();

            Scan scan = new Scan()
                .setRowPrefixFilter(rowPrefix.getBytes());
            columnQualifiers
                .forEach(columnQualifier -> scan.addColumn(family.getBytes(), columnQualifier.getBytes()));
            rowsLimit.ifPresent(scan::setLimit);
            startRowKeyExclusive.ifPresent(startRow -> scan.withStartRow(startRow.getBytes(), false));

            return new HBaseTableScanQuery<>(rowPrefix, family, columnQualifiers, clazz, rowBuilder, scan);
        }

        private void validate() {
            Preconditions.checkNotNull(rowPrefix, "rowPrefix cannot be null");
            Preconditions.checkNotNull(family, "family cannot be null");
            Preconditions.checkNotNull(columnQualifiers, "columnQualifiers cannot be null");
            Preconditions.checkNotNull(rowBuilder, "rowBuilder cannot be null");
        }
    }
}
