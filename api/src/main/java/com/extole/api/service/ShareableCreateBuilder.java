package com.extole.api.service;

import java.util.List;
import java.util.Map;

public interface ShareableCreateBuilder {

    ShareableContentBuilder getContentBuilder();

    ShareableCreateBuilder withKey(String key);

    ShareableCreateBuilder withCode(String code);

    ShareableCreateBuilder withPreferredCodePrefixes(List<String> preferredCodePrefixes);

    ShareableCreateBuilder withLabel(String label);

    ShareableCreateBuilder withNoLabel();

    ShareableCreateBuilder addData(String name, String value);

    ShareableCreateBuilder withData(Map<String, String> data);

    ShareableCreateBuilder withClientDomainId(String programId);

    PersonBuilder done();

}
