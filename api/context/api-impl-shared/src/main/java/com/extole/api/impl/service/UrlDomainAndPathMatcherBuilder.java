package com.extole.api.impl.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Strings;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;

import com.extole.util.url.SchemeRecognizer;

public final class UrlDomainAndPathMatcherBuilder {

    private final List<String> domainsAndPaths;

    private UrlDomainAndPathMatcherBuilder() {
        domainsAndPaths = new LinkedList<>();
    }

    public static UrlDomainAndPathMatcherBuilder newBuilder() {
        return new UrlDomainAndPathMatcherBuilder();
    }

    public UrlDomainAndPathMatcherBuilder addDomainAndPath(String domainAndPath) throws BadDomainAndPathException {
        if (!isValidDomainAndPath(domainAndPath)) {
            throw new BadDomainAndPathException(domainAndPath);
        }
        domainsAndPaths.add(domainAndPath);

        return this;
    }

    public UrlDomainAndPathMatcher build() {
        TrieBuilder trieBuilder = Trie.builder().ignoreOverlaps().ignoreCase();
        for (String domainAndPath : domainsAndPaths) {
            trieBuilder.addKeyword(domainAndPath);
        }

        return new UrlDomainAndPathMatcher(trieBuilder.build());
    }

    /**
     * Needs to be a valid URL without a scheme
     */
    private static boolean isValidDomainAndPath(String domainAndPath) {
        if (SchemeRecognizer.hasScheme(domainAndPath)) {
            return false;
        }

        try {
            validateURI(domainAndPath);
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }

    public static final class UrlDomainAndPathMatcher {

        private final Trie trie;

        private UrlDomainAndPathMatcher(Trie trie) {
            this.trie = trie;
        }

        public boolean isUrlMatching(String url) throws BadDomainAndPathException {
            if (Strings.isNullOrEmpty(url)) {
                return false;
            }

            try {
                validateURI(url);
            } catch (URISyntaxException e) {
                // We are only matching URIs, if the URL isn't a URI, it can't be matched
                return false;
            }
            try {
                Collection<Emit> matches = trie.parseText(url);
                for (Emit match : matches) {
                    if (fuzzyMatch(url, match)) {
                        return true;
                    }
                }
                // Trie throws NPE for some invalid inputs
            } catch (NullPointerException e) {
                throw new BadDomainAndPathException("Trie failed to parse/match on URL:" + url, e);
            }

            return false;
        }

        private static boolean fuzzyMatch(String url, Emit match) {
            int startIndex = match.getStart();
            int endIndex = match.getEnd();

            // pharmacy.com does not match macy.com, however couponsite.macy.com does match
            if (startIndex != 0 && "./".indexOf(url.charAt(startIndex - 1)) == -1) {
                return false;
            }

            // macy.com/pathmore does not match macy.com/path, however macy.com/path/more
            // or macy.com/path?more=true does
            if (endIndex != url.length() - 1 && "?/\\".indexOf(url.charAt(endIndex + 1)) == -1) {
                return false;
            }

            return true;
        }

    }

    @SuppressWarnings("unused")
    private static void validateURI(String url) throws URISyntaxException {
        new URI(SchemeRecognizer.addSchemeToUrl(url));
    }

}
