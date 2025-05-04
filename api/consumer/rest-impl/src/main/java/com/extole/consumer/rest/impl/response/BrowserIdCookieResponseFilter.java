package com.extole.consumer.rest.impl.response;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.extole.common.rest.ExtoleCookie;
import com.extole.common.rest.ExtoleCookieType;
import com.extole.consumer.rest.impl.request.ConsumerContextAttributeName;
import com.extole.model.entity.program.PublicProgram;

@Provider
public class BrowserIdCookieResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        PublicProgram program =
            (PublicProgram) requestContext.getProperty(ConsumerContextAttributeName.PROGRAM.getAttributeName());
        Long browserId = (Long) requestContext.getProperty(ConsumerContextAttributeName.BROWSER_ID.getAttributeName());
        if (browserId != null) {
            new ExtoleCookie(ExtoleCookieType.BROWSER_ID.getCookieName(), browserId.toString(), "/",
                program.getProgramDomain().toString(), null, ExtoleCookie.DEFAULT_AGE)
                    .addCookieToResponse(responseContext);
        }
    }

}
