package com.extole.client.rest.shareable;

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
import javax.ws.rs.core.Response;

import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.client.rest.program.ProgramRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v3/persons/{person_id}/shareables")
public interface ClientPersonShareableEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonShareableV4Response> getAll(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId) throws UserAuthorizationRestException, ClientShareableRestException;

    @GET
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableV4Response get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, @PathParam("code") String code)
        throws UserAuthorizationRestException, ClientShareableRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableV4Response create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, CreateClientShareableRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ClientShareableCreateRestException,
        ClientShareableValidationRestException, ClientShareableRestException;

    @PUT
    @Path("/{code : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableV4Response update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId, @PathParam("code") String code, UpdateClientShareableRequest request)
        throws UserAuthorizationRestException, ClientShareableRestException, ClientShareableValidationRestException,
        ProgramRestException;

    @POST
    @Path("/{code : .+}/update-shareable-owner")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateShareableOwner(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("person_id") String personId, @PathParam("code") String code,
        UpdateClientShareableOwnerRequest request)
        throws UserAuthorizationRestException, ClientShareableRestException, ClientShareableValidationRestException;

    @DELETE
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableV4Response delete(@UserAccessTokenParam String accessToken, @PathParam("person_id") String personId,
        @PathParam("code") String code) throws UserAuthorizationRestException, ClientShareableRestException;

}
