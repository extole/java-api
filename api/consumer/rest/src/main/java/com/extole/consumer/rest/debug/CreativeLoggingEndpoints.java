package com.extole.consumer.rest.debug;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;

@Tag(name = "/v4/debug/logs", description = "CreativeLogging")
@Path("/v4/debug/logs")
public interface CreativeLoggingEndpoints {
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Operation(summary = "Used to send log messages to Extole")
    CreateCreativeLogResponse create(@AccessTokenParam(readCookie = false) String accessToken,
        CreateCreativeLogRequest request);
}
