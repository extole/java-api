package com.extole.client.rest.impl.webhook.reward.filter.supplier;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.webhook.reward.filter.supplier.SupplierRewardWebhookFilterResponse;
import com.extole.client.rest.webhook.reward.filter.supplier.built.BuiltSupplierRewardWebhookFilterResponse;
import com.extole.model.entity.webhook.built.reward.filter.supplier.BuiltSupplierRewardWebhookFilter;
import com.extole.model.entity.webhook.reward.filter.supplier.SupplierRewardWebhookFilter;

@Component
public class SupplierRewardWebhookFilterRestMapper {

    public SupplierRewardWebhookFilterResponse toRewardWebhookSupplierFilterResponse(
        SupplierRewardWebhookFilter filter, ZoneId timeZone) {
        return new SupplierRewardWebhookFilterResponse(filter.getId().getValue(),
            filter.getRewardSupplierId(),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }

    public BuiltSupplierRewardWebhookFilterResponse toBuiltRewardWebhookSupplierFilterResponse(
        BuiltSupplierRewardWebhookFilter filter, ZoneId timeZone) {
        return new BuiltSupplierRewardWebhookFilterResponse(filter.getId().getValue(),
            filter.getRewardSupplierId().getValue(),
            filter.getCreatedAt().atZone(timeZone),
            filter.getUpdatedAt().atZone(timeZone));
    }
}
