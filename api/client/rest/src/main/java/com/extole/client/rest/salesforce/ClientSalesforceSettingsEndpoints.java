package com.extole.client.rest.salesforce;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/settings/salesforce")
public interface ClientSalesforceSettingsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientSalesforceSettingsResponse>
        list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
            throws UserAuthorizationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientSalesforceSettingsResponse create(@UserAccessTokenParam String accessToken,
        ClientSalesforceSettingsCreateRequest request)
        throws UserAuthorizationRestException, ClientSalesforceSettingsCreateRestException,
        SalesforceConnectionRestException, ClientSalesforceSettingsValidationRestException;

    @PUT
    @Path("/{settingsId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientSalesforceSettingsResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("settingsId") String settingsId,
        ClientSalesforceSettingsUpdateRequest request)
        throws UserAuthorizationRestException, SalesforceConnectionRestException, ClientSalesforceSettingsRestException,
        ClientSalesforceSettingsValidationRestException, ClientSalesforceSettingsUpdateRestException,
        ClientSalesforceSettingsCreateRestException;

    @DELETE
    @Path("/{settingsId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientSalesforceSettingsResponse archive(@UserAccessTokenParam String accessToken,
        @PathParam("settingsId") String settingsId)
        throws UserAuthorizationRestException, ClientSalesforceSettingsRestException;

    @GET
    @Path("/decrypt")
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientSalesforceSettingsResponse> listDecrypt(@UserAccessTokenParam String accessToken)
        throws UserAuthorizationRestException;

}
