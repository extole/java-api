package com.extole.api.impl.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.extole.api.impl.service.UrlDomainAndPathMatcherBuilder.UrlDomainAndPathMatcher;

public class BlacklistRefererHeaderTest {

    private UrlDomainAndPathMatcher createDomainBlacklist() throws Exception {
        return UrlDomainAndPathMatcherBuilder
            .newBuilder()
            .addDomainAndPath("domain.com")
            .build();
    }

    private UrlDomainAndPathMatcher createSubDomainBlacklist() throws Exception {
        return UrlDomainAndPathMatcherBuilder
            .newBuilder()
            .addDomainAndPath("sub.domain.com")
            .build();
    }

    private UrlDomainAndPathMatcher createPathBlacklist() throws Exception {
        return UrlDomainAndPathMatcherBuilder
            .newBuilder()
            .addDomainAndPath("domain.com/path")
            .build();
    }

    @Test
    public void simpleMatch() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("domain.com"));
    }

    @Test
    public void simpleNonMatch() throws Exception {
        assertFalse(createDomainBlacklist().isUrlMatching("not.com"));
    }

    @Test
    public void preNonMatch() throws Exception {
        assertFalse(createDomainBlacklist().isUrlMatching("notdomain.com"));
    }

    @Test
    public void preMatch() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("not.domain.com"));
    }

    @Test
    public void postNotMatch() throws Exception {
        assertFalse(createDomainBlacklist().isUrlMatching("domain.comnot"));
    }

    @Test
    public void postMatch() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("domain.com/not"));
    }

    @Test
    public void postPostyMatch() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("domain.com/not/not"));
    }

    @Test
    public void postPostyEmptyMatch() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("domain.com/not/"));
    }

    @Test
    public void postEmptyMatch() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("domain.com/"));
    }

    @Test
    public void simpleMatchWithSchema() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("http://domain.com"));
    }

    @Test
    public void simpleNonMatchWithSchema() throws Exception {
        assertFalse(createDomainBlacklist().isUrlMatching("http://not.com"));
    }

    @Test
    public void preNonMatchWithSchema() throws Exception {
        assertFalse(createDomainBlacklist().isUrlMatching("http://notdomain.com"));
    }

    @Test
    public void preMatchWithSchema() throws Exception {
        assertTrue(createDomainBlacklist().isUrlMatching("http://not.domain.com"));
    }

    @Test
    public void badRefererDomain() throws Exception {
        assertFalse(createDomainBlacklist().isUrlMatching("%notadomain"));
    }

    @Test
    public void simpleSubMatch() throws Exception {
        assertTrue(createSubDomainBlacklist().isUrlMatching("sub.domain.com"));
    }

    @Test
    public void preNonSubMatch() throws Exception {
        assertFalse(createSubDomainBlacklist().isUrlMatching("subdomain.com"));
    }

    @Test
    public void preMatchSubWithSchema() throws Exception {
        assertTrue(createSubDomainBlacklist().isUrlMatching("http://sub.domain.com"));
    }

    @Test
    public void postSubMatch() throws Exception {
        assertTrue(createSubDomainBlacklist().isUrlMatching("http://sub.domain.com/path"));
    }

    @Test
    public void simplePathMatch() throws Exception {
        assertTrue(createPathBlacklist().isUrlMatching("domain.com/path"));
    }

    @Test
    public void postPostyPathMatch() throws Exception {
        assertTrue(createPathBlacklist().isUrlMatching("domain.com/path/not"));
    }

    @Test
    public void postPostyEmptyPathMatch() throws Exception {
        assertTrue(createPathBlacklist().isUrlMatching("domain.com/path/"));
    }

}
