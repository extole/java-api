package com.extole.util.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    private static final String INTERNAL_TAG_TYPE = "internal";
    private static final String EXTOLE_TAG_TYPE = "extole";
    private static final String INTERNAL_TAG_PREFIX = INTERNAL_TAG_TYPE + ":";
    private static final String EXTOLE_TAG_PREFIX = EXTOLE_TAG_TYPE + ":";

    @Test
    public void createNameTag() {
        String name = "name";

        Tag tag = Tag.create(name).build();
        assertNameTag(tag, name);

        tag = Tag.createInternal(name).build();
        assertNameTag(tag, name);
        assertThat(tag.getType()).hasValue(INTERNAL_TAG_TYPE);

        tag = Tag.createExtole(name).build();
        assertNameTag(tag, name);
        assertThat(tag.getType()).hasValue(EXTOLE_TAG_TYPE);
    }

    @Test
    public void createNameValueTag() {
        String name = "name";
        String value = "value";

        Tag tag = Tag.create(name).withValue(value).build();
        assertNameValueTag(tag, name, value);

        tag = Tag.createInternal(name).withValue(value).build();
        assertNameValueTag(tag, name, value);
        assertThat(tag.getType()).hasValue(INTERNAL_TAG_TYPE);

        tag = Tag.createExtole(name).withValue(value).build();
        assertNameValueTag(tag, name, value);
        assertThat(tag.getType()).hasValue(EXTOLE_TAG_TYPE);
    }

    @Test
    public void createNameTagAsString() {
        String name = "name";

        String tagString = Tag.create(name).buildAsString();
        assertThat(tagString).isEqualTo(name);

        tagString = Tag.createInternal(name).buildAsString();
        assertThat(tagString).isEqualTo(INTERNAL_TAG_PREFIX + name);

        tagString = Tag.createExtole(name).buildAsString();
        assertThat(tagString).isEqualTo(EXTOLE_TAG_PREFIX + name);
    }

    @Test
    public void createNameValueTagAsString() {
        String name = "name";
        String value = "value";
        String separator = "=";

        String tagString = Tag.create(name).withValue(value).buildAsString();
        assertThat(tagString).isEqualTo(name + separator + value);

        tagString = Tag.createInternal(name).withValue(value).buildAsString();
        assertThat(tagString).isEqualTo(INTERNAL_TAG_PREFIX + name + separator + value);

        tagString = Tag.createExtole(name).withValue(value).buildAsString();
        assertThat(tagString).isEqualTo(EXTOLE_TAG_PREFIX + name + separator + value);
    }

    @Test
    public void createTagWithInvalidFields() {
        Tag.Builder tagBuilder = Tag.create("name");

        // INVALID NAME
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tagBuilder.withName("    "));
        assertThat(exception.getMessage()).contains("Name cannot be blank, empty or null");
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withName(""));
        assertThat(exception.getMessage()).contains("Name cannot be blank, empty or null");
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withName(null));
        assertThat(exception.getMessage()).contains("Name cannot be blank, empty or null");

        // INVALID VALUE
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withValue("    "));
        assertThat(exception.getMessage()).contains("Value cannot be blank, empty or null");
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withValue(""));
        assertThat(exception.getMessage()).contains("Value cannot be blank, empty or null");
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withValue(null));
        assertThat(exception.getMessage()).contains("Value cannot be blank, empty or null");

        // INVALID TYPE
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withType("    "));
        assertThat(exception.getMessage()).contains("Type cannot be blank, empty or null");
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withType(""));
        assertThat(exception.getMessage()).contains("Type cannot be blank, empty or null");
        exception = assertThrows(RuntimeException.class, () -> tagBuilder.withType(null));
        assertThat(exception.getMessage()).contains("Type cannot be blank, empty or null");
    }

    private static void assertNameTag(Tag tag, String expectedName) {
        assertThat(tag.getName()).isEqualTo(expectedName);
        assertThat(tag.getValue()).isEmpty();
    }

    private static void assertNameValueTag(Tag tag, String expectedName, String expectedValue) {
        assertThat(tag.getName()).isEqualTo(expectedName);
        assertThat(tag.getValue()).hasValue(expectedValue);
    }
}
