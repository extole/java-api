package com.extole.common.rest.support.request.resolver;

public interface PolymorphicRequestTypeResolver {

    String resolve(PolymorphicRequestTypeResolverContext context)
        throws PolymorphicRequestTypeResolverRestWrapperException, MissingIdRequestTypeResolverException;
}
