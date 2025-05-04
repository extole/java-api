package com.extole.client.rest.salesforce;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v1/salesforce/customer/properties")
public interface SalesforceCustomerPropertyEndpoints {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    List<SalesforceCustomerPropertyResponse> createOrUpdate(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_ADMIN) String accessToken,
        SalesforceCustomerPropertyCreationRequest property) throws UserAuthorizationRestException;
}
