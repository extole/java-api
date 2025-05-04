package com.extole.api.impl.model;

import com.extole.api.model.DashboardReportType;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.report.type.DashboardReportTypePojo;

final class DashboardReportTypeImpl implements DashboardReportType {

    private final DashboardReportTypePojo dashboardReportType;

    DashboardReportTypeImpl(DashboardReportTypePojo dashboardReportType) {
        this.dashboardReportType = dashboardReportType;
    }

    @Override
    public String getId() {
        return dashboardReportType.getId().getValue();
    }

    @Override
    public String getType() {
        return dashboardReportType.getType().name();
    }

    @Override
    public String getName() {
        return dashboardReportType.getName();
    }

    @Override
    public String getDisplayName() {
        return dashboardReportType.getDisplayName();
    }

    @Override
    public String getDescription() {
        return dashboardReportType.getDescription();
    }

    @Override
    public String[] getCategories() {
        return dashboardReportType.getCategories().toArray(String[]::new);
    }

    @Override
    public String[] getScopes() {
        return dashboardReportType.getScopes().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getVisibility() {
        return dashboardReportType.getVisibility().name();
    }

    @Override
    public String[] getTags() {
        return dashboardReportType.getTags().stream().map(value -> value.getName()).toArray(String[]::new);
    }

    @Override
    public String[] getFormats() {
        return dashboardReportType.getFormats().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getDataStart() {
        return dashboardReportType.getDataStart().toString();
    }

    @Override
    public String getExecutorType() {
        return "DASHBOARD";
    }

    @Override
    public String[] getAllowedScopes() {
        return dashboardReportType.getAllowedScopes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
