package com.extole.common.rest.support.authorization.extractor;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;

public interface AccessTokenExtractor {

    Optional<String> extract(ContainerRequestContext requestContext);

}
