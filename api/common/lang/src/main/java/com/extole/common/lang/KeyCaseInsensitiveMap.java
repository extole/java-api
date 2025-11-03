package com.extole.common.lang;

import java.util.Map;

public interface KeyCaseInsensitiveMap<V> extends Map<String, V> {

    static <V> KeyCaseInsensitiveMap<V> create() {
        return new KeyCaseInsensitiveTreeMap<>();
    }

    static <V> KeyCaseInsensitiveMap<V> create(Map<String, V> map) {
        return new KeyCaseInsensitiveTreeMap<>(map);
    }

}
