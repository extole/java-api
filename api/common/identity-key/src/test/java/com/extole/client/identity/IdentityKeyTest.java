package com.extole.client.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import com.extole.common.lang.ObjectMapperProvider;

public class IdentityKeyTest {

    @Test
    public void testIdentityKeyIsNotEqual() {
        IdentityKey identityKey1 = IdentityKey.valueOf(" h1lloWorld  ");
        IdentityKey identityKey2 = IdentityKey.valueOf(" HelloWorld  ");

        assertThat(identityKey1).isNotEqualTo(identityKey2);
    }

    @Test
    public void testIdentityKeyEqualsAndHashCodeContract() {
        IdentityKey identityKey1 = IdentityKey.valueOf(" HelloWorld  ");
        IdentityKey identityKey2 = IdentityKey.valueOf(" HelloWorld  ");

        assertThat(identityKey1.hashCode()).isEqualTo(identityKey2.hashCode());
        assertEquality(identityKey1, identityKey2);
    }

    @Test
    public void testIdentityKeyEqualsIsTransitive() {
        IdentityKey identityKey1 = IdentityKey.valueOf(" helloWorld  ");
        IdentityKey identityKey2 = IdentityKey.valueOf(" HElloWorld  ");
        IdentityKey identityKey3 = IdentityKey.valueOf(" HeLLoWorld  ");
        assertEquality(identityKey1, identityKey2);
        assertEquality(identityKey2, identityKey3);
        assertEquality(identityKey1, identityKey3);
    }

    @Test
    public void testIdentityKeyEqualsIsSymmetric() {
        IdentityKey identityKey1 = IdentityKey.valueOf(" HelloWorld  ");
        IdentityKey identityKey2 = IdentityKey.valueOf(" HelloWorld  ");
        assertEquality(identityKey1, identityKey2);
    }

    @Test
    public void testIdentityKeyEqualsIsReflexive() {
        IdentityKey identityKey = IdentityKey.valueOf(" HelloWorld  ");
        assertThat(identityKey).isEqualTo(identityKey);
    }

    @Test
    public void testIdentityKeyEqualityCaseSensitive() {
        assertEquality(IdentityKey.valueOf(" HelloWorld  "), IdentityKey.valueOf(" HelloWorld  "));
    }

    @Test
    public void testIdentityKeyDeserialize() {
        assertThat(deserialize("\"helloworld\""))
            .isEqualTo(IdentityKey.valueOf("helloworld"));
    }

    @Test
    public void testIdentityKeyAfterSerializeDeserializeIsEqualToInitialValue() {
        IdentityKey identityKey = IdentityKey.valueOf(" HelloWorld  ");
        assertThat(deserialize(serialize(identityKey)))
            .isEqualTo(identityKey);
    }

    @Test
    public void testIdentityKeySerialize() {
        IdentityKey identityKey = IdentityKey.valueOf(" HelloWorld  ");
        assertThat(serialize(identityKey))
            .isEqualTo("\" HelloWorld  \"");
    }

    @Test
    public void testIdentityKeyNullValueIsForbidden() {
        assertThatThrownBy(() -> IdentityKey.valueOf(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("value can't be null");
    }

    private IdentityKey deserialize(String serialized) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().readValue(serialized, IdentityKey.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String serialize(Object object) {
        try {
            return ObjectMapperProvider.getConfiguredInstance().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private void assertEquality(IdentityKey identityKey1, IdentityKey identityKey2) {
        assertThat(identityKey1).isEqualTo(identityKey2);
        assertThat(identityKey2).isEqualTo(identityKey1);
    }
}
