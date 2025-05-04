package com.extole.client.rest.impl.security.key;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyCreateRequest;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.FileBasedClientKeyCreateRequest;
import com.extole.client.rest.security.key.FileClientKeyRequest;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.lead.perfection.OAuthLeadPerfectionClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.sfdc.password.OAuthSfdcPasswordClientKeyValidationRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.model.entity.client.security.key.ClientKey;

public interface ClientKeyCreateRequestMapper<REQUEST extends ClientKeyCreateRequest, KEY extends ClientKey> {

    KEY create(Authorization authorization, REQUEST createRequest)
        throws BuildClientKeyRestException, UserAuthorizationRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, ClientKeyValidationRestException, OAuthClientKeyValidationRestException,
        OAuthClientKeyBuildRestException;

    default KEY create(Authorization authorization,
        FileClientKeyRequest<? extends FileBasedClientKeyCreateRequest> createRequest)
        throws BuildClientKeyRestException, UserAuthorizationRestException, ClientKeyRestException,
        OAuthSalesforceClientKeyValidationRestException, OAuthLeadPerfectionClientKeyValidationRestException,
        CampaignComponentValidationRestException, OAuthSfdcPasswordClientKeyValidationRestException,
        ExternalElementRestException, OAuthClientKeyValidationRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException {
        throw new UnsupportedOperationException();
    }

    ClientKeyAlgorithm getAlgorithm();
}
