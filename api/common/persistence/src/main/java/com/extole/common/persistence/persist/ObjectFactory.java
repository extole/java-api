package com.extole.common.persistence.persist;

/**
 * Interface to create returned objects from a query.
 *
 * @param <T> the type of the object returned from a query.
 */
public interface ObjectFactory<T> {

    T create();

    Class<T> getDtoClass();
}
