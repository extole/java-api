package com.extole.common.hbase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.filter.FilterList;

public final class HBaseTableGetRowQuery {

    private final Get query;
    private final String rowKey;
    private final String family;
    private final List<String> columnQualifiers;

    private HBaseTableGetRowQuery(Get query, String rowKey, String family, List<String> columnQualifiers) {
        this.query = query;
        this.rowKey = rowKey;
        this.family = family;
        this.columnQualifiers = columnQualifiers;
    }

    public Get getQuery() {
        return query;
    }

    public String getRowKey() {
        return rowKey;
    }

    public String getFamily() {
        return family;
    }

    public List<String> getColumnQualifiers() {
        return columnQualifiers;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "HBaseTableGetRowRequest{" +
            "query=" + query +
            '}';
    }

    public static final class Builder {
        private String rowKey;
        private String family;
        private Optional<FilterList> filters = Optional.empty();
        private final List<String> columnQualifiers = new ArrayList<>();

        private Builder() {
        }

        public Builder withRowKey(String rowKey) {
            this.rowKey = rowKey;
            return this;
        }

        public Builder withFamily(String family) {
            this.family = family;
            return this;
        }

        public Builder withFilters(FilterList filters) {
            this.filters = Optional.of(filters);
            return this;
        }

        public Builder withColumnQualifiers(Collection<String> columnQualifiers) {
            this.columnQualifiers.clear();
            this.columnQualifiers.addAll(columnQualifiers);
            return this;
        }

        public HBaseTableGetRowQuery build() {
            validate();

            Get get = new Get(rowKey.getBytes())
                .addFamily(family.getBytes());
            filters.ifPresent(get::setFilter);

            columnQualifiers.forEach(columnQualifier -> get.addColumn(family.getBytes(), columnQualifier.getBytes()));
            return new HBaseTableGetRowQuery(get, rowKey, family, columnQualifiers);
        }

        private void validate() {
            Preconditions.checkNotNull(rowKey, "rowKey cannot be null");
            Preconditions.checkNotNull(family, "family cannot be null");
        }
    }
}
