package com.extole.client.rest.impl.prehandler.condition.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.condition.response.HttpHeaderMatchPrehandlerConditionResponse;
import com.extole.model.entity.prehandler.PrehandlerConditionType;
import com.extole.model.entity.prehandler.condition.HttpHeaderMatchPrehandlerCondition;

@Component
public class HttpHeaderMatchPrehandlerConditionResponseMapper implements
    PrehandlerConditionResponseMapper<HttpHeaderMatchPrehandlerCondition, HttpHeaderMatchPrehandlerConditionResponse> {

    @Override
    public HttpHeaderMatchPrehandlerConditionResponse toResponse(HttpHeaderMatchPrehandlerCondition condition) {
        return new HttpHeaderMatchPrehandlerConditionResponse(condition.getId().getValue(),
            condition.getHttpHeaders(), condition.getHttpHeaderNames());
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.HTTP_HEADER_MATCH;
    }
}
