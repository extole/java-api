package com.extole.util.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class TagsCollectionTest {

    @Test
    public void containsTag() {
        Set<String> tagStrings =
            Set.of("key", "key2=value", "internal:key3", "internal:key4=value2", "extole:key5", "extole:key6=value3");
        TagCollection tagCollection = TagParser.INSTANCE.parse(tagStrings);

        assertTrue(tagCollection.contains("key"));
        assertTrue(tagCollection.contains("key2"));
        assertFalse(tagCollection.contains("key3"));
        assertFalse(tagCollection.contains("key4"));
        assertFalse(tagCollection.contains("key5"));
        assertFalse(tagCollection.contains("key6"));

        // INTERNAL TAGS
        assertTrue(tagCollection.containsWithType("key3", Tag.TYPE_INTERNAL));
        assertTrue(tagCollection.containsWithType("key4", Tag.TYPE_INTERNAL));
        assertFalse(tagCollection.containsWithType("key", Tag.TYPE_INTERNAL));
        assertFalse(tagCollection.containsWithType("key2", Tag.TYPE_INTERNAL));
        assertFalse(tagCollection.containsWithType("key5", Tag.TYPE_INTERNAL));
        assertFalse(tagCollection.containsWithType("key6", Tag.TYPE_INTERNAL));

        // EXTOLE TAGS
        assertTrue(tagCollection.containsWithType("key5", Tag.TYPE_EXTOLE));
        assertTrue(tagCollection.containsWithType("key6", Tag.TYPE_EXTOLE));
        assertFalse(tagCollection.containsWithType("key", Tag.TYPE_EXTOLE));
        assertFalse(tagCollection.containsWithType("key2", Tag.TYPE_EXTOLE));
        assertFalse(tagCollection.containsWithType("key3", Tag.TYPE_EXTOLE));
        assertFalse(tagCollection.containsWithType("key4", Tag.TYPE_EXTOLE));

        // UNEXISTING TAGS
        assertFalse(tagCollection.contains("invalid"));
        assertFalse(tagCollection.containsWithType("invalid", Tag.TYPE_INTERNAL));
        assertFalse(tagCollection.containsWithType("invalid", Tag.TYPE_EXTOLE));

        // DUPLICATED TAGS
        tagStrings = Set.of("key", "key2=value2", "internal:key", "internal:key2", "internal:key2=value", "extole:key",
            "extole:key2=value3");
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        assertTrue(tagCollection.contains("key"));
        assertTrue(tagCollection.contains("key2"));
        assertTrue(tagCollection.containsWithType("key", Tag.TYPE_INTERNAL));
        assertTrue(tagCollection.containsWithType("key2", Tag.TYPE_INTERNAL));
        assertTrue(tagCollection.containsWithType("key", Tag.TYPE_EXTOLE));
        assertTrue(tagCollection.containsWithType("key2", Tag.TYPE_EXTOLE));
    }

    @Test
    public void findTag() {
        Set<String> tagStrings =
            Set.of("key", "key2=value", "internal:key3", "internal:key4=value2", "extole:key5", "extole:key6=value3");
        TagCollection tagCollection = TagParser.INSTANCE.parse(tagStrings);

        assertOptionalTagNameValueAndType(tagCollection.findFirst("key"), "key", Optional.empty(), Optional.empty());
        assertOptionalTagNameValueAndType(tagCollection.findFirst("key2"), "key2", Optional.of("value"),
            Optional.empty());
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key3", Tag.TYPE_INTERNAL), "key3",
            Optional.empty(), Optional.of("internal"));
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key4", Tag.TYPE_INTERNAL), "key4",
            Optional.of("value2"), Optional.of("internal"));
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key5", Tag.TYPE_EXTOLE), "key5",
            Optional.empty(), Optional.of("extole"));
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key6", Tag.TYPE_EXTOLE), "key6",
            Optional.of("value3"), Optional.of("extole"));

        // DUPLICATED KEYS
        tagStrings =
            Set.of("key", "key2=value", "internal:key", "internal:key2=value2", "extole:key", "extole:key2=value3");
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        assertOptionalTagNameValueAndType(tagCollection.findFirst("key"), "key", Optional.empty(), Optional.empty());
        assertOptionalTagNameValueAndType(tagCollection.findFirst("key2"), "key2", Optional.of("value"),
            Optional.empty());
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key", Tag.TYPE_INTERNAL), "key",
            Optional.empty(), Optional.of("internal"));
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key2", Tag.TYPE_INTERNAL), "key2",
            Optional.of("value2"), Optional.of("internal"));
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key", Tag.TYPE_EXTOLE), "key",
            Optional.empty(), Optional.of("extole"));
        assertOptionalTagNameValueAndType(tagCollection.findFirstWithType("key2", Tag.TYPE_EXTOLE), "key2",
            Optional.of("value3"), Optional.of("extole"));
    }

    @Test
    public void addTag() {
        Set<String> tagStrings =
            Set.of("key", "key2=value", "internal:key3", "internal:key4=value2", "extole:key5", "extole:key6=value3");
        testAddTag(tagKey -> Tag.create(tagKey), tagStrings);

        // DUPLICATED TAG WITH DIFFERENT TYPE - INTERNAL
        Tag newTag = Tag.create("key3").build();
        TagCollection tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);
        Set<String> actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size() + 1).containsAll(tagStrings).contains(newTag.toString());

        // DUPLICATED TAG WITH DIFFERENT TYPE - EXTOLE
        newTag = Tag.create("key6").withValue("value").build();
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);
        actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size() + 1).containsAll(tagStrings).contains(newTag.toString());
    }

    @Test
    public void addInternalTag() {
        Set<String> tagStrings =
            Set.of("internal:key", "internal:key2=value", "key3", "key4=value2", "extole:key5", "extole:key6=value3");
        testAddTag(tagKey -> Tag.createInternal(tagKey), tagStrings);
    }

    @Test
    public void addExtoleTag() {
        Set<String> tagStrings =
            Set.of("extole:key", "extole:key2=value", "internal:key3", "internal:key4=value2", "key5", "key6=value3");
        testAddTag(tagKey -> Tag.createExtole(tagKey), tagStrings);
    }

    private void testAddTag(Function<String, Tag.Builder> tagBuilder, Set<String> tagStrings) {
        // NEW TAG
        Tag newTag = tagBuilder.apply("key7").build();
        TagCollection tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);
        Set<String> actualTags =
            tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size() + 1).containsAll(tagStrings).contains(newTag.toString());

        // NEW TAG WITH VALUE
        newTag = tagBuilder.apply("key8").withValue("value").build();
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);
        actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size() + 1).containsAll(tagStrings).contains(newTag.toString());

        // DUPLICATED TAG
        newTag = tagBuilder.apply("key").build();
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);
        actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size()).containsAll(tagStrings);

        // DUPLICATED TAG WITH SAME VALUE
        newTag = tagBuilder.apply("key2").withValue("value").build();
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);
        actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size()).containsAll(tagStrings);

        // DUPLICATED TAG WITH DIFFERENT VALUE
        newTag = tagBuilder.apply("key2").withValue("value2").build();

        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);

        actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size() + 1)
            .containsAll(tagStrings)
            .contains(newTag.toString());

        // DUPLICATED TAG WITH VALUE AND EXISTING TAG IS WITHOUT VALUE
        newTag = tagBuilder.apply("key").withValue("value").build();

        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        tagCollection.add(newTag);

        actualTags = tagCollection.stringValues();
        assertThat(actualTags).hasSize(tagStrings.size() + 1)
            .containsAll(tagStrings)
            .contains(newTag.toString());
    }

    @Test
    public void removeTag() {
        Set<String> tagStrings =
            Set.of("key", "key2=value", "internal:key3", "internal:key4=value2", "extole:key5", "extole:key6=value3");
        testRemoveTag(tagKey -> Tag.create(tagKey), tagStrings);
    }

    @Test
    public void removeInternalTag() {
        Set<String> tagStrings =
            Set.of("internal:key", "internal:key2=value", "key3", "key4=value2", "extole:key5", "extole:key6=value3");
        testRemoveTag(tagKey -> Tag.createInternal(tagKey), tagStrings);
    }

    @Test
    public void removeExtoleTag() {
        Set<String> tagStrings =
            Set.of("extole:key", "extole:key2=value", "internal:key3", "internal:key4=value2", "key5", "key6=value3");
        testRemoveTag(tagKey -> Tag.createExtole(tagKey), tagStrings);
    }

    private void testRemoveTag(Function<String, Tag.Builder> tagBuilder, Set<String> tagStrings) {
        // REMOVE EXISTING TAG WITHOUT VALUE
        TagCollection tagCollection = TagParser.INSTANCE.parse(tagStrings);
        Tag tag = tagBuilder.apply("key").build();
        Set<String> expectedTags =
            tagStrings.stream().filter(tagString -> !tagString.equals(tag.toString())).collect(Collectors.toSet());
        assertTrue(tagCollection.remove(tag));
        assertThat(tagCollection.stringValues()).hasSize(tagStrings.size() - 1)
            .containsExactlyInAnyOrderElementsOf(expectedTags);

        // REMOVE EXISTING TAG WITH VALUE
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        Tag tag2 = tagBuilder.apply("key2").withValue("value").build();
        expectedTags =
            tagStrings.stream().filter(tagString -> !tagString.equals(tag2.toString())).collect(Collectors.toSet());
        assertTrue(tagCollection.remove(tag2));
        assertThat(tagCollection.stringValues()).hasSize(tagStrings.size() - 1)
            .containsExactlyInAnyOrderElementsOf(expectedTags);

        // REMOVE NON EXISTING TAG
        tagCollection = TagParser.INSTANCE.parse(tagStrings);
        Tag tag3 = tagBuilder.apply("key7").withValue("value").build();
        assertFalse(tagCollection.remove(tag3));
        assertThat(tagCollection.stringValues()).containsExactlyInAnyOrderElementsOf(tagStrings);
    }

    private void assertOptionalTagNameValueAndType(Optional<Tag> tagOptional, String name, Optional<String> value,
        Optional<String> type) {
        assertThat(tagOptional).isPresent();
        Tag tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo(name);
        value.ifPresent(expectedValue -> assertThat(tag.getValue()).hasValue(expectedValue));
        type.ifPresent(expectedType -> assertThat(tag.getType()).hasValue(expectedType));
    }
}
