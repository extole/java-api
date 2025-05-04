package com.extole.client.rest.person.iss;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.junit.jupiter.api.Test;

class IdentityShapeShiftCandidateTypeTest {

    @Test
    void testAllRestIdentityShapeShiftEnumValuesMatchModelEnumValues() {
        Set<String> restIdentityShapeShiftTypes = getEnumNames(IdentityShapeShiftType.class);
        Set<String> modelIdentityShapeShiftTypes = getEnumNames(
            com.extole.person.service.profile.iss.IdentityShapeShiftType.class);

        assertThat(SetUtils.difference(restIdentityShapeShiftTypes, modelIdentityShapeShiftTypes))
            .isEmpty();
    }

    private static <E extends Enum<E>> Set<String> getEnumNames(Class<E> enumClass) {
        return Set.of(enumClass.getEnumConstants()).stream()
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());
    }
}
