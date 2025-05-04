package com.extole.api.step.action.display;

import java.util.Map;

public interface ApiResponseBuilder {
    ApiResponseBuilder withBody(String body);

    ApiResponseBuilder withHeader(String name, String value);

    ApiResponseBuilder withHeaders(Map<String, String> headers);

    ApiResponseBuilder withStatusCode(int statusCode);

    ApiResponse build();
}
