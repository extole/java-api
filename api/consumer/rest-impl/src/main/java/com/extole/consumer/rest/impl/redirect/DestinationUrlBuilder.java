package com.extole.consumer.rest.impl.redirect;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.extole.consumer.service.zone.ZoneRenderRequest;

public class DestinationUrlBuilder {
    private static final String PARAM_ACCESS_TOKEN = "access_token";
    private static final String PARAM_EXTOLE_TOKEN = "extole_token";
    private static final String DEFAULT_SCHEME = "http";

    private URI destinationUrl;

    @Nullable
    private URI incomingRequest;

    public DestinationUrlBuilder withIncomingRequest(@Nullable URI incomingRequest) {
        this.incomingRequest = incomingRequest;
        return this;
    }

    public DestinationUrlBuilder withDestinationUrl(URI destinationUrl) {
        this.destinationUrl = Objects.requireNonNull(destinationUrl, "Destination URL cannot be null");
        return this;
    }

    public URI build() throws URISyntaxException {
        if (destinationUrl == null) {
            throw new IllegalStateException("Destination URL must be set. Incoming request: " + incomingRequest);
        }

        String scheme = MoreObjects.firstNonNull(getScheme(destinationUrl, incomingRequest), DEFAULT_SCHEME);
        String authority = Strings.nullToEmpty(destinationUrl.getAuthority());
        String path = Strings.nullToEmpty(destinationUrl.getPath());
        String fragment = getFragment(destinationUrl, incomingRequest);
        List<NameValuePair> queryParams = getQueryParams(destinationUrl, incomingRequest);

        return buildLink(scheme, authority, path, queryParams, fragment);
    }

    @Nullable
    private String getScheme(URI destinationUrl, @Nullable URI incomingRequest) {
        if (destinationUrl.getScheme() != null) {
            return destinationUrl.getScheme();
        }
        return incomingRequest != null ? incomingRequest.getScheme() : null;
    }

    @Nullable
    private String getFragment(URI destinationUrl, @Nullable URI incomingRequest) {
        if (destinationUrl.getFragment() != null) {
            return destinationUrl.getFragment();
        }
        return incomingRequest != null ? incomingRequest.getFragment() : null;
    }

    private static List<NameValuePair> getQueryParams(URI destinationUrl, @Nullable URI incomingRequest) {
        Multimap<String, NameValuePair> queryParams = ArrayListMultimap.create();

        addQueryParams(queryParams, destinationUrl, false);
        List<NameValuePair> labelParams =
            new ArrayList<>(queryParams.removeAll(ZoneRenderRequest.ZONE_PARAMETER_LABELS));
        List<NameValuePair> requiredLabelParams =
            new ArrayList<>(queryParams.removeAll(ZoneRenderRequest.ZONE_PARAMETER_REQUIRED_LABELS));

        if (incomingRequest != null) {
            addQueryParams(queryParams, incomingRequest, true);
            labelParams.addAll(queryParams.removeAll(ZoneRenderRequest.ZONE_PARAMETER_LABELS));
            requiredLabelParams.addAll(queryParams.removeAll(ZoneRenderRequest.ZONE_PARAMETER_REQUIRED_LABELS));
        }

        Set<String> labelNames = labelParams.stream()
            .map(pair -> pair.getValue())
            .filter(labelName -> !Strings.isNullOrEmpty(labelName))
            .collect(Collectors.toSet());
        Set<String> requiredLabelNames = requiredLabelParams.stream()
            .map(pair -> pair.getValue())
            .filter(labelName -> !Strings.isNullOrEmpty(labelName))
            .collect(Collectors.toSet());

        if (!labelNames.isEmpty()) {
            queryParams.put(ZoneRenderRequest.ZONE_PARAMETER_LABELS,
                new BasicNameValuePair(ZoneRenderRequest.ZONE_PARAMETER_LABELS, StringUtils.join(labelNames, ",")));
        }
        if (!requiredLabelNames.isEmpty()) {
            queryParams.put(ZoneRenderRequest.ZONE_PARAMETER_REQUIRED_LABELS, new BasicNameValuePair(
                ZoneRenderRequest.ZONE_PARAMETER_REQUIRED_LABELS, StringUtils.join(requiredLabelNames, ",")));
        }

        queryParams.removeAll(PARAM_ACCESS_TOKEN);
        queryParams.removeAll(PARAM_EXTOLE_TOKEN);
        return new ArrayList<>(queryParams.values());
    }

    private static void addQueryParams(Multimap<String, NameValuePair> queryParams, URI url, boolean replaceExisting) {
        String queryString = url.getQuery();
        if (queryString == null) {
            return;
        }
        Multimap<String, NameValuePair> newParamsMap = ArrayListMultimap.create();
        List<NameValuePair> newParams = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        for (NameValuePair pair : newParams) {
            newParamsMap.put(pair.getName(), pair);
        }
        for (Entry<String, Collection<NameValuePair>> entry : newParamsMap.asMap().entrySet()) {
            if (replaceExisting) {
                queryParams.removeAll(entry.getKey());
            }
            queryParams.putAll(entry.getKey(), entry.getValue());
        }
    }

    private URI buildLink(String scheme, String authority, String path, List<NameValuePair> queryParams,
        @Nullable String fragment) throws URISyntaxException {
        URIBuilder builder;
        builder = new URIBuilder(scheme + "://" + authority + path);
        if (!queryParams.isEmpty()) {
            builder.addParameters(queryParams);
        }
        if (fragment != null) {
            builder.setFragment(fragment);
        }
        return builder.build();
    }

}
