package com.extole.common.lock;

import java.util.regex.Pattern;

public class LockDescription {

    private static final Pattern PATTERN = Pattern.compile("[0-9a-z\\_\\-]*", Pattern.CASE_INSENSITIVE);

    private final String description;

    public LockDescription(String description) {
        if (!PATTERN.matcher(description).matches()) {
            throw new RuntimeException("Lock description " + description + " is not in allowed format.");
        }
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("LockDescription[%s]", description);
    }
}
