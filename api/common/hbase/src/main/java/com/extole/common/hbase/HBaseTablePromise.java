package com.extole.common.hbase;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.hadoop.hbase.TableName;

public class HBaseTablePromise {
    private final AtomicReference<HBaseTable> table = new AtomicReference<>();

    private final HBaseTableFactory hBaseTableFactory;
    private final TableName tableName;

    public HBaseTablePromise(HBaseTableFactory hBaseTableFactory, TableName tableName) {
        this.hBaseTableFactory = hBaseTableFactory;
        this.tableName = tableName;
    }

    public HBaseTable get() {
        if (table.get() == null) {
            synchronized (hBaseTableFactory) {
                table.compareAndSet(null, hBaseTableFactory.create(tableName.getNameAsString()));
            }
        }
        return table.get();
    }
}
