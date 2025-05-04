package com.extole.consumer.rest.debug.security;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v4/debug/content-security-policy-reports")
public interface ContentSecurityPolicyReportEndpoints {

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    Response submit(Optional<ContentSecurityPolicyReportRequest> reportRequest);
}
