package com.extole.consumer.rest.impl.support.filter;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.common.rest.support.filter.CorsHeaderInjector;
import com.extole.consumer.rest.impl.common.Scheme;
import com.extole.consumer.rest.impl.common.SitePatternsUrlValidator;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.shared.program.ProgramDomainCache;

@Provider
public class ConsumerCorsFilter implements ContainerResponseFilter {
    private final ProgramDomainCache programCache;
    private final SitePatternsUrlValidator sitePatternsUrlValidator;

    @Autowired
    public ConsumerCorsFilter(
        ProgramDomainCache programCache,
        SitePatternsUrlValidator sitePatternsUrlValidator) {
        this.programCache = programCache;
        this.sitePatternsUrlValidator = sitePatternsUrlValidator;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String clientIdAsString =
            (String) requestContext.getProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName());
        Optional<Id<ClientHandle>> clientId = Optional.ofNullable(clientIdAsString).map(id -> Id.valueOf(id));
        CorsHeaderInjector.builder(requestContext, clientId)
            .withRequestOriginValidator((requestedDomain, origin) -> {
                if (clientId.isEmpty()) {
                    return false;
                }
                return sitePatternsUrlValidator.isSupported(clientId.get(), requestedDomain, origin);
            })
            .withRejectionHeaderProvider(() -> {
                if (clientId.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(getDefaultSupportedOriginHeaderValue(clientId.get()));
            })
            .withContext(this.getClass().getName())
            .withZoomEmptyResponseOnFailure()
            .build()
            .inject(responseContext);
    }

    private String getDefaultSupportedOriginHeaderValue(Id<ClientHandle> clientId) {
        Optional<PublicProgram> defaultProgram = programCache.getDefaultProgram(clientId);
        if (!defaultProgram.isPresent()) {
            return "*";
        }
        return new StringBuilder().append(Scheme.HTTPS).append("://")
            .append(defaultProgram.get().getProgramDomain().toString()).toString();
    }
}
