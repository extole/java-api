package com.extole.common.rest.support.openapi;

import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.jaxrs2.integration.JaxrsApplicationAndResourcePackagesAnnotationScanner;

public class ExtoleOpenApiScanner extends JaxrsApplicationAndResourcePackagesAnnotationScanner {

    public ExtoleOpenApiScanner() {
        super();
    }

    @Override
    public Set<Class<?>> classes() {
        Set<Class<?>> classes = super.classes();
        if (openApiConfiguration.getResourcePackages() != null
            && !openApiConfiguration.getResourcePackages().isEmpty()) {
            Set<String> resourcePackages = openApiConfiguration.getResourcePackages();
            return classes.stream()
                .filter(clazz -> resourcePackages.stream()
                    .anyMatch(resourcePackage -> clazz.getPackage().getName().startsWith(resourcePackage)))
                .collect(Collectors.toSet());
        }
        return classes;
    }
}
