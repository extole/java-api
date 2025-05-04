package com.extole.api.impl.model;

import com.extole.api.model.SparkReportType;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.report.type.SparkReportTypePojo;

final class SparkReportTypeImpl implements SparkReportType {

    private final SparkReportTypePojo sparkReportType;

    SparkReportTypeImpl(SparkReportTypePojo sparkReportType) {
        this.sparkReportType = sparkReportType;
    }

    @Override
    public String getId() {
        return sparkReportType.getId().getValue();
    }

    @Override
    public String getType() {
        return sparkReportType.getType().name();
    }

    @Override
    public String getName() {
        return sparkReportType.getName();
    }

    @Override
    public String getDisplayName() {
        return sparkReportType.getDisplayName();
    }

    @Override
    public String getDescription() {
        return sparkReportType.getDescription();
    }

    @Override
    public String[] getCategories() {
        return sparkReportType.getCategories().toArray(String[]::new);
    }

    @Override
    public String[] getScopes() {
        return sparkReportType.getScopes().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getVisibility() {
        return sparkReportType.getVisibility().name();
    }

    @Override
    public String[] getTags() {
        return sparkReportType.getTags().stream().map(value -> value.getName()).toArray(String[]::new);
    }

    @Override
    public String[] getFormats() {
        return sparkReportType.getFormats().stream().map(value -> value.name()).toArray(String[]::new);
    }

    @Override
    public String getDataStart() {
        return sparkReportType.getDataStart().toString();
    }

    @Override
    public String getExecutorType() {
        return "SPARK";
    }

    @Override
    public String[] getAllowedScopes() {
        return sparkReportType.getAllowedScopes().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
