package com.extole.common.persistence.persist;

import javax.sql.DataSource;

import net.sf.persist.Persist;
import net.sf.persist.PersistRegistry;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.persistence.persist.custom.IdPersistAttributeReader;
import com.extole.common.persistence.persist.custom.IdPersistAttributeWriter;
import com.extole.common.persistence.persist.custom.PrimaryKeyPersistAttributeReader;
import com.extole.common.persistence.persist.custom.PrimaryKeyPersistAttributeWriter;
import com.extole.id.Id;
import com.extole.id.PrimaryKey;
import com.extole.spring.ServiceLocator;

/**
 * Factory class for creating instances of PersistTemplate
 */
public class PersistTemplateFactory {
    static {
        PersistRegistry persistRegistry = PersistRegistry.getInstance();

        persistRegistry.registerPersistAttributeReader(PrimaryKey.class, new PrimaryKeyPersistAttributeReader());
        persistRegistry.registerPersistAttributeWriter(PrimaryKey.class, new PrimaryKeyPersistAttributeWriter());

        persistRegistry.registerPersistAttributeReader(Id.class, new IdPersistAttributeReader());
        persistRegistry.registerPersistAttributeWriter(Id.class, new IdPersistAttributeWriter());
    }
    private final DataSource dataSource;
    private final ServiceLocator serviceLocator;
    private final ExtolePersistExceptionTranslator exceptionTranslator;
    private final ExtoleMetricRegistry metricRegistry;

    private final DatabaseName databaseName;

    public PersistTemplateFactory(DataSource dataSource, ServiceLocator serviceLocator,
        ExtoleMetricRegistry metricRegistry, DatabaseName databaseName) {
        this.dataSource = dataSource;
        this.serviceLocator = serviceLocator;
        this.exceptionTranslator = new ExtolePersistExceptionTranslator(dataSource);
        this.metricRegistry = metricRegistry;
        this.databaseName = databaseName;
    }

    /**
     * This is to be used for unwired "simple" DTO objects.
     */
    public <T> PersistTemplate<T> newSimplePersistTemplate(Class<T> defaultObjectClass) {
        return new PersistTemplate<>(dataSource, exceptionTranslator, defaultObjectClass, null, metricRegistry,
            databaseName);
    }

    public <T> PersistTemplate<T> newPersistTemplate(Class<? extends ObjectFactory<T>> objectFactoryClass) {
        ObjectFactory<T> objectFactory = serviceLocator.lookupSingleton(objectFactoryClass);
        return new PersistTemplate<>(dataSource, exceptionTranslator, objectFactory.getDtoClass(),
            new PersistObjectFactory(objectFactory), metricRegistry, databaseName);
    }

    /**
     * Object factory which wraps the persist specific implementation with our own.
     */
    private static final class PersistObjectFactory implements Persist.ObjectFactory {
        private final ObjectFactory<?> objectFactory;

        PersistObjectFactory(ObjectFactory<?> objectFactory) {
            this.objectFactory = objectFactory;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Object newInstance(Class clazz) {
            return objectFactory.create();
        }
    }
}
