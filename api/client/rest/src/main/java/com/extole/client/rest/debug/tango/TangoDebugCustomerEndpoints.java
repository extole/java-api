package com.extole.client.rest.debug.tango;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/debug/tango/customers")
public interface TangoDebugCustomerEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<TangoDebugCustomerViewSummary> list(@UserAccessTokenParam String accessToken)
        throws UserAuthorizationRestException, TangoConnectionRestException;

    @GET
    @Path("/{customer_identifier}")
    @Produces(MediaType.APPLICATION_JSON)
    TangoDebugCustomerViewSummary getByCustomerIdentifier(@UserAccessTokenParam String accessToken,
        @PathParam("customer_identifier") String customerIdentifier)
        throws UserAuthorizationRestException, TangoDebugRestException, TangoConnectionRestException;

    @GET
    @Path("/{customer_identifier}/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    List<TangoDebugAccountViewSummary> listCustomerAccounts(@UserAccessTokenParam String accessToken,
        @PathParam("customer_identifier") String customerIdentifier)
        throws UserAuthorizationRestException, TangoDebugRestException, TangoConnectionRestException;

}
