package com.extole.reporting.rest.impl.posthandler.condition;

import com.extole.reporting.rest.posthandler.ConditionType;
import com.extole.reporting.rest.posthandler.ReportPostHandlerConditionValidationRestException;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionRequest;
import com.extole.reporting.service.posthandler.ReportPostHandlerBuilder;

public interface ReportPostHandlerConditionRequestMapper<C extends ReportPostHandlerConditionRequest> {

    void upload(ReportPostHandlerBuilder builder, C request) throws ReportPostHandlerConditionValidationRestException;

    ConditionType getType();

}
