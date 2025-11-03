package com.extole.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;

public class JacksonExceptionRestTranslatorTest {

    private static final class IdentificationDocument {

        private String uniqueIdentifier;

        @JsonIgnore
        private int secretCode;

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public int getSecretCode() {
            return secretCode;
        }

    }

    private static final class CalendarRecord {
        private Date showedDate;

        public Date getShowedDate() {
            return showedDate;
        }
    }

    private abstract static class Animal {
        private String name;

        public String getName() {
            return name;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    private static final class TypeIdPojo {
    }

    private static final class PojoWithId {
        private TypeIdPojo id;

        public TypeIdPojo getId() {
            return id;
        }
    }

    private static final class Car {
        private String seat;

        @JsonSetter(nulls = Nulls.FAIL)
        private String motor;

        public String getMotor() {
            return motor;
        }

        public String getSeat() {
            return seat;
        }
    }

    private JacksonExceptionRestTranslator jacksonExceptionRestTranslator;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.jacksonExceptionRestTranslator = new JacksonExceptionRestTranslator();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void unexpectedCharacterInsideJsonTest() {
        String invalidJsonExample = "{Q\"testField\":\"Test124\"}";

        JsonParseException e = assertThrowsExactly(JsonParseException.class,
            () -> objectMapper.readValue(invalidJsonExample,
                IdentificationDocument.class));

        RestException restException = jacksonExceptionRestTranslator.translate(e);

        assertThat(restException.getParameters()).containsEntry("detailed_message",
            "Unexpected character ('Q' (code 81)): was expecting double-quote to start field name");
        assertThat(restException.getErrorCode())
            .isEqualTo(WebApplicationRestRuntimeException.INVALID_JSON_NON_PARSEABLE.getName());
    }

    @Test
    public void invalidJsonMalformedTest() {
        String invalidJsonExample = "{\"showedDate\": \"WWW2\"}";

        InvalidFormatException invalidFormatException =
            assertThrowsExactly(InvalidFormatException.class,
                () -> objectMapper.readValue(invalidJsonExample, CalendarRecord.class));

        AssertionsForClassTypes.assertThat(invalidFormatException.getClass()).isEqualTo(InvalidFormatException.class);
        RestException restException = jacksonExceptionRestTranslator.translate(invalidFormatException);

        assertThat(restException.getParameters()).containsAllEntriesOf(ImmutableMap.of(
            "invalid_property", "showedDate",
            "invalid_value", "WWW2",
            "location", "{line: 1, column: 16}"));
        assertThat(restException.getErrorCode())
            .isEqualTo(WebApplicationRestRuntimeException.INVALID_JSON_MALFORMED.getName());
    }

    @Test
    public void invalidJsonUnknownTypeIdTest() {
        String invalidJsonExample = "{\"id\":{\"type\":\"foo\",\"a\":4}}";

        InvalidTypeIdException invalidTypeIdException =
            assertThrowsExactly(InvalidTypeIdException.class,
                () -> objectMapper.readValue(invalidJsonExample, PojoWithId.class));

        AssertionsForClassTypes.assertThat(invalidTypeIdException.getClass()).isEqualTo(InvalidTypeIdException.class);
        RestException restException = jacksonExceptionRestTranslator.translate(invalidTypeIdException);

        assertThat(restException.getParameters()).containsAllEntriesOf(ImmutableMap.of(
            "invalid_property", "id",
            "location", "{line: 1, column: 15}"));
        assertThat(restException.getErrorCode())
            .isEqualTo(WebApplicationRestRuntimeException.INVALID_JSON_UNKNOWN_TYPE_ID.getName());
    }

    @Test
    public void invalidJsonUnrecognizedPropertyTest() {
        InputStream invalidJsonExample = new ByteArrayInputStream(
            "{\"uniqueIdentifier\":\"Test124\", \"foo\": 2}".getBytes());

        UnrecognizedPropertyException unrecognizedPropertyException =
            assertThrowsExactly(UnrecognizedPropertyException.class,
                () -> objectMapper.readValue(invalidJsonExample, IdentificationDocument.class));

        AssertionsForClassTypes.assertThat(unrecognizedPropertyException.getClass())
            .isEqualTo(UnrecognizedPropertyException.class);
        RestException restException = jacksonExceptionRestTranslator.translate(unrecognizedPropertyException);

        assertThat(restException.getParameters()).containsAllEntriesOf(ImmutableMap.of(
            "unrecognized_property", "foo",
            "known_properties", "[uniqueIdentifier]",
            "location",
            "{line: 1, column: 40}"));
        assertThat(restException.getErrorCode())
            .isEqualTo(WebApplicationRestRuntimeException.INVALID_JSON_UNRECOGNIZED_PROPERTY.getName());
    }

    @Test
    public void invalidNullPropertyAsGenericInvalidJsonTest() {
        String invalidJsonExample = "{ \"seat\":null, \"motor\":null }";
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        InvalidNullException invalidNullException = assertThrowsExactly(InvalidNullException.class,
            () -> objectMapper.readValue(invalidJsonExample, Car.class));

        RestException restException = jacksonExceptionRestTranslator.translate(invalidNullException);

        assertThat(restException.getParameters())
            .containsAllEntriesOf(ImmutableMap.of("location", "{line: 1, column: 24}"));
        assertThat(restException.getErrorCode())
            .isEqualTo(WebApplicationRestRuntimeException.INVALID_JSON.getName());
    }

}
