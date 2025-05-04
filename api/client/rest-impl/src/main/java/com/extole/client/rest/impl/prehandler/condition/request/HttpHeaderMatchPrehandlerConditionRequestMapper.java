package com.extole.client.rest.impl.prehandler.condition.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.exception.HttpHeaderMatchPrehandlerConditionRestException;
import com.extole.client.rest.prehandler.condition.request.HttpHeaderMatchPrehandlerConditionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.condition.HttpHeaderMatchPrehandlerConditionBuilder;
import com.extole.model.service.prehandler.condition.exception.EmptyHttpHeaderPrehandlerConditionException;
import com.extole.model.service.prehandler.condition.exception.InvalidHttpHeaderNamePrehandlerConditionException;
import com.extole.model.service.prehandler.condition.exception.InvalidHttpHeaderValuePrehandlerConditionException;
import com.extole.model.service.prehandler.condition.exception.MissingHttpHeaderValuePrehandlerConditionException;

@Component
public class HttpHeaderMatchPrehandlerConditionRequestMapper
    implements PrehandlerConditionRequestMapper<HttpHeaderMatchPrehandlerConditionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, HttpHeaderMatchPrehandlerConditionRequest condition)
        throws PrehandlerConditionValidationRestException {
        try {
            HttpHeaderMatchPrehandlerConditionBuilder builder = prehandlerBuilder
                .addCondition(com.extole.model.entity.prehandler.PrehandlerConditionType.HTTP_HEADER_MATCH);
            builder.withHttpHeaders(condition.getHttpHeaders());
            builder.withHttpHeaderNames(condition.getHttpHeaderNames());
            builder.done();
        } catch (EmptyHttpHeaderPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(HttpHeaderMatchPrehandlerConditionRestException.class)
                .withErrorCode(
                    HttpHeaderMatchPrehandlerConditionRestException.PREHANDLER_CONDITION_IS_EMPTY)
                .withCause(e).build();
        } catch (InvalidHttpHeaderNamePrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(HttpHeaderMatchPrehandlerConditionRestException.class)
                .withErrorCode(
                    HttpHeaderMatchPrehandlerConditionRestException.PREHANDLER_CONDITION_HTTP_HEADER_NAME_INVALID)
                .addParameter("name", e.getHeaderName())
                .withCause(e).build();
        } catch (MissingHttpHeaderValuePrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(HttpHeaderMatchPrehandlerConditionRestException.class)
                .withErrorCode(
                    HttpHeaderMatchPrehandlerConditionRestException.PREHANDLER_CONDITION_HTTP_HEADER_VALUE_MISSING)
                .addParameter("name", e.getHeaderName())
                .withCause(e).build();
        } catch (InvalidHttpHeaderValuePrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(HttpHeaderMatchPrehandlerConditionRestException.class)
                .withErrorCode(
                    HttpHeaderMatchPrehandlerConditionRestException.PREHANDLER_CONDITION_HTTP_HEADER_VALUE_INVALID)
                .addParameter("name", e.getHeaderName())
                .addParameter("value", e.getHeaderValue())
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.HTTP_HEADER_MATCH;
    }
}
