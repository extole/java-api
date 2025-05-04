package com.extole.reporting.rest.impl.posthandler.action;

import com.extole.reporting.rest.posthandler.ActionType;
import com.extole.reporting.rest.posthandler.ReportPostHandlerActionValidationRestException;
import com.extole.reporting.rest.posthandler.action.ReportPostHandlerActionRequest;
import com.extole.reporting.service.posthandler.ReportPostHandlerBuilder;

public interface ReportPostHandlerActionRequestMapper<A extends ReportPostHandlerActionRequest> {

    void upload(ReportPostHandlerBuilder builder, A request) throws ReportPostHandlerActionValidationRestException;

    ActionType getType();

}
