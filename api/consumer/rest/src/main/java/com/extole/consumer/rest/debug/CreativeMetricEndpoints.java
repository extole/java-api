package com.extole.consumer.rest.debug;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Hidden
@Path("/v4/debug/metrics")
public interface CreativeMetricEndpoints {

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Operation(summary = "Record metric internally in grafana")
    CreativeMetricResponse record(@AccessTokenParam(readCookie = false) String accessToken,
        CreativeMetricRequest request) throws CreativeMetricRestException, AuthorizationRestException;

}
