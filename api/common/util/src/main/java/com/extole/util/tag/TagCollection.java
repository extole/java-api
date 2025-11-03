package com.extole.util.tag;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public final class TagCollection {

    private final Set<Tag> tags;

    public TagCollection(List<Tag> tags) {
        this.tags = new HashSet<>(tags);
    }

    public Set<Tag> values() {
        return Set.copyOf(tags);
    }

    public Set<String> stringValues() {
        return tags.stream().map(tag -> tag.toString()).collect(Collectors.toSet());
    }

    public boolean contains(String name) {
        return contains(tags, name, Optional.empty());
    }

    public boolean containsAny(Set<String> names) {
        return containsAny(tags, names, Optional.empty());
    }

    public boolean containsWithType(String name, String type) {
        return contains(tags, name, Optional.of(type));
    }

    public boolean containsAnyWithType(Set<String> names, String type) {
        return containsAny(tags, names, Optional.of(type));
    }

    public Optional<Tag> findFirst(String name) {
        return findFirst(tags, name, Optional.empty());
    }

    public Optional<Tag> findFirstWithType(String name, String type) {
        return findFirst(tags, name, Optional.of(type));
    }

    public boolean remove(Tag tag) {
        return tags.remove(tag);
    }

    public void add(Tag newTag) {
        tags.add(newTag);
    }

    @Override
    public String toString() {
        return "TagCollection{" +
            "tags=" + tags +
            '}';
    }

    private static boolean contains(Set<Tag> tags, String name, Optional<String> type) {
        return findFirst(tags, name, type).isPresent();
    }

    private static boolean containsAny(Set<Tag> tags, Set<String> names, Optional<String> type) {
        for (String name : names) {
            if (contains(tags, name, type)) {
                return true;
            }
        }
        return false;
    }

    private static Optional<Tag> findFirst(Set<Tag> tags, String name, Optional<String> type) {
        String trimmedName = StringUtils.trimToNull(name);
        if (trimmedName == null) {
            return Optional.empty();
        }
        return getTypeFilteredTagsStream(tags, type)
            .filter(tag -> tag.getName().equals(trimmedName))
            .findFirst();
    }

    private static Stream<Tag> getTypeFilteredTagsStream(Set<Tag> tags, Optional<String> type) {
        Stream<Tag> tagStream = tags.stream();
        if (type.isEmpty()) {
            return tagStream.filter(tag -> tag.getType().isEmpty());
        }
        return tagStream.filter(tag -> tag.getType().isPresent() && tag.getType().get().equals(type.get()));
    }
}
