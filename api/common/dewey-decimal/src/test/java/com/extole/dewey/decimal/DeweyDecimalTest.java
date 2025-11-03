package com.extole.dewey.decimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DeweyDecimalTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    @ParameterizedTest
    @MethodSource("provideArgumentsForDeserializeWithInvalidFormatTest")
    void testDeserializeWithInvalidFormat(String expression, String expectedErrorMessage) {
        DeweyDecimalInvalidFormatException exception = assertThrowsExactly(DeweyDecimalInvalidFormatException.class,
            () -> {
                JavaType valueType =
                    objectMapper.constructType(new TypeReference<DummyModelWithNestedDeweyDecimal>() {});
                objectMapper.readValue(expression, valueType);
            });

        assertThat(exception.getOriginalMessage()).isEqualTo(expectedErrorMessage);
    }

    private static Stream<Arguments> provideArgumentsForDeserializeWithInvalidFormatTest() {
        return Stream.of(
            Arguments.of("{\"value\":\" \"}", "Blank string not allowed"),
            Arguments.of("{\"value\":\"abc\"}", "String has an invalid format for a Dewey decimal=abc"),
            Arguments.of("{\"value\":\"1.a.b.2\"}", "String has an invalid format for a Dewey decimal=1.a.b.2"),
            Arguments.of("{\"value\":\"1.1.\"}", "String has an invalid format for a Dewey decimal=1.1."));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForDeserializeWithMismatchedInputTest")
    void testDeserializeWithMismatchedInput(String expression, String expectedErrorMessage) {
        DeweyDecimalMismatchedInputException exception = assertThrowsExactly(DeweyDecimalMismatchedInputException.class,
            () -> {
                JavaType valueType =
                    objectMapper.constructType(new TypeReference<DummyModelWithNestedDeweyDecimal>() {});
                objectMapper.readValue(expression, valueType);
            });

        assertThat(exception.getOriginalMessage()).isEqualTo(expectedErrorMessage);
    }

    private static Stream<Arguments> provideArgumentsForDeserializeWithMismatchedInputTest() {
        return Stream.of(
            Arguments.of("{\"value\":1}", "Token with type=VALUE_NUMBER_INT can't be deserialized as a Dewey decimal"),
            Arguments.of("{\"value\":1.2}",
                "Token with type=VALUE_NUMBER_FLOAT can't be deserialized as a Dewey decimal"),
            Arguments.of("{\"value\":[\"123\"]}",
                "Token with type=START_ARRAY can't be deserialized as a Dewey decimal"),
            Arguments.of("{\"value\":true}", "Token with type=VALUE_TRUE can't be deserialized as a Dewey decimal"),
            Arguments.of("{\"value\":false}", "Token with type=VALUE_FALSE can't be deserialized as a Dewey decimal"));
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("provideArgumentsForDeserializeWithValidFormatTest")
    void testDeserializeWithValidFormat(String serialized, DeweyDecimal expectedValue) throws Exception {
        DummyModelWithNestedDeweyDecimal deserialized =
            objectMapper.readValue(serialized, DummyModelWithNestedDeweyDecimal.class);
        assertThat(deserialized.getValue()).isEqualTo(expectedValue);

        assertThat(objectMapper.writeValueAsString(deserialized)).isEqualTo(serialized);
    }

    private static Stream<Arguments> provideArgumentsForDeserializeWithValidFormatTest() {
        return Stream.of(
            Arguments.of("{\"value\":\"1\"}", DeweyDecimal.valueOf("1")),
            Arguments.of("{\"value\":\"1.2\"}", DeweyDecimal.valueOf("1.2")),
            Arguments.of("{\"value\":\"1.2.3\"}", DeweyDecimal.valueOf("1.2.3")),
            Arguments.of("{\"value\":null}", null));
    }

}
