package com.extole.client.rest.person;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v5/persons")
@Tag(name = "/v5/persons", description = "Person")
public interface PersonEndpoints {

    @GET
    @Path("/{person_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets details for a Person")
    PersonResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.",
            required = true) @PathParam("person_id") String personId)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lookup a Person by search criteria",
        description = "This allows searching for a person at Extole using either the email address,\n" +
            "partner user id (partner user id is YOUR unique identifier for this person),\n" +
            "last name or person keys defined by the client (partner_conversion_id, partner_shipment_id etc.).\n" +
            "The result is currently an array as sometimes " +
            "Extole will create multiple profiles for the same person,\n " +
            "such as the instance where the same email address may be tied to multiple partner user ids,\n" +
            "or multiple persons matched the client person keys that were passed in the query.")
    List<PersonResponse> search(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam PersonSearchRequest request) throws UserAuthorizationRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a Person in the Extole Platform.",
        description = "This operation can be done by an authorized user")
    PersonResponse create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @RequestBody(description = "PersonRequest object", required = true) PersonRequest person)
        throws UserAuthorizationRestException, PersonValidationRestException, PersonRestException;

    @PUT
    @Path("/{person_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a Person", description = "This operation can be done by an authorized user")
    PersonResponse update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "Id for person to be updated") @PathParam("person_id") String personId,
        @RequestBody(description = "PersonRequest object", required = true) PersonRequest person)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationRestException;
}
