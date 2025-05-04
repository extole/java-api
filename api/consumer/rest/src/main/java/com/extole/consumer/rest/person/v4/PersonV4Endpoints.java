package com.extole.consumer.rest.person.v4;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Deprecated // TODO remove ENG-10143
@Path("/v4/person")
public interface PersonV4Endpoints {

    @GET
    @Path("/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    PublicPersonV4Response getPublicPerson(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("personId") String personId) throws AuthorizationRestException, PersonRestV4Exception;

    @GET
    @Path("/profile-picture-url/{personId}")
    @Produces(MediaType.TEXT_PLAIN)
    String getPersonProfilePictureUrl(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("personId") String personId) throws AuthorizationRestException, PersonRestV4Exception;

}
