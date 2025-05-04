package com.extole.api.impl.model;

import com.extole.api.model.ReportPostHandler;
import com.extole.common.lang.ToString;
import com.extole.reporting.pojo.posthandler.ReportPostHandlerPojo;

final class ReportPostHandlerImpl implements ReportPostHandler {

    private final ReportPostHandlerPojo reportPostHandler;

    ReportPostHandlerImpl(ReportPostHandlerPojo reportPostHandler) {
        this.reportPostHandler = reportPostHandler;
    }

    @Override
    public String getId() {
        return reportPostHandler.getId().getValue();
    }

    @Override
    public String getName() {
        return reportPostHandler.getName();
    }

    @Override
    public boolean isEnabled() {
        return reportPostHandler.isEnabled();
    }

    @Override
    public String getCreatedDate() {
        return reportPostHandler.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return reportPostHandler.getUpdatedDate().toString();
    }

    @Override
    public ReportPostHandlerAction[] getActions() {
        return reportPostHandler.getActions().stream()
            .map(value -> new ReportPostHandlerActionImpl(value))
            .toArray(ReportPostHandlerAction[]::new);
    }

    @Override
    public ReportPostHandlerCondition[] getConditions() {
        return reportPostHandler.getConditions().stream()
            .map(value -> new ReportPostHandlerConditionImpl(value))
            .toArray(ReportPostHandlerCondition[]::new);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    private static final class ReportPostHandlerConditionImpl implements ReportPostHandlerCondition {
        private final String type;

        private ReportPostHandlerConditionImpl(
            com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition condition) {
            this.type = condition.getType().name();
        }

        @Override
        public String getType() {
            return type;
        }
    }

    private static final class ReportPostHandlerActionImpl implements ReportPostHandlerAction {
        private final String type;

        private ReportPostHandlerActionImpl(
            com.extole.reporting.entity.report.posthandler.action.ReportPostHandlerAction action) {
            this.type = action.getType().name();
        }

        @Override
        public String getType() {
            return type;
        }
    }

}
