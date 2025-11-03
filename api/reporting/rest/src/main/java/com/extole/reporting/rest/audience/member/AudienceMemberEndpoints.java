package com.extole.reporting.rest.audience.member;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.api.audience.Audience;
import com.extole.api.person.Person;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;

@Path("/v1/audiences/{audience_id}/members")
@Tag(name = "/v1/audiences/{audience_id}/members")
public interface AudienceMemberEndpoints {

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List audience members ids")
    List<Id<Person>> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @BeanParam AudienceMemberQueryParameters queryParameters)
        throws UserAuthorizationRestException, AudienceMemberRestException;

    @GET
    @Path("/view/details")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "List audience members")
    List<AudienceMemberWithDataResponse> listPeople(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @BeanParam AudienceMemberQueryParameters queryParameters,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceMemberRestException;

    @GET
    @Path("/download{format :(\\.csv|\\.json|\\.xlsx)?}")
    Response download(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId,
        @Nullable @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @Nullable @PathParam("format") String format,
        @BeanParam AudienceMemberDownloadParameters downloadParameters,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceMemberRestException, AudienceMemberDownloadRestException;

}
