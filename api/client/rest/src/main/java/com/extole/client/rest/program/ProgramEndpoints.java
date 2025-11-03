package com.extole.client.rest.program;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.email.DomainValidationStatus;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;

@Path("/v2/programs")
public interface ProgramEndpoints {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<ProgramResponse> getPrograms(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("status") Optional<DomainValidationStatus> status) throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programId}")
    ProgramResponse getProgram(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("programId") String programId) throws UserAuthorizationRestException, ProgramRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ProgramResponse create(@UserAccessTokenParam String accessToken, ProgramCreateRequest request)
        throws UserAuthorizationRestException, ProgramValidationRestException, ProgramCreateRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programId}")
    ProgramResponse edit(@UserAccessTokenParam String accessToken, @PathParam("programId") String programId,
        ProgramUpdateRequest request)
        throws UserAuthorizationRestException, ProgramRestException, ProgramValidationRestException,
        OmissibleRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programId}/decrypt")
    ProgramResponse getDecrypt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("programId") String programId) throws UserAuthorizationRestException, ProgramRestException;

    // TODO remove this endpoint - ENG-24087
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{programId}")
    ProgramResponse archive(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("programId") String programId, @DefaultValue("false") @QueryParam("force") boolean force)
        throws UserAuthorizationRestException, ProgramRestException, ProgramArchiveRestException;

    @GET
    @Path("/{programId}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    ProgramDomainValidationResponse validate(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("programId") String programId) throws UserAuthorizationRestException, ProgramRestException;
}
