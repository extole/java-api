package com.extole.consumer.rest.debug;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

@Path("/v4/debug/mode")
public interface ContainerEndpoints {

    @GET
    @Path("/{container}")
    Response setEnvironmentCookie(@PathParam("container") String container,
        @DefaultValue("true") @QueryParam("enable") boolean enable, @CookieParam("container") Cookie cookie)
        throws ContainerRestException;

}
