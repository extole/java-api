package com.extole.client.rest.impl.reward.supplier;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.reward.supplier.BuildRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.CustomRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.PayPalPayoutsRewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.RewardSupplierUpdateRequest;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierCreateRestException;
import com.extole.client.rest.reward.supplier.SalesforceCouponRewardSupplierValidationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierCreationRestException;
import com.extole.client.rest.reward.supplier.TangoRewardSupplierValidationRestException;
import com.extole.client.rest.salesforce.ClientSalesforceSettingsRestException;
import com.extole.client.rest.salesforce.SalesforceConnectionRestException;
import com.extole.client.rest.tango.TangoConnectionRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.model.entity.reward.supplier.RewardSupplier;

public interface RewardSupplierUpdateRequestMapper<REQUEST extends RewardSupplierUpdateRequest,
    KEY extends RewardSupplier> {

    KEY update(Authorization authorization, String rewardSupplierId, REQUEST updateRequest)
        throws CustomRewardSupplierRestException, UserAuthorizationRestException, RewardSupplierRestException,
        RewardSupplierCreationRestException, BuildRewardSupplierRestException,
        CampaignComponentValidationRestException, SalesforceCouponRewardSupplierCreateRestException,
        ClientSalesforceSettingsRestException, SalesforceCouponRewardSupplierValidationRestException,
        SalesforceConnectionRestException, TangoRewardSupplierCreationRestException, TangoConnectionRestException,
        TangoRewardSupplierValidationRestException, PayPalPayoutsRewardSupplierRestException;

    RewardSupplierType getType();
}
