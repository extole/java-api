package com.extole.client.rest.person.v2;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.RuntimePersonEndpoints;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.producer.DefaultApplicationJSON;

/**
 * @deprecated Use {@link RuntimePersonEndpoints} for reading data.
 *             Create/update operations exist just in deprecated /v2/persons/parameters
 */
@Deprecated // TODO remove in ENG-13035
@Path("/v2/persons/{person_id}/parameters")
public interface PersonParametersV2Endpoints {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> getPersonProfileParameters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId)
        throws UserAuthorizationRestException, PersonRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    SuccessResponse editPersonProfileParameters(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        PersonParametersBulkUpdateV2Request bulkUpdateRequest)
        throws PersonParametersV2RestException, UserAuthorizationRestException, PersonRestException;

    @PUT
    @Path("/{parameter_name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    SuccessResponse putPersonProfileParameter(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("parameter_name") String parameterName, PersonParametersUpdateV2Request updateRequest)
        throws PersonParametersV2RestException, UserAuthorizationRestException, PersonRestException;

}
