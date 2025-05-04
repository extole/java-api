package com.extole.email.rest.audit;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/emails")
public interface EmailSendAuditEndpoints {

    @GET
    @Path("/{email_id}")
    @Produces(MediaType.APPLICATION_JSON)
    EmailSendResponse getEmailByEmailId(@UserAccessTokenParam String accessToken, @PathParam("email_id") String emailId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, EmailSendRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<EmailSendResponse> getEmails(@UserAccessTokenParam String accessToken, @QueryParam("action_id") String causeId,
        @QueryParam("person_id") String personId, @QueryParam("email") String email, @QueryParam("limit") String limit,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, EmailSendAddressRestException;

    @GET
    @Path("/{email_id}/results")
    @Produces(MediaType.APPLICATION_JSON)
    List<EmailSendResultResponse> getEmailResultsByEmailId(@UserAccessTokenParam String accessToken,
        @PathParam("email_id") String emailId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, EmailSendRestException;

}
