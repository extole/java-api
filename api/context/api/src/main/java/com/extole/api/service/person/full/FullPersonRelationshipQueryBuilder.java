package com.extole.api.service.person.full;

import java.util.List;

import com.extole.api.person.PersonReferral;

public interface FullPersonRelationshipQueryBuilder extends PersonCollectionQueryBuilder<PersonReferral> {

    FullPersonRelationshipQueryBuilder withMyRoles(List<String> myRoles);

    FullPersonRelationshipQueryBuilder withContainers(List<String> containers);

    FullPersonRelationshipQueryBuilder withExcludeAnonymous(boolean excludeAnonymous);
}
