package com.extole.common.persistence.persist;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import net.sf.persist.Persist;
import net.sf.persist.Persist.ObjectFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.Id;
import com.extole.id.PrimaryKey;

/**
 * Template wrapper which takes care of getting and returning a connection from the data source for jdbc operations.
 * As more Persist specific operations need to be exposed, delegating methods should be added here.
 *
 * Note that each delegating method is responsible for checking out a connection and then releasing it when done.
 * Make sure to use spring's DataSourceUtils to both obtain and then release the connection so that the transaction
 * manager is coordinated with properly. If in a transaction, the transaction manager will handle giving the
 * same connection from call to call and not returning it to the pool until commit time even when release
 * connection is called. If not in a transaction, then getting and releasing the connection will check it out
 * and return it to the connection pool.
 *
 * @param <T> the type of the default object class that this template is being used to read/write to.
 */
public final class PersistTemplate<T> {
    private final DataSource dataSource;
    private final ExtolePersistExceptionTranslator exceptionTranslator;
    private final Class<T> defaultObjectClass;
    private final Optional<Persist.ObjectFactory> persistObjectFactory;
    private final MeteredTemplateExecutor meteredTemplateExecutor;

    public PersistTemplate(DataSource dataSource,
        ExtolePersistExceptionTranslator exceptionTranslator,
        Class<T> defaultObjectClass, ObjectFactory persistObjectFactory, ExtoleMetricRegistry metricRegistry,
        DatabaseName databaseName) {
        this.dataSource = dataSource;
        this.exceptionTranslator = exceptionTranslator;
        this.defaultObjectClass = defaultObjectClass;
        this.persistObjectFactory = Optional.ofNullable(persistObjectFactory);
        meteredTemplateExecutor = new MeteredTemplateExecutor(metricRegistry, databaseName);
    }

    /**
     * @return the row count from the insert operation.
     */
    public int insert(T object) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.INSERT, () -> {
            try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                try {
                    return Integer.valueOf(newPersistInstance(connection).insert(object));
                } catch (RuntimeException e) {
                    throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                }
            }
        }).intValue();
    }

    /**
     * @return the row count from the update operation.
     */
    public int update(T object) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.UPDATE, () -> {
            try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                try {
                    return Integer.valueOf(newPersistInstance(connection).update(object));
                } catch (RuntimeException e) {
                    throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                }
            }
        }).intValue();
    }

    /**
     * @return the row count from the delete operation.
     */
    public int delete(T object) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.DELETE, () -> {
            try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                try {
                    return Integer.valueOf(newPersistInstance(connection).delete(object));
                } catch (RuntimeException e) {
                    throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                }
            }
        }).intValue();
    }

    /**
     * @return the row count from the sql operation.
     */
    public int executeUpdate(String sql, Object... parameters) {
        return meteredTemplateExecutor
            .execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.UPDATE, sql, () -> {
                try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                    try {
                        return Integer.valueOf(newPersistInstance(connection).executeUpdate(sql, parameters));
                    } catch (RuntimeException e) {
                        throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                    }
                }
            }).intValue();
    }

    /**
     * Reads an object from the database by its primary keys.
     *
     * @param primaryKeys
     * @return the single object found, or null if the query had no results.
     */
    @Nullable
    public T readByPrimaryKey(PrimaryKey<?>... primaryKeys) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.READ_BY_KEYS,
            () -> {
                try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                    try {
                        Object[] primaryKeyObjects =
                            Arrays.stream(primaryKeys).map(primaryKey -> primaryKey.getValue()).toArray();
                        return newPersistInstance(connection).readByPrimaryKey(defaultObjectClass, primaryKeyObjects);
                    } catch (RuntimeException e) {
                        throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                    }
                }
            });
    }

    /**
     * Reads an object from the database by its primary keys.
     *
     * @param ids
     * @return the single object found, or null if the query had no results.
     */
    @Nullable
    public T readByPrimaryKey(Id<?>... ids) {
        return meteredTemplateExecutor.execute(defaultObjectClass,
            MeteredTemplateExecutor.MetricType.READ_BY_IDS_AS_KEYS, () -> {
                try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                    try {
                        Object[] idObjects = Arrays.stream(ids).map(id -> id.getValue()).toArray();
                        return newPersistInstance(connection).readByPrimaryKey(defaultObjectClass, idObjects);
                    } catch (RuntimeException e) {
                        throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                    }
                }
            });
    }

    /**
     * Executes the given sql with the given parameters. Expected form of the sql is something like:
     *
     * SELECT * FROM table WHERE column = ?
     *
     * If the given query returns more than one row, a runtime exception is thrown.
     *
     * @return the single object found, or null if the query had no results.
     */
    @Nullable
    public T read(String sql, Object... parameters) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.READ, sql,
            () -> {
                try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                    try {
                        return newPersistInstance(connection).read(defaultObjectClass, sql, parameters);
                    } catch (RuntimeException e) {
                        throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                    }
                }
            });
    }

    /**
     * Reads all objects from the database.
     *
     * @return the list of objects found, or an empty list if the query had no results.
     */
    @SuppressWarnings("unchecked")
    public <L> List<L> readAll() {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.READ_ALL, () -> {
            try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                try {
                    List<T> list = newPersistInstance(connection).readList(defaultObjectClass);
                    List<L> results = new ArrayList<>();
                    for (T object : list) {
                        results.add((L) object);
                    }
                    return results;
                } catch (RuntimeException e) {
                    throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                }
            }
        });
    }

    /**
     * Executes the given sql with the given parameters. Expected form of the sql is something like:
     *
     * SELECT * FROM table WHERE column = ?
     *
     * @return the list of objects found, or an empty list if the query had no results.
     */
    @SuppressWarnings("unchecked")
    public <L> List<L> readList(String sql, Object... parameters) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.READ, sql,
            () -> {
                try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                    try {
                        List<T> list = newPersistInstance(connection).readList(defaultObjectClass, sql, parameters);
                        List<L> results = new ArrayList<>();
                        for (T object : list) {
                            results.add((L) object);
                        }
                        return results;
                    } catch (RuntimeException e) {
                        throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                    }
                }
            });
    }

    /**
     * Reads a single object from the database by mapping the results of the SQL
     * query into an instance of {@link java.util.Map}.
     */
    public Map<String, Object> readMap(String sql, Object... parameters) {
        return meteredTemplateExecutor.execute(defaultObjectClass, MeteredTemplateExecutor.MetricType.READ, sql,
            () -> {
                try (CloseableConnectionWrapper connection = new CloseableConnectionWrapper(dataSource)) {
                    try {
                        return newPersistInstance(connection).readMap(sql, parameters);
                    } catch (RuntimeException e) {
                        throw exceptionTranslator.translateExceptionIfPossible(e, connection);
                    }
                }
            });
    }

    private Persist newPersistInstance(CloseableConnectionWrapper connectionWrapper) {
        Persist persist;
        if (persistObjectFactory.isPresent()) {
            persist = new Persist(defaultObjectClass.getName(), connectionWrapper.getConnection(),
                persistObjectFactory.get());
        } else {
            persist = new Persist(defaultObjectClass.getName(), connectionWrapper.getConnection());
        }
        persist.setUpdateAutoGeneratedKeys(true);
        return persist;
    }

    private static final class CloseableConnectionWrapper extends ConnectionWrapper implements AutoCloseable {

        CloseableConnectionWrapper(DataSource dataSource) {
            super(dataSource);
        }

        public Connection getConnection() {
            return connection;
        }

        @Override
        public void close() {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
