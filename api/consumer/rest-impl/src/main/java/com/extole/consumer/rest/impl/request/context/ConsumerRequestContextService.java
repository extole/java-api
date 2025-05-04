package com.extole.consumer.rest.impl.request.context;

import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import com.extole.consumer.event.service.processor.EventProcessorConfigurator;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.model.entity.program.PublicProgram;

public interface ConsumerRequestContextService {

    PublicProgram extractProgramDomain(HttpServletRequest servletRequest);

    ConsumerRequestContextBuilder createBuilder(HttpServletRequest servletRequest);

    enum ConsumerRequestType {
        API, WEB
    }

    enum HttpRequestBodyCapturingType {
        FULL, LIMITED
    }

    interface ConsumerRequestContextBuilder {
        ConsumerRequestContextBuilder withConsumerRequestType(ConsumerRequestType requestType);

        ConsumerRequestContextBuilder withReplaceableAccessTokenBasedOnCoreSettings(String accessToken);

        ConsumerRequestContextBuilder withReplaceableAccessToken(String accessToken);

        ConsumerRequestContextBuilder withAccessToken(@Nullable String accessToken);

        ConsumerRequestContextBuilder withHttpHeaders(HttpHeaders httpHeaders);

        ConsumerRequestContextBuilder withUriInfo(UriInfo uriInfo);

        ConsumerRequestContextBuilder withEventName(String eventName);

        ConsumerRequestContextBuilder withEventProcessing(Consumer<EventProcessorConfigurator> processorConfigurator);

        ConsumerRequestContextBuilder
            withHttpRequestBodyCapturing(HttpRequestBodyCapturingType requestBodyCapturingType);

        ConsumerRequestContext build() throws AuthorizationRestException;
    }
}
