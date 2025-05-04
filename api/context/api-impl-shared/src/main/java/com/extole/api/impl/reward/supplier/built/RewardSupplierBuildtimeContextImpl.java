package com.extole.api.impl.reward.supplier.built;

import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.impl.campaign.ExtendableComponentBuildtimeContextImpl;
import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;

public class RewardSupplierBuildtimeContextImpl extends ExtendableComponentBuildtimeContextImpl
    implements RewardSupplierBuildtimeContext {

    public RewardSupplierBuildtimeContextImpl(ComponentBuildtimeContext context) {
        super(context);
    }

}
