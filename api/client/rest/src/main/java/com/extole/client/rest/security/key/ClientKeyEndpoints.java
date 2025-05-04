package com.extole.client.rest.security.key;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.security.key.built.BuiltClientKeyResponse;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/settings/security/keys")
public interface ClientKeyEndpoints {

    @Produces(APPLICATION_JSON)
    @GET
    <T extends ClientKeyResponse> List<T> listClientKeys(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ClientKeyFilterRequest clientKeyFilterRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(APPLICATION_JSON)
    @GET
    @Path("/built")
    <T extends BuiltClientKeyResponse> List<T> listBuiltClientKeys(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ClientKeyFilterRequest clientKeyFilterRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    <T extends ClientKeyResponse> T create(@UserAccessTokenParam String accessToken,
        StringBasedClientKeyCreateRequest createRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildClientKeyRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, ClientKeyValidationRestException, OAuthClientKeyValidationRestException,
        OAuthClientKeyBuildRestException;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    <REQUEST extends FileBasedClientKeyCreateRequest> ClientKeyResponse create(
        @UserAccessTokenParam String accessToken, FileClientKeyRequest<REQUEST> createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildClientKeyRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, OAuthClientKeyValidationRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException;

    @PUT
    @Path("/{key_id}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    <T extends ClientKeyResponse> T update(@UserAccessTokenParam String accessToken, @PathParam("key_id") String keyId,
        ClientKeyUpdateRequest createRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildClientKeyRestException, OAuthClientKeyValidationRestException,
        OAuthSalesforceClientKeyValidationRestException, ClientKeyRestException,
        CampaignComponentValidationRestException, ExternalElementRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException;

    @GET
    @Path("/{key_id}/decrypted")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    ClientKeyResponse getDecrypted(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("key_id") String keyId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildClientKeyRestException, OAuthClientKeyValidationRestException,
        OAuthSalesforceClientKeyValidationRestException, ClientKeyRestException,
        CampaignComponentValidationRestException, ExternalElementRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException;

    @DELETE
    @Path("/{key_id}")
    @Produces(APPLICATION_JSON)
    <T extends ClientKeyResponse> T archive(@UserAccessTokenParam String accessToken, @PathParam("key_id") String keyId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException, ClientKeyArchiveRestException,
        ExternalElementRestException;

    @POST
    @Path("/{key_id}/delete")
    @Produces(APPLICATION_JSON)
    <T extends ClientKeyResponse> T delete(@UserAccessTokenParam String accessToken, @PathParam("key_id") String keyId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException, ClientKeyArchiveRestException,
        ExternalElementRestException;

    @POST
    @Path("/{key_id}/unarchive")
    @Produces(APPLICATION_JSON)
    <T extends ClientKeyResponse> T unArchive(@UserAccessTokenParam String accessToken,
        @PathParam("key_id") String keyId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException, ClientKeyArchiveRestException,
        ExternalElementRestException;

    @GET
    @Path("/pgp-extole")
    @Produces(APPLICATION_JSON)
    GenericClientKeyResponse getPgpExtoleClientKey(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientKeyRestException;

    @POST
    @Path("/pgp-extole")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    GenericClientKeyResponse createPgpExtoleClientKey(@UserAccessTokenParam String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, BuildClientKeyRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, ClientKeyValidationRestException;

}
