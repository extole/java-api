package com.extole.common.hbase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import org.apache.hadoop.hbase.client.Get;

public final class HBaseTableGetColumnsQuery<COLUMN> {

    private final Get query;
    private final Function<String, COLUMN> columnBuilder;

    private HBaseTableGetColumnsQuery(Get query,
        Function<String, COLUMN> columnBuilder) {
        this.query = query;
        this.columnBuilder = columnBuilder;
    }

    public Function<String, COLUMN> getColumnBuilder() {
        return columnBuilder;
    }

    public Get getQuery() {
        return query;
    }

    public static <ROW> Builder<ROW> builder() {
        return new Builder<>();
    }

    @Override
    public String toString() {
        return "HBaseTableGetColumnsRequest{" +
            "query=" + query +
            '}';
    }

    public static final class Builder<COLUMN> {
        private String rowKey;
        private String family;
        private final List<String> columnQualifiers = new ArrayList<>();
        private Function<String, COLUMN> columnBuilder;
        private Optional<Integer> limit = Optional.empty();
        private Optional<Integer> offset = Optional.empty();

        private Builder() {
        }

        public Builder<COLUMN> withRowKey(String rowKey) {
            this.rowKey = rowKey;
            return this;
        }

        public Builder<COLUMN> withFamily(String family) {
            this.family = family;
            return this;
        }

        public Builder<COLUMN> withColumnQualifiers(Collection<String> columnQualifiers) {
            this.columnQualifiers.clear();
            this.columnQualifiers.addAll(columnQualifiers);
            return this;
        }

        public Builder<COLUMN> withColumnBuilder(Function<String, COLUMN> columnBuilder) {
            this.columnBuilder = columnBuilder;
            return this;
        }

        public Builder<COLUMN> withLimit(int limit) {
            this.limit = Optional.of(Integer.valueOf(limit));
            return this;
        }

        public Builder<COLUMN> withOffset(int offset) {
            this.offset = Optional.of(Integer.valueOf(offset));
            return this;
        }

        public HBaseTableGetColumnsQuery<COLUMN> build() {
            validate();

            Get get = new Get(rowKey.getBytes())
                .addFamily(family.getBytes());

            columnQualifiers.forEach(columnQualifier -> get.addColumn(family.getBytes(), columnQualifier.getBytes()));
            limit.ifPresent(integer -> get.setMaxResultsPerColumnFamily(integer.intValue()));
            offset.ifPresent(integer -> get.setRowOffsetPerColumnFamily(integer.intValue()));
            return new HBaseTableGetColumnsQuery<>(get, columnBuilder);
        }

        private void validate() {
            Preconditions.checkNotNull(rowKey, "rowPrefix cannot be null");
            Preconditions.checkNotNull(family, "family cannot be null");
            Preconditions.checkNotNull(columnBuilder, "column value mapper cannot be null");
        }
    }
}
