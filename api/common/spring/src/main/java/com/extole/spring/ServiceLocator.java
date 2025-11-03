package com.extole.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates beans in the spring context.
 *
 * This is intended to create beans that are of @Scope("prototype").
 */
public final class ServiceLocator implements ApplicationContextAware {

    public static final String PROTOTYPE = "prototype";

    private ApplicationContext context;

    public <T> T create(Class<T> clazz) {
        String[] beanNames = context.getBeanNamesForType(clazz);

        if (beanNames.length < 1) {
            throw new RuntimeException("Unable to find bean of type " + clazz.getName() + " in application context");
        }

        if (beanNames.length > 1) {
            throw new RuntimeException("Multiple beans of type " + clazz.getName() + " in application context");
        }

        return create(beanNames[0], clazz);
    }

    public <T> T create(String beanName, Class<T> clazz) {
        if (!context.isPrototype(beanName)) {
            throw new RuntimeException("Bean of type " + clazz.getName() + " is not a prototype.");
        }

        return context.getBean(beanName, clazz);
    }

    /**
     * Note that we don't enforce that only singletons are looked up.
     */
    public <T> T lookupSingleton(Class<T> clazz) {
        return context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
