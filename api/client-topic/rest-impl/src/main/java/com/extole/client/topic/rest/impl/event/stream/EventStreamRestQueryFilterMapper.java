package com.extole.client.topic.rest.impl.event.stream;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

import com.extole.event.stream.EventStreamEvent;

@Component
public class EventStreamRestQueryFilterMapper {

    Predicate<EventStreamEvent> parse(String jsonPathFilters) {
        return event -> {
            Map<String, String> filters = parseJsonPathFilter(jsonPathFilters);
            if (filters.isEmpty()) {
                return true;
            }
            return filters.entrySet()
                .stream()
                .allMatch(jsonPath -> {
                    try {
                        Object result = JsonPath.read(event.getEvent(), jsonPath.getKey());
                        if (result == null) {
                            return false;
                        }
                        if (result instanceof String) {
                            String actualValue = result.toString();
                            return actualValue.matches(jsonPath.getValue());
                        } else if (result instanceof JSONObject) {
                            JSONObject actualValue = (JSONObject) result;
                            return !actualValue.isEmpty();
                        } else if (result instanceof JSONArray) {
                            JSONArray actualValue = (JSONArray) result;
                            return !actualValue.isEmpty();
                        }
                        return true;
                    } catch (InvalidPathException | IllegalArgumentException e) {
                        return false;
                    }
                });
        };
    }

    private static Map<String, String> parseJsonPathFilter(String jsonPathFilters) {
        return Arrays.stream(jsonPathFilters.split("\\|"))
            .filter(filterValue -> !Strings.isNullOrEmpty(filterValue))
            .map(filterValue -> filterValue.split("(?<![=])=(?![=])"))
            .map(filterPair -> Arrays.stream(filterPair).map(String::trim).toArray(String[]::new))
            .filter(filterPair -> filterPair.length >= 1)
            .collect(Collectors.toMap(filterPair -> filterPair[0],
                filterPair -> filterPair.length > 1 ? filterPair[1] : ""));
    }
}
