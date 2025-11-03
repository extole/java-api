package com.extole.common.lang;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class CaseInsensitiveSetTest {

    @Test
    void testCaseInsensitiveSetWithNoArgumentsConstructor() {
        CaseInsensitiveSet caseInsensitiveSet = CaseInsensitiveSet.create();
        caseInsensitiveSet.add("ABC");
        caseInsensitiveSet.add("abc");

        assertThat(caseInsensitiveSet).hasSize(1);
        assertThat(caseInsensitiveSet).contains("ABC");
        assertThat(caseInsensitiveSet).contains("abc");
        assertThat(caseInsensitiveSet).contains("Abc");
        assertThat(caseInsensitiveSet).doesNotContain("qwe");
    }

    @Test
    void testCaseInsensitiveSetBasedOnCollection() {
        CaseInsensitiveSet caseInsensitiveSet = CaseInsensitiveSet.create(List.of("ABC", "abc"));

        assertThat(caseInsensitiveSet).hasSize(1);
        assertThat(caseInsensitiveSet).contains("ABC");
        assertThat(caseInsensitiveSet).contains("abc");
        assertThat(caseInsensitiveSet).contains("Abc");
        assertThat(caseInsensitiveSet).doesNotContain("qwe");
    }

}
