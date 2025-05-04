package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface UrlService {

    boolean isUrlMatchingAnyOfTheDomainsAndPaths(String url, String[] domainsAndPaths) throws BadDomainAndPathException;

}
