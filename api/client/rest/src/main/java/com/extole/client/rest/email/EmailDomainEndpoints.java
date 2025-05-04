package com.extole.client.rest.email;

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

@Path("/v4/email-domains")
public interface EmailDomainEndpoints {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    EmailDomainResponse create(@UserAccessTokenParam String accessToken, EmailDomainCreateRequest request)
        throws UserAuthorizationRestException, EmailDomainValidationRestException, CnameRecordValidationRestException;

    @GET
    @Path("/{emailDomainId}")
    @Produces(MediaType.APPLICATION_JSON)
    EmailDomainResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("emailDomainId") String emailDomainId)
        throws UserAuthorizationRestException, EmailDomainRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<EmailDomainResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;

    @PUT
    @Path("/{emailDomainId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    EmailDomainResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("emailDomainId") String emailDomainId, EmailDomainUpdateRequest request)
        throws UserAuthorizationRestException, EmailDomainRestException, EmailDomainValidationRestException,
        CnameRecordValidationRestException;

    @DELETE
    @Path("/{emailDomainId}")
    @Produces(MediaType.APPLICATION_JSON)
    EmailDomainResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("emailDomainId") String emailDomainId)
        throws UserAuthorizationRestException, EmailDomainRestException;

    @GET
    @Path("/{emailDomainId}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    EmailDomainValidationResponse validate(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("emailDomainId") String emailDomainId)
        throws UserAuthorizationRestException, EmailDomainRestException;
}
