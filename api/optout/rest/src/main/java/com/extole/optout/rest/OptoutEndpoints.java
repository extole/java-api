package com.extole.optout.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface OptoutEndpoints {
    @GET
    @Path("/{clientId}/check")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Boolean isOptout(@PathParam("clientId") long clientId, @QueryParam("email") String email);

    @POST
    @Path("/{clientId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    OptoutResponse addOptout(@PathParam("clientId") long clientId, @QueryParam("email") String email,
        @QueryParam("type") String type, @FormParam("email") String fEmail, @FormParam("type") String fType,
        @QueryParam("online") Boolean online);

    @DELETE
    @Path("/{clientId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    OptoutResponse deleteOptout(@PathParam("clientId") long clientId, @QueryParam("email") String email,
        @QueryParam("value") String value);
}
