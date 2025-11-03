package com.extole.common.lang;

import java.util.Collection;
import java.util.Set;

public interface CaseInsensitiveSet extends Set<String> {
    static CaseInsensitiveSet create() {
        return new CaseInsensitiveTreeSet<>();
    }

    static CaseInsensitiveSet create(Collection<String> elements) {
        return new CaseInsensitiveTreeSet<>(elements);
    }
}
