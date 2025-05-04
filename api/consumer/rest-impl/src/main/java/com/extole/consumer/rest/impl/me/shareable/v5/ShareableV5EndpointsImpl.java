package com.extole.consumer.rest.impl.me.shareable.v5;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.shareable.v5.ShareableV5Endpoints;
import com.extole.consumer.rest.me.shareable.v5.ShareableV5Response;
import com.extole.consumer.rest.me.shareable.v5.ShareableV5RestException;
import com.extole.consumer.service.shareable.ConsumerShareable;
import com.extole.consumer.service.shareable.ConsumerShareableService;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Provider
public class ShareableV5EndpointsImpl implements ShareableV5Endpoints {
    private final ConsumerShareableService consumerShareableService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final HttpServletRequest servletRequest;

    @Inject
    public ShareableV5EndpointsImpl(ConsumerShareableService shareableService,
        ConsumerRequestContextService consumerRequestContextService,
        @Context HttpServletRequest servletRequest) {
        this.consumerShareableService = shareableService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.servletRequest = servletRequest;
    }

    @Override
    public ShareableV5Response get(String accessToken, String code)
        throws AuthorizationRestException, ShareableV5RestException {
        try {
            Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
                .withAccessToken(accessToken)
                .build()
                .getAuthorization();
            ConsumerShareable shareable = consumerShareableService.getByCode(authorization, code);
            return toResponse(shareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableV5RestException.class)
                .withErrorCode(ShareableV5RestException.SHAREABLE_NOT_FOUND).addParameter("code", code).withCause(e)
                .build();
        }
    }

    private ShareableV5Response toResponse(ConsumerShareable shareable) {
        return new ShareableV5Response(shareable.getCode(), shareable.getKey(), shareable.getLabel().orElse(null),
            shareable.getLink().toString(), MeShareableV5EndpointsImpl.toShareableContent(shareable.getContent()),
            shareable.getData(), shareable.getPersonId().getValue());
    }

}
