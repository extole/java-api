package com.extole.client.rest.tango;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;

@Path("/v2/settings/tango/accounts/{accountId}/credit-cards")
public interface ClientTangoSettingsCreditCardEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<TangoCreditCardResponse> listCreditCards(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId)
        throws UserAuthorizationRestException, TangoAccountRestException, TangoConnectionRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TangoCreditCardResponse registerCreditCard(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId, TangoTestCreditCardRegistrationRequest request)
        throws UserAuthorizationRestException, TangoAccountRestException, TangoCreditCardRegistrationRestException,
        TangoConnectionRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{creditCardId}")
    TangoCreditCardResponse getCreditCard(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId, @PathParam("creditCardId") String creditCardId)
        throws UserAuthorizationRestException, TangoAccountRestException, TangoCreditCardRestException,
        TangoConnectionRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{creditCardId}/unregister")
    SuccessResponse unregisterCreditCard(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId, @PathParam("creditCardId") String creditCardId)
        throws UserAuthorizationRestException, TangoCreditCardRestException, TangoConnectionRestException,
        TangoAccountRestException;

}
