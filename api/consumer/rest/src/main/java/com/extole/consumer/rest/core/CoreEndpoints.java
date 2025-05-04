package com.extole.consumer.rest.core;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public interface CoreEndpoints {

    @GET
    @Path("/core.js")
    @Produces("application/javascript")
    Response coreJs();

    @GET
    @Path("/core-loader.js")
    @Produces("application/javascript")
    Response coreLoaderJs();

    @Deprecated // TBD - OPEN TICKET
    @GET
    @Path("/{clientId: \\d+}/core.js")
    @Produces("application/javascript")
    Response coreJsWithId(@PathParam("clientId") String clientId);

    @Deprecated // TBD - OPEN TICKET
    @GET
    @Path("/origin/{clientId: \\d+}/core.js")
    @Produces("application/javascript")
    Response coreJsWithIdWithPrefix(@PathParam("clientId") String clientId);

    @Deprecated // TBD - OPEN TICKET
    @GET
    @Path("/{clientName: .+}/core.js")
    @Produces("application/javascript")
    Response coreJsWithName(@PathParam("clientName") String clientName);

    @Deprecated // TBD - OPEN TICKET
    @GET
    @Path("/origin/{clientName: .+}/core.js")
    @Produces("application/javascript")
    Response coreJsWithNameWithPrefix(@PathParam("clientName") String clientName);

}
