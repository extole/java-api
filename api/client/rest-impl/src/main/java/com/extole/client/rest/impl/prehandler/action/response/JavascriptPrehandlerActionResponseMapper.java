package com.extole.client.rest.impl.prehandler.action.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.action.response.JavascriptPrehandlerActionResponse;
import com.extole.id.JavascriptFunction;
import com.extole.model.entity.prehandler.PrehandlerActionType;
import com.extole.model.entity.prehandler.action.JavascriptPrehandlerAction;

@Component
public class JavascriptPrehandlerActionResponseMapper
    implements PrehandlerActionResponseMapper<JavascriptPrehandlerAction, JavascriptPrehandlerActionResponse> {

    @Override
    public JavascriptPrehandlerActionResponse toResponse(JavascriptPrehandlerAction action) {
        return new JavascriptPrehandlerActionResponse(action.getId().getValue(),
            new JavascriptFunction<>(action.getJavascript().getValue()));
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.JAVASCRIPT_V1;
    }
}
