package com.extole.client.rest.sftp;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
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
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/sftp-destinations")
public interface SftpDestinationEndpoints {

    String SFTP_DESTINATION_ID_PATH_PARAM_NAME = "sftpDestinationId";

    @GET
    @Path("/{" + SFTP_DESTINATION_ID_PATH_PARAM_NAME + "}")
    @Produces(MediaType.APPLICATION_JSON)
    SftpDestinationResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam(SFTP_DESTINATION_ID_PATH_PARAM_NAME) String sftpDestinationId, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<SftpDestinationResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam SftpDestinationListQuery query, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SftpDestinationResponse create(@UserAccessTokenParam String accessToken,
        SftpDestinationCreateRequest createRequest, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationValidationRestException;

    @PUT
    @Path("/{" + SFTP_DESTINATION_ID_PATH_PARAM_NAME + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SftpDestinationResponse update(@UserAccessTokenParam String accessToken,
        @PathParam(SFTP_DESTINATION_ID_PATH_PARAM_NAME) String sftpDestinationId,
        SftpDestinationUpdateRequest updateRequest, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationRestException, SftpDestinationValidationRestException,
        OmissibleRestException;

    @DELETE
    @Path("/{" + SFTP_DESTINATION_ID_PATH_PARAM_NAME + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SftpDestinationResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam(SFTP_DESTINATION_ID_PATH_PARAM_NAME) String sftpDestinationId, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationRestException;

    @POST
    @Path("/{" + SFTP_DESTINATION_ID_PATH_PARAM_NAME + "}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    SftpDestinationValidationResponse validate(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam(SFTP_DESTINATION_ID_PATH_PARAM_NAME) String sftpDestinationId, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationRestException;

    @POST
    @Path("/{" + SFTP_DESTINATION_ID_PATH_PARAM_NAME + "}/sync")
    @Produces(MediaType.APPLICATION_JSON)
    SftpDestinationResponse sync(@UserAccessTokenParam String accessToken,
        @PathParam(SFTP_DESTINATION_ID_PATH_PARAM_NAME) String sftpDestinationId, @TimeZoneParam ZoneId timezone)
        throws UserAuthorizationRestException, SftpDestinationRestException;
}
