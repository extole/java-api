package com.extole.client.rest.program;

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

@Path("/v2/programs/{programId}/sites")
public interface ProgramSitePatternEndpoints {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<ProgramSitePatternResponse> getProgramSitePatterns(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("programId") String programId)
        throws UserAuthorizationRestException, ProgramRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programSitePatternId}")
    ProgramSitePatternResponse getProgramSitePattern(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("programId") String programId, @PathParam("programSitePatternId") String programSitePatternId)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ProgramSitePatternResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("programId") String programId,
        ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException;

    @Deprecated // TBD - OPEN TICKET use edit ( with PUT )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programSitePatternId}")
    ProgramSitePatternResponse editWithPost(@UserAccessTokenParam String accessToken,
        @PathParam("programId") String programId,
        @PathParam("programSitePatternId") String programSitePatternId,
        ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programSitePatternId}")
    ProgramSitePatternResponse edit(@UserAccessTokenParam String accessToken, @PathParam("programId") String programId,
        @PathParam("programSitePatternId") String programSitePatternId, ProgramSitePatternRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programSitePatternId}")
    ProgramSitePatternResponse discard(@UserAccessTokenParam String accessToken,
        @PathParam("programId") String programId,
        @PathParam("programSitePatternId") String programSitePatternId)
        throws UserAuthorizationRestException, ProgramRestException, ProgramSitePatternRestException;

}
