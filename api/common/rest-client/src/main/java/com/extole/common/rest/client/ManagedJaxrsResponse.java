package com.extole.common.rest.client;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.glassfish.jersey.internal.util.Tokenizer;

final class ManagedJaxrsResponse extends Response {
    private static final byte[] EMPTY_BYTES = new byte[] {};
    private final int status;
    private final StatusType statusInfo;
    private final byte[] entity;
    private final MediaType mediaType;
    private final Locale locale;
    private final int length;
    private final Set<String> allowedMethods;
    private final Map<String, NewCookie> cookies;
    private final EntityTag entityTag;
    private final Date date;
    private final Date lastModified;
    private final URI location;
    private final Set<Link> links;
    private final MultivaluedMap<String, Object> headers;
    private final MultivaluedMap<String, String> stringHeaders;
    private final Map<Class<?>, Function<byte[], ?>> entityReaders;

    public static Builder builder(Response response) {
        return new Builder(response);
    }

    private ManagedJaxrsResponse(Response response, Map<Class<?>, Function<byte[], ?>> entityReaders) {
        this.status = response.getStatus();
        this.statusInfo = response.getStatusInfo();
        this.entity = response.hasEntity() ? response.readEntity(byte[].class) : EMPTY_BYTES;
        this.mediaType = response.getMediaType();
        this.locale = response.getLanguage();
        this.length = response.getLength();
        this.allowedMethods = ImmutableSet.copyOf(response.getAllowedMethods());
        this.cookies = ImmutableMap.copyOf(response.getCookies());
        this.entityTag = response.getEntityTag();
        this.date = response.getDate();
        this.lastModified = response.getLastModified();
        this.location = response.getLocation();
        this.links = ImmutableSet.copyOf(response.getLinks());
        this.headers = response.getHeaders();
        this.stringHeaders = response.getStringHeaders();
        this.entityReaders = ImmutableMap.copyOf(entityReaders);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public StatusType getStatusInfo() {
        return statusInfo;
    }

    @Override
    public Object getEntity() {
        return new ByteArrayInputStream(entity);
    }

    @Override
    public <T> T readEntity(Class<T> entityType) {
        return internalRead(entityType);
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType) {
        @SuppressWarnings("unchecked")
        T result = (T) internalRead(entityType.getRawType());
        return result;
    }

    @Override
    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        return internalRead(entityType);
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        @SuppressWarnings("unchecked")
        T result = (T) internalRead(entityType.getRawType());
        return result;
    }

    @Override
    public boolean hasEntity() {
        return entity != EMPTY_BYTES;
    }

    @Override
    public boolean bufferEntity() {
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public Locale getLanguage() {
        return locale;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        return cookies;
    }

    @Override
    public EntityTag getEntityTag() {
        return entityTag;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public URI getLocation() {
        return location;
    }

    @Override
    public Set<Link> getLinks() {
        return links;
    }

    @Override
    public boolean hasLink(String relation) {
        for (Link link : links) {
            List<String> relations = getLinkRelations(link.getRel());

            if (relations != null && relations.contains(relation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Link getLink(String relation) {
        for (Link link : getLinks()) {
            List<String> relations = getLinkRelations(link.getRel());
            if (relations != null && relations.contains(relation)) {
                return link;
            }
        }
        return null;
    }

    @Override
    public Link.Builder getLinkBuilder(String relation) {
        Link link = getLink(relation);
        if (link == null) {
            return null;
        }

        return Link.fromLink(link);
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return headers;
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        return stringHeaders;
    }

    @Override
    public String getHeaderString(String name) {
        List<String> values = this.stringHeaders.get(name);
        if (values == null) {
            return null;
        }
        if (values.isEmpty()) {
            return "";
        }

        final Iterator<String> valuesIterator = values.iterator();
        StringBuilder buffer = new StringBuilder(valuesIterator.next());
        while (valuesIterator.hasNext()) {
            buffer.append(',').append(valuesIterator.next());
        }

        return buffer.toString();
    }

    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        return headers;
    }

    private <T> T internalRead(Class<T> expectedType) {
        @SuppressWarnings("unchecked")
        Function<byte[], T> reader = (Function<byte[], T>) entityReaders.get(expectedType);
        if (reader == null) {
            throw new UnsupportedOperationException("Explicit reader for the type " + expectedType
                + " wasn't found");
        }
        return reader.apply(entity);
    }

    private static List<String> getLinkRelations(final String rel) {
        return (rel == null) ? null : Arrays.asList(Tokenizer.tokenize(rel, "\" "));
    }

    public static final class Builder {
        private final Map<Class<?>, Function<byte[], ?>> entityReaders = new HashMap<>();
        private final Response response;

        private Builder(Response response) {
            this.response = response;
        }

        public <T> Builder withEntityReader(Class<T> entityType, Function<byte[], T> entityReader) {
            entityReaders.put(entityType, entityReader);
            return this;
        }

        public ManagedJaxrsResponse build() {
            return new ManagedJaxrsResponse(response, entityReaders);
        }
    }
}
