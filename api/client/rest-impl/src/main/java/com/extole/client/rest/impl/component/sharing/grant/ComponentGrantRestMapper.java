package com.extole.client.rest.impl.component.sharing.grant;

import java.time.ZoneId;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.component.sharing.grant.ComponentGrantResponse;
import com.extole.client.rest.component.sharing.grant.ComponentGranterResponse;
import com.extole.client.rest.component.sharing.grant.SubscriptionMode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.client.Client;
import com.extole.model.entity.client.PublicClient;
import com.extole.model.entity.component.sharing.grant.ComponentGrant;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;
import com.extole.model.shared.program.ProgramDomainCache;

@Component
public class ComponentGrantRestMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentGrantRestMapper.class);
    private static final String ALL_CLIENTS = "*";
    private static final String UNKNOWN = "unknown";
    private final ClientCache clientCache;
    private final ProgramDomainCache programDomainCache;

    @Autowired
    public ComponentGrantRestMapper(ClientCache clientCache, ProgramDomainCache programDomainCache) {
        this.clientCache = clientCache;
        this.programDomainCache = programDomainCache;
    }

    public ComponentGrantResponse toComponentGrantResponse(ComponentGrant componentGrant, ZoneId timeZone) {
        return new ComponentGrantResponse(
            componentGrant.getId().getValue(),
            computeTargetClientId(componentGrant),
            SubscriptionMode.valueOf(componentGrant.getSubscriptionMode().name()),
            componentGrant.getCreatedDate().atZone(timeZone));
    }

    public ComponentGranterResponse toComponentGranterResponse(ComponentGrant componentGrant,
        ZoneId timeZone) {
        Optional<PublicClient> granter = getGranter(componentGrant);
        String previewDomain = getPreviewDomain(componentGrant);
        return new ComponentGranterResponse(
            componentGrant.getId().getValue(),
            componentGrant.getClientId().getValue(),
            granter.map(PublicClient::getName).orElse(UNKNOWN),
            granter.map(PublicClient::getShortName).orElse(UNKNOWN),
            previewDomain,
            componentGrant.getCreatedDate().atZone(timeZone));
    }

    private String getPreviewDomain(ComponentGrant componentGrant) {
        return programDomainCache.getDefaultProgram(componentGrant.getClientId())
            .map(publicProgram -> publicProgram.getProgramDomain())
            .map(internetDomainName -> internetDomainName.toString())
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build());
    }

    private Optional<PublicClient> getGranter(ComponentGrant componentGrant) {
        try {
            return Optional.of(clientCache.getById(componentGrant.getClientId()));
        } catch (ClientNotFoundException e) {
            LOG.warn("Could not find client {} for grant {}.", componentGrant.getClientId(),
                componentGrant.getId().getValue());
            return Optional.empty();
        }
    }

    private static String computeTargetClientId(ComponentGrant componentGrant) {
        return Client.EXTOLE_CLIENT_ID.equals(componentGrant.getTargetClientId()) ? ALL_CLIENTS
            : componentGrant.getTargetClientId().getValue();
    }

}
