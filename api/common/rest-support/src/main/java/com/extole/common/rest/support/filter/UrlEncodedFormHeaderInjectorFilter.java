package com.extole.common.rest.support.filter;

import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.ContainerRequest;

import com.extole.common.rest.support.reader.BeanMap;

@Provider
@PreMatching
public class UrlEncodedFormHeaderInjectorFilter implements ContainerRequestFilter {

    public static final String HEADERS_PREFIX = "::headers";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (requestContext instanceof ContainerRequest) {
            ContainerRequest request = (ContainerRequest) requestContext;

            if (requestContext.hasEntity()
                && MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
                request.bufferEntity();
                Form form = request.readEntity(Form.class);
                BeanMap requestMap = new BeanMap();
                MultivaluedMap<String, String> formParams = form.asMap();
                for (String eachParameter : formParams.keySet()) {
                    String value = formParams.getFirst(eachParameter);
                    if (value != null) {
                        requestMap.setProperty(eachParameter, value);
                    }
                }
                Map<String, String> headers = requestMap.getSubBean(HEADERS_PREFIX).getProperties();
                for (String headerKey : headers.keySet()) {
                    ((ContainerRequest) requestContext).getHeaders().putSingle(headerKey, headers.get(headerKey));
                }
            }
        }
    }

}
