package com.extole.common.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.JacksonJsonValue;
import com.github.wnameless.json.flattener.JsonFlattener;

public final class JsonMap implements Map<String, Object> {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getInstance();

    private final Map<String, Object> keyCaseInsensitiveFlattenedMap;

    private JsonMap(Map<String, ? extends Object> map) {
        if (areAllValuesJavascriptPrimitives(map)) {
            this.keyCaseInsensitiveFlattenedMap = (Map<String, Object>) KeyCaseInsensitiveMap.create(map);
        } else {
            Map<String, Object> flattenedMap =
                new JsonFlattener(new JacksonJsonValue(OBJECT_MAPPER.valueToTree(map)))
                    .flattenAsMap();
            this.keyCaseInsensitiveFlattenedMap = KeyCaseInsensitiveMap.create(flattenedMap);
        }
    }

    public Map<String, String> toFlattenedMap() {
        Map<String, String> flattenedMap = KeyCaseInsensitiveMap.create();
        keyCaseInsensitiveFlattenedMap.entrySet()
            .forEach(entry -> flattenedMap.put(entry.getKey(),
                entry.getValue() != null ? entry.getValue().toString() : null));
        return flattenedMap;
    }

    public Optional<String> getValueAsString(String key) {
        Object value = keyCaseInsensitiveFlattenedMap.get(key);
        return value == null ? Optional.empty() : Optional.of(value.toString());
    }

    @Override
    public int size() {
        return keyCaseInsensitiveFlattenedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return keyCaseInsensitiveFlattenedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyCaseInsensitiveFlattenedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return keyCaseInsensitiveFlattenedMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return keyCaseInsensitiveFlattenedMap.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> mapToAdd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(keyCaseInsensitiveFlattenedMap.keySet());
    }

    @Override
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(keyCaseInsensitiveFlattenedMap.values());
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(keyCaseInsensitiveFlattenedMap.entrySet());
    }

    public static JsonMap valueOf(Map<String, ? extends Object> map) {
        return new JsonMap(map);
    }

    private static boolean areAllValuesJavascriptPrimitives(Map<String, ? extends Object> map) {
        return map.values().stream()
            .allMatch(value -> value instanceof Boolean ||
                value instanceof Number ||
                value instanceof String ||
                value instanceof Character);
    }

    @Override
    public String toString() {
        Iterator<Entry<String, Object>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<String, Object> e = i.next();
            String key = e.getKey();
            Object value = e.getValue();
            sb.append(key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }

}
