package com.extole.common.persistence.persist;

import javax.sql.DataSource;

import com.google.common.base.Strings;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;

import com.extole.common.metrics.ExtoleMetricRegistry;

public class MeteredJdbcTemplate extends JdbcTemplate {

    private final MeteredTemplateExecutor meteredTemplateExecutor;

    public MeteredJdbcTemplate(DataSource dataSource, ExtoleMetricRegistry metricRegistry, DatabaseName databaseName) {
        super(dataSource);
        this.meteredTemplateExecutor = new MeteredTemplateExecutor(metricRegistry, databaseName);
    }

    @Override
    public <T> T execute(StatementCallback<T> action) {
        String sql = "StatementCallback";
        if (action instanceof SqlProvider && !Strings.isNullOrEmpty(((SqlProvider) action).getSql())) {
            sql = ((SqlProvider) action).getSql();
        }

        return meteredTemplateExecutor.execute(MeteredJdbcTemplate.class,
            MeteredTemplateExecutor.MetricType.STATEMENT_CALLBACK, sql, () -> super.execute(action));
    }

    @Override
    public <T> T execute(PreparedStatementCreator preparedStatementCreator, PreparedStatementCallback<T> action) {
        String sql = "PreparedStatementCreator.PreparedStatementCallback";
        if (preparedStatementCreator instanceof SqlProvider
            && !Strings.isNullOrEmpty(((SqlProvider) preparedStatementCreator).getSql())) {
            sql = ((SqlProvider) preparedStatementCreator).getSql();
        }

        return meteredTemplateExecutor.execute(MeteredJdbcTemplate.class,
            MeteredTemplateExecutor.MetricType.PREPARED_STATEMENT_CALLBACK, sql,
            () -> super.execute(preparedStatementCreator, action));
    }

    @Override
    public <T> T execute(CallableStatementCreator callableStatementCreator, CallableStatementCallback<T> action) {
        String sql = "CallableStatementCreator.CallableStatementCallback";
        if (callableStatementCreator instanceof SqlProvider
            && !Strings.isNullOrEmpty(((SqlProvider) callableStatementCreator).getSql())) {
            sql = ((SqlProvider) callableStatementCreator).getSql();
        }

        return meteredTemplateExecutor.execute(MeteredJdbcTemplate.class,
            MeteredTemplateExecutor.MetricType.CALLABLE, sql, () -> super.execute(callableStatementCreator, action));
    }

    @Override
    public <T> T execute(ConnectionCallback<T> action) {
        return meteredTemplateExecutor.execute(MeteredJdbcTemplate.class,
            MeteredTemplateExecutor.MetricType.CONNECTION_CALLBACK, "ConnectionCallback", () -> super.execute(action));
    }
}
