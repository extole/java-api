package com.extole.api.impl.model;

import com.extole.api.model.SqlReportType;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.report.type.SqlReportTypePojo;

final class SqlReportTypeImpl implements SqlReportType {

    private final SqlReportTypePojo sqlReportType;

    SqlReportTypeImpl(SqlReportTypePojo sqlReportType) {
        this.sqlReportType = sqlReportType;
    }

    @Override
    public String getId() {
        return sqlReportType.getId().getValue();
    }

    @Override
    public String getType() {
        return sqlReportType.getType().name();
    }

    @Override
    public String getName() {
        return sqlReportType.getName();
    }

    @Override
    public String getDisplayName() {
        return sqlReportType.getDisplayName();
    }

    @Override
    public String getDescription() {
        return sqlReportType.getDescription();
    }

    @Override
    public String[] getCategories() {
        return sqlReportType.getCategories().toArray(String[]::new);
    }

    @Override
    public String[] getScopes() {
        return sqlReportType.getScopes().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getVisibility() {
        return sqlReportType.getVisibility().name();
    }

    @Override
    public String[] getTags() {
        return sqlReportType.getTags().stream().map(value -> value.getName()).toArray(String[]::new);
    }

    @Override
    public String[] getFormats() {
        return sqlReportType.getFormats().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getDataStart() {
        return sqlReportType.getDataStart().toString();
    }

    @Override
    public String getExecutorType() {
        return "SQL";
    }

    @Override
    public String[] getAllowedScopes() {
        return sqlReportType.getAllowedScopes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String getDatabase() {
        return sqlReportType.getDatabase().name();
    }

    @Override
    public String getQuery() {
        return sqlReportType.getQuery();
    }

    @Override
    public String getCreatedDate() {
        return sqlReportType.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return sqlReportType.getUpdatedDate().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
