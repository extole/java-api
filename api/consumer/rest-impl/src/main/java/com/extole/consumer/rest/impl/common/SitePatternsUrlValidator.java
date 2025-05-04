package com.extole.consumer.rest.impl.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.shared.program.ProgramDomainCache;

@Component
public class SitePatternsUrlValidator {
    private static final Logger LOG = LoggerFactory.getLogger(SitePatternsUrlValidator.class);
    private final ProgramDomainCache programCache;
    private final boolean onlySecureSitesAllowed;
    private final Set<String> clientsWithOnlySecureSitesAllowed;

    @Autowired
    public SitePatternsUrlValidator(
        @Value("${consumer.cors.clientsWithOnlySecureSitesAllowed:*}") String clientsWithOnlySecureSitesAllowed,
        ProgramDomainCache programCache) {
        this.programCache = programCache;
        this.clientsWithOnlySecureSitesAllowed = Collections.unmodifiableSet(
            Stream.of(StringUtils.split(Strings.nullToEmpty(clientsWithOnlySecureSitesAllowed), ","))
                .map(clientId -> clientId.trim())
                .filter(clientId -> StringUtils.isNotEmpty(clientId))
                .collect(Collectors.toSet()));
        this.onlySecureSitesAllowed = this.clientsWithOnlySecureSitesAllowed.contains("*");
    }

    public boolean isSupported(Id<ClientHandle> clientId, String requestedDomain, String origin) {
        Optional<String> originHost = extractOriginHost(clientId, origin);
        if (originHost.isEmpty()) {
            return false;
        }
        List<PublicProgram> programs;
        try {
            programs = programCache.getMatchingPrograms(clientId, new URI(requestedDomain));
        } catch (URISyntaxException e) {
            LOG.warn("Is not possible to create URI instance for {}", requestedDomain, e);
            return false;
        }
        return programs.stream().flatMap(program -> program.getSitePatterns().stream())
            .anyMatch(pattern -> pattern.getRegex().matcher(originHost.get()).matches());
    }

    private Optional<String> extractOriginHost(Id<ClientHandle> clientId, String origin) {
        try {
            URI uri = new URI(origin);
            if ((onlySecureSitesAllowed || clientsWithOnlySecureSitesAllowed.contains(clientId.getValue()))
                && !Scheme.HTTPS.name().equalsIgnoreCase(uri.getScheme())) {
                return Optional.empty();
            }

            final String originHost;
            if (!Strings.isNullOrEmpty(uri.getHost())) {
                originHost = uri.getHost();
            } else if (!Strings.isNullOrEmpty(uri.getPath())) {
                originHost = uri.getPath();
            } else {
                originHost = origin;
            }
            return Optional.of(originHost);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
