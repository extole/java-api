package com.extole.client.rest.tango;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;

@Path("/v2/settings/tango/accounts")
public interface ClientTangoSettingsAccountEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientTangoSettingsAccountResponse> list(@UserAccessTokenParam String accessToken)
        throws UserAuthorizationRestException, TangoConnectionRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientTangoSettingsAccountResponse create(@UserAccessTokenParam String accessToken,
        ClientTangoSettingsAccountCreationRequest request)
        throws UserAuthorizationRestException, ClientTangoSettingsAccountCreationRestException,
        TangoConnectionRestException;

    @GET
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientTangoSettingsAccountResponse get(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId)
        throws UserAuthorizationRestException, ClientTangoSettingsAccountCreationRestException,
        TangoAccountRestException, TangoConnectionRestException;

    @PUT
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientTangoSettingsAccountResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId, ClientTangoSettingsAccountUpdateRequest request)
        throws UserAuthorizationRestException, ClientTangoSettingsAccountCreationRestException,
        TangoAccountRestException, TangoConnectionRestException;

    @POST
    @Path("/{accountId}/credit-card-deposits")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse createCreditCardDeposit(@UserAccessTokenParam String accessToken,
        @PathParam("accountId") String accountId, ClientTangoSettingsAccountCreditCardDepositRequest request)
        throws UserAuthorizationRestException, ClientTangoSettingsAccountCreationRestException,
        TangoConnectionRestException, TangoAccountRestException, TangoCreditCardRestException;

}
