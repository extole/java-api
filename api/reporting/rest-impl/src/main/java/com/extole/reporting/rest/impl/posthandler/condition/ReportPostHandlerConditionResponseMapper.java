package com.extole.reporting.rest.impl.posthandler.condition;

import com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition;
import com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition.ConditionType;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionResponse;

public interface ReportPostHandlerConditionResponseMapper<C extends ReportPostHandlerCondition, R extends ReportPostHandlerConditionResponse> {

    R toReponse(C condition);

    ConditionType getType();
}
