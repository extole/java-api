package com.extole.common.lang;

import java.util.Collection;
import java.util.TreeSet;

class CaseInsensitiveTreeSet<V> extends TreeSet<String> implements CaseInsensitiveSet {

    CaseInsensitiveTreeSet() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    CaseInsensitiveTreeSet(Collection<String> elements) {
        super(String.CASE_INSENSITIVE_ORDER);
        if (elements != null) {
            addAll(elements);
        }
    }

}
