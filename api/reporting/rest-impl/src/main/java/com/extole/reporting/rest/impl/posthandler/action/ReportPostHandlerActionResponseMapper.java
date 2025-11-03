package com.extole.reporting.rest.impl.posthandler.action;

import com.extole.reporting.entity.report.posthandler.action.ReportPostHandlerAction;
import com.extole.reporting.entity.report.posthandler.action.ReportPostHandlerAction.ActionType;
import com.extole.reporting.rest.posthandler.action.ReportPostHandlerActionResponse;

public interface ReportPostHandlerActionResponseMapper<A extends ReportPostHandlerAction, R extends ReportPostHandlerActionResponse> {

    R toReponse(A action);

    ActionType getType();
}
