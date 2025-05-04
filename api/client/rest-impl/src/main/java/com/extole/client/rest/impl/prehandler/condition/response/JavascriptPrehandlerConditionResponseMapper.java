package com.extole.client.rest.impl.prehandler.condition.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.condition.response.JavascriptPrehandlerConditionResponse;
import com.extole.id.JavascriptFunction;
import com.extole.model.entity.prehandler.PrehandlerConditionType;
import com.extole.model.entity.prehandler.condition.JavascriptPrehandlerCondition;

@Component
public class JavascriptPrehandlerConditionResponseMapper implements
    PrehandlerConditionResponseMapper<JavascriptPrehandlerCondition, JavascriptPrehandlerConditionResponse> {

    @Override
    public JavascriptPrehandlerConditionResponse toResponse(JavascriptPrehandlerCondition condition) {
        return new JavascriptPrehandlerConditionResponse(condition.getId().getValue(),
            new JavascriptFunction<>(condition.getJavascript().getValue()));
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.JAVASCRIPT_V1;
    }
}
