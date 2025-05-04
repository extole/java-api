package com.extole.api.impl.service;

import com.extole.api.impl.service.UrlDomainAndPathMatcherBuilder.UrlDomainAndPathMatcher;
import com.extole.api.service.BadDomainAndPathException;
import com.extole.api.service.UrlService;

public class UrlServiceImpl implements UrlService {

    @Override
    public boolean isUrlMatchingAnyOfTheDomainsAndPaths(String url, String[] domainsAndPaths)
        throws BadDomainAndPathException {
        if (url == null) {
            return false;
        }

        if (domainsAndPaths == null || domainsAndPaths.length == 0) {
            return false;
        }

        UrlDomainAndPathMatcherBuilder builder = UrlDomainAndPathMatcherBuilder.newBuilder();
        for (String domainAndPath : domainsAndPaths) {
            try {
                builder.addDomainAndPath(domainAndPath);
            } catch (com.extole.api.impl.service.BadDomainAndPathException e) {
                throw new BadDomainAndPathException(e);
            }
        }

        UrlDomainAndPathMatcher matcher = builder.build();

        try {
            return matcher.isUrlMatching(url);
        } catch (com.extole.api.impl.service.BadDomainAndPathException e) {
            throw new BadDomainAndPathException(e);
        }
    }

}
