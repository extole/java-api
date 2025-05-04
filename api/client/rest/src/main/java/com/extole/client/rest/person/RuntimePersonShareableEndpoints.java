package com.extole.client.rest.person;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.person.v4.PersonShareableV4Response;
import com.extole.client.rest.program.ProgramRestException;
import com.extole.client.rest.shareable.ClientShareableCreateRestException;
import com.extole.client.rest.shareable.ClientShareableRestException;
import com.extole.client.rest.shareable.ClientShareableValidationRestException;
import com.extole.client.rest.shareable.CreateClientShareableRequest;
import com.extole.client.rest.shareable.ShareableRestException;
import com.extole.client.rest.shareable.UpdateClientShareableRequest;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v4/runtime-persons/{person_id}/shareables")
@Tag(name = "/v4/runtime-persons/{person_id}/shareables", description = "RuntimePersonShareable")
public interface RuntimePersonShareableEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with shareables", description = "Returns shareables for a person.")
    List<PersonShareableV4Response> getShareables(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a shareable", description = "Returns a shareable by a given code.")
    PersonShareableV4Response getShareableByCode(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Shareable code") @PathParam("code") String code)
        throws UserAuthorizationRestException, PersonRestException, ShareableRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableV4Response create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        CreateClientShareableRequest request) throws UserAuthorizationRestException, ProgramRestException,
        ClientShareableCreateRestException, ClientShareableValidationRestException, ClientShareableRestException;

    @PUT
    @Path("/{code : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableV4Response update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("code") String code, UpdateClientShareableRequest request)
        throws UserAuthorizationRestException, ClientShareableRestException, ClientShareableValidationRestException,
        ProgramRestException;
}
