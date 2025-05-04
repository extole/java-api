package com.extole.consumer.rest.impl.common;

import java.net.URI;

public enum Scheme {
    HTTP, HTTPS;

    public static Scheme fromUri(URI uri) throws UriException {
        return fromProtocol(uri.getScheme());
    }

    public static Scheme fromProtocol(String protocol) throws UriException {
        if (HTTP.name().equalsIgnoreCase(protocol)) {
            return HTTP;
        } else if (HTTPS.name().equalsIgnoreCase(protocol)) {
            return HTTPS;
        } else {
            throw new UriException("Invalid scheme: " + protocol);
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
