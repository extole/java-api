package com.extole.common.rest.support.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.model.RequestContextAttributeName;
import com.extole.id.Id;

@Provider
@Priority(FilterPriority.CORS)
public class ExtoleSiteCorsFilter implements ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ExtoleSiteCorsFilter.class);
    private static final String DEFAULT_EXTOLE_SITE_TEMPLATE = "my{0}.extole.com";

    private final List<Pattern> extoleSitePatterns;
    private final String defaultExtoleSite;

    @Autowired
    public ExtoleSiteCorsFilter(@Value("${extole.environment:lo}") String environment) {

        String environmentSitePrefix = "";
        if (!environment.equals("pr")) {
            environmentSitePrefix = "." + environment;
        }

        String siteWithoutSchema = MessageFormat.format(DEFAULT_EXTOLE_SITE_TEMPLATE, environmentSitePrefix);
        defaultExtoleSite = "https://" + siteWithoutSchema;
        Pattern myExtole = Pattern.compile(siteWithoutSchema.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*"),
            Pattern.CASE_INSENSITIVE);
        Pattern extoleGithubIo = Pattern.compile("extole\\.github\\.io", Pattern.CASE_INSENSITIVE);
        Pattern extoleIo = Pattern.compile(".*\\.extole\\.io", Pattern.CASE_INSENSITIVE);
        Pattern localhost = Pattern.compile(".*localhost", Pattern.CASE_INSENSITIVE);
        extoleSitePatterns = new ArrayList<>();
        extoleSitePatterns.add(myExtole);
        extoleSitePatterns.add(extoleGithubIo);
        // ENG-15766 fix cross client security risk
        extoleSitePatterns.add(extoleIo);
        extoleSitePatterns.add(localhost);
        LOG.debug(MessageFormat.format("Added {0} to allowed client sites.", defaultExtoleSite));
        LOG.debug("Set the default extole site to {}", defaultExtoleSite);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String clientIdAsString =
            (String) requestContext.getProperty(RequestContextAttributeName.CLIENT_ID.getAttributeName());
        Optional<Id<ClientHandle>> clientId = Optional.ofNullable(clientIdAsString).map(id -> Id.valueOf(id));
        CorsHeaderInjector.builder(requestContext, clientId)
            .withRequestOriginValidator((requestedDomain, origin) -> {
                return isSupportedClientApi(origin);
            })
            .withRejectionHeaderProvider(() -> {
                return Optional.of(defaultExtoleSite);
            })
            .build().inject(responseContext);
    }

    private boolean isSupportedClientApi(String origin) {
        Optional<String> originHost = extractHost(origin);
        if (!originHost.isPresent()) {
            return false;
        }
        return extoleSitePatterns.stream().anyMatch(pattern -> pattern.matcher(originHost.get()).matches());
    }

    private Optional<String> extractHost(String origin) {
        try {
            URI uri = new URI(origin);
            final String originHost;
            if (!Strings.isNullOrEmpty(uri.getHost())) {
                originHost = uri.getHost();
            } else if (!Strings.isNullOrEmpty(uri.getPath())) {
                originHost = uri.getPath();
            } else {
                originHost = origin;
            }
            return Optional.ofNullable(originHost);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
