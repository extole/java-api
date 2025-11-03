package com.extole.common.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class MultimapUtils {

    private MultimapUtils() {
    }

    public static <K, V> Map<K, List<V>> copyToNew(Map<K, List<V>> map) {
        Map<K, List<V>> copy = new HashMap<>(map.size());
        copy(map, copy);
        return copy;
    }

    public static <K, V> void copy(Map<K, List<V>> from, Map<K, List<V>> to) {
        for (Entry<K, List<V>> entry : from.entrySet()) {
            to.put(entry.getKey(), entry.getValue() != null ? new ArrayList<>(entry.getValue()) : null);
        }
    }
}
