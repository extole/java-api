package com.extole.client.rest.shareable.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
@Path("/v2/persons/{person_id}/shareables")
public interface ClientPersonShareableV2Endpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ShareableV2Response> getAll(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId)
        throws UserAuthorizationRestException, PersonRestException, ClientShareableV2RestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ShareableV2Response create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        CreateShareableV2Request request)
        throws UserAuthorizationRestException, PersonRestException, ClientShareableValidationV2RestException,
        ClientShareableCreateV2RestException;

    @PUT
    @Path("/{shareable_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ShareableV2Response update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("shareable_id") String shareableId, UpdateShareableV2Request request)
        throws UserAuthorizationRestException, PersonRestException, ClientShareableV2RestException,
        ClientShareableValidationV2RestException, ClientShareableCreateV2RestException;

    @GET
    @Path("/{shareable_id}")
    @Produces(MediaType.APPLICATION_JSON)
    ShareableV2Response get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("shareable_id") String shareableId)
        throws UserAuthorizationRestException, PersonRestException, ClientShareableV2RestException;

}
