package com.extole.client.rest.person.relationship.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO remove in ENG-13035
@Path("/v2/persons/{person_id}/relationships")
public interface PersonRelationshipV2Endpoints {

    @PUT
    @Path("/{role}/{other_person_id}/{container}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    void updateRelationship(
        @UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @Parameter(description = "Role of the other person in the relationship.",
            examples = {@ExampleObject(value = "friend"),
                @ExampleObject(value = "advocate")}) @PathParam("role") String role,
        @Parameter(
            description = "Other person's Extole unique profile identifier.") @PathParam("other_person_id") String otherPersonId,
        @Parameter(description = "Relationship's container.") @PathParam("container") String container,
        @RequestBody(description = "PersonRelationshipUpdateRequest object",
            required = true) PersonRelationshipV2UpdateRequest updateRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonRelationshipV2RestException,
        PersonRelationshipValidationV2RestException;

}
