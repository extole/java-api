package com.extole.common.lang.event.name;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class EventNameValidator {

    private static final Pattern ALLOWED_CHARACTER_PATTERN =
        Pattern.compile("^[0-9a-zA-Z_\\-.]+[0-9a-zA-Z_\\-.\\s]*[0-9a-zA-Z_\\-.]+$");

    private static final String NOT_ALLOWED_CHARACTER_PATTERN = "[^0-9a-zA-Z_\\-.\\s]";

    private static final int EVENT_NAME_MIN_LENGTH = 2;
    private static final int EVENT_NAME_MAX_LENGTH = 200;

    private EventNameValidator() {
    }

    public static void validate(String eventName)
        throws EventNameLengthException, IllegalCharacterInEventNameException {
        if (StringUtils.isBlank(eventName) || eventName.length() < EVENT_NAME_MIN_LENGTH
            || eventName.length() > EVENT_NAME_MAX_LENGTH) {
            throw new EventNameLengthException(
                String.format("Name length should be within %s and %s", Integer.valueOf(EVENT_NAME_MIN_LENGTH),
                    Integer.valueOf(EVENT_NAME_MAX_LENGTH)),
                EVENT_NAME_MIN_LENGTH, EVENT_NAME_MAX_LENGTH);
        }

        if (!ALLOWED_CHARACTER_PATTERN.matcher(eventName).matches()) {
            throw new IllegalCharacterInEventNameException(
                String.format("Name should match %s", ALLOWED_CHARACTER_PATTERN.pattern()));
        }
    }

    public static String normalizeEventName(String eventName) {
        return eventName.replaceAll(NOT_ALLOWED_CHARACTER_PATTERN, StringUtils.EMPTY);
    }

}
