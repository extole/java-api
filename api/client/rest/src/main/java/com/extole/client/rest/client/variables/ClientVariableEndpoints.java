package com.extole.client.rest.client.variables;

import java.time.ZoneId;
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
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/client-variables")
public interface ClientVariableEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientVariableResponse create(@UserAccessTokenParam String accessToken,
        ClientVariableCreateRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableCreationRestException,
        ClientVariableValidationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientVariableResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientVariableResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("name") String name, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableRestException;

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ClientVariableResponse update(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        ClientVariableUpdateRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableRestException, ClientVariableValidationRestException,
        OmissibleRestException;

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientVariableResponse delete(@UserAccessTokenParam String accessToken, @PathParam("name") String name,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientVariableRestException;

}
