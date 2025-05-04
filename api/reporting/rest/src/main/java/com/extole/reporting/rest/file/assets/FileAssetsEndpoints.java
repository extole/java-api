package com.extole.reporting.rest.file.assets;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.OmissibleRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/files")
@Tag(name = "/v6/files", description = "FileAssets")
public interface FileAssetsEndpoints {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Creates a FileAsset")
    FileAssetResponse create(@UserAccessTokenParam String accessToken, FileAssetRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FileAssetValidationRestException, FileAssetEncryptionRestException;

    @PUT
    @Path("/{fileId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Updates a FileAsset")
    FileAssetResponse update(@UserAccessTokenParam String accessToken,
        @Parameter(description = "File asset id") @PathParam("fileId") String fileId,
        FileAssetUpdateRequest updateRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, OmissibleRestException, FileAssetRestException,
        FileAssetValidationRestException;

    @POST
    @Path("/{fileId}/expire")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Expires a FileAsset")
    FileAssetResponse expire(@UserAccessTokenParam String accessToken,
        @Parameter(description = "File asset id") @PathParam("fileId") String fileId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, FileAssetRestException;

    @DELETE
    @Path("/{fileId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Used to remove a FileAsset by id")
    FileAssetResponse delete(@UserAccessTokenParam String accessToken,
        @Parameter(description = "File asset id", required = true) @PathParam("fileId") String fileId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, FileAssetRestException;

    @GET
    @Path("/{fileId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns a FileAsset by id")
    FileAssetResponse get(@UserAccessTokenParam String accessToken,
        @Parameter(description = "File asset id") @PathParam("fileId") String fileId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FileAssetRestException;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns a filtered list of FileAssets")
    List<FileAssetResponse> list(@UserAccessTokenParam String accessToken,
        @BeanParam FileAssetsQueryParams flowQueryParams, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{fileId}/download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Downloads a FileAsset by id")
    Response download(@UserAccessTokenParam String accessToken,
        @Parameter(description = "File asset id", required = true) @PathParam("fileId") String fileId,
        @Parameter(description = "Limit parameter") @QueryParam("limit") Optional<String> limit,
        @Parameter(description = "Offset parameter") @QueryParam("offset") Optional<String> offset)
        throws UserAuthorizationRestException, QueryLimitsRestException, FileAssetRestException,
        FileAssetExpiredRestException;
}
