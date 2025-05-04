package com.extole.api;

import java.util.Map;

public interface ApiException {

    String getCode();

    Map<String, String> getParameters();

}
