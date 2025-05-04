package com.extole.consumer.rest.person;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.me.PublicPersonStepResponse;

@Path("/v4/persons")
public interface PersonEndpoints {

    @GET
    @Path("/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    PublicPersonResponse getPublicPerson(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("personId") String personId) throws AuthorizationRestException, PersonRestException;

    @GET
    @Path("/{personId}/profile-picture-url")
    @Produces(MediaType.TEXT_PLAIN)
    String getPersonProfilePictureUrl(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("personId") String personId) throws AuthorizationRestException, PersonRestException;

    @GET
    @Path("/{personId}/steps")
    @Produces(MediaType.APPLICATION_JSON)
    List<PublicPersonStepResponse> getPublicPersonSteps(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("personId") String personId,
        @Nullable @QueryParam("campaign_id") String campaignId,
        @Nullable @QueryParam("program_label") String programLabel,
        @Nullable @QueryParam("step_name") String stepName) throws AuthorizationRestException, PersonRestException;

}
