package com.extole.reporting.rest.report.type;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.reporting.rest.report.ReportTypeRestException;

@Path("/v6/report-types")
@Tag(name = "/v6/report-types", description = "ReportType")
public interface ReportTypeEndpoints {

    String REPORT_ID_PATH_PARAM_NAME = "id";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a list of report types")
    List<? extends ReportTypeResponse> listReportTypes(@UserAccessTokenParam String accessToken,
        @BeanParam ReportTypeGetRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException;

    @GET
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get report type for the specified id.")
    ReportTypeResponse readReportType(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id)
        throws UserAuthorizationRestException, ReportTypeRestException;

    @GET
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/clients")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get ids of the clients that have access to the specified report type.")
    List<String> listReportTypeClients(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id)
        throws UserAuthorizationRestException, ReportTypeRestException;

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get report types and associated clients.")
    List<ReportTypeWithClientsResponse>
        listReportTypesWithClients(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
            @BeanParam ReportTypeGetRequest request)
            throws UserAuthorizationRestException, QueryLimitsRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a report type.")
    ReportTypeResponse createReportType(@UserAccessTokenParam String accessToken,
        ReportTypeCreateRequest request)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @PUT
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update report type for the specified id.")
    ReportTypeResponse updateReportType(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        ReportTypeUpdateRequest request)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @PUT
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/clients")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update report type client Ids for the specified id.")
    ReportTypeResponse updateReportTypeClients(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        List<String> clientIds)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @PUT
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/tags-add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add report type tags for the specified id.")
    ReportTypeResponse addReportTypeTags(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        List<String> tags)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @DELETE
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete report type for the specified id.")
    ReportTypeResponse deleteReportType(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id)
        throws UserAuthorizationRestException, ReportTypeRestException;

    @DELETE
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/clients")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete report type client Ids for the specified id.")
    ReportTypeResponse deleteReportTypeClients(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        @QueryParam("client_ids") List<String> clientIds)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @PUT
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/tags-delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete report type tags for the specified id.")
    ReportTypeResponse deleteReportTypeTags(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        List<String> tags)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @PUT
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/parameters-add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add report type parameters for the specified id.")
    ReportTypeResponse addReportTypeParameters(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        List<DynamicReportTypeParameterDetailsRequest> parameters)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;

    @PUT
    @Path("/{" + REPORT_ID_PATH_PARAM_NAME + "}/parameters-delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete report type parameters for the specified id.")
    ReportTypeResponse deleteReportTypeParameters(@UserAccessTokenParam String accessToken,
        @Parameter(
            description = "The Extole unique report type identifier.") @PathParam(REPORT_ID_PATH_PARAM_NAME) String id,
        List<String> parameters)
        throws UserAuthorizationRestException, ReportTypeRestException, ReportTypeValidationRestException;
}
