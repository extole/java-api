package com.extole.client.rest.settings;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum PasswordStrength {
    NONE, LETTERS_AND_DIGITS, LETTERS_DIGITS_PUNCTUATION
}
