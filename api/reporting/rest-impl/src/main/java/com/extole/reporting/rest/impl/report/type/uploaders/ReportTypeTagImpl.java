package com.extole.reporting.rest.impl.report.type.uploaders;

import com.extole.reporting.entity.report.ReportTypeTag;
import com.extole.reporting.entity.report.ReportTypeTagType;

public class ReportTypeTagImpl implements ReportTypeTag {
    private final String name;
    private final ReportTypeTagType type;

    public ReportTypeTagImpl(String name, ReportTypeTagType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ReportTypeTagType getType() {
        return type;
    }
}
