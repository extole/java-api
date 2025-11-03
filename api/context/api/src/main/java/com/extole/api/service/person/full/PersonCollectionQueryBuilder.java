package com.extole.api.service.person.full;

import javax.annotation.Nullable;

public interface PersonCollectionQueryBuilder<T> {
    PersonCollectionQueryBuilder<T> withLimit(int limit);

    PersonCollectionQueryBuilder<T> withOffset(int offset);

    PersonCollectionIterator<T> iterator();

    interface PersonCollectionIterator<T> {
        @Nullable
        T next();
    }

    enum CollectionType {
        JOURNEYS, REWARDS, STEPS, SHARES, SHAREABLES, RELATIONSHIPS, DATA
    }
}
