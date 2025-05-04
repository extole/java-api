package com.extole.client.rest.impl.security.key;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ExternalElementRestException;
import com.extole.client.rest.security.key.BuildClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyAlgorithm;
import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.client.rest.security.key.ClientKeyUpdateRequest;
import com.extole.client.rest.security.key.ClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyBuildRestException;
import com.extole.client.rest.security.key.oauth.OAuthClientKeyValidationRestException;
import com.extole.client.rest.security.key.oauth.salesforce.OAuthSalesforceClientKeyValidationRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.model.entity.client.security.key.ClientKey;

public interface ClientKeyUpdateRequestMapper<REQUEST extends ClientKeyUpdateRequest, KEY extends ClientKey> {

    KEY update(Authorization authorization, Id<ClientKey> clientKeyId, REQUEST updateRequest)
        throws BuildClientKeyRestException, UserAuthorizationRestException, OAuthClientKeyValidationRestException,
        OAuthSalesforceClientKeyValidationRestException, ClientKeyRestException,
        CampaignComponentValidationRestException, ExternalElementRestException, ClientKeyValidationRestException,
        OAuthClientKeyBuildRestException;

    ClientKeyAlgorithm getAlgorithm();

}
