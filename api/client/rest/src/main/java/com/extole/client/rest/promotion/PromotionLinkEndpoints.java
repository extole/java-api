package com.extole.client.rest.promotion;

import java.util.List;

import javax.ws.rs.Consumes;
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

@Path("/v2/promotion-links")
public interface PromotionLinkEndpoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PromotionLinkResponse>
        getPromotionLinks(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
            throws UserAuthorizationRestException;

    @GET
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    PromotionLinkResponse getPromotionLink(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("code") String code) throws UserAuthorizationRestException, PromotionLinkRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PromotionLinkResponse create(@UserAccessTokenParam String accessToken, PromotionLinkCreateRequest request)
        throws UserAuthorizationRestException, PromotionLinkCreateRestException, PromotionLinkValidationRestException;

    @PUT
    @Path("/{code : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PromotionLinkResponse update(@UserAccessTokenParam String accessToken, @PathParam("code") String code,
        PromotionLinkUpdateRequest request)
        throws UserAuthorizationRestException, PromotionLinkRestException, PromotionLinkValidationRestException;

}
