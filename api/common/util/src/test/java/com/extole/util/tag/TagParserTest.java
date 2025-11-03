package com.extole.util.tag;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class TagParserTest {

    @Test
    public void parseNameTag() {
        String name = "name";

        Optional<Tag> tagOptional = TagParser.INSTANCE.parse(name);
        assertThat(tagOptional).isPresent();
        Tag tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo(name);
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();
    }

    @Test
    public void parseNameTagWithType() {
        String tagString = "internal:name";

        Optional<Tag> tagOptional = TagParser.INSTANCE.parse(tagString);
        assertThat(tagOptional).isPresent();
        Tag tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("name");
        assertThat(tag.getType()).hasValue("internal");
        assertThat(tag.getValue()).isEmpty();

        tagString = "extole:name";
        tagOptional = TagParser.INSTANCE.parse(tagString);
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("name");
        assertThat(tag.getType()).hasValue("extole");
        assertThat(tag.getValue()).isEmpty();
    }

    @Test
    public void parseNameValueTag() {
        String tagString = "name=value";

        Optional<Tag> tagOptional = TagParser.INSTANCE.parse(tagString);
        assertThat(tagOptional).isPresent();
        Tag tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("name");
        assertThat(tag.getValue()).hasValue("value");
        assertThat(tag.getType()).isEmpty();
    }

    @Test
    public void parseNameValueTagWithType() {
        String tagString = "internal:name=value";

        Optional<Tag> tagOptional = TagParser.INSTANCE.parse(tagString);
        assertThat(tagOptional).isPresent();
        Tag tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("name");
        assertThat(tag.getValue()).hasValue("value");
        assertThat(tag.getType()).hasValue("internal");

        tagString = "extole:name=value";

        tagOptional = TagParser.INSTANCE.parse(tagString);
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("name");
        assertThat(tag.getValue()).hasValue("value");
        assertThat(tag.getType()).hasValue("extole");
    }

    @Test
    public void parseInvalidString() {
        Optional<Tag> tagOptional = TagParser.INSTANCE.parse("    ");
        assertThat(tagOptional).isEmpty();

        tagOptional = TagParser.INSTANCE.parse("");
        assertThat(tagOptional).isEmpty();

        tagOptional = TagParser.INSTANCE.parse((String) null);
        assertThat(tagOptional).isEmpty();
    }

    @Test
    public void parseNonMatchingString() {
        Optional<Tag> tagOptional = TagParser.INSTANCE.parse("internal:");
        assertThat(tagOptional).isPresent();
        Tag tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("internal:");
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();

        tagOptional = TagParser.INSTANCE.parse("internal:name=");
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("internal:name=");
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();

        tagOptional = TagParser.INSTANCE.parse("name=");
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("name=");
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();

        tagOptional = TagParser.INSTANCE.parse("inte rnal:name");
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("inte rnal:name");
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();

        tagOptional = TagParser.INSTANCE.parse("n ame=value");
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("n ame=value");
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();

        tagOptional = TagParser.INSTANCE.parse("internal:name:value");
        assertThat(tagOptional).isPresent();
        tag = tagOptional.get();
        assertThat(tag.getName()).isEqualTo("internal:name:value");
        assertThat(tag.getType()).isEmpty();
        assertThat(tag.getValue()).isEmpty();
    }

    @Test
    public void parseTagCollection() {
        Set<String> tagStrings =
            Set.of("key", "key2=value", "internal:key3", "internal:key4=value2", "extole:key5", "extole:key6=value3");

        List<Tag> expectedTagsCollection = List.of(
            Tag.create("key").build(),
            Tag.create("key2").withValue("value").build(),
            Tag.createInternal("key3").build(),
            Tag.createInternal("key4").withValue("value2").build(),
            Tag.createExtole("key5").build(),
            Tag.createExtole("key6").withValue("value3").build());

        TagCollection actualTagsCollection = TagParser.INSTANCE.parse(tagStrings);
        assertThat(actualTagsCollection.values()).hasSize(6)
            .usingFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedTagsCollection);
    }

    @Test
    public void parseTagCollectionWithInvalidValues() {
        Set<String> tagStrings = Set.of("internal:", "internal:name=", "key=", "ke y2=value", "inter nal:key3",
            "extole:ke y5", "extole:ke y6=value3", "", "    ", "k ey");

        Set<String> expectedTags = Set.of("internal:", "internal:name=", "key=", "ke y2=value", "inter nal:key3",
            "extole:ke y5", "extole:ke y6=value3", "k ey");

        TagCollection actualTagsCollection = TagParser.INSTANCE.parse(tagStrings);
        assertThat(actualTagsCollection.values())
            .allMatch(tag -> tag.getValue().isEmpty() && tag.getType().isEmpty())
            .extracting(tag -> tag.toString())
            .containsExactlyInAnyOrderElementsOf(expectedTags);
    }
}
