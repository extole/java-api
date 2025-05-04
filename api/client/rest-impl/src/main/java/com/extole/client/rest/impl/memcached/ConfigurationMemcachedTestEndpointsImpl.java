package com.extole.client.rest.impl.memcached;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.google.common.annotations.Beta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.common.memcached.ExtoleMemcachedClient;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;

@Provider
@Singleton
@Beta
@Path("/v2/test/configuration-cache")
public class ConfigurationMemcachedTestEndpointsImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationMemcachedTestEndpointsImpl.class);
    private static final String STORE_NAME = "config-memcached-test-endpoint";

    private final ExtoleMemcachedClient<String, String> memcachedClient;

    @Autowired
    public ConfigurationMemcachedTestEndpointsImpl(TestConfigurationRelatedMemcachedFactory memcachedFactory) {
        this.memcachedClient = memcachedFactory.createMemcachedClient(STORE_NAME);
    }

    @GET
    @Path("/{key}")
    public String get(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("key") String key) {
        try {
            return memcachedClient.get(key).orElse(null);
        } catch (Exception e) {
            LOG.error("Failed to retrieve memcached value for key: {}", key, e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class).withCause(e)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build();
        }
    }

    @DELETE
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean delete(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @PathParam("key") String key) {
        try {
            return memcachedClient.delete(key);
        } catch (Exception e) {
            LOG.error("Failed to delete memcached value for key: {}", key, e);
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class).withCause(e)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build();
        }
    }
}
