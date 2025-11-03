package com.extole.util.tag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

public final class TagParser {

    public static final TagParser INSTANCE = new TagParser();

    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String VALUE = "value";

    private static final Pattern TAG_PATTERN =
        Pattern.compile("^((?<" + TYPE + ">[^=:\\s]+):)?(?<" + NAME + ">[^=:\\s]+)(=(?<" + VALUE + ">.+))?$");

    public Optional<Tag> parse(@Nullable String tag) {
        if (StringUtils.trimToNull(tag) == null) {
            return Optional.empty();
        }

        Matcher tagMatcher = TAG_PATTERN.matcher(tag);
        if (!tagMatcher.matches()) {
            return Optional.of(Tag.create(tag).build());
        }
        Tag.Builder tagBuilder = Tag.create(tagMatcher.group(NAME));
        String type = tagMatcher.group(TYPE);
        if (type != null) {
            tagBuilder.withType(type);
        }
        String value = tagMatcher.group(VALUE);
        if (value != null) {
            tagBuilder.withValue(value);
        }
        return Optional.of(tagBuilder.build());
    }

    public TagCollection parse(Collection<String> tagStrings) {
        List<Tag> tags = tagStrings.stream()
            .map(tag -> parse(tag))
            .filter(tagOptional -> tagOptional.isPresent())
            .map(tagOptional -> tagOptional.get())
            .collect(Collectors.toList());
        return new TagCollection(tags);
    }

    private TagParser() {
    }
}
