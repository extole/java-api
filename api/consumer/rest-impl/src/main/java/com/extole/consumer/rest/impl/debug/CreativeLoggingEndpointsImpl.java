package com.extole.consumer.rest.impl.debug;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Provider;

import com.extole.consumer.rest.debug.CreateCreativeLogRequest;
import com.extole.consumer.rest.debug.CreateCreativeLogResponse;
import com.extole.consumer.rest.debug.CreativeLoggingEndpoints;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.model.entity.program.PublicProgram;

@Provider
public class CreativeLoggingEndpointsImpl implements CreativeLoggingEndpoints {

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;

    @Inject
    public CreativeLoggingEndpointsImpl(
        HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
    }

    @Override
    public CreateCreativeLogResponse create(String accessToken, CreateCreativeLogRequest request) {
        CreativeLogBuilderImpl creativeLogBuilder = new CreativeLogBuilderImpl()
            .withMessage(request.getMessage())
            .withAccessToken(accessToken)
            .withUserAgent(HttpUserAgentExtractor.getInstance().getUserAgent(servletRequest).orElse(null));
        if (request.getLevel() != null) {
            creativeLogBuilder.withLevel(request.getLevel());
        }

        PublicProgram programDomain = consumerRequestContextService.extractProgramDomain(servletRequest);
        if (programDomain != null) {
            creativeLogBuilder.withClientId(programDomain.getClientId());
        }

        String pollingId = creativeLogBuilder.save();
        return new CreateCreativeLogResponse(pollingId);
    }
}
