package com.extole.common.lang.event.name;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

public class EventNameValidatorTest {

    @Test
    public void testValidateValidEventName() throws Exception {
        EventNameValidator.validate("test_event-name");
    }

    @Test
    public void testValidateTooShortEventName() {
        assertThrows(EventNameLengthException.class, () -> EventNameValidator.validate("a"));
    }

    @Test
    public void testValidateTooLongEventName() {
        assertThrows(EventNameLengthException.class,
            () -> EventNameValidator.validate(RandomStringUtils.randomAlphabetic(201)));
    }

    @Test
    public void testValidateEventNameMinLength() throws Exception {
        EventNameValidator.validate(RandomStringUtils.randomAlphabetic(2));
    }

    @Test
    public void testValidateEventNameMaxLength() throws Exception {
        EventNameValidator.validate(RandomStringUtils.randomAlphabetic(200));
    }

    @Test
    public void testValidateSpaceCharacterAtTheBeginning() {
        assertThrows(IllegalCharacterInEventNameException.class, () -> EventNameValidator.validate(" test"));
    }

    @Test
    public void testValidateManySpaceCharactersAtTheBeginning() {
        assertThrows(IllegalCharacterInEventNameException.class, () -> EventNameValidator.validate("     test"));
    }

    @Test
    public void testValidateSpaceCharacterAtTheEnd() {
        assertThrows(IllegalCharacterInEventNameException.class, () -> EventNameValidator.validate("test "));
    }

    @Test
    public void testValidateManySpaceCharactersAtTheEnd() {
        assertThrows(IllegalCharacterInEventNameException.class, () -> EventNameValidator.validate("test      "));
    }

    @Test
    public void testValidateSpaceCharacterInTheMiddle() throws Exception {
        EventNameValidator.validate("test event name");
    }

    @Test
    public void testValidateIllegalCharacters() {
        assertThrows(IllegalCharacterInEventNameException.class, () -> EventNameValidator.validate("test!event*name"));
    }

}
