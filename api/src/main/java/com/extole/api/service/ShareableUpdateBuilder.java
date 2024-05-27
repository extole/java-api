package com.extole.api.service;

public interface ShareableUpdateBuilder {

    ShareableContentBuilder getContentBuilder();

    ShareableUpdateBuilder withKey(String key);

    ShareableUpdateBuilder withLabel(String label);

    ShareableUpdateBuilder withNoLabel();

    ShareableUpdateBuilder addData(String name, String value);

    ShareableUpdateBuilder removeData(String name);

    PersonBuilder done();

}
