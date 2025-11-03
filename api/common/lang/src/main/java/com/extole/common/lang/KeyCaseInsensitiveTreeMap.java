package com.extole.common.lang;

import java.util.Map;
import java.util.TreeMap;

class KeyCaseInsensitiveTreeMap<V> extends TreeMap<String, V> implements KeyCaseInsensitiveMap<V> {

    KeyCaseInsensitiveTreeMap() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    KeyCaseInsensitiveTreeMap(Map<String, V> map) {
        super(String.CASE_INSENSITIVE_ORDER);
        if (map != null) {
            putAll(map);
        }
    }

}
