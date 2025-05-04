package com.extole.reporting.rest.demo.data;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v1/demo")
public interface DemoDataEndpoints {

    @POST
    @Path("/generate-file-asset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    DemoDataFileAssetResponse generateFileAsset(@UserAccessTokenParam String accessToken, DemoDataRequest request)
        throws UserAuthorizationRestException, DemoDataRestException;
}
