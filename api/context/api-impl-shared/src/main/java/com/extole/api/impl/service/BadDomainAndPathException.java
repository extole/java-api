package com.extole.api.impl.service;

public class BadDomainAndPathException extends Exception {

    public BadDomainAndPathException(String domainAndPath) {
        super(generateErrorString(domainAndPath));
    }

    public BadDomainAndPathException(String domainAndPath, Throwable cause) {
        super(generateErrorString(domainAndPath), cause);
    }

    private static String generateErrorString(String domainAndPath) {
        return "The domain and path " + domainAndPath + " is not a valid URI or contains a scheme";
    }

}
