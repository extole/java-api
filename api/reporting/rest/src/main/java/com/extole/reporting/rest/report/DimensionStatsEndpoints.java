package com.extole.reporting.rest.report;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v6/dimensions")
@Tag(name = "/v6/dimensions")
public interface DimensionStatsEndpoints {

    @GET
    @Path("/{dimensionName}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get dimension values.")
    List<DimensionStatsResponse> getDimensionStats(@UserAccessTokenParam String accessToken,
        @PathParam("dimensionName") String dimensionName,
        @BeanParam DimensionStatsGetRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get dimension combinations.")
    List<List<String>> getSummaryDimensions(@UserAccessTokenParam String accessToken,
        @BeanParam SummaryDimensionsListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException;
}
