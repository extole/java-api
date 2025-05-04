package com.extole.client.rest.person.shareables;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
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

import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.program.ProgramRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v5/persons/{person_id}/shareables")
@Tag(name = "/v5/persons/{person_id}/shareables", description = "Person shareables")
public interface PersonShareablesEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{code : .+}")
    @Operation(summary = "Get a shareable by id",
        description = "Returns shareable for a person identified by id.")
    PersonShareableResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Shareable id parameter") @PathParam("code") String code,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareableRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with shareables",
        description = "Returns shareables for a person, sorted by created date in descending order.")
    List<PersonShareableResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @BeanParam PersonShareablesListRequest shareableListRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareablesListRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableResponse create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        PersonShareableCreateRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ProgramRestException, PersonRestException,
        PersonShareableRestException, PersonShareableValidationRestException;

    @PUT
    @Path("/{code : .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonShareableResponse update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("code") String code, PersonShareableUpdateRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonShareableRestException,
        PersonShareableValidationRestException,
        ProgramRestException;
}
