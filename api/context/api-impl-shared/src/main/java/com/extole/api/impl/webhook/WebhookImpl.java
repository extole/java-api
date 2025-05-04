package com.extole.api.impl.webhook;

import javax.annotation.Nullable;

import com.extole.api.client.security.key.ClientKey;
import com.extole.api.component.ComponentReference;
import com.extole.api.impl.client.security.key.ClientKeyImpl;
import com.extole.api.impl.component.ComponentReferenceImpl;
import com.extole.api.webhook.Webhook;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.key.provider.service.KeyProviderService;
import com.extole.model.entity.webhook.built.BuiltWebhook;
import com.extole.model.service.client.security.key.ClientKeyNotFoundException;
import com.extole.model.shared.client.security.key.webhook.WebhookClientKeyCache;

public class WebhookImpl implements Webhook {

    private ClientKey clientKey;
    private final String url;
    private final String defaultMethod;
    private final ComponentReference[] componentReferences;

    public WebhookImpl(
        KeyProviderService keyProviderService,
        WebhookClientKeyCache webhookClientKeyCache,
        BuiltWebhook webhook) {
        if (webhook.getClientKeyId().isPresent()) {
            Id<com.extole.model.entity.client.security.key.ClientKey> clientKeyId = webhook.getClientKeyId().get();
            Id<ClientHandle> clientId = webhook.getClientId();
            try {
                com.extole.model.entity.client.security.key.ClientKey webhookClientKey =
                    webhookClientKeyCache.getByClientKeyId(clientId, clientKeyId);
                this.clientKey = new ClientKeyImpl(keyProviderService, webhookClientKey);
            } catch (ClientKeyNotFoundException e) {
                throw new RuntimeException(
                    "Was unable to find webhook client key with id=" + clientKeyId + " for client=" + clientId, e);
            }
        }

        this.url = webhook.getUrl().getValue();
        this.defaultMethod = webhook.getDefaultMethod();
        this.componentReferences = webhook.getComponentReferences()
            .stream()
            .map(builtRef -> new ComponentReferenceImpl(builtRef.getComponentId().getValue()))
            .toArray(ComponentReference[]::new);
    }

    @Nullable
    @Override
    public ClientKey getClientKey() {
        return clientKey;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getDefaultMethod() {
        return defaultMethod;
    }

    @Override
    public ComponentReference[] getComponentReferences() {
        return componentReferences;
    }

}
